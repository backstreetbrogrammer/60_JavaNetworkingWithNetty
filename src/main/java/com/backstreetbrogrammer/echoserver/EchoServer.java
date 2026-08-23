package com.backstreetbrogrammer.echoserver;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class EchoServer {
    private final int port;

    public EchoServer(final int port) {
        this.port = port;
    }

    public void start() throws Exception {
        final EventLoopGroup group = new NioEventLoopGroup();
        try {
            final ServerBootstrap b = new ServerBootstrap();
            b.group(group)
             .channel(NioServerSocketChannel.class) // specifies the use of an NIO transport Channel
             .localAddress(new InetSocketAddress(port))
             /*
               When a new connection is accepted, a new child Channel will be created, and the ChannelInitializer will
               add an instance of our EchoServerHandler to the Channel’s ChannelPipeline
              */
             .childHandler(new ChannelInitializer<SocketChannel>() { // adds an EchoServerHandler to the Channel's ChannelPipeline
                 @Override
                 public void initChannel(final SocketChannel ch) {
                     ch.pipeline().addLast(
                             new EchoServerHandler()); // EchoServerHandler is @Sharable so we can always use the same one
                 }
             });

            final ChannelFuture f = b.bind().sync(); // Binds the server asynchronously; sync() waits for the bind to complete
            System.out.printf("%s started and listen on %s%n", EchoServer.class.getName(), f.channel().localAddress());
            f.channel().closeFuture().sync(); // Gets the CloseFuture of the Channel and blocks the current thread until it's complete
        } finally {
            group.shutdownGracefully().sync(); // Shuts down the EventLoopGroup, releasing all resources
        }
    }

    public static void main(final String[] args) throws Exception {
        if (args.length != 1) {
            System.err.printf(
                    "Usage: %s <port>%n", EchoServer.class.getSimpleName());
            return;
        }
        final int port = Integer.parseInt(args[0]);
        new EchoServer(port).start();
    }
}


