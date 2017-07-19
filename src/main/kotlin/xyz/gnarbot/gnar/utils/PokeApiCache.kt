//package xyz.gnarbot.gnar.utils
//
//import me.sargunvohra.lib.pokekotlin.model.Pokemon
//import xyz.gnarbot.gnar.Bot
//
//class PokeApiCache {
//    private val pokemonMap: MutableMap<Int, Pokemon> = mutableMapOf()
//    private val pokemonNameIdMap: MutableMap<String, Int> = mutableMapOf()
//
//    init {
//        val limit = 100
//        var offset = 0
//        var count = 1
//
//        while (offset < count) {
//            val resourceList = Bot.getPokeAPI().getPokemonList(offset, limit)
//            count = resourceList.count
//
//            resourceList.results.forEachIndexed { index, (name) ->
//                pokemonNameIdMap.put(name, index + offset + 1)
//            }
//
//            offset += limit
//        }
//
//        Bot.LOG.info("Initialized PokeAPI cache.")
//    }
//
//    fun getPokemon(id: Int): Pokemon? {
//        return if (id in 1..721) {
//            pokemonMap.getOrPut(id) {
//                Bot.getPokeAPI().getPokemon(id)
//            }
//        } else {
//            null
//        }
//
//    }
//
//    fun getPokemon(name: String): Pokemon? {
//        return pokemonNameIdMap[name]?.let(this::getPokemon)
//    }
//}
