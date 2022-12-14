package ar.edu.utn.frba.mobile.a2019c1.superheroes.api

import org.springframework.stereotype.Service
import kotlin.streams.toList

@Service
class MarvelService(private val superheroesRepository: List<Superhero>, private val randomService: RandomService) {

	fun getCards(quantity: Int): List<Card> {
		val selectedSuperheroes = mutableListOf<Superhero>()
		for (x in 1..quantity) {
			val i = randomService.generate(SuperheroesRepository.getTotal())
			selectedSuperheroes.add(superheroesRepository[i])
		}
		return selectedSuperheroes.stream().map { it.asCard() }.toList()
	}

	private fun Superhero.asCard() = Card(
			id = id,
			name = name,
			description = description,
			thumbnail = thumbnail,
			power = randomService.generate(100) + 1)

}

