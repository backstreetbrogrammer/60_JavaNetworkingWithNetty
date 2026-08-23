package com.backstreetbrogrammer.echoserver;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;

import java.net.InetSocketAddress;
import java.util.Scanner;

public class EchoClient {
    private final String host;
    private final int port;

    public EchoClient(final String host, final int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        final EventLoopGroup group = new NioEventLoopGroup();
        try {
            final Bootstrap b = new Bootstrap();
            b.group(group) // specifies EventLoopGroup to handle client events; NIO implementation is needed
             .channel(NioSocketChannel.class) // Channel type is the one for NIO transport
             .remoteAddress(new InetSocketAddress(host, port))
             // adds an EchoClientHandler to the pipeline when a Channel is created
             .handler(new ChannelInitializer<SocketChannel>() {
                 @Override
                 public void initChannel(final SocketChannel ch) {
                     ch.pipeline().addLast(
                             new EchoClientHandler());
                 }
             });

            final ChannelFuture f = b.connect().sync();
            final Channel channel = f.channel();
            
            // Start a thread to read user input and send messages
            readAndSendMessages(channel);
            
            channel.closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }
    }

    private void readAndSendMessages(final Channel channel) {
        try (final Scanner scanner = new Scanner(System.in)) {
            while (channel.isActive()) {
                final String message = scanner.nextLine();
                
                if (message.equalsIgnoreCase("quit") || message.equalsIgnoreCase("exit")) {
                    channel.close();
                    break;
                }
                
                if (!message.isEmpty()) {
                    channel.writeAndFlush(Unpooled.copiedBuffer(message, CharsetUtil.UTF_8));
                }
            }
        }
    }

    public static void main(final String[] args) throws Exception {
        if (args.length != 2) {
            System.err.printf(
                    "Usage: %s <host> <port>%n", EchoClient.class.getSimpleName());
            return;
        }

        final String host = args[0];
        final int port = Integer.parseInt(args[1]);

        new EchoClient(host, port).start();
    }
}


