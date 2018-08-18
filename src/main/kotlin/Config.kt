
data class CoreConfig(
		// ports
		val port: Int,
		val sslPort: Int,
		// keystore
		val keystorePath: String,
		val keystorePassword: String,
		// coreMongo
		val mongoHost: String,
		val mongoUserName: String,
		val mongoPassword: String,
		// thyme
		val cacheActive: Boolean,
		val cacheResetKey: String
)

data class MessengerConfig(
		val PAGE_ACCESS_TOKEN: String,
		val APP_SECRET: String,
		val VERIFY_TOKEN: String
)


data class DiscordConfig(
		val token: String
)
