package com.backstreetbrogrammer.echoserver;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

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
                 public void initChannel(final SocketChannel ch)
                         throws Exception {
                     ch.pipeline().addLast(
                             new EchoClientHandler());
                 }
             });

            final ChannelFuture f = b.connect().sync(); // connects to the remote peers; waits until the connect completes
            f.channel().closeFuture().sync(); // blocks until the Channel closes
        } finally {
            group.shutdownGracefully().sync(); // shuts down the thread pools and the release of all resources
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


