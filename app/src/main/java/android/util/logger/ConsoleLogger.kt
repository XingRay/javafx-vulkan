package android.util.logger

class ConsoleLogger : Logger {
    override fun log(message: String) {
        println(message)
    }
}