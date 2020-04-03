package xyz.gnarbot.gnar.apis.patreon

import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import org.slf4j.LoggerFactory
import xyz.gnarbot.gnar.utils.RequestUtil
import xyz.gnarbot.gnar.utils.extensions.buildUrl
import xyz.gnarbot.gnar.utils.extensions.url
import java.net.URI
import java.net.URLDecoder
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class PatreonAPI(var accessToken: String) {
// (val clientId: String, val clientSecret: String, val refreshToken: String) {
    private val scheduler = Executors.newSingleThreadScheduledExecutor()
    //var accessToken: String = ""

    fun fetchPledges(campaignId: String = "754103") = fetchPledgesOfCampaign0(campaignId)

//    fun refreshAccessToken() {
//        val url = baseUrl.newBuilder().apply {
//            addPathSegment("token")
//            addQueryParameter("grant_type", "refresh_token")
//            addQueryParameter("refresh_token", refreshToken)
//            addQueryParameter("client_id", clientId)
//            addQueryParameter("client_secret", clientSecret)
//        }.build()
//        println(url)
//
//        request {
//            url(url)
//            post(RequestBody.create(null, ByteArray(0)))
//        }.thenAccept {
//            val token = it.getString("access_token")
//            val expiresEpochSeconds = it.getLong("expires_in")
//
//            accessToken = token
//            scheduler.schedule(::refreshAccessToken, expiresEpochSeconds, TimeUnit.SECONDS)
//            log.info("Successfully refreshed Patreon access token.")
//        }.exceptionally {
//            log.error("Unable to refresh Patreon access token!", it)
//            return@exceptionally null
//        }
//    }

    private fun fetchPledgesOfCampaign0(campaignId: String, offset: String? = null): CompletableFuture<List<PatreonUser>> {
        val users = mutableListOf<PatreonUser>()

        return fetchPageOfPledge(campaignId, offset)
            .thenCompose {
                users.addAll(it.pledges)

                if (it.hasMore) {
                    fetchPledgesOfCampaign0(campaignId, it.offset)
                } else {
                    CompletableFuture.completedFuture(emptyList())
                }
            }
            .thenAccept { users.addAll(it) }
            .thenApply { users }
    }

    private fun fetchPageOfPledge(campaignId: String, offset: String?): CompletableFuture<ResultPage> {
        return get {
            addPathSegments("api/campaigns/$campaignId/pledges")
            setQueryParameter("include", "pledge,patron")
            offset?.let { setQueryParameter("page[cursor]", it) }
        }.thenApply {
            val pledges = it.getJSONArray("data")
            val nextPage = getNextPage(it)
            val users = mutableListOf<PatreonUser>()

            for ((index, obj) in it.getJSONArray("included").withIndex()) {
                obj as JSONObject

                if (obj.getString("type") == "user") {
                    val pledge = pledges.getJSONObject(index)
                    users.add(PatreonUser.fromJsonObject(obj, pledge))
                }
            }

            ResultPage(users, nextPage != null, nextPage)
        }
    }

    private fun getNextPage(json: JSONObject): String? {
        return json.getJSONObject("links")
            .takeIf { it.has("next") }
            ?.let { parseQueryString(it.getString("next"))["page[cursor]"] }
    }

    private fun parseQueryString(url: String): Map<String, String> {
        return URI(url).query
            .split('&')
            .map { it.split("=") }
            .associateBy({ decode(it[0]) }, { decode(it[1]) })
    }

    private fun decode(s: String) = URLDecoder.decode(s, Charsets.UTF_8)

    private fun get(urlOpts: HttpUrl.Builder.() -> Unit): CompletableFuture<JSONObject> {
        if (accessToken.isEmpty()) {
            return CompletableFuture.failedFuture(IllegalStateException("Access token is empty!"))
        }

        val url = baseUrl.newBuilder().apply(urlOpts).build()
        return request { url(url) }
    }

    private fun request(requestOpts: Request.Builder.() -> Unit): CompletableFuture<JSONObject> {
        return RequestUtil.jsonObject({
            apply(requestOpts)
            header("Authorization", "Bearer $accessToken")
        }, true)
    }

    companion object {
        private val log = LoggerFactory.getLogger(PatreonAPI::class.java)
        private val baseUrl = HttpUrl.get("https://www.patreon.com/api/oauth2")
    }
}
