package com.example.netty4client;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleStateHandler;

public class Client {

    public static String HOST = "192.168.199.177";
    public static int PORT = 9000;
    static final int RECONNECT_DELAY = Integer.parseInt(System.getProperty("reconnectDelay", "5"));
    static final int READ_TIMEOUT = 24;//客户端READ_TIMEOUT秒没有接收到服务器消息，则会触发READ_TIMEOUT事件，指定0表示不起作用
    static final int WRITE_TIMEOUT = 10;//客户端WRITE_TIMEOUT秒没有向服务器发送消息，则会触发WRITER_IDLE事件，指定0表示不起作用
    static final int ALL_TIMEOUT = 0; //客户端ALL_TIMEOUT秒没有向服务器发送消息和没有接收到服务器消息，则会触发ALL_TIMEOUT事件，指定0表示不起作用
    
    private static final ClientHandler handler = new ClientHandler();

    public static Bootstrap configureBootstrap(Bootstrap b) {
        return configureBootstrap(b, new NioEventLoopGroup());
    }

    static Bootstrap configureBootstrap(Bootstrap b, EventLoopGroup g) {
        b.group(g)
                .channel(NioSocketChannel.class)
                .remoteAddress(HOST, PORT)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
//                    	ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,Delimiters.lineDelimiter()));
                    	ch.pipeline().addLast(new IdleStateHandler(READ_TIMEOUT, WRITE_TIMEOUT, 0,TimeUnit.SECONDS));
                    	ByteBuf delimiter = Unpooled.copiedBuffer("$_".getBytes());
                    	ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                    	ch.pipeline().addLast(new StringDecoder());
                    	ch.pipeline().addLast(handler);
                    }
                });
        return b;
    }

    static void connect(Bootstrap b) {
        b.connect().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.cause() != null) {
                    System.out.println("Failed to connect: " + future.cause());
                }
            }
        });
    }
}
