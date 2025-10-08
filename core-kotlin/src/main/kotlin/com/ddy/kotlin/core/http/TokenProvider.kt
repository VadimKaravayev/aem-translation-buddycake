package com.ddy.kotlin.core.http

import com.adobe.granite.rest.Constants.CT_JSON
import com.adobe.granite.rest.Constants.CT_WWW_FORM_URLENCODED
import com.ddy.kotlin.core.utils.JsonUtils.mapper
import org.apache.http.HttpHeaders
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.message.BasicNameValuePair
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.concurrent.locks.LockSupport
import kotlin.concurrent.Volatile
import kotlin.io.encoding.Base64
import kotlin.math.pow
import kotlin.random.Random
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

private const val GRANT_TYPE = "grant_type"
private const val CLIENT_CREDENTIALS = "client_credentials"
private const val TIME_OFFSET_SECONDS = 60L
const val ACCESS_TOKEN_PROP = "access_token"
const val EXPIRES_IN_PROP = "expires_in"

class TokenProvider(
    private val clientId: String,
    private val clientSecret: String,
    private val tokenUrl: String,
    private val httpClient: CloseableHttpClient,
    private val maxRetries: Int = 3,
    private val baseDelayMs: Long = 300L,
    private val maxJitterMs: Long = 200L
) {

    @Volatile
    private var accessToken: String? = null

    @OptIn(ExperimentalTime::class)
    private var expiryTime: Instant = Instant.DISTANT_PAST

    private val lock = Any()


    @Throws(IOException::class)
    fun getToken(): String {
        if (isExpired()) synchronized(lock) {
            if (isExpired()) {
                retrying { requestAccessToken() }
            }
        }
        return accessToken
            ?: throw IOException( "Access token is still null after refresh")
    }

    @Throws(IOException::class)
    private inline fun retrying(block: () -> Unit) {
        var lastError: IOException? = null
        repeat(maxRetries) { attempt ->
            try {
                block()
                return
            } catch (e: IOException) {
                lastError = e
                if (attempt < maxRetries - 1) {
                    LockSupport.parkNanos(calculateBackoff(attempt).inWholeNanoseconds)
                }
            }
        }
        throw IOException("Failed to obtain token after $maxRetries attempts", lastError)
    }

    private fun calculateBackoff(attempt: Int): Duration =
        (baseDelayMs * 2.0.pow(attempt))
            .toLong()
            .plus(Random.nextLong(0, maxJitterMs))
            .milliseconds

    @OptIn(ExperimentalTime::class)
    @Throws(IOException::class)
    private fun requestAccessToken() {
        val post = HttpPost(tokenUrl).apply {
            setHeader(HttpHeaders.CONTENT_TYPE, CT_WWW_FORM_URLENCODED)
            setHeader(HttpHeaders.ACCEPT, CT_JSON)
            setHeader(HttpHeaders.AUTHORIZATION, basicAuthHeader())
            entity = UrlEncodedFormEntity(
                listOf(BasicNameValuePair(GRANT_TYPE, CLIENT_CREDENTIALS)),
                StandardCharsets.UTF_8)
        }

        httpClient.execute(post).use { response ->
            val statusCode = response.statusLine.statusCode

            if (statusCode !in 200..299) {
                throw IOException("Failed to fetch token, status $statusCode")
            }

            val entity = response.entity ?: throw IOException("Empty response entity when fetching token")
            entity.content.use { content ->
                val (token, expiresInSeconds ) = parseTokenResponse(content)
                accessToken = token
                expiryTime = Clock.System.now() + maxOf(0,expiresInSeconds - TIME_OFFSET_SECONDS).seconds
            }
        }
    }

    private fun parseTokenResponse(content: InputStream): Pair<String, Long> {
        val json = mapper.readTree(content)
        val token = json[ACCESS_TOKEN_PROP]?.asText()
            ?: throw IOException("Missing $ACCESS_TOKEN_PROP in token response")
        val expiresIn = json[EXPIRES_IN_PROP]?.asLong() ?: 0L
        return token to expiresIn
    }

    @OptIn(ExperimentalTime::class)
    private fun isExpired(): Boolean =
        accessToken.isNullOrEmpty() || Clock.System.now() > expiryTime

    private fun basicAuthHeader(): String =
        "Basic " + Base64.encode("$clientId:$clientSecret".encodeToByteArray())
}