package com.xinchen.tool;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Hello world!
 *
 */
public class Main
{
    static final int LOCAL_PORT = Integer.parseInt(System.getProperty("localPort", "80"));
    static final String REMOTE_HOST = System.getProperty("remoteHost", "localhost");
    static final int REMOTE_PORT = Integer.parseInt(System.getProperty("remotePort", "80"));


    public static void main(String[] args) throws InterruptedException {
        System.err.println("Proxying *:" + LOCAL_PORT + " to " + REMOTE_HOST + ":" + REMOTE_PORT + " ...");

        //Configure the bootstrap

        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ProxyInitializer(REMOTE_HOST, REMOTE_PORT))
                    .childOption(ChannelOption.AUTO_READ, false)
                    .bind(LOCAL_PORT).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
