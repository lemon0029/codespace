package me.oyb.redis.server

import me.oyb.demo.common.getLogger
import org.slf4j.Logger
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.stereotype.Component
import java.net.InetSocketAddress
import java.nio.channels.ServerSocketChannel
import java.util.concurrent.Executors
import kotlin.concurrent.thread

@Component
class Server : CommandLineRunner {

    private lateinit var bossGroup: Array<Reactor>
    private lateinit var workerGroup: Array<SubReactor>

    companion object {
        private val log: Logger = getLogger<Server>()
    }

    fun run(port: Int) {
        val workerThreadPool = Executors.newFixedThreadPool(8)

        workerGroup = Array(100) { SubReactor(workerThreadPool) }
        bossGroup = Array(10) { Reactor(workerGroup) }

        val serverSocketChannel = ServerSocketChannel.open()
        serverSocketChannel.configureBlocking(false)
        serverSocketChannel.bind(InetSocketAddress(port))

        log.info("server started on port {}", port)

        // before running the reactor, we need to register the server socket channel to the reactor
        bossGroup[0].register(serverSocketChannel)
        thread { bossGroup[0].run() }
    }

    override fun run(vararg args: String?) {
        thread(name = "nio-echo-server") { run(8081) }
    }
}

@SpringBootApplication
class MultipleReactorServerApplication

fun main() {
    runApplication<MultipleReactorServerApplication>()
}