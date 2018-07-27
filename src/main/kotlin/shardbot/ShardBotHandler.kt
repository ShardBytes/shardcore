package shardbot

import com.github.messenger4j.Messenger
import io.javalin.Context
import io.javalin.Handler

class ShardBotHandler(private val messenger: Messenger) : Handler {
	
	override fun handle(ctx: Context) {
		println("ShardBotHandler -> post -> ${ctx.body()}")
	}
	
}