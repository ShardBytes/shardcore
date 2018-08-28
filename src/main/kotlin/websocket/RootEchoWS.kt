package websocket


import io.javalin.websocket.WsHandler
import java.util.function.Consumer

class RootEchoWS : Consumer<WsHandler> {
	
	override fun accept(ws: WsHandler) {
		
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