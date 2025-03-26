import psycopg2
import numpy as np
from datetime import datetime
from llama_index.core import Document
from llama_index.embeddings.openai import OpenAIEmbedding
from llama_index.vector_stores.postgres import PGVectorStore

class PGVectorService:
    def __init__(self, DB_NAME, DB_USER, DB_PASSWORD, DB_HOST, DB_PORT):
        self.conn = psycopg2.connect(
            dbname=DB_NAME, user=DB_USER, password=DB_PASSWORD, host=DB_HOST, port=DB_PORT
        )
        self.cursor = self.conn.cursor()

    def get_existing_embeddings(self):
        # Fetch all existing embeddings from the database.
        self.cursor.execute("SELECT file_name, embedding FROM document_embeddings")
        records = self.cursor.fetchall()
        return {r[0].partition("__chunk_")[0]: np.array(r[1]) for r in records}

    def store_embeddings_batch(self, file_names, embeddings, summaries):
        # Insert or update multiple document embeddings in one batch
        data = [
            (file_names[i], embeddings[i], summaries[i], datetime.now())
            for i in range(len(file_names))
        ]

        # Insert embeddings into the database (Postgres)
        self.cursor.executemany(
            """
            INSERT INTO document_embeddings (file_name, embedding, summary, last_modified) 
            VALUES (%s, %s, %s, %s)
            ON CONFLICT (file_name) 
            DO UPDATE SET embedding = EXCLUDED.embedding, summary = EXCLUDED.summary, last_modified = EXCLUDED.last_modified;
            """,
            data
        )
        self.conn.commit()
        
    def close(self):
        self.cursor.close()
        self.conn.close()
