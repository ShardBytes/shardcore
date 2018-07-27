package shardbot

import com.github.messenger4j.Messenger
import com.github.messenger4j.exception.MessengerApiException
import com.github.messenger4j.exception.MessengerIOException
import com.github.messenger4j.send.MessagePayload
import com.github.messenger4j.send.MessagingType
import com.github.messenger4j.send.message.TextMessage
import io.javalin.Context
import io.javalin.Handler
import java.util.*

class ShardBotHandler(private val messenger: Messenger) : Handler {
	
	override fun handle(ctx: Context) {
		println("[ShardBot] WEBHOOK TRIGGERED -> echo")
		
		val payload = ctx.body()
		
		messenger.onReceiveEvents(payload, Optional.empty()) { event ->
			val senderId = event.senderId()
			if (event.isTextMessageEvent) {
				val text = event.asTextMessageEvent().text()
				
				val textMessage = TextMessage.create(text)
				val messagePayload = MessagePayload.create(senderId,
						MessagingType.RESPONSE, textMessage)
				
				try {
					messenger.send(messagePayload)
				} catch (e: MessengerApiException) {
					// Oops, something went wrong
				} catch (e: MessengerIOException) {
				}
				
			}
		}
		
	}
	
}