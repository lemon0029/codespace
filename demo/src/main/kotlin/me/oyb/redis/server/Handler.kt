package me.oyb.redis.server

import me.oyb.demo.common.getLogger
import org.slf4j.Logger
import java.nio.ByteBuffer
import java.nio.channels.SocketChannel
import java.util.concurrent.TimeUnit

class Handler(private val socketChannel: SocketChannel, private val bytes: ByteArray) {

    companion object {
        private val log: Logger = getLogger<Handler>()
    }

    fun handle() {
//        val replaced = bytes.decodeToString().replace("\n", "\\n").replace("\r", "\\r")
//        log.info("read from ${socketChannel.remoteAddress}: $replaced")
        socketChannel.write(ByteBuffer.wrap("$3\r\nbar\r\n".toByteArray()))
    }
}