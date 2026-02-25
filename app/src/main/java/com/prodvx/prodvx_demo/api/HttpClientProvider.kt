package com.prodvx.prodvx_demo.api

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * API: Helper Class for providing the HTTP Client for the required configuration to send requests to the ProDVX API
 */
val httpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json { ignoreUnknownKeys = true }) // Handles unknown JSON fields
    }
}