package me.oyb.demo.nio.netty

import io.netty.bootstrap.ServerBootstrap
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInitializer
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.netty.handler.codec.LengthFieldBasedFrameDecoder


fun main() {
    val serverBootstrap = ServerBootstrap()
    serverBootstrap.group(NioEventLoopGroup(1), NioEventLoopGroup(4))
    serverBootstrap.channel(NioServerSocketChannel::class.java)
    // echo
    serverBootstrap.childHandler(object : ChannelInitializer<SocketChannel>() {
        override fun initChannel(ch: SocketChannel) {
            ch.pipeline().addLast(object : SimpleChannelInboundHandler<String>() {
                override fun channelRead0(ctx: ChannelHandlerContext, msg: String?) {
                    println(msg)
                    ctx.channel().write(msg)
                }

                override fun channelRead(ctx: ChannelHandlerContext?, msg: Any) {
                    val byteBuf = msg as ByteBuf

                    ctx?.write(byteBuf)
                }

                override fun channelReadComplete(ctx: ChannelHandlerContext?) {
                     ctx?.flush()
                }
            })
        }
    })

    val future = serverBootstrap.bind(8083).sync()
    future.channel().closeFuture().sync()
}