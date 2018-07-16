package rest

import dbDemo
import io.javalin.Context
import io.javalin.Handler
import org.litote.kmongo.*

class FruitRest : Handler {

	data class Fruit(val id: String, val price: Double, val amount: Long)

	override fun handle(ctx: Context) {

		val fruits = dbDemo.getCollection<Fruit>("fruit").find().toMutableList()
		ctx.json(fruits)

	}
}