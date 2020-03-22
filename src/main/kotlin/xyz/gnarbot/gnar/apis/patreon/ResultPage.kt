package xyz.gnarbot.gnar.apis.patreon

data class ResultPage(
    val pledges: List<PatreonUser>,
    val hasMore: Boolean,
    val offset: String?
)
