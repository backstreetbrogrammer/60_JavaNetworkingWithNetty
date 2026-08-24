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
4. [Netty components and design](https://github.com/backstreetbrogrammer/60_JavaNetworkingWithNetty#04-netty-components-and-design)

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

[Echo Protocol](https://datatracker.ietf.org/doc/html/rfc862)

We will implement our first client/server application which is an **Echo server**, using Netty.

After the client establishes a connection, it sends one or more messages to the server, which in turn echoes each
message to the client.

**_Writing the Echo server_**

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

**_Bootstrapping the server_**

Bootstrapping of the server involves the following:

- Bind to the port on which the server will listen for and accept incoming connection requests
- Configure Channels to notify an `EchoServerHandler` instance about inbound messages

`EchoServer` class is used for bootstrapping the server.

**Summary**

Primary code components of the server:

- The `EchoServerHandler` implements the business logic.
- The `EchoServer.main()` method bootstraps the server.

The following steps are required in bootstrapping:

- Create a `ServerBootstrap` instance to bootstrap and bind the server.
- Create and assign an `NioEventLoopGroup` instance to handle event processing, such as accepting new connections and
  reading/writing data.
- Specify the local `InetSocketAddress` to which the server binds.
- Initialize each new `Channel` with an `EchoServerHandler` instance.
- Call `ServerBootstrap.bind()` to bind the server.

At this point the server is initialized and ready to be used.

---

**_Writing an Echo client_**

The Echo client will:

1. Connect to the server
2. Send one or more messages
3. For each message, wait for and receive the same message back from the server
4. Close the connection

Writing the client involves the same two main code areas we saw in the server: **business logic** and **bootstrapping**.

The client will have a `ChannelInboundHandler` to process the data.

This requires overriding the following methods:

- `channelActive()` — Called after the connection to the server is established
- `channelRead0()` — Called when a message is received from the server
- `exceptionCaught()` — Called if an exception is raised during processing

Class `EchoClientHandler` implements the business logic for the client.

**_Bootstrapping the client_**

Bootstrapping a client is similar to bootstrapping a server, with the difference that instead of binding to a listening
port the client uses host and port parameters to connect to a remote address, here that of the Echo server.

Class `EchoClient` implements the bootstrapping of the client.

**Summary**

- A `Bootstrap` instance is created to initialize the client.
- An `NioEventLoopGroup` instance is assigned to handle the event processing, which includes creating new connections
  and processing inbound and outbound data.
- An `InetSocketAddress` is created for the connection to the server.
- An `EchoClientHandler` will be installed in the pipeline when the connection is established.
- After everything is set up, `Bootstrap.connect()` is called to connect to the remote peer.

---

## 04. Netty components and design

From a high-level perspective, Netty addresses two corresponding areas of concern:

1. Its asynchronous and event-driven implementation, built on Java NIO, guarantees maximum application performance and
   scalability under heavy load.
2. Netty embodies a set of design patterns that decouple application logic from the network layer, simplifying
   development while maximizing the testability, modularity, and reusability of code.

`Channel`, `EventLoop`, and `ChannelFuture` classes which, taken together, can be thought of as representing Netty’s
networking abstraction:

- `Channel` — Sockets
- `EventLoop` — Control flow, multithreading, concurrency
- `ChannelFuture` — Asynchronous notification

### Interface Channel

Basic I/O operations (`bind()`, `connect()`, `read()`, and `write()`) depend on primitives supplied by the underlying
network transport.

In Java-based networking, the fundamental construct is class `Socket`.

Netty’s `Channel` interface provides an API that greatly reduces the complexity of working directly with `Socket`.

Additionally, `Channel` is the root of an extensive class hierarchy having many predefined, specialized implementations,
of which the following is a short list:

- `EmbeddedChannel`
- `LocalServerChannel`
- `NioDatagramChannel`
- `NioSctpChannel`
- `NioSocketChannel`

### Interface EventLoop

The `EventLoop` defines Netty’s core abstraction for handling events that occur during the lifetime of a connection.

These relationships are:

- An `EventLoopGroup` contains one or more `EventLoop`.
- An `EventLoop` is bound to a single `Thread` for its lifetime.
- All I/O events processed by an `EventLoop` are handled on its dedicated `Thread`.
- A `Channel` is registered for its lifetime with a single `EventLoop`.
- A single `EventLoop` may be assigned to one or more `Channel`.

Note that this design, in which the I/O for a given `Channel` is executed by the same `Thread`, virtually eliminates the
need for synchronization.

### Interface ChannelFuture

All I/O operations in Netty are **asynchronous**.

Because an operation may not return immediately, we need a way to determine its result at a later time.

For this purpose, Netty provides `ChannelFuture`, whose `addListener()` method registers a `ChannelFutureListener` to be
notified when an operation has completed (whether or not successfully).

Think of a `ChannelFuture` as a placeholder for the result of an operation that’s to be executed in the future.

When exactly it will be executed may depend on several factors and thus be impossible to predict with precision, but it
is certain that it will be executed.

Furthermore, all operations belonging to the same `Channel` are guaranteed to be executed in the order in which they
were invoked.

### Interface ChannelHandler

From the application developer’s standpoint, the primary component of Netty is the `ChannelHandler`, which serves as the
container for all application logic that applies to handling inbound and outbound data.

`ChannelHandler` methods are triggered by network events, this means it can be dedicated to almost any kind of action,
such as converting data from one format to another or handling exceptions thrown during processing.

For example, `ChannelInboundHandler` is a subinterface we’ll implement frequently.

This type receives **inbound events** and data to be handled by our application’s business logic.

We can also flush data from a `ChannelInboundHandler` when we’re sending a response to a connected client.

The business logic of our application will often reside in one or more `ChannelInboundHandler`.

### Interface ChannelPipeline

A `ChannelPipeline` provides a container for a chain of `ChannelHandler`s and defines an API for propagating the flow of
inbound and outbound events along the chain.

When a `Channel` is created, it is automatically assigned its own `ChannelPipeline`.

`ChannelHandler`s are installed in the `ChannelPipeline` as follows:

- A `ChannelInitializer` implementation is registered with a `ServerBootstrap`.
- When `ChannelInitializer.initChannel()` is called, the `ChannelInitializer` installs a custom set of `ChannelHandler`s
  in the pipeline.
- The `ChannelInitializer` removes itself from the `ChannelPipeline`.

The movement of an event through the pipeline is the work of the `ChannelHandler`s that have been installed during the 
initialization, or bootstrapping phase of the application. 

These objects receive events, execute the processing logic for which they have been implemented, and pass the data to 
the next handler in the chain. 

The order in which they are executed is determined by the order in which they were added.

