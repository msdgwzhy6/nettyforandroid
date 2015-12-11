package com.example.netty4client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

import java.util.concurrent.TimeUnit;

import android.util.Log;


@ChannelHandler.Sharable
public class ClientHandler extends ChannelInboundHandlerAdapter {
	
	 static final String ECHO_REQ = "Frome netty client.$_";

	@Override
	public void channelActive(final ChannelHandlerContext ctx) {
		Log.d("test","channelActive");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		Log.d("test", "channelRead");
		Log.d("test", "Received msg:"+msg);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
		Log.d("test","userEventTriggered");
		if (!(evt instanceof IdleStateEvent)) {
			return;
		}

		IdleStateEvent e = (IdleStateEvent) evt;
		if (e.state().equals(IdleState.READER_IDLE)) {
			System.out.println("READER_IDLE");
		}else if (e.state().equals(IdleState.WRITER_IDLE) ) {
			System.out.println("WRITER_IDLE");
			ctx.writeAndFlush(Unpooled.copiedBuffer(ECHO_REQ.getBytes()));
		}else if (e.state().equals(IdleState.ALL_IDLE)) {
			System.out.println("ALL_IDLE");
		}
	}

	@Override
	public void channelInactive(final ChannelHandlerContext ctx) {
		System.out.println("Disconnected from: "+ctx.channel().remoteAddress());
	}

	@Override
	public void channelUnregistered(final ChannelHandlerContext ctx)
			throws Exception {
		final EventLoop loop = ctx.channel().eventLoop();
		loop.schedule(new Runnable() {
			public void run() {
				System.out.println("Reconnecting to: " + Client.HOST + ':' + Client.PORT);
				Client.connect(Client.configureBootstrap(new Bootstrap(), loop));
			}
		}, Client.RECONNECT_DELAY, TimeUnit.SECONDS);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}




}
