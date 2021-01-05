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
    static final int LOCAL_PORT = Integer.parseInt(System.getProperty("localPort", "8080"));
    static final String REMOTE_HOST = System.getProperty("remoteHost", "localhost");
    static final int REMOTE_PORT = Integer.parseInt(System.getProperty("remotePort", "1323"));


    public static void main(String[] args){
        //Configure the bootstrap
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);

        EventLoopGroup workGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup2 = new NioEventLoopGroup();

        proxy(bossGroup,workGroup,8081,REMOTE_HOST,REMOTE_PORT);
        proxy(bossGroup,workGroup2,8082,REMOTE_HOST,REMOTE_PORT);
    }

    static void proxy(EventLoopGroup bossGroup,
                      EventLoopGroup workGroup,
                      int bindPort,
                      String remoteHost,
                      int remotePort) {
        new Thread(()->{
            System.err.println("Proxying *:" + bindPort + " to " + remoteHost + ":" + remotePort + " ...");
            try {
                ServerBootstrap b = new ServerBootstrap();
                b.group(bossGroup, workGroup)
                        .channel(NioServerSocketChannel.class)
                        .handler(new LoggingHandler(LogLevel.INFO))
                        .childHandler(new ProxyInitializer(remoteHost, remotePort))
                        .childOption(ChannelOption.AUTO_READ, false)
                        .bind(bindPort).sync().channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                bossGroup.shutdownGracefully();
                workGroup.shutdownGracefully();
            }
        }).start();
    }
}
