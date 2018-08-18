package discord

import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.events.message.MessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class Shardy(private val jda: JDA) : ListenerAdapter() {
	
	init {
		jda.addEventListener(this)
	}
	
	fun shutdown() = jda.shutdownNow()
	
	override fun onMessageReceived(e: MessageReceivedEvent) {
		
		// ignore bots!!
		if (e.author.isBot) return
		
		val text = e.message.contentRaw
		
		when (text) {
			
			"ping" -> e.channel.sendMessage("Pong from ShardCore !").complete()
			
		}
		
	}

}