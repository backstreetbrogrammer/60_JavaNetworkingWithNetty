package com.backstreetbrogrammer.echoserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

@ChannelHandler.Sharable // Indicates that a ChannelHandler can be safely shared by multiple channels
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) {
        final ByteBuf in = (ByteBuf) msg;
        System.out.printf("Server received: %s%n", in.toString(CharsetUtil.UTF_8));
        ctx.write(in); // Writes the received message to the sender without flushing the outbound message
    }

    @Override
    public void channelReadComplete(final ChannelHandlerContext ctx) {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)
           .addListener(ChannelFutureListener.CLOSE); // Flushes pending messages to the remote peer and closes the channel
    }

    @Override
    public void exceptionCaught(final ChannelHandlerContext ctx, final Throwable cause) {
        System.err.println(cause.getMessage());
        ctx.close(); // Closes the channel
    }
}
