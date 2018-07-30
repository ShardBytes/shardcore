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
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class ShardBotHandler(private val messenger: Messenger) : Handler {
	
	fun process(event: Event, text: String): String = when(text) {
		
		"echo", "ping", "Echo", "Ping" -> "ok"
		
		"hi", "hello", "Hi", "Hello" -> "Hello ! This is a bot of a small group of students called ShardBytes. We like to use" +
				"Java and Kotlin to create useful software (but sometimes games). You can visit us at https://shardbytes.com"
		
		"time", "Time", "Date", "date" -> LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
		
		"help", "Help", "info", "Info", "menu", "Menu" -> "Here are the commands that I understand right now :\n" +
				"hello/hi - says hello and introduces myself\n" +
				"date - tells you the date (on the server)\n" +
				"help/info/menu - displays this message\n" +
				"echo - simple command that returns \"ok\" if the bot is working\n"
		
		else -> "Sorry, but I couldn't understand. Try to type \"help\" and I'll tell you what I can do."
	}
	
	override fun handle(ctx: Context) {
		
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