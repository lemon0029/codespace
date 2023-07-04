package me.oyb.demo.nio

import me.oyb.demo.common.getLogger
import org.slf4j.Logger
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel
import java.nio.channels.SocketChannel
import kotlin.concurrent.thread

@Component
class SimpleNioEchoServer : CommandLineRunner {

    private val log: Logger = getLogger<SimpleNioEchoServer>()

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
                    log.error("error dispatching selection key", e)
                    selectionKey.cancel()

                    when (val channel = selectionKey.channel()) {
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
            }
        }
    }

    fun dispatch(key: SelectionKey) {
        when {
            key.isAcceptable -> {
                val socketChannel = (key.channel() as ServerSocketChannel).accept()

                socketChannel
                    .configureBlocking(false)
                    .register(selector, SelectionKey.OP_READ)

                log.info("new connection accepted: {}", socketChannel.remoteAddress)
            }

            key.isReadable -> {
                val socketChannel = key.channel() as SocketChannel
                val bytes = socketChannel.readAllBytes()
                val remoteAddress = socketChannel.remoteAddress

                log.info("read \"{}\" from {}", bytes.decodeToString(), remoteAddress)

                socketChannel.write(ByteBuffer.wrap(bytes))
                log.info("write \"{}\" to {}", bytes.decodeToString(), remoteAddress)

                socketChannel.close()
                log.info("connection closed: {}", remoteAddress)
            }
        }
    }

    override fun run(vararg args: String?) {
        thread(name = "nio-echo-server") { run(8080) }
    }
}