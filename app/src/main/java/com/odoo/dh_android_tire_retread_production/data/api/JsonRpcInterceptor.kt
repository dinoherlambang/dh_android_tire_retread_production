package com.odoo.dh_android_tire_retread_production.data.api

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import org.json.JSONObject

class JsonRpcInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // 1. Wrap POST request body in JSON-RPC envelope
        // Odoo type='json' routes REQUIRE a JSON-RPC 2.0 envelope
        val outRequest = if (request.method == "POST") {
            val mediaType = request.body?.contentType() ?: "application/json".toMediaTypeOrNull()
            
            val params = if (request.body != null) {
                val buf = Buffer()
                request.body!!.writeTo(buf)
                buf.readUtf8()
            } else "{}"
            
            val safeParams = if (params.trim().isEmpty() || params == "null") "{}" else params
            val rpcBody = """{"jsonrpc":"2.0","method":"call","params":$safeParams}"""
            val newBody = rpcBody.toRequestBody(mediaType)
            
            request.newBuilder()
                .header("Content-Type", "application/json")
                .method("POST", newBody)
                .build()
        } else {
            request
        }

        val response = chain.proceed(outRequest)

        // 2. Only unwrap POST responses if they are JSON
        if (request.method != "POST") {
            return response
        }

        val body = response.body ?: return response
        val contentType = body.contentType()
        
        // Only try to parse if it's actually JSON
        if (contentType?.subtype?.contains("json", ignoreCase = true) != true) {
            return response
        }

        val raw = body.string()

        val payload = try {
            val json = JSONObject(raw)
            when {
                json.has("result") -> {
                    val resultObj = json.optJSONObject("result")
                    val resultArr = json.optJSONArray("result")
                    when {
                        resultObj != null -> resultObj.toString()
                        resultArr != null -> resultArr.toString()
                        else -> json.opt("result")?.toString() ?: raw
                    }
                }
                json.has("error") -> {
                    val err = json.getJSONObject("error")
                    val msg = err.optJSONObject("data")?.optString("message") ?: err.optString("message")
                    JSONObject().apply {
                        put("success", false)
                        put("message", msg)
                        put("error_code", "server_error")
                        put("data", JSONObject())
                    }.toString()
                }
                else -> raw
            }
        } catch (e: Exception) {
            raw
        }

        return response.newBuilder()
            .body(payload.toResponseBody(contentType))
            .build()
    }
}
