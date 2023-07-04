package me.oyb.demo.nio

import java.nio.ByteBuffer
import java.nio.channels.SocketChannel

fun SocketChannel.readAllBytes(): ByteArray {
    val buffer = ByteBuffer.allocate(1024)
    var result = ByteArray(0)

    while (read(buffer) > 0) {
        buffer.flip()
        result += buffer.getBytes()
        buffer.clear()
    }

    return result
}

fun ByteBuffer.getBytes(): ByteArray = ByteArray(remaining()).apply { get(this) }