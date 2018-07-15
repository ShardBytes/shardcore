package data

import io.javalin.Context
import io.javalin.Handler
import io.javalin.Javalin

data class DataRandom(val value: Double)

class RandomREST(app: Javalin) : Handler {

	override fun handle(ctx: Context) {
		ctx.json(DataRandom(getNumber()))
	}

	fun getNumber() = Math.random()

}