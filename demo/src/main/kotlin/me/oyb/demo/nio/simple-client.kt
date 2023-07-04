package me.oyb.demo.nio

import me.oyb.demo.common.getLogger
import org.slf4j.Logger
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.util.concurrent.TimeUnit

class SimpleClient {

    private val log: Logger = getLogger<SimpleClient>()

    fun run(serverPort: Int = 8080, serverHost: String = "localhost") {
        val socketChannel = SocketChannel.open()

        socketChannel.connect(InetSocketAddress(serverHost, serverPort))
        socketChannel.configureBlocking(true)
        log.info("connected to {}", socketChannel.remoteAddress)

        val message = "hello, world!".toByteArray()
        socketChannel.write(ByteBuffer.wrap(message))

        log.info("write \"{}\" to {}", message.decodeToString(), socketChannel.remoteAddress)

        // wait for server response
        TimeUnit.MILLISECONDS.sleep(200)

        val response = socketChannel.readAllBytes()
        log.info("read \"{}\" from {}", response.decodeToString(), socketChannel.remoteAddress)
    }
}

fun main() {
    SimpleClient().run(serverPort = 8080)
}