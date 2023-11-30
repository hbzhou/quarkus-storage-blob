package com.itsz

import com.azure.core.util.BinaryData
import com.azure.storage.blob.BlobContainerAsyncClient
import com.azure.storage.blob.BlobServiceAsyncClient
import com.azure.storage.blob.BlobServiceClient
import com.azure.storage.blob.BlobServiceClientBuilder
import io.quarkus.runtime.annotations.ConfigItem
import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.ws.rs.GET
import jakarta.ws.rs.POST
import jakarta.ws.rs.Path
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.Response
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.jboss.logging.Logger
import reactor.core.publisher.Mono
import java.io.ByteArrayInputStream
import java.time.LocalDateTime

private const val STORAGE_BLOB_CONTAINER_NAME = "orderitemreserver"
private const val STORAGE_BLOB_NAME = "quarkus-azure-storage-blob.txt"


@ApplicationScoped
@Path("/api/storageBlobAsync")
class StorageBlobAsyncResource(val blobServiceAsyncClient: BlobServiceAsyncClient) {
    private val logger = Logger.getLogger(StorageBlobAsyncResource::class.java)

    @GET
    fun downloadBlobStorage(): Uni<Response> {
        logger.info("downloading storage blob async")
        val bytes = blobServiceAsyncClient.createBlobContainerIfNotExists(STORAGE_BLOB_CONTAINER_NAME)
                .flatMap { it.getBlobAsyncClient(STORAGE_BLOB_NAME).downloadContent() }
                .map { it.toBytes() }
        return Uni.createFrom().completionStage { bytes.toFuture() }.map { Response.ok(it).build() }
    }

    @POST
    fun uploadBlobStorage(): Uni<Response> {
        logger.info("uploading storage blob async")
        val blockBlobItem  = blobServiceAsyncClient.createBlobContainerIfNotExists(STORAGE_BLOB_CONTAINER_NAME)
                .map { it.getBlobAsyncClient(STORAGE_BLOB_NAME) }
                .flatMap { it.upload(BinaryData.fromString("Hello Quarkus storage blob uploaded at ${LocalDateTime.now()}"), true) }
        return Uni.createFrom().completionStage { blockBlobItem.toFuture() }.map { Response.status(Response.Status.CREATED).build() }
    }

}