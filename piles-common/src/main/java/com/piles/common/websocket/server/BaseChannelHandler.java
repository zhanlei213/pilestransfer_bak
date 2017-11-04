package com.piles.common.websocket.server;

import com.piles.common.business.IBusinessHandler;
import com.piles.common.util.ChannelMap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Sharable
public class BaseChannelHandler extends SimpleChannelInboundHandler<byte[]> {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private IBusinessHandler businessHandler;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, byte[] msg) throws Exception {
		Channel incoming = ctx.channel();
		logger.info("[" + incoming.remoteAddress() + "]发送信息:" + msg);
		byte[] response = businessHandler.process(msg, incoming);
		logger.info("返回信息:" + response);
		//包装
		ByteBuf encoded = ctx.alloc().buffer(response.length);
		encoded.writeBytes(response);
		ctx.writeAndFlush(response);
	}

	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception { // (2)
		Channel incoming = ctx.channel();
		logger.info("add connect:" + incoming.remoteAddress());
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception { // (3)
		Channel incoming = ctx.channel();
		logger.info("remove connect:" + incoming.remoteAddress());
		ChannelMap.removeChannel(incoming);
	}

	/**
	 * 当从Channel中读数据时被调用
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		super.channelRead(ctx, msg);
	}

	/**
	 * 当Channel变成活跃状态时被调用；Channel是连接/绑定、就绪的
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Channel incoming = ctx.channel();
		logger.info("channelActive:" + incoming.remoteAddress() + "在线");
		super.channelActive(ctx);
	}

	/**
	 * Channel未连接到远端
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception { // (6)
		Channel incoming = ctx.channel();
		logger.info("channelInactive:" + incoming.remoteAddress() + "掉线");
		incoming.close();
		ctx.close();
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		super.channelReadComplete(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		Channel incoming = ctx.channel();
		ctx.close();
		logger.error("exceptioncaught," + incoming.remoteAddress(), cause);
	}
}
