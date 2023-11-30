package com.itsz

import com.azure.core.util.BinaryData
import com.azure.storage.blob.BlobServiceAsyncClient
import com.azure.storage.blob.BlobServiceClient
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.core.Response
import org.jboss.logging.Logger
import java.io.ByteArrayInputStream
import java.time.LocalDateTime


private const val STORAGE_BLOB_CONTAINER_NAME = "orderitemreserver"
private const val STORAGE_BLOB_NAME = "quarkus-azure-storage-blob.txt"


@ApplicationScoped
@Path("/api/storageBlob")
class StorageBlobResource(val blobServiceClient: BlobServiceClient) {

    private val logger = Logger.getLogger(StorageBlobResource::class.java)

    @GET
    fun downloadBlobStorage(): Response {
        logger.info("downloading the blob storage from azure")
        val data = blobServiceClient.createBlobContainerIfNotExists(STORAGE_BLOB_CONTAINER_NAME)
            .getBlobClient(STORAGE_BLOB_NAME).downloadContent().toBytes()
        val byteArrayInputStream = ByteArrayInputStream(data)
        return Response.ok(byteArrayInputStream).header("content-disposition", "attachment; filename = $STORAGE_BLOB_NAME").build()
    }

    @GET
    @Path("/list")
    fun listBlobStorage(): List<String> {
        return blobServiceClient.createBlobContainerIfNotExists(STORAGE_BLOB_CONTAINER_NAME).listBlobs().map { it.name }
    }

    @POST
    fun uploadBlobStorage(): Response {
        logger.info("uploading the storage blob from azure ")
        blobServiceClient.createBlobContainerIfNotExists(STORAGE_BLOB_CONTAINER_NAME).getBlobClient(STORAGE_BLOB_NAME)
            .upload(
                BinaryData.fromString("Hello Quarkus storage blob uploaded at ${LocalDateTime.now()}"), true
            )
        return Response.status(Response.Status.CREATED).build()
    }

}