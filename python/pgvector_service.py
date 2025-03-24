import psycopg2
import numpy as np
from datetime import datetime
from llama_index.core import Document
from llama_index.embeddings.openai import OpenAIEmbedding
from llama_index.vector_stores.postgres import PGVectorStore

# ✅ PGVector Configuration
DB_NAME = "ai_db"
DB_USER = "ai_user"
DB_PASSWORD = "ai_password"
DB_HOST = "localhost"
DB_PORT = "5432"

class PGVectorService:
    def __init__(self):
        self.conn = psycopg2.connect(
            dbname=DB_NAME, user=DB_USER, password=DB_PASSWORD, host=DB_HOST, port=DB_PORT
        )
        self.cursor = self.conn.cursor()

    def get_existing_embeddings(self):
        # Fetch all existing embeddings from the database.
        self.cursor.execute("SELECT file_name, embedding FROM document_embeddings")
        records = self.cursor.fetchall()
        return {r[0]: np.array(r[1]) for r in records}

    def store_embeddings_batch(self, file_names, embeddings):
        # Insert or update multiple document embeddings in one batch.
        data = [(file_names[i], embeddings[i], datetime.now()) for i in range(len(file_names))]

        self.cursor.executemany(
            """
            INSERT INTO document_embeddings (file_name, embedding, last_modified) 
            VALUES (%s, %s, %s)
            ON CONFLICT (file_name) 
            DO UPDATE SET embedding = EXCLUDED.embedding, last_modified = EXCLUDED.last_modified;
            """,
            data
        )
        self.conn.commit()

    def get_vector_store(self):
        # Fetch all existing embeddings from the database.
        vector_store = PGVectorStore.from_params(
            database=DB_NAME, user=DB_USER, password=DB_PASSWORD, host=DB_HOST, port=DB_PORT
        )
        return vector_store

    def close(self):
        self.cursor.close()
        self.conn.close()
