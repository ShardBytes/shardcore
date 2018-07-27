package shardbot

import com.github.messenger4j.Messenger
import com.github.messenger4j.exception.MessengerApiException
import com.github.messenger4j.exception.MessengerIOException
import com.github.messenger4j.send.MessagePayload
import com.github.messenger4j.send.MessagingType
import com.github.messenger4j.send.message.TextMessage
import com.github.messenger4j.webhook.Event
import io.javalin.Context
import io.javalin.Handler
import java.util.*

class ShardBotHandler(private val messenger: Messenger) : Handler {
	
	fun process(event: Event, text: String): String = when(text) {
		
		"echo" -> "ok"
		
		"myId" -> event.senderId()
		"botId" -> event.recipientId()
		
		else -> "Unknown command my dude"
	}
	
	override fun handle(ctx: Context) {
		println("[ShardBot] WEBHOOK TRIGGERED -> echo")
		
		val payload = ctx.body()
		
		messenger.onReceiveEvents(payload, Optional.empty()) { event ->
			val senderId = event.senderId()
			if (event.isTextMessageEvent) {
				val text = event.asTextMessageEvent().text()
				
				val response = process(event, text)
				
				val textMessage = TextMessage.create(response)
				val messagePayload = MessagePayload.create(senderId,
						MessagingType.RESPONSE, textMessage)
				
				try {
					messenger.send(messagePayload)
				} catch (e: MessengerApiException) {
					println(e.message)
				} catch (e: MessengerIOException) {
					println(e.message)
				}
				
			}
		}
		
	}
	
}