package xyz.gnarbot.gnar.utils

import okhttp3.*
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.CompletableFuture

object RequestUtil {
    private val httpClient = OkHttpClient()

    fun get(options: Request.Builder.() -> Unit): PendingRequest {
        val request = Request.Builder()
            .apply(options)
            .build()

        return PendingRequest(request)
    }

    fun jsonObject(options: Request.Builder.() -> Unit): CompletableFuture<JSONObject> {
        return get(options).submit()
            .thenApply { it.body()?.string() ?: throw IllegalStateException("ResponseBody was null!") }
            .thenApply { JSONObject(it) }
    }

    class PendingRequest(private val req: Request) {
        fun submit(): CompletableFuture<Response> {
            val future = CompletableFuture<Response>()

            httpClient.newCall(req).enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    future.completeExceptionally(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    future.complete(response)
                }
            })

            return future
        }

//        suspend fun await(): RequestBody {
//            return submit().await()
//        }
        // Requires: kotlinx.coroutines
    }
}
