package rest

import io.javalin.Context
import io.javalin.Handler

class RandomRest : Handler {

	data class DataRandom(val value: Double)

	override fun handle(ctx: Context) {
		ctx.json(DataRandom(getNumber()))
	}

	fun getNumber() = Math.random()

}