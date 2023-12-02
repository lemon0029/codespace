package me.oyb.demo.nio.reactor

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
        log.info("read from ${socketChannel.remoteAddress}: ${bytes.decodeToString().replace("\n", "\\n")}")

        // simulate a long time operation
//        TimeUnit.MILLISECONDS.sleep(30L)
//        socketChannel.write(ByteBuffer.wrap(bytes))
//        log.info("write to ${socketChannel.remoteAddress}: ${bytes.decodeToString().replace("\n", "\\n")}")
//        socketChannel.close()
    }
}