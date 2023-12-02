
import me.oyb.demo.common.getLogger
import me.oyb.demo.nio.readLine
import org.slf4j.Logger
import org.springframework.util.StopWatch
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

class SimpleClient {

    companion object {
        private val log: Logger = getLogger<SimpleClient>()
    }

    private lateinit var socketChannel: SocketChannel

    fun run(serverPort: Int = 8080, serverHost: String = "localhost") {
        socketChannel = SocketChannel.open()

        socketChannel.connect(InetSocketAddress(serverHost, serverPort))
        socketChannel.configureBlocking(true)
        log.info("connected to {}", socketChannel.remoteAddress)
    }

    fun auth() {
        val message = "*2\r\n\$4\r\nauth\r\n\$7\r\nredispw\r\n".toByteArray()
        socketChannel.write(ByteBuffer.wrap(message))

        val response = socketChannel.readLine()
        log.info("read \"{}\" from {}", response.decodeToString(), socketChannel.remoteAddress)
    }

    fun test() {
        val message = "*2\r\n\$3\r\nget\r\n\$3\r\nfoo\r\n".toByteArray()
        socketChannel.write(ByteBuffer.wrap(message))
//        socketChannel.shutdownOutput()

//        log.info("write \"{}\" to {}", message.decodeToString(), socketChannel.remoteAddress)

        val response = socketChannel.readLine()
        log.info("read \"{}\" from {}", response.decodeToString(), socketChannel.remoteAddress)
    }

    fun close() {
        socketChannel.close()
    }
}

fun test(stopWatch: StopWatch, serverPort: Int): Int {
    stopWatch.start("$serverPort")

    val nThreads = 50
    val countDownLatch = CountDownLatch(nThreads)

    var failed = 0

    repeat(nThreads) {
        thread {
            try {
                val simpleClient = SimpleClient()
                simpleClient.run(serverPort)
                simpleClient.auth()

                repeat(100) {
                    simpleClient.test()
                }

                simpleClient.close()
            } catch (e: Exception) {
                e.printStackTrace()
                failed++
            } finally {
                countDownLatch.countDown()
            }
        }
    }

    countDownLatch.await()

    stopWatch.stop()

    return failed
}

fun main() {
    val stopWatch = StopWatch("normal client")

//    val a = test(stopWatch, 8080)
    val b = test(stopWatch, 55000)

    println(stopWatch.prettyPrint())
    println(stopWatch.totalTimeSeconds)
    println(10_000 / stopWatch.totalTimeSeconds)
//    println("failed: $a, $b")
}