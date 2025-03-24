from fastapi import FastAPI, HTTPException

from llama_index.core import (
    VectorStoreIndex, Settings, StorageContext, Document
)
#from llama_index.llms.openai import OpenAI Removed OpenAI call
from llama_index.embeddings.openai import OpenAIEmbedding
from llama_index.vector_stores.faiss import FaissVectorStore
from llama_index.vector_stores.postgres import PGVectorStore
from llama_index.core.indices.keyword_table import KeywordTableIndex
from llama_index.core.indices.composability import ComposableGraph
from llama_index.core.postprocessor import SimilarityPostprocessor
from pgvector_service import PGVectorService  # Import our Postgres service
from pydantic import BaseModel
import uvicorn
import faiss


import os
import requests
from github import Github  # PyGithub for private repos
from llama_index.core import Document
from pdfminer.high_level import extract_text as extract_pdf_text
from docx import Document as DocxDocument
import pandas as pd
from tika import parser
import io

# ✅ GitHub Config
GITHUB_TOKEN = ""  # 🔹 Required for private repos
#GITHUB_REPO = "mirkopetracca/genai-automation-elastic-poc"
GITHUB_BRANCH = "main"
#RAW_GITHUB_URL = f"https://raw.githubusercontent.com/{GITHUB_REPO}/{GITHUB_BRANCH}/"

# ✅ OpenAI Config
OPENAI_API_KEY = ""

# ✅ Function to extract text from different file formats
def extract_text_from_file(file_url, file_extension):
    response = requests.get(file_url)
    response.raise_for_status()
    content = response.content

    if file_extension in ["txt", "md"]:
        return content.decode("utf-8", errors="ignore")  # ✅ Plain text

    elif file_extension in ["doc", "docx"]:
        doc = DocxDocument(io.BytesIO(content))  # ✅ Read .docx
        return "\n".join([para.text for para in doc.paragraphs])

    elif file_extension in ["xls", "xlsx", "csv"]:
        df = pd.read_excel(io.BytesIO(content)) if "xls" in file_extension else pd.read_csv(io.BytesIO(content))  # ✅ Read Excel/CSV
        return df.to_string()

    elif file_extension == "pdf":
        with open("temp.pdf", "wb") as temp_file:
            temp_file.write(content)  # ✅ Save temporarily
        text = extract_pdf_text("temp.pdf")
        os.remove("temp.pdf")
        return text

    elif file_extension in ["ppt", "pptx", "uml"]:
        parsed = parser.from_buffer(content)  # ✅ Read PPT/UML
        return parsed["content"] if parsed else ""

    else:
        return None  # ❌ Unsupported file

# ✅ Load Documents from GitHub
#def load_documents_from_github():
#    return load_documents_from_github_repo(GITHUB_REPO)
    
# ✅ Load Documents from GitHub
def load_documents_from_github_repo(github_repo, folder):
    documents = []
    g = Github(GITHUB_TOKEN)
    githubUrl = f"https://raw.githubusercontent.com/{github_repo}/{GITHUB_BRANCH}/"
    repo = g.get_repo(github_repo)

    # 🔹 Get a list of all files in the repo
    contents = repo.get_contents(folder)
    
    while contents:
        file_content = contents.pop(0)

        if file_content.type == "dir":
            contents.extend(repo.get_contents(file_content.path))  # ✅ Recursively get subfolders
        else:
            file_url = githubUrl + file_content.path
            file_extension = file_content.path.split(".")[-1].lower()

            text = extract_text_from_file(file_url, file_extension)
            if text:
                documents.append(Document(text=text, metadata={"file_name": file_content.name}))

    return get_vector_index(documents)

# ✅ Load or create Documents from Postgres and create vector index
def get_vector_index(documents):
    # ✅ Postgres Vector Store Connection
    pg_vector_service = PGVectorService()

    # ✅ Check Existing Embeddings
    existing_embeddings = pg_vector_service.get_existing_embeddings()

    # ✅ Separate New Documents (Batch Processing)
    new_file_names = []
    new_texts = []
    document_embeddings = {}
    
    for doc in documents:
        file_name = doc.metadata["file_name"]

        # ✅ If embedding exists in DB, use it
        if file_name in existing_embeddings:
            document_embeddings[file_name] = existing_embeddings[file_name]
        else:
            new_file_names.append(file_name)
            new_texts.append(doc.text)

    # ✅ Batch Process Only New Documents
    if new_texts:
        print(f"🔹 Generating embeddings for {len(new_texts)} new documents...")
        
        # ✅ Call OpenAI ONCE with all texts
        new_embeddings = Settings.embed_model._get_text_embeddings(new_texts)

        # ✅ Store all embeddings in Postgres in a single transaction
        pg_vector_service.store_embeddings_batch(new_file_names, new_embeddings)

        # ✅ Add them to our local index
        for i in range(len(new_file_names)):
            document_embeddings[new_file_names[i]] = new_embeddings[i]
    
    # ✅ Close DB Connection
    pg_vector_service.close()
    
    # ✅ Initialize Postgres Vector Store
    vector_store = pg_vector_service.get_vector_store()
    storage_context = StorageContext.from_defaults(vector_store=vector_store)

    global vector_index
    # ✅ Index with Existing or Newly Generated Embeddings
    vector_index = VectorStoreIndex.from_vector_store(storage_context.vector_store)

    return new_file_names


# ✅ FastAPI API Setup
app = FastAPI()

# ✅ Use ONLY OpenAI Embeddings (No LLM Calls)
Settings.llm = None #No LLM #llm = OpenAI(model="gpt-4", temperature=0.0, api_key=OPENAI_API_KEY) 
Settings.embed_model = OpenAIEmbedding(model="text-embedding-3-small", api_key=OPENAI_API_KEY)

# ✅ Index with Existing or Newly Generated Embeddings
vector_index = None
get_vector_index([])

class QueryRequest(BaseModel):
    query: str

@app.post("/query")
async def query_llama(request: QueryRequest):
    try:
        # ✅ Create Retriever
        retriever = vector_index.as_retriever(similarity_top_k=10)
        response = retriever.retrieve(request.query)

        # ✅ Fix: Iterate over response directly (No `.source_nodes`)
        results = [
            {"id": i, "filename": r.metadata.get("file_name", "Unknown").rpartition("\\")[2], "text": r.text, "score": r.score}
            for i, r in enumerate(response)
        ]
        return {"query": request.query, "results": results}

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


class DocumentsRequest(BaseModel):
    githubRepo: str
    folder: str
    
@app.post("/documents")
async def query_llama(request: DocumentsRequest):
    try:
        response = load_documents_from_github_repo(request.githubRepo, request.folder)
        message = "{} file/s added".format(len(response))
        results = [
            {"id": i, "filename": r}
            for i, r in enumerate(response)
        ]
        return {"githubRepo": request.githubRepo, "message": message, "results": results}

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8001)