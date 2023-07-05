package me.oyb.demo.nio.reactor

import me.oyb.demo.common.getLogger
import org.slf4j.Logger
import java.nio.channels.SelectionKey
import java.nio.channels.ServerSocketChannel

class Acceptor(private val workerGroup: Array<SubReactor>) {

    companion object {
        private val log: Logger = getLogger<Acceptor>()
    }

    private var nextSubReactorIndex = 0

    fun accept(selectionKey: SelectionKey) {
        val serverSocketChannel = selectionKey.channel() as ServerSocketChannel
        val socketChannel = serverSocketChannel.accept()
        log.info("accepted connection from {}", socketChannel.remoteAddress)

        nextSubReactor().apply {
            register(socketChannel)
            if (!isStarted()) {
                start()
            }
        }
    }

    private fun nextSubReactor() = workerGroup[nextSubReactorIndex++ % workerGroup.size]
}