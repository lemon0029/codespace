package me.oyb.redis.server

import me.oyb.demo.common.getLogger
import org.slf4j.Logger
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.ServerSocketChannel

class Reactor(workerGroup: Array<SubReactor>) : Runnable {

    companion object {
        private val log: Logger = getLogger<Reactor>()
    }

    private val selector = Selector.open()
    private val acceptor = Acceptor(workerGroup)

    override fun run() {
        log.info("main reactor started")

        while (true) {
            selector.select()

            val selectedKeys = selector.selectedKeys()
            val selectionKeyMutableIterator = selectedKeys.iterator()

            while (selectionKeyMutableIterator.hasNext()) {
                val selectionKey = selectionKeyMutableIterator.next()
                selectionKeyMutableIterator.remove()

                when {
                    selectionKey.isAcceptable -> acceptor.accept(selectionKey)
                }
            }
        }
    }

    fun register(serverSocketChannel: ServerSocketChannel) {
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT)
    }
}