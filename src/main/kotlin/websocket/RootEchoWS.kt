package websocket

import io.javalin.embeddedserver.jetty.websocket.WebSocketConfig
import io.javalin.embeddedserver.jetty.websocket.WebSocketHandler

class RootEchoWS : WebSocketConfig {
	
	override fun configure(ws: WebSocketHandler) {
		
		ws.onConnect {
			println("${it.remoteAddress} connected")
			if (!it.isSecure) {
				println("${it.remoteAddress} unsecure !! closing ws")
				it.close()
				it.disconnect()
			} else {
				println("${it.remoteAddress} SECURE, keeping connection wss")
			}
		}
		
		ws.onClose { sess, _, _ ->
			println("${sess.remoteAddress} closed")
		}
		
		ws.onMessage { sess, msg ->
			sess.send(msg)
		}
	
	}
	
}