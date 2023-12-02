package me.oyb.redis.server

import me.oyb.demo.common.getLogger
import me.oyb.demo.nio.readCommand
import me.oyb.demo.nio.readLine
import org.slf4j.Logger
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.nio.channels.SocketChannel
import java.util.concurrent.Executor
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

class SubReactor(private val threadPool: Executor) : Runnable {

    private val selector = Selector.open()
    private val taskQueue = LinkedBlockingQueue<Runnable>()

    companion object {
        private val log: Logger = getLogger<SubReactor>()
    }

    private var started = false

    fun register(channel: SocketChannel) {
        // just put the register task into the queue, and the sub reactor will register the channel later
        // why we need to do this? because the selector is not thread safe, we can't register the channel in the main thread
        taskQueue.offer {
            channel.configureBlocking(false)
            channel.register(selector, SelectionKey.OP_READ)
            log.info("registered channel: {}", channel)
        }

        log.info("queued register task for channel: {}", channel)
    }

    override fun run() {
        log.info("sub reactor started")

        while (true) {
            while (taskQueue.isNotEmpty()) {
                taskQueue.take().run()
            }

            // timeout is 100ms, so that we can check the task queue frequently
            selector.select(100)

            val selectedKeys = selector.selectedKeys()
            val selectionKeyMutableIterator = selectedKeys.iterator()

            while (selectionKeyMutableIterator.hasNext()) {
                val selectionKey = selectionKeyMutableIterator.next()
                selectionKeyMutableIterator.remove()

                if (!selectionKey.isValid) {
                    continue
                }

                when {
                    selectionKey.isReadable -> {
                        val socketChannel = selectionKey.channel() as SocketChannel

                        val bytes = socketChannel.readCommand()
                        if (bytes.isEmpty()) {
                            continue
                        }

                        threadPool.execute {
                            Handler(socketChannel, bytes).handle()
                        }
                    }
                }
            }
        }
    }

    fun isStarted() = started

    fun start() {
        if (!started) {
            started = true
            thread { run() }
        }
    }
}