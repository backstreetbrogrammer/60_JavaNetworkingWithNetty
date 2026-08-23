package com.backstreetbrogrammer.echoserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable // can be shared among channels
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        // when notified that the channel is active after connection established, sends a message
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
    }

    @Override
    public void channelRead0(final ChannelHandlerContext ctx, final ByteBuf in) {
        // logs a dump of the received message from server
        System.out.printf("Client received: %s%n", in.toString(CharsetUtil.UTF_8));
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        System.err.printf("Exception caught: %s%n", cause.getMessage());
        ctx.close();
    }
}
