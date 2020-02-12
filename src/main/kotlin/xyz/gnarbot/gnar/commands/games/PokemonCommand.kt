//package xyz.gnarbot.gnar.commands.games;
//
//import me.sargunvohra.lib.pokekotlin.client.ErrorResponse
//import me.sargunvohra.lib.pokekotlin.model.Pokemon
//import xyz.gnarbot.gnar.Bot
//import xyz.gnarbot.gnar.commands.Command
//import xyz.gnarbot.gnar.commands.template.CommandTemplate
//import xyz.gnarbot.gnar.commands.template.Executor
//import xyz.gnarbot.gnar.utils.Context
//
//@Command(
//        id = 78,
//        aliases = arrayOf("pokemon"),
//        description = "Pokemon fun."
//)
//class PokemonCommand : CommandTemplate() {
//    @Executor(0, description = "Show pokemon information.")
//    fun id(context: Context, id: Int) {
//        try {
//            sendPokemonToContext(context, Bot.getPokeCache().getPokemon(id))
//        } catch (e: ErrorResponse) {
//            context.send().exception(e).queue()
//        }
//    }
//
//    @Executor(1, description = "Show pokemon information.")
//    fun name(context: Context, name: String) {
//        try {
//            sendPokemonToContext(context, Bot.getPokeCache().getPokemon(name.toLowerCase()))
//        } catch (e: ErrorResponse) {
//            context.send().exception(e).queue()
//        }
//    }
//
//    fun sendPokemonToContext(context: Context, pokemon: Pokemon?) {
//        if (pokemon == null) {
//            context.send().error("That's not a valid pokemon.").queue()
//            return
//        }
//
//        context.send().embed(pokemon.name.capitalize()) {
//            field("ID", true) { "`${pokemon.id}`" }
//            field("Types", true) {
//                pokemon.types.joinToString("`, `", "`", "`") {
//                    it.type.name.toUpperCase()
//                }
//            }
//
//            field("Height", true) { "`${pokemon.height}`" }
//            field("Weight", true) { "`${pokemon.weight}`" }
//
//            pokemon.stats.forEach {
//                field(it.stat.name.capitalize(), true) {
//                    "`${it.baseStat}`"
//                }
//            }
//
//            thumbnail {
//                pokemon.sprites.frontDefault
//            }
//        }.action().queue()
//    }
//}
