package xyz.gnarbot.gnar.apis.patreon

import okhttp3.HttpUrl
import org.json.JSONObject
import xyz.gnarbot.gnar.utils.RequestUtil
import java.net.URI
import java.net.URLDecoder
import java.util.concurrent.CompletableFuture


class PatreonAPI(var accessToken: String) {
    fun printCampaignIds() {
        request {
            addPathSegments("current_user/campaigns")
        }.thenAccept { println(it) }

        fetchPledgesOfCampaign("750822").thenAccept { println(it.size) }
    }

    fun fetchPledgesOfCampaign(campaignId: String) = fetchPledgesOfCampaign0(campaignId)

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
        return request {
            addPathSegments("campaigns/$campaignId/pledges")
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

    fun getNextPage(json: JSONObject): String? {
        val links = json.getJSONObject("links")

        if (!links.has("next")) {
            return null
        }

        return parseQueryString(links.getString("next"))["page[cursor]"]
    }

    fun parseQueryString(url: String): Map<String, String> {
        val pairs = URI(url).query.split("&")

        return pairs
            .map { it.split("=") }
            .map { Pair(decode(it[0]), decode(it[1])) }
            .toMap()
    }

    private fun decode(s: String) = URLDecoder.decode(s, Charsets.UTF_8)

    private fun request(urlOpts: HttpUrl.Builder.() -> Unit): CompletableFuture<JSONObject> {
        val url = baseUrl.newBuilder().apply(urlOpts).build()
        return RequestUtil.jsonObject {
            url(url)
            header("Authorization", "Bearer $accessToken")
        }
    }

    companion object {
        private val baseUrl = HttpUrl.get("https://www.patreon.com/api/oauth2/api")
    }
}
