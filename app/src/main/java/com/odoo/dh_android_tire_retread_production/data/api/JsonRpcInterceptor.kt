package com.odoo.dh_android_tire_retread_production.data.api

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class JsonRpcInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        // --- Wrap POST request body in JSON-RPC envelope ---
        val outRequest = if (request.method == "POST" && request.body != null) {
            val buf = okio.Buffer()
            request.body!!.writeTo(buf)
            val params = buf.readUtf8()
            val rpc = """{"jsonrpc":"2.0","method":"call","params":$params}"""
            val newBody = rpc.toRequestBody("application/json; charset=utf-8".toMediaType())
            request.newBuilder().method("POST", newBody).build()
        } else request

        val response = chain.proceed(outRequest)

        // --- Unwrap JSON-RPC response envelope ---
        val body = response.body ?: return response
        val raw = body.string()
        val payload = try {
            val json = JSONObject(raw)
            when {
                json.has("result") -> json.getJSONObject("result").toString()
                json.has("error") -> {
                    val err = json.getJSONObject("error")
                    val msg = err.optJSONObject("data")
                        ?.optString("message") ?: err.optString("message")
                    JSONObject().apply {
                        put("success", false)
                        put("message", msg)
                        put("error_code", "server_error")
                        put("data", JSONObject())
                    }.toString()
                }
                else -> raw  // GET endpoints return flat JSON — pass through
            }
        } catch (e: Exception) { raw }

        return response.newBuilder()
            .body(payload.toResponseBody("application/json".toMediaTypeOrNull()))
            .build()
    }
}
