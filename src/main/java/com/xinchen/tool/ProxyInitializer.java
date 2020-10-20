package com.xinchen.tool;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * @author Xin Chen (xinchenmelody@gmail.com)
 * @version 1.0
 * @date Created In 2020/3/3 21:23
 */
public class ProxyInitializer extends ChannelInitializer<SocketChannel> {
    private final String remoteHost;
    private final int port;

    public ProxyInitializer(String remoteHost, int port) {
        this.remoteHost = remoteHost;
        this.port = port;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        // 初始化pipeline
        ch.pipeline()
                .addLast(new LoggingHandler(LogLevel.INFO),new ProxyFrontendHandler(remoteHost,port));
    }
}
