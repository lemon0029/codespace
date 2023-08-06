package me.oyb.altorithm.template

import java.util.*
import java.util.concurrent.ThreadLocalRandom
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

data class Server(
    val ip: String,
    val weight: Int,
    val tag: String
)

val servers = listOf(
    Server("192.168.1.1", 4, "A"),
    Server("192.168.1.2", 3, "B"),
    Server("192.168.1.3", 2, "C"),
)

interface LoadBalancer {
    fun choose(): Server
}

class RandomLoadBalancer : LoadBalancer {

    override fun choose(): Server {
        val i = ThreadLocalRandom.current().nextInt(servers.size)
        return servers[i]
    }

}

class RoundRobinLoadBalancer : LoadBalancer {

    private var position = AtomicInteger()

    override fun choose(): Server {
        if (servers.size == 1) {
            return servers[0]
        }

        val pos = position.incrementAndGet() and Int.MAX_VALUE

        return servers[pos % servers.size]
    }

}

class WeightedRandomLoadBalancer : LoadBalancer {

    private val pool: TreeMap<Int, Server> = TreeMap()
    private var totalWeight = 0

    init {
        servers.forEach {
            totalWeight += it.weight
            pool[totalWeight] = it
        }
    }

    override fun choose(): Server {
        val pos = Random.nextInt(1, totalWeight + 1)
        return pool.ceilingEntry(pos).value
    }

}

class WeightedRandomLoadBalancer1 : LoadBalancer {

    private val pool: TreeMap<Int, Server> = TreeMap()
    private var totalWeight = 0

    init {
        servers.forEach {
            totalWeight += it.weight
            pool[totalWeight] = it
        }
    }

    override fun choose(): Server {
        val pos = Random.nextInt(1, totalWeight + 1)
        return pool.ceilingEntry(pos).value
    }

}

class WeightedRoundRobinLoadBalancer : LoadBalancer {

    private var i = -1 // 上次选择的服务下标
    private var cw = 0 // 当前计划中的权重

    private var r = -1 // 所有服务权重的最大公约数
    private var m = -1 // 所有服务权重的最大值

    init {
        for (server in servers) {
            m = maxOf(m, server.weight)
            r = if (r == -1) server.weight else gcd(server.weight, r)
        }
    }

    override fun choose(): Server {

        while (true) {
            i = (i + 1) % servers.size

            if (i == 0) {
                cw -= r
                if (cw <= 0) cw = m
            }

            if (servers[i].weight >= cw) {
                return servers[i]
            }
        }
    }

    private fun gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)

}