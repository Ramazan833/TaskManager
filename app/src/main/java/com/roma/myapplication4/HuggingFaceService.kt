package com.roma.myapplication4

import com.roma.myapplication4.BuildConfig
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

// We are removing all response data classes to parse the JSON manually.

object CohereService {
    private val json = Json { ignoreUnknownKeys = true }

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(json)
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 60000
        }
    }

    private const val API_URL = "https://api.cohere.ai/v1/chat"

    suspend fun getCompletion(prompt: String): String {
        return try {
            val apiKey = BuildConfig.COHERE_API_KEY
            if (apiKey.isEmpty()) {
                return "Ошибка: API-ключ Cohere не найден. Проверьте ваш файл local.properties."
            }

            val requestBody = """
                {
                    "message": "$prompt"
                }
            """.trimIndent()

            val response: HttpResponse = client.post(API_URL) {
                contentType(ContentType.Application.Json)
                header("Authorization", "Bearer $apiKey")
                setBody(requestBody)
            }
            
            val responseBody = response.bodyAsText()

            if (response.status != HttpStatusCode.OK) {
                 return "Ошибка сервера: ${response.status.value}. Ответ: $responseBody"
            }

            // FINAL, BULLETPROOF FIX: Manually parse the JSON response to bypass all serializer issues.
            val jsonElement = json.parseToJsonElement(responseBody)
            val responseText = jsonElement.jsonObject["text"]?.jsonPrimitive?.content

            responseText?.trim() ?: "Получен пустой или некорректный ответ от модели."
            
        } catch (e: Exception) {
            "Ошибка: ${e.javaClass.simpleName} - ${e.localizedMessage}"
        }
    }
}