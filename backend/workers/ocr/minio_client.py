from minio import Minio

class MinioWrapper:
    def __init__(self, endpoint, access, secret):
        self.client = Minio(
            endpoint.replace("http://", "").replace("https://", ""),
            access,
            secret,
            secure=endpoint.startswith("https")
        )

    def get_pdf_bytes(self, bucket, key) -> bytes:
        resp = self.client.get_object(bucket, key)
        data = resp.read()
        resp.close()
        resp.release_conn()
        return data