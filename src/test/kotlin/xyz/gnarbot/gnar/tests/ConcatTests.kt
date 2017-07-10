package xyz.gnarbot.gnar.tests

import me.sargunvohra.lib.pokekotlin.client.PokeApiClient

fun main(args: Array<String>) {
    val pokeApi = PokeApiClient()
    val generations = pokeApi.getGenerationList(0, 6)
    generations.results.forEach {
        println("Generation ${it.id}")
    }
}