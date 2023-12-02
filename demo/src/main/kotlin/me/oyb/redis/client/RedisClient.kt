package me.oyb.redis.client

import me.oyb.demo.common.getLogger
import org.slf4j.Logger
import java.io.ByteArrayOutputStream
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors

class RedisClient {


    companion object {
        private val log: Logger = getLogger<RedisClient>()
    }

    private lateinit var socketChannel: SocketChannel

    fun connect(serverPort: Int = 8080, serverHost: String = "localhost") {
        socketChannel = SocketChannel.open()

        socketChannel.connect(InetSocketAddress(serverHost, serverPort))
        socketChannel.configureBlocking(true)
        log.info("connected to {}", socketChannel.remoteAddress)
    }

    fun auth() {
        val message = "*2\r\n\$4\r\nauth\r\n\$7\r\nredispw\r\n".toByteArray()
        socketChannel.write(ByteBuffer.wrap(message))

        onReceiving()
    }

    fun get() {
        val message = "*2\r\n\$3\r\nget\r\n\$3\r\nfoo\r\n".toByteArray()
        socketChannel.write(ByteBuffer.wrap(message))
        val replaced = message.decodeToString().replace("\r", "\\r").replace("\n", "\\n")
        log.info("write \"{}\" to {}", replaced, socketChannel.remoteAddress)

        onReceiving()
    }

    private fun onReceiving() {
        val response = read()
        val replaced = response.decodeToString().replace("\n", "\\n").replace("\r", "\\r")
        log.info("read \"{}\" from {}", replaced, socketChannel.remoteAddress)
    }

    fun close() {
        socketChannel.close()
    }

    private fun read(): ByteArray {
        val inputStream = socketChannel.socket().getInputStream()
        val allBytes = ByteArrayOutputStream()

        val flag = inputStream.read()
        allBytes.write(flag)

        if (flag.toChar() == '+') {
            var prev = flag
            var curr = -1

            while (true) {
                curr = inputStream.read()
                allBytes.write(curr)

                if (prev == '\r'.code && curr == '\n'.code) {
                    break
                }

                prev = curr
            }

            return allBytes.toByteArray()
        }

        var curr = -1

        fun readLength(): Int {
            var length = 0
            while (true) {
                curr = inputStream.read()
                allBytes.write(curr)

                val t = Char(curr)
                if (t.isDigit()) {
                    length = length * 10 + (t - '0')
                } else {
                    break
                }
            }

            return length
        }

        fun readCRLF() {
            val t = Char(curr)
            if (t == '\r') {
                curr = inputStream.read()
                allBytes.write(curr)
            } else {
                curr = inputStream.read()
                allBytes.write(curr)

                curr = inputStream.read()
                allBytes.write(curr)
            }
        }

        val length = readLength()

        readCRLF()

        val bytes = inputStream.readNBytes(length)
        allBytes.write(bytes)

        readCRLF()

        return allBytes.toByteArray()
    }
}

fun main() {
    val redisClient = RedisClient()
    redisClient.connect(8081, "127.0.0.1")

//    redisClient.auth()

    val threads = 10
    val clients = 10
    val requests = 100_000

    val timeSpent = CopyOnWriteArrayList<Long>()
    val threadPool = Executors.newFixedThreadPool(threads)
    val latch = CountDownLatch(clients)

    repeat(clients) {
        threadPool.execute {
            repeat(requests / clients) {
                val t1 = System.currentTimeMillis()
                redisClient.get()
                val t2 = System.currentTimeMillis()
                timeSpent.add(t2 - t1)
            }

            latch.countDown()
        }
    }

    latch.await()
    println()
    println("throughput: ${(requests * 1.0 / timeSpent.sum()) * 1000} requests per second")
    println("latency: avg ${timeSpent.average()}ms, min ${timeSpent.min()}ms, max ${timeSpent.max()}ms")
    threadPool.shutdown()
}