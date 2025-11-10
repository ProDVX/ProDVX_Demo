package com.prodvx.prodvx_demo.api

import com.prodvx.prodvx_demo.BuildConfig
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod

private const val PROTOCOL = "http"
private const val HOST = "localhost"
private const val PORT = "3535"
private const val VERSION = "v1"
private const val API_URL = "${PROTOCOL}://${HOST}:${PORT}/${VERSION}"

var TOKEN: String? = null

fun initApi(){
    if(BuildConfig.IS_DEVELOPMENT) {
        TOKEN = BuildConfig.API_TOKEN
    }
}

suspend fun sendRequest(method: HttpMethod, endpoint: String, params: Map<String, String>? = null): HttpResponse? {
    try {
        val url = buildString {
            append("${API_URL}$endpoint")
            params?.let {
                append("?")
                append(it.entries.joinToString("&") {entry -> "${entry.key}=${entry.value}" })
            }
        }
        val response: HttpResponse = httpClient.request(url) {
            this.method = method
            header(HttpHeaders.Authorization, "Bearer ${TOKEN!!}")
        }
        return response

    } catch (e: Exception){
        "Error: ${e.message}"
        return null
    }
}

suspend fun sleepDevice(): HttpResponse? {
    return sendRequest(HttpMethod.Get, "/sleepDevice")
}

fun updateToken(newToken: String) {
    println("Set new token: $newToken")
    TOKEN = newToken
}