package me.oyb.demo.nio.normal

import me.oyb.demo.common.getLogger
import me.oyb.demo.nio.readAllBytes
import org.slf4j.Logger
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

@Component
class SimpleServer : CommandLineRunner {

    companion object {
        private val log: Logger = getLogger<SimpleServer>()
    }

    private val selector = Selector.open()
    private val serverSocketChannel = ServerSocketChannel.open()

    fun run(port: Int) {
        serverSocketChannel.bind(InetSocketAddress(port))
            .configureBlocking(false)
            .register(selector, SelectionKey.OP_ACCEPT)

        log.info("server started on port {}", port)

        while (true) {
            selector.select()

            val selectedKeys = selector.selectedKeys()
            val selectionKeyMutableIterator = selectedKeys.iterator()

            while (selectionKeyMutableIterator.hasNext()) {
                val selectionKey = selectionKeyMutableIterator.next()
                log.info("dispatching selection key: {}", selectionKey)
                selectionKeyMutableIterator.remove()

                try {
                    dispatch(selectionKey)
                } catch (e: Exception) {
                    handleError(e, selectionKey)
                }
            }
        }
    }

    fun dispatch(key: SelectionKey) {
        when {
            key.isAcceptable -> doAccept(key)
            key.isReadable -> doRead(key)
        }
    }

    fun doAccept(key: SelectionKey) {
        val socketChannel = (key.channel() as ServerSocketChannel).accept()

        socketChannel
            .configureBlocking(false)
            .register(selector, SelectionKey.OP_READ)

        log.info("new connection accepted: {}", socketChannel.remoteAddress)
    }

    fun doRead(key: SelectionKey) {
        val socketChannel = key.channel() as SocketChannel
        val bytes = socketChannel.readAllBytes()
        val remoteAddress = socketChannel.remoteAddress

        log.info("read \"{}\" from {}", bytes.decodeToString(), remoteAddress)

        // simulate a long time operation
        TimeUnit.MILLISECONDS.sleep(30L)
        socketChannel.write(ByteBuffer.wrap(bytes))
        log.info("write \"{}\" to {}", bytes.decodeToString(), remoteAddress)

        socketChannel.close()
        log.info("connection closed: {}", remoteAddress)
    }

    fun handleError(e: Exception, key: SelectionKey) {
        log.error("error dispatching selection key", e)
        key.cancel()

        when (val channel = key.channel()) {
            is SocketChannel -> {
                log.info("connection closed: {}", channel.remoteAddress)
                channel.close()
            }

            is ServerSocketChannel -> {
                log.info("server socket closed")
                channel.close()
            }
        }
    }

    override fun run(vararg args: String?) {
        thread(name = "nio-echo-server") { run(8080) }
    }
}

@SpringBootApplication(scanBasePackages = ["me.oyb.demo.nio.normal"])
class SimpleServerApplication

fun main() {
    runApplication<SimpleServerApplication>()
}
