# Java Networking with Netty

Netty is a NIO client server framework which enables quick and easy development of network applications such as protocol
servers and clients. It greatly simplifies and streamlines network programming such as TCP and UDP socket server.

Tools used:

- JDK 23
- Netty 4.2.17
- Maven
- JUnit 5, Mockito
- IntelliJ IDE

## Table of Contents

1. [Introduction to Netty](https://github.com/backstreetbrogrammer/60_JavaNetworkingWithNetty#01-introduction-to-netty)
2. [Project Setup](https://github.com/backstreetbrogrammer/60_JavaNetworkingWithNetty#02-project-setup)
3. [Hello Netty - first program](https://github.com/backstreetbrogrammer/60_JavaNetworkingWithNetty#03-hello-netty---first-program)

---

## 01. Introduction to Netty

Netty is an advanced framework for creating high-performance network applications.

**Netty's Core Components**

**_1. Channels_**

A Channel represents an open connection to an entity such as a hardware device, a file, a network socket, or a program
component that is capable of performing one or more distinct I/O operations, for example reading or writing.

Think of a Channel as a vehicle for incoming (inbound) and outgoing (outbound) data.

It can be open or closed, connected or disconnected.

**_2. Callbacks_**

A Callback is a function that is passed as an argument to another function and is intended to be executed after some
operation has been completed.

In Netty, callbacks are used to handle events such as the completion of an I/O operation or the occurrence of an
exception.

When a callback is triggered, the event can be handled by an implementation of the interface `ChannelHandler`.

**_3. Futures_**

A Future represents the result of an asynchronous operation.

It provides methods to check if the operation is complete, to wait for its completion, and to retrieve the result of the
operation.

In Netty, Futures are used to handle the result of asynchronous I/O operations.

`ChannelFuture` provides methods that allow us to register one or more `ChannelFutureListener` instances.

The listener’s callback method, `operationComplete()`, is called when the operation has completed.

The listener can then determine whether the operation completed successfully or with an error.

If the latter, we can retrieve the `Throwable` that was produced.

In short, the notification mechanism provided by the `ChannelFutureListener` eliminates the need for manually checking
operation completion.

Each of Netty’s outbound I/O operations returns a `ChannelFuture`; that is, none of them block.

**_4. Events and handlers_**

Netty uses distinct events to notify us about changes of state or the status of operations.

This allows us to trigger the appropriate action based on the event that has occurred.

Such actions might include:

- Logging
- Data transformation
- Flow-control
- Application logic

Events that may be triggered by **inbound** data or an associated change of state include:

- Active or inactive connections
- Data reads
- User events
- Error events

An **outbound** event is the result of an operation that will trigger an action in the future, which may be:

- Opening or closing a connection to a remote peer
- Writing or flushing data to a socket

Every event can be dispatched to a user-implemented method of a handler class.

**Summary**

Netty’s asynchronous programming model is built on the concepts of **Futures** and **callbacks**, with the dispatching
of events to handler methods happening at a deeper level.

Taken together, these elements provide a processing environment that allows the logic of our application to evolve
independently of any concerns with network operations.

This is a key goal of Netty’s design approach.

Under the covers, an `EventLoop` is assigned to each `Channel` to handle all of the events, including:

- Registration of interesting events
- Dispatching events to ChannelHandlers
- Scheduling further actions

The `EventLoop` itself is driven by only **one thread** that handles all of the I/O events for one `Channel` and does
not change during the lifetime of the `EventLoop`.

This simple and powerful design eliminates any concern we might have about synchronization in our `ChannelHandlers`, so
we can focus on providing the right logic to be executed when there is interesting data to process.

---

## 02. Project Setup

- [JDK 23 download](https://www.oracle.com/java/technologies/javase/jdk23-archive-downloads.html)
- [Maven download](https://maven.apache.org/download.cgi)
- [IntelliJ IDEA download](https://www.jetbrains.com/idea/download/#section=windows)
- Set `JAVA_HOME`, `M2_HOME`, `MAVEN_HOME` system variables and set in PATH

As this is a maven project, the setup `pom.xml` is provided in the project root.

The only dependency we need to add is Netty v4.2.17 as:

```xml

<dependency>
    <groupId>io.netty</groupId>
    <artifactId>netty-all</artifactId>
    <version>4.2.17.Final</version>
</dependency>
```

---

## 03. Hello Netty - first program

We will implement our first client/server application which is an **Echo server**, using Netty.

After the client establishes a connection, it sends one or more messages to the server, which in turn echoes each
message to the client.

**Writing the Echo server**

All Netty servers require the following:

- **At least one ChannelHandler** — This component implements the server’s processing of data received from the client —
  its business logic.
- **Bootstrapping** — This is the startup code that configures the server. At a minimum, it binds the server to the port
  on which it will listen for connection requests.

Because our Echo server will respond to incoming messages, it will need to implement interface `ChannelInboundHandler`,
which defines methods for acting on **inbound events**.

This simple application will require only a few of these methods, so it will be sufficient to subclass
`ChannelInboundHandlerAdapter`, which provides a default implementation of `ChannelInboundHandler`.

The following methods interest us:

- `channelRead()` — Called for each incoming message
- `channelReadComplete()` — Notifies the handler that the last call made to `channelRead()` was the last message in the
  current batch
- `exceptionCaught()` — Called if an exception is thrown during the read operation

The Echo server’s `ChannelHandler` implementation is `EchoServerHandler`.

Key points:

- `ChannelHandlers` are invoked for different types of events.
- Applications implement or extend `ChannelHandlers` to hook into the event lifecycle and provide custom application
  logic.
- Architecturally, `ChannelHandlers` help to keep our business logic decoupled from networking code. This simplifies
  development as the code evolves in response to changing requirements.

**Bootstrapping the server**

