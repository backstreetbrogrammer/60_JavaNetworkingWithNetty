# Java Networking with Netty

Java tutorial covering networking, asynchronous programming and using Netty.

Tools used:

- JDK 23
- Netty 4.2.17
- Maven
- JUnit 5, Mockito
- IntelliJ IDE

## Table of Contents

- [Part 1 - Java Networking and Asynchronous Programming](#part-1---java-networking-and-asynchronous-programming)
    - [01. Introduction to Networking](#01-introduction-to-networking)
    - [02. TCP/IP model](#02-tcpip-model)
    - [03. HTTP Basics](#03-http-basics)
    - [04. TCP Client and Server](#04-tcp-client-and-server)
    - [05. UDP Client and Server](#05-udp-client-and-server)
    - [06. Introduction to asynchronous programming](#06-introduction-to-asynchronous-programming)
    - [07. Chaining and Splitting tasks](#07-chaining-and-splitting-tasks)
    - [08. Controlling threads executing tasks](#08-controlling-threads-executing-tasks)
    - [09. Error Handling](#09-error-handling)
    - [10. Best patterns](#10-best-patterns)
- [Part 2 - Java Networking with Netty](#part-2---java-networking-with-netty)
    - [01. Introduction to Netty](#01-introduction-to-netty)
    - [02. Project Setup](#02-project-setup)
    - [03. Hello Netty - first program](#03-hello-netty---first-program)
    - [04. Netty components and design](#04-netty-components-and-design)
    - [05. Transports](#05-transports)
    - [06. ByteBuf](#06-bytebuf)
    - [07. ChannelHandler and ChannelPipeline](#07-channelhandler-and-channelpipeline)
    - [08. EventLoop and threading model](#08-eventloop-and-threading-model)
    - [09. Bootstrapping](#09-bootstrapping)
    - [10. The codec framework](#10-the-codec-framework)
    - [11. Provided ChannelHandlers and codecs](#11-provided-channelhandlers-and-codecs)
    - [12. WebSocket](#12-websocket)
    - [13. Broadcasting events with UDP](#13-broadcasting-events-with-udp)

---

## Part 1 - Java Networking and Asynchronous Programming

---

## 01. Introduction to Networking

Computer networking is the practice of connecting computers, servers, and other digital devices so they can share data,
files, and hardware resources.

**Key Building Blocks**

- **Nodes**: The connected devices, like laptops, phones, printers, and smart home gadgets.
- **Links**: The physical wires (like Ethernet cables or fiber optics) or wireless signals (like Wi-Fi) that join the
  nodes together.
- **Hardware**: Devices like **Routers** that direct data between different networks, and **switches** that link devices
  within the same network.
- **Protocols**: The shared rules that allow different machines to send and receive information accurately.

**Common Types of Networks**

- **LAN (Local Area Network)**: Connects devices in a small space, such as a single home, school, or office.
- **WAN (Wide Area Network)**: Covers large distances across countries; the **internet** is the biggest WAN.
- **WLAN (Wireless Local Area Network)**: A LAN that uses Wi-Fi instead of physical cables.

To implement a **networking protocol**, the protocol software modules are interfaced with a framework implemented on the
machine's operating system. This framework implements the networking functionality of the operating system.

When protocol algorithms are expressed in a portable programming language, the protocol software may be made operating
system independent. The best-known frameworks are the **TCP/IP model** and the **OSI model**.

**Types of communication protocols**

There are **two** types of communication protocols, based on their representation of the content being carried:

- **Text-based**: A text-based protocol or plain text protocol represents its content in human-readable format, often in
  plain text encoded in a machine-readable encoding such as `ASCII` or `UTF-8`, or in structured text-based formats such
  as `XML` or `JSON`. Examples: **FTP**, **SMTP**, **HTTP** (earlier versions), **Finger Protocol**

- **Binary**: A binary protocol utilizes all values of a `byte`, as opposed to a text-based protocol which only uses
  values corresponding to human-readable characters in `ASCII` encoding. Binary protocols are intended to be read by a
  machine rather than a human being. Binary protocols have the advantage of terseness, which translates into speed of
  transmission and interpretation. Examples: **HTTP/2**, **HTTP/3**, **EbXML**, **EDOC**

---

## 02. TCP/IP model

The Internet protocol suite, commonly known as TCP/IP, is a framework for organizing the set of communication protocols
used in the Internet and similar computer networks according to functional criteria.

The foundational protocols in the suite are:

- Transmission Control Protocol (TCP)
- User Datagram Protocol (UDP)
- Internet Protocol (IP)

![TCP_networking](TCP_networking.PNG)

Conceptual data flow in a simple network topology of two hosts (A and B) connected by a link between their respective
routers.

The application on each host executes read and write operations as if the processes were directly connected to each
other by some kind of data pipe.

After establishment of this pipe, most details of the communication are hidden from each process, as the underlying
principles of communication are implemented in the lower protocol layers.

In analogy, at the transport layer the communication appears as host-to-host, without knowledge of the application data
structures and the connecting routers, while at the inter-networking layer, individual network boundaries are traversed
at each router.

![TCP_IP_model](TCP_IP_model.PNG)

**_Layer 1 - Data Link_**

The data link layer defines the networking methods within the scope of the **local network** link on which hosts
communicate without intervening **routers**.

This layer includes the protocols used to describe the local network topology and the interfaces needed to affect the
transmission of internet layer datagrams to next-neighbor hosts.

For example, Ethernet protocol wraps the data into dataframes and uses machines MAC addresses to deliver data frames.

![DataLink](DataLink.PNG)

To summarize,

- Physical delivery of data over a single link
- In charge of:
    - encapsulation of data
    - flow control
    - error detection and correction, etc.
- Examples: Ethernet, 802.11 (Wi-Fi), ARP, RAPR, NDP, PPP, etc.

**_Layer 2 - Internet_**

The internet layer exchanges **datagrams** across network boundaries.

It provides a uniform networking interface that hides the actual topology (layout) of the underlying network
connections.

It is therefore also the layer that establishes inter-networking. Indeed, it defines and establishes the **Internet**.

This layer defines the addressing and routing structures used for the TCP/IP protocol suite.

The primary protocol in this scope is the **Internet Protocol**, which defines **IP addresses**.

Its function in routing is to transport datagrams to the next host, functioning as an IP router, that has the
connectivity to a network closer to the final data destination.

![Internet](Internet.PNG)

**_Layer 3 - Transport_**

The transport layer performs host-to-host communications on either the local network or remote networks separated by
**routers**.

It provides a channel for the communication needs of applications.

There are two main protocols in the transport layer:

- **Transmission Control Protocol (TCP)** provides flow-control, connection establishment, and reliable transmission of
  data:
    - Reliable - guarantees data delivery as sent, without any losses
    - Connection between 2 points needs to be created before data is sent and should be shut down in the end
    - Works as a streaming interface - stream of bytes flowing through the dedicated connection

- **User Datagram Protocol (UDP)** provides an unreliable connectionless datagram service:
    - Connectionless
    - Best effort - unreliable
    - Messages can be lost, duplicated or re-ordered
    - Based on a unit called _Datagram_ which is limited in size
    - Allows multicasting and broadcasting

![UDP_TCP](UDP_TCP.PNG)

The main differences between TCP and UDP:

![TCP_vs_UDP](TCP_vs_UDP.PNG)

**_Layer 4 - Application_**

The application layer is the scope within which **applications**, or **processes**, create user data and communicate
this data to other applications on another or the same host.

The applications make use of the services provided by the underlying lower layers, especially the **transport layer**
which provides reliable or unreliable pipes to other processes.

The communications partners are characterized by the application architecture, such as the client–server model and
peer-to-peer networking. This is the layer in which all application protocols, such as SMTP, FTP, SSH, HTTP, operate.

Processes are addressed via ports which essentially represent services.

Encapsulation of application data descending through the layers:

![TCP_IP_DataFlow](TCP_IP_DataFlow.PNG)

---

## 03. HTTP Basics

The **Hypertext Transfer Protocol (HTTP)** is an **application layer** protocol in the **Internet protocol suite model**
for distributed, collaborative, hypermedia information systems.

HTTP is the foundation of data communication for the **World Wide Web**, where hypertext documents include hyperlinks to
other resources that the user can easily access, for example, by a mouse click or by tapping the screen in a web
browser.

HTTP functions as a **request–response protocol** in the client–server model.

A **web browser**, for example, may be the client whereas a process, named **web server**, running on a computer hosting
one or more websites may be the **server**. The **client** submits an HTTP request message to the **server**.

The **server**, which provides resources such as HTML files and other content or performs other functions on behalf of
the client, returns a **response** message to the **client**.

The **response** contains completion status information about the request and may also contain requested content in its
message body.

![HTTP_flow](HTTP_flow.PNG)

An example HTTP **request**:

![HTTP_request](HTTP_request.PNG)

An example **response**:

![HTTP_response](HTTP_response.PNG)

The first digit of the **status code** defines its class:

- **1XX (informational)**: The request was received, continuing process.
- **2XX (successful)**: The request was successfully received, understood, and accepted.
- **3XX (redirection)**: Further action needs to be taken in order to complete the request.
- **4XX (client error)**: The request contains bad syntax or cannot be fulfilled.
- **5XX (server error)**: The server failed to fulfill an apparently valid request.

**_HTTP Headers_**

HTTP header fields are a list of strings sent and received by both the client program and server on every HTTP request
and response.

HTTP headers let the client and the server pass additional information with an HTTP request or response.

An HTTP header consists of its case-insensitive name followed by a colon (:), then by its value. Whitespace before the
value is ignored.

These headers are usually invisible to the end-user and are only processed or logged by the server and client
applications.

They define how information sent/received through the connection is encoded (as in Content-Encoding), the session
verification and identification of the client (as in browser cookies, IP address, user-agent) or their anonymity thereof
(VPN or proxy masking, user-agent spoofing), how the server should handle data (as in Do-Not-Track), the age (the time
it has resided in a shared cache) of the document being downloaded, amongst others.

**_HTTP Methods_**

HTTP defines **methods** (sometimes referred to as **verbs**) to indicate the desired action to be performed on the
identified resource.

**GET**

GET method requests that the target resource transfer a representation of its state.

GET requests should only retrieve data and should have no other effect.

For retrieving resources without making changes, **GET** is preferred over **POST**, as they can be addressed through a
URL.

This enables bookmarking and sharing and makes GET responses eligible for caching, which can save bandwidth.

**POST**

The POST method requests that the target resource process the representation enclosed in the request according to the
semantics of the target resource.

For example, it is used for posting a message to an Internet forum, subscribing to a mailing list, or completing an
online shopping transaction.

**PUT**

The PUT method requests that the target resource create or update its state with the state defined by the representation
enclosed in the request.

A distinction from **POST** is that the client specifies the target location on the server.

**DELETE**

The DELETE method requests that the target resource delete its state.

**_HTTP versions_**

![HTTP_versions](HTTP_versions.PNG)

HTTP **resources** are identified and located on the network by **Uniform Resource Locators (URLs)**, using the
**Uniform Resource Identifiers (URI's)** schemes `http` and `https`.

URIs are encoded as `hyperlinks` in HTML documents to form interlinked hypertext documents.

- In `HTTP/1.0`, a **separate** TCP connection to the same server is made for every resource request
- In `HTTP/1.1`,
    - a TCP connection can be reused to make multiple resource requests (i.e., of HTML pages, frames, images, scripts,
      stylesheets, etc.)
    - communications therefore experience less latency as the establishment of TCP connections presents considerable
      overhead, especially under high traffic conditions
- In `HTTP/2`,
    - use a **compressed binary** representation of metadata (HTTP headers) instead of a **textual** one, so that
      headers require much less space
    - use a single TCP/IP (usually encrypted) connection per accessed server domain instead of 2 to 8 TCP/IP connections
    - use one or more **bidirectional** streams per TCP/IP connection in which HTTP requests and responses are broken
      down and transmitted in small packets to almost solve the problem of the HOLB (head-of-line blocking)
    - add a **push** capability to allow server application to send data to clients whenever new data is available
      (without forcing clients to request periodically new data to server by using polling methods)
- In `HTTP/3`,
    - revision of previous `HTTP/2` in order to use `QUIC + UDP` transport protocols instead of TCP
    - Before this version, TCP/IP connections were used, now only the IP layer (which UDP, like TCP, builds on) also is
      used to slightly improve the average speed of communications and to avoid the occasional (very rare) problem of
      TCP **connection congestion** that can temporarily block or slow down the data flow of all its streams (another
      form of HOLB or "head of line blocking")

![HTTP_versions_difference](HTTP_versions_difference.PNG)

However, one can summarize the main difference between `HTTP/1.1` and `HTTP/2` is that `HTTP/2` supports
**multiplexing**:

![Multiplexing](Multiplexing.PNG)

Similarly, the main difference between `HTTP/2` and `HTTP/3` is use of UDP and IP instead of TCP and the way security is
handled:

![HTTP2_vs_HTTP3](HTTP2_vs_HTTP3.PNG)

### What is HTTPS and how is it different from HTTP?

![HTTPS](HTTPS.PNG)

Main difference between `HTTP` and `HTTPS`:

![HTTP_vs_HTTPS](HTTP_vs_HTTPS.PNG)

To show it in a diagram:

![Detailed_HTTP_vs_HTTPS](Detailed_HTTP_vs_HTTPS.PNG)

Short and sweet summary of differences:

![Short_HTTP_vs_HTTPS](Short_HTTP_vs_HTTPS.PNG)

### HTTP Server

We are going to build an HTTP server which will be having two end points:

- "`/task`": using HTTP **POST**
- "`/status`": using HTTP **GET**

End point `/task` will accept a list or array of numbers and return the product.

End point `/status` will ensure the server is still alive.

We will also use some **custom header keys** in both HTTP **Request** and HTTP **Response**.

Complete code for our HTTP server:

```java
package com.backstreetbrogrammer.http.httpserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.Executors;

public class GuidemyWebServer {

    private static final String TASK_ENDPOINT = "/task";
    private static final String STATUS_ENDPOINT = "/status";
    private static final String CUSTOM_HEADER_KEY1 = "Rishi-Test";
    private static final String CUSTOM_HEADER_KEY2 = "Rishi-Debug";
    private static final String CUSTOM_HEADER_RESPONSE_KEY = "Rishi-Debug-Info";

    private final int port;

    public GuidemyWebServer(final int port) {
        this.port = port;
    }

    public static void main(final String[] args) {
        int serverPort = 8080;
        if (args.length == 1) {
            serverPort = Integer.parseInt(args[0]);
        }

        final GuidemyWebServer webServer = new GuidemyWebServer(serverPort);
        webServer.startServer();

        System.out.printf("Server is listening on port: %d%n", serverPort);
    }

    public void startServer() {
        final HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        final HttpContext statusContext = server.createContext(STATUS_ENDPOINT);
        final HttpContext taskContext = server.createContext(TASK_ENDPOINT);

        statusContext.setHandler(this::handleStatusCheckRequest);
        taskContext.setHandler(this::handleTaskRequest);

        server.setExecutor(Executors.newFixedThreadPool(4));
        server.start();
    }

    private void handleTaskRequest(final HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase(HttpMethod.POST.getHttpMethod())) {
            exchange.close();
            return;
        }

        final Headers headers = exchange.getRequestHeaders();
        if (headers.containsKey(CUSTOM_HEADER_KEY1)
                && headers.get(CUSTOM_HEADER_KEY1).get(0).equalsIgnoreCase("true")) {
            final String dummyResponse = "dummy\n";
            sendResponse(dummyResponse.getBytes(StandardCharsets.UTF_8), exchange);
            return;
        }

        boolean isDebugMode = false;
        if (headers.containsKey(CUSTOM_HEADER_KEY2)
                && headers.get(CUSTOM_HEADER_KEY2).get(0).equalsIgnoreCase("true")) {
            isDebugMode = true;
        }

        final Instant start = Instant.now();
        final byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        final byte[] responseBytes = calculateResponse(requestBytes);

        if (isDebugMode) {
            final String debugMsg = String.format("Operation took %d ns", Duration.between(start, Instant.now()).toNanos());
            exchange.getResponseHeaders().put(CUSTOM_HEADER_RESPONSE_KEY, Collections.singletonList(debugMsg));
        }

        sendResponse(responseBytes, exchange);
    }

    private byte[] calculateResponse(final byte[] requestBytes) {
        final String bodyString = new String(requestBytes);
        final String[] stringNumbers = bodyString.split(",");
        BigInteger result = BigInteger.ONE;
        for (final String number : stringNumbers) {
            final BigInteger bigInteger = new BigInteger(number);
            result = result.multiply(bigInteger);
        }

        return String.format("Result of the multiplication is %s%n", result).getBytes(StandardCharsets.UTF_8);
    }

    private void handleStatusCheckRequest(final HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase(HttpMethod.GET.getHttpMethod())) {
            exchange.close();
            return;
        }

        final String responseMessage = "Server is alive";
        sendResponse(responseMessage.getBytes(StandardCharsets.UTF_8), exchange);
    }

    private void sendResponse(final byte[] responseBytes, final HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, responseBytes.length);
        try (final OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
            os.flush();
        }
    }
}
```

Helper `enum` used in the server code:

```java
public enum HttpMethod {

    GET("GET"),
    POST("POST");

    private String httpMethod;

    HttpMethod(final String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getHttpMethod() {
        return httpMethod;
    }
}
```

To test our server, we will use `cURL` on command line or GIT Bash.

**Test Case 1: Running GET request with invalid end point**

`curl --request GET -v localhost:8080/anything`

Output:

![testcase1_server](testcase1_server.PNG)

**Test Case 2: Running GET status request**

`curl --request GET -v localhost:8080/status`

Output:

![testcase2_server](testcase2_server.PNG)

**Test Case 3: Running POST task request**

`curl --request POST -v --data '50,80,100' localhost:8080/task`

Output:

![testcase3_server](testcase3_server.PNG)

**Test Case 4: Running POST task request with custom test header**

`curl --request POST -v --header "Rishi-Test: true" --data '50,80,100' localhost:8080/task`

Output:

![testcase4_server](testcase4_server.PNG)

**Test Case 5: Running POST task request with custom debug header**

`curl --request POST -v --header "Rishi-Debug: true" --data '50,80,100' localhost:8080/task`

Output:

![testcase5_server](testcase5_server.PNG)

### HTTP Client

Java 11 supports HTTP client creation. It supports both HTTP 1.1 and HTTP 2.

Here is our `MyWebClient` class:

```java
package com.backstreetbrogrammer.part1.http.httpclient;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class MyWebClient {

    private final HttpClient client;

    public MyWebClient() {
        this.client = HttpClient.newBuilder()
                                .version(HttpClient.Version.HTTP_1_1)
                                .build();
    }

    public CompletableFuture<String> sendTask(final String url, final byte[] requestPayload) {
        final HttpRequest request = HttpRequest.newBuilder()
                                               .POST(HttpRequest.BodyPublishers.ofByteArray(requestPayload))
                                               .uri(URI.create(url))
                                               .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                     .thenApply(HttpResponse::body);
    }
}
```

Client is sending the HTTP POST request method **asynchronously** to the server and returning `HttpResponse` body
`String` wrapped inside `CompletableFuture<String>`.

Let's create a helper program to collect all the client requests and get all the results.

```java
package com.backstreetbrogrammer.part1.http.httpclient;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Accumulator {

    private final MyWebClient webClient;

    public Accumulator() {
        this.webClient = new MyWebClient();
    }

    public List<String> sendTasksToWorkers(final List<String> workersAddresses, final List<String> tasks) {
        final CompletableFuture<String>[] futures = new CompletableFuture[workersAddresses.size()];

        for (int i = 0; i < workersAddresses.size(); i++) {
            final String workerAddress = workersAddresses.get(i);
            final String task = tasks.get(i);

            final byte[] requestPayload = task.getBytes();
            futures[i] = webClient.sendTask(workerAddress, requestPayload);
        }

        return Stream.of(futures)
                     .map(CompletableFuture::join)
                     .collect(Collectors.toList());
    }
}
```

For the demo servers run, we will run two instances of `MyWebServer` running on ports `8081` and `8082`.

![GuidemyWebServer8081](GuidemyWebServer8081.PNG)

![GuidemyWebServer8082](GuidemyWebServer8082.PNG)

For the demo client run, we will use this `Main` class:

```java
package com.backstreetbrogrammer.part1.http.httpclient;

import java.util.List;

public class Main {

    private static final String WORKER_ADDRESS_1 = "http://localhost:8081/task";
    private static final String WORKER_ADDRESS_2 = "http://localhost:8082/task";

    public static void main(final String[] args) {
        final Accumulator accumulator = new Accumulator();
        final String task1 = "10,200";
        final String task2 = "123456789,100000000000000,700000002342343";

        final List<String> results = accumulator.sendTasksToWorkers(List.of(WORKER_ADDRESS_1, WORKER_ADDRESS_2),
                                                                    List.of(task1, task2));

        for (final String result : results) {
            System.out.println(result);
        }
    }
}
```

Here is what the output will look like:

![GuidemyWebClient](GuidemyWebClient.PNG)

---

## 04. TCP Client and Server

### Limitation of current server applications

Server applications generally handle concurrent user requests that are independent of each other, so it makes sense for
an application to handle a request by dedicating a thread to that request for its entire duration.

This **thread-per-request** style is easy to understand, easy to program, and easy to debug and profile because it uses
the platform's unit of concurrency to represent the application's unit of concurrency.

The scalability of server applications is governed by **_Little's Law_**, which relates **latency**, **concurrency**,
and **throughput**:

For a given request-processing duration (i.e., **latency**), the number of requests an application handles at the same
time (i.e., **concurrency**) must grow in proportion to the rate of arrival (i.e., **throughput**).

For example, suppose an application with an average **latency** of `50ms` achieves a **throughput** of
`200 requests per second` by processing `10 requests` **concurrently**.

```
1 request takes 50 ms
2 requests takes 50*2=100 ms
20 requests takes 50*20=1000 ms or 1 second

Thus, to increase throughput from 20 requests per second to 200 requests per second, we need to process 10 requests 
concurrently.
```

In order for that application to scale to a **throughput** of `2000 requests per second`, it will need to process
`100 requests` **concurrently**.

If each request is handled in a thread for the request's duration then, for the application to keep up, the number of
threads must grow as throughput grows.

Unfortunately, the number of available threads is limited because the JDK implements threads as wrappers around
operating system (OS) threads.

OS threads are costly, so we cannot have too many of them, which makes the implementation ill-suited to the
**thread-per-request** style.

If each request consumes a thread, and thus an OS thread, for its duration, then the number of threads often becomes the
limiting factor long before other resources, such as CPU or network connections, are exhausted.

The JDK's current implementation of threads caps the application's throughput to a level well below what the hardware
can support.

This happens even when threads are pooled, since pooling helps avoid the high cost of starting a new thread but does not
increase the total number of threads.

### Blocking, Non-Blocking, Asynchronous Server

An example of TCP client-server socket connection:

![SocketAPI](SocketAPI.PNG)

```java
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketAPIDemoServer {

    public static void main(final String[] args) throws IOException {
        final var serverSocket = new ServerSocket(8080);
        while (!serverSocket.isClosed()) {
            final var socket = serverSocket.accept(); // blocks and socket can never be null
            handle(socket);
        }
    }

    private static void handle(final Socket socket) throws IOException {
        System.out.printf("Connected to %s%n", socket);
        try (
                socket;
                final var in = socket.getInputStream();
                final var out = socket.getOutputStream()
        ) {
            // default buffer size is 8192
            // in.transferTo(out);

            int data;
            while ((data = in.read()) != -1) { // read one byte at a time and -1 means EOF
                out.write(transformAndEcho(data));
            }
        } finally {
            System.out.printf("Disconnected from %s%n", socket);
        }
    }

    private static int transformAndEcho(final int data) {
        return Character.isLetter(data) ? data ^ ' ' : data;
    }
}
```

**Designing a simple TCP-based Order Management System (OMS) server**

Suppose, we have an Order Management System (OMS) which is working as a TCP server and receiving stock trading orders
from various clients.

Once the order is received, following processing is done on the `Order` object before it is sent down to algorithmic
trading engine or directly to exchange (DMA):

- Validate the order client's wallet if enough funds
- Enrich the order with latest market data (best bid / best ask)
- Update the latest order state to persistence (log or database)

**_Single Threaded Blocking OMS_**

```java
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class SingleThreadedBlockingOMS {

    private static final AtomicInteger clientCounter = new AtomicInteger();

    public static void main(final String[] args) throws IOException {
        final var port = 8080;
        final var serverSocket = new ServerSocket(port);
        System.out.printf("Listening on port %d%n", port);
        while (!serverSocket.isClosed()) {
            final var socket = serverSocket.accept(); // blocks and socket can never be null
            handle(socket);
        }
    }

    private static void handle(final Socket socket) {
        System.out.println("\n----------------------------");
        System.out.printf("Connected to Client-%d on socket=[%s]%n", clientCounter.addAndGet(1), socket);
        try (
                socket
        ) {
            final var start = Instant.now();
            final var request = new Request(socket);          // parse the request
            final var order = new Order(request);             // create an Order from the request

            order.validate(ClientWallet.validate(request))    // validate the order client's wallet if enough funds
                 .enrich(MarketData.enrich(request))          // enrich the order with latest market data
                 .persist(OrderStatePersist.persist(request)) // update the latest order state to persistence
                 .sendToDownstream();                         // send the order to downstream

            final var timeElapsed = (Duration.between(start, Instant.now()).toMillis());
            System.out.printf("%nOrder [%s] sent to downstream in [%d] ms%n%n", order, timeElapsed);

        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.printf("Disconnected from Client-%d on socket=[%s]%n", clientCounter.get(), socket);
            System.out.println("----------------------------\n");
        }
    }

}
```

This server is purely sequential and uses a single thread that does everything.

The thread is first blocked on `accept()`, listening for connections.

After a connection is established, that thread performs all the handling work before it can go back to listen and wait
for more connections.

In the example code above, each of the following takes around `1 second`:

- parse the request
- create an Order from the request
- validate the order client's wallet if enough funds
- enrich the order with latest market data
- update the latest order state to persistence
- send the order to downstream

Therefore, it will take around `6 seconds` to complete **one** request and send the order to downstream (if no error).

This also means that if **three** requests arrive at the same time, it will take `6 + 6 + 6 = 18 seconds` to fulfill
them at `6 seconds` per request sequentially.

We will use the following `SocketClient` class to mimic client requests:

```java
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class SocketClient {

    public static void main(final String[] args) throws IOException, InterruptedException {
        if (args == null || args.length != 1) {
            throw new IllegalArgumentException("Specify the number of sockets to create and connect");
        }

        final var numberOfSocketsToCreate = Integer.parseInt(args[0]);
        if (numberOfSocketsToCreate <= 0) {
            throw new IllegalArgumentException("Number of sockets connection should be greater than 0");
        }

        final var sockets = new Socket[numberOfSocketsToCreate];

        // connect
        for (var i = 0; i < sockets.length; i++) {
            sockets[i] = new Socket("localhost", 8080);
            System.out.printf("Connected: [%s]%n", sockets[i]);
        }

        TimeUnit.SECONDS.sleep(1L);

        // disconnect
        for (final var socket : sockets) {
            if (socket != null) {
                socket.close();
                System.out.printf("Disconnected: [%s]%n", socket);
            }
        }
    }

}
```

**_Thread per client OMS_**

```java
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPerClientOMS {

    private static final AtomicInteger clientCounter = new AtomicInteger();

    public static void main(final String[] args) throws IOException {
        final var port = 8080;
        final var serverSocket = new ServerSocket(port);
        System.out.printf("Listening on port %d%n", port);
        while (!serverSocket.isClosed()) {
            final var socket = serverSocket.accept(); // blocks and socket can never be null
            new Thread(() -> handle(socket, clientCounter.addAndGet(1))).start();    // create a new thread to handle request
        }
    }

    private static void handle(final Socket socket, final int clientNo) {
        System.out.println("\n----------------------------");
        System.out.printf("Connected to Client-%d on socket=[%s]%n", clientNo, socket);
        try (
                socket
        ) {
            final var start = Instant.now();
            final var request = new Request(socket);          // parse the request
            final var order = new Order(request);             // create an Order from the request

            order.validate(ClientWallet.validate(request))    // validate the order client's wallet if enough funds
                 .enrich(MarketData.enrich(request))          // enrich the order with latest market data
                 .persist(OrderStatePersist.persist(request)) // update the latest order state to persistence
                 .sendToDownstream();                         // send the order to downstream

            final var timeElapsed = (Duration.between(start, Instant.now()).toMillis());
            System.out.printf("%nOrder [%s] sent to downstream in [%d] ms%n%n", order, timeElapsed);

        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.printf("Disconnected from Client-%d on socket=[%s]%n", clientNo, socket);
            System.out.println("----------------------------\n");
        }
    }

}
```

We can do a quick optimization to handle multiple connections in parallel by creating a new **Thread** for each client
handling.

```
        while (!serverSocket.isClosed()) {
            final Socket socket = serverSocket.accept(); // blocks and socket can never be null
            new Thread(() -> handle(socket)).start();    // create a new thread to handle request
        }
```

The connection listening thread that calls `accept()` creates and starts a new **Thread** to handle the connection and
quickly goes back to accepting more connections.

In this case, the handling of each request is completely independent, and the time to process a request and send the
order to downstream goes down from `6 + 6 + 6 = 18 seconds` to `6 seconds`.

However, there are few caveats:

- Within a single request in the `handle()` method => parsing, validating, persisting and sending the order to
  downstream is all done in the same thread.
- Threads are scarce, and there is a limitation imposed on the maximum number of threads which can be created in an OS.
  Thus, the design doesn't scale well when the number of client connections will increase.

**_Thread per client and Thread per order-parsing OMS_**

```java
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPerOrderHandlerOMS {

    private static final AtomicInteger clientCounter = new AtomicInteger();

    public static void main(final String[] args) throws IOException {
        final var port = 8080;
        final var serverSocket = new ServerSocket(port);
        System.out.printf("Listening on port %d%n", port);
        while (!serverSocket.isClosed()) {
            final var socket = serverSocket.accept(); // blocks and socket can never be null
            new Thread(() -> handle(socket, clientCounter.addAndGet(1))).start();    // create a new thread to handle request
        }
    }

    private static void handle(final Socket socket, final int clientNo) {
        System.out.println("\n----------------------------");
        System.out.printf("Connected to Client-%d on socket=[%s]%n", clientNo, socket);
        try (
                socket
        ) {
            final var start = Instant.now();
            final var request = new Request(socket);          // parse the request
            final var order = new Order(request);             // create an Order from the request

            final var threads = getOrderParsingThreads(order, request);
            for (final var t : threads) {
                t.start();
            }
            for (final var t : threads) {
                t.join();
            }

            // send the order to downstream
            order.sendToDownstream();

            final var timeElapsed = (Duration.between(start, Instant.now()).toMillis());
            System.out.printf("%nOrder [%s] sent to downstream in [%d] ms%n%n", order, timeElapsed);

        } catch (final IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.printf("Disconnected from Client-%d on socket=[%s]%n", clientNo, socket);
            System.out.println("----------------------------\n");
        }
    }

    private static List<Thread> getOrderParsingThreads(final Order order, final Request request) {
        // validate the order client's wallet if enough funds
        final var t1 = new Thread(() -> order.validate(ClientWallet.validate(request)));

        // enrich the order with latest market data
        final var t2 = new Thread(() -> order.enrich(MarketData.enrich(request)));

        // update the latest order state to persistence
        final var t3 = new Thread(() -> order.persist(OrderStatePersist.persist(request)));

        return List.of(t1, t2, t3);
    }

}
```

We can do a small optimization here that instead of creating `4n` threads, we can use the connection handling thread to
make it `3n` threads per request.

This design is slightly better - `3n` threads instead of `4n` but it's still unnecessarily wasteful of threads.

And, the time to process a request is still same: `1 + 1 + max(1, 1, 1) + 1 = 4 seconds`.

A better approach is to **pool** generic worker threads together and rely on them as tasks occur.

**Thread pools** were introduced because threads are expensive; when threads become cheaper, the need for pooling
decreases.

**_Thread Pool Based OMS_**

```java
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolBasedOMS {

    private static final AtomicInteger clientCounter = new AtomicInteger();
    private static final ExecutorService connectionHandlerPool = Executors.newFixedThreadPool(4);
    private static final ExecutorService orderHandlerPool = Executors.newFixedThreadPool(12);

    public static void main(final String[] args) throws IOException {
        final var port = 8080;
        final var serverSocket = new ServerSocket(port);
        System.out.printf("Listening on port %d%n", port);

        try {
            while (!serverSocket.isClosed()) {
                final var socket = serverSocket.accept(); // blocks and socket can never be null
                connectionHandlerPool.execute(() -> handle(socket, clientCounter.addAndGet(1)));
            }
        } finally {
            connectionHandlerPool.close();
            orderHandlerPool.close();
        }
    }

    private static void handle(final Socket socket, final int clientNo) {
        System.out.println("\n----------------------------");
        System.out.printf("Connected to Client-%d on socket=[%s]%n", clientNo, socket);
        try (
                socket
        ) {
            final var start = Instant.now();
            final var request = new Request(socket);          // parse the request
            final var order = new Order(request);             // create an Order from the request

            final var latch = new CountDownLatch(3);

            orderHandlerPool.execute(() -> {
                order.validate(ClientWallet.validate(request));
                latch.countDown();
            });

            orderHandlerPool.execute(() -> {
                order.enrich(MarketData.enrich(request));
                latch.countDown();
            });

            orderHandlerPool.execute(() -> {
                order.persist(OrderStatePersist.persist(request));
                latch.countDown();
            });

            latch.await();

            // send the order to downstream
            order.sendToDownstream();

            final var timeElapsed = (Duration.between(start, Instant.now()).toMillis());
            System.out.printf("%nOrder [%s] sent to downstream in [%d] ms%n%n", order, timeElapsed);

        } catch (final IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.printf("Disconnected from Client-%d on socket=[%s]%n", clientNo, socket);
            System.out.println("----------------------------\n");
        }
    }

}
```

The **Executor Pattern** aims to fix the above-mentioned issues:

- by creating pools of ready-to-use threads
- passing tasks to this pool of threads that will execute it

![Executor Service](ExecutorService.PNG)

The threads in this pool will be kept alive as long as this pool is alive.

It means that the single thread will execute the submitted task => once the task finishes, the thread will return to the
pool and wait for a new task to be submitted for execution.

As compared to `Runnable` pattern, `Executor` pattern does NOT create a new thread.

However, the behavior is the same: both calls return immediately, and the task is executed in **another** thread.

We will create two thread pools in our OMS:

```
    private static final ExecutorService connectionHandlerPool = Executors.newFixedThreadPool(4);
    private static final ExecutorService orderHandlerPool = Executors.newFixedThreadPool(12);
```

Our client handler code will look like this:

```
        try {
            while (!serverSocket.isClosed()) {
                final var socket = serverSocket.accept(); // blocks and socket can never be null
                connectionHandlerPool.execute(() -> handle(socket));
            }
        } finally {
            connectionHandlerPool.close();
            orderHandlerPool.close();
        }
```

And, the order handler code will be changed to use the second pool:

```
            final var request = new Request(socket);          // parse the request
            final var order = new Order(request);             // create an Order from the request

            final var latch = new CountDownLatch(3);

            orderHandlerPool.execute(() -> {
                order.validate(ClientWallet.validate(request));
                latch.countDown();
            });

            orderHandlerPool.execute(() -> {
                order.enrich(MarketData.enrich(request));
                latch.countDown();
            });

            orderHandlerPool.execute(() -> {
                order.persist(OrderStatePersist.persist(request));
                latch.countDown();
            });

            latch.await();

            // send the order to downstream
            order.sendToDownstream();
```

This is a better design as now we have a limited number of threads as guaranteed in the thread pool size.

However, the time to process a request is still same: `1 + 1 + max(1, 1, 1) + 1 = 4 seconds`.

Few of the caveats here are:

- order is a shared object amongst all the threads and needs to be synchronized
- code is more complex and using helper objects like `CountDownLatch`

The OMS servers we have created so far are very imperative in style:

- Threads do things
- they act on a shared object, and
- they modify it

A better approach is to use **futures**.

**_Futures Based OMS_**

```java
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class FuturesBasedOMS {

    private static final AtomicInteger clientCounter = new AtomicInteger();
    private static final ExecutorService connectionHandlerPool = Executors.newFixedThreadPool(4);
    private static final ExecutorService orderHandlerPool = Executors.newFixedThreadPool(12);

    public static void main(final String[] args) throws IOException {
        final var port = 8080;
        final var serverSocket = new ServerSocket(port);
        System.out.printf("Listening on port %d%n", port);

        try {
            while (!serverSocket.isClosed()) {
                final var socket = serverSocket.accept(); // blocks and socket can never be null
                connectionHandlerPool.execute(() -> handle(socket, clientCounter.addAndGet(1)));
            }
        } finally {
            connectionHandlerPool.close();
            orderHandlerPool.close();
        }
    }

    private static void handle(final Socket socket, final int clientNo) {
        System.out.println("\n----------------------------");
        System.out.printf("Connected to Client-%d on socket=[%s]%n", clientNo, socket);
        try (
                socket
        ) {
            final var start = Instant.now();
            final var request = new Request(socket);          // parse the request
            final var order = new Order(request);             // create an Order from the request

            final CompletableFuture<Void> future
                    = CompletableFuture.runAsync(() -> order.validate(ClientWallet.validate(request)), orderHandlerPool)
                                       .thenRunAsync(() -> order.enrich(MarketData.enrich(request)), orderHandlerPool)
                                       .thenRunAsync(() -> order.persist(OrderStatePersist.persist(request)), orderHandlerPool);

            // send the order to downstream
            order.sendToDownstream();

            final var timeElapsed = (Duration.between(start, Instant.now()).toMillis());
            System.out.printf("%nOrder [%s] sent to downstream in [%d] ms%n%n", order, timeElapsed);

        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.printf("Disconnected from Client-%d on socket=[%s]%n", clientNo, socket);
            System.out.println("----------------------------\n");
        }
    }

}
```

The main difference in the code is that we use `CompletableFuture.runAsync()` to run the tasks asynchronously.

The `thenRunAsync()` method is used to execute the next task after the previous one completes.

The order of execution is the same as the order of method calls.

However, there is one drawback:

- Order must be **thread-safe** again and needs to be constructed first, before the validation, enrichment and
  persistence tasks are started.

This can be avoided by bringing back **futures** but having the futures run by **virtual threads**.

**_Virtual Threads with Futures OMS_**

```java
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class VirtualThreadWithFuturesOMS {

    private static final AtomicInteger clientCounter = new AtomicInteger();

    public static void main(final String[] args) throws IOException {
        final var port = 8080;
        final var serverSocket = new ServerSocket(port);
        System.out.printf("Listening on port %d%n", port);
        while (!serverSocket.isClosed()) {
            final var socket = serverSocket.accept(); // blocks and socket can never be null
            Thread.startVirtualThread(
                    () -> handle(socket, clientCounter.addAndGet(1))); // create a new virtual thread to handle request
        }
    }

    private static void handle(final Socket socket, final int clientNo) {
        System.out.println("\n----------------------------");
        System.out.printf("Connected to Client-%d on socket=[%s]%n", clientNo, socket);
        try (
                socket
        ) {
            final var start = Instant.now();
            final var request = new Request(socket);

            final CompletableFuture<Void> future
                    = CompletableFuture.runAsync(() -> order.validate(ClientWallet.validate(request)))
                                       .thenRunAsync(() -> order.enrich(MarketData.enrich(request)))
                                       .thenRunAsync(() -> order.persist(OrderStatePersist.persist(request)));

            // send the order to downstream
            order.sendToDownstream();

            final var timeElapsed = (Duration.between(start, Instant.now()).toMillis());
            System.out.printf("%nOrder [%s] sent to downstream in [%d] ms%n%n", order, timeElapsed);

        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.printf("Disconnected from Client-%d on socket=[%s]%n", clientNo, socket);
            System.out.println("----------------------------\n");
        }
    }

}
```

Time taken to process a request is: `1 + 1 + max(1, 1, 1) + 1 = 4 seconds`.

The main difference with using traditional threads is that virtual threads do NOT entail any **OS-level blocking**.

When a virtual thread invokes `join` on a thread that is still running, it does not block the underlying OS thread,
which continues to run other virtual threads.

In this case, the only actual processing that the connection-handling thread performs is the building of a base order.

Virtual threads help to improve the **throughput** of typical server applications precisely because such applications
consist of a great number of concurrent tasks that spend much of their time **waiting**.

However, there is one caveat here:

- Callbacks are notoriously hard to write and even harder to debug.

In the simple callback illustration above, `thenAccept` calls are nested **three levels** deep.

**_Futures with composition OMS_**

```java
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class FuturesWithCompositionOMS {

    private static final AtomicInteger clientCounter = new AtomicInteger();
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(12);

    public static void main(final String[] args) throws IOException {
        final var port = 8080;
        final var serverSocket = new ServerSocket(port);
        System.out.printf("Listening on port %d%n", port);

        try {
            while (!serverSocket.isClosed()) {
                final var socket = serverSocket.accept(); // blocks and socket can never be null
                threadPool.execute(() -> handle(socket, clientCounter.addAndGet(1)));
            }
        } finally {
            threadPool.close();
        }
    }

    private static void handle(final Socket socket, final int clientNo) {
        System.out.println("\n----------------------------");
        System.out.printf("Connected to Client-%d on socket=[%s]%n", clientNo, socket);
        try (
                socket
        ) {
            final var start = Instant.now();
            final var request = new Request(socket);

            final CompletableFuture<MarketData> cfReuters = CompletableFuture.supplyAsync(fetchMarketDataReuters);
            final CompletableFuture<MarketData> cfBloomberg = CompletableFuture.supplyAsync(fetchMarketDataBloomberg);
            final CompletableFuture<MarketData> cfExegy = CompletableFuture.supplyAsync(fetchMarketDataExegy);

            CompletableFuture.allOf(cfReuters, cfBloomberg, cfExegy) // CompletableFuture<Void>
                             .thenAccept(v -> {
                                 try {
                                     final MarketData bestMarketData = Stream.of(cfReuters, cfBloomberg, cfExegy)  // Stream<CompletableFuture<MarketData>>
                                                                             .map(CompletableFuture::join)         // Stream<MarketData>
                                                                             .min(comparing(MarketData::getPrice)) // Optional<MarketData>
                                                                             .orElseThrow();
                                     System.out.printf("Best price [CF ] = %s%n", bestMarketData);
                                 } catch (final Exception e) {
                                     System.err.printf("Error: %s%n", e.getMessage());
                                 }
                             }).join();

            final var timeElapsed = (Duration.between(start, Instant.now()).toMillis());
            System.out.printf("%nOrder [%s] sent to downstream in [%d] ms%n%n", order, timeElapsed);

        } catch (final IOException e) {
            throw new RuntimeException(e);
        } finally {
            System.out.printf("Disconnected from Client-%d on socket=[%s]%n", clientNo, socket);
            System.out.println("----------------------------\n");
        }
    }

}
```
