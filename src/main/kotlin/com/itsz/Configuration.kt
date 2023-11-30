package com.itsz

import com.azure.storage.blob.BlobServiceAsyncClient
import com.azure.storage.blob.BlobServiceClientBuilder
import jakarta.enterprise.context.Dependent
import jakarta.enterprise.inject.Produces
import org.eclipse.microprofile.config.inject.ConfigProperty

@Dependent
class Configuration {

    @ConfigProperty(name = "quarkus.azure.storage.blob.connection-string")
    lateinit var connectionString: String

    @Produces
    fun getBlobServiceAsyncClient(): BlobServiceAsyncClient =
        BlobServiceClientBuilder().connectionString(connectionString).buildAsyncClient()

}