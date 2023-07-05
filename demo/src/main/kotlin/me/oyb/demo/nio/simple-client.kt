package me.oyb.demo.nio

import me.oyb.demo.common.getLogger
import org.slf4j.Logger
import org.springframework.util.StopWatch
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread

class SimpleClient {

    companion object {
        private val log: Logger = getLogger<SimpleClient>()
    }

    fun run(serverPort: Int = 8080, serverHost: String = "localhost") {
        val socketChannel = SocketChannel.open()

        socketChannel.connect(InetSocketAddress(serverHost, serverPort))
        socketChannel.configureBlocking(true)
        log.info("connected to {}", socketChannel.remoteAddress)

        val message = "hello, world!".toByteArray()
        socketChannel.write(ByteBuffer.wrap(message))
        socketChannel.shutdownOutput()

        log.info("write \"{}\" to {}", message.decodeToString(), socketChannel.remoteAddress)

        val response = socketChannel.readAllBytes()
        log.info("read \"{}\" from {}", response.decodeToString(), socketChannel.remoteAddress)
    }
}

fun test(stopWatch: StopWatch, serverPort: Int): Int {
    stopWatch.start("${serverPort}")

    val nThreads = 10
    val countDownLatch = CountDownLatch(nThreads)

    var failed = 0

    repeat(nThreads) {
        thread {
            try {
                repeat(100) {
                    SimpleClient().run(serverPort)
                }
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

    val a = test(stopWatch, 8080)
    val b = test(stopWatch, 8081)

    println(stopWatch.prettyPrint())
    println("failed: $a, $b")
}