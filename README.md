This repo provides samples of using [dxFeed Java API](https://docs.dxfeed.com/dxfeed/api/overview-summary.html).

## Table of Contents

- [Documentation](#documentation)
- [Usage](#usage)
    * [How to connect to QD endpoint](#how-to-connect-to-QD-endpoint)
    * [How to connect to dxLink](#how-to-connect-to-dxlink)
- [Samples](#samples)

## Documentation

Find useful information in our self-service dxFeed Knowledge Base:

- [dxFeed Knowledge Base](https://kb.dxfeed.com/index.html?lang=en)
    * [Getting Started](https://kb.dxfeed.com/en/getting-started.html)
    * [Troubleshooting](https://kb.dxfeed.com/en/troubleshooting-guidelines.html)
    * [Market Events](https://kb.dxfeed.com/en/data-model/dxfeed-api-market-events.html)
    * [Event Delivery contracts](https://kb.dxfeed.com/en/data-model/model-of-event-publishing.html#event-delivery-contracts)
    * [dxFeed API Event classes](https://kb.dxfeed.com/en/data-model/model-of-event-publishing.html#dxfeed-api-event-classes)
    * [Exchange Codes](https://kb.dxfeed.com/en/data-model/exchange-codes.html)
    * [Order Sources](https://kb.dxfeed.com/en/data-model/qd-model-of-market-events.html#order-x)
    * [Order Book reconstruction](https://kb.dxfeed.com/en/data-model/dxfeed-order-book/order-book-reconstruction.html)
    * [Symbology Guide](https://kb.dxfeed.com/en/data-model/symbology-guide.html)

## Usage
### How to connect to QD endpoint
```java
// For token-based authorization, use the following address format:
// "demo.dxfeed.com:7300[login=entitle:token]"
val endpoint = DXEndpoint.newBuilder().build()
val subscription = endpoint.feed.createSubscription(Quote::class.java)
subscription?.addEventListener {
    it.forEach { event -> println(event) }
}
subscription?.addSymbols("AAPL")
endpoint.connect("demo.dxfeed.com:7300")
```

<details>
<summary>Output</summary>
<br>

```
I 231130 124734.411 [main] QD - Using QDS-3.325+file-UNKNOWN, (C) Devexperts
I 231130 124734.415 [main] QD - Using scheme com.dxfeed.api.impl.DXFeedScheme slfwemJduh1J7ibvy9oo8DABTNhNALFQfw0KmE40CMI
I 231130 124734.418 [main] MARS - Started time synchronization tracker using multicast 239.192.51.45:5145 with dPyAu
I 231130 124734.422 [main] MARS - Started JVM self-monitoring
I 231130 124734.423 [main] QD - monitoring with collectors [Ticker, Stream, History]
I 231130 124734.424 [main] QD - monitoring DXEndpoint with dxfeed.address=demo.dxfeed.com:7300
I 231130 124734.425 [main] ClientSocket-Distributor - Starting ClientSocketConnector to demo.dxfeed.com:7300
I 231130 124734.425 [demo.dxfeed.com:7300-Reader] ClientSocketConnector - Resolving IPs for demo.dxfeed.com
I 231130 124734.427 [demo.dxfeed.com:7300-Reader] ClientSocketConnector - Connecting to 208.93.103.170:7300
I 231130 124734.530 [demo.dxfeed.com:7300-Reader] ClientSocketConnector - Connected to 208.93.103.170:7300
D 231130 124734.634 [demo.dxfeed.com:7300-Reader] QD - Distributor received protocol descriptor multiplexor@fFLro [type=qtp, version=QDS-3.319, opt=hs, mars.root=mdd.demo-amazon.multiplexor-demo1] sending [TICKER, STREAM, HISTORY, DATA] from 208.93.103.170
Quote{AAPL, eventTime=0, time=20231130-123206.000, timeNanoPart=0, sequence=0, bidTime=20231130-123206.000, bidExchange=P, bidPrice=189.36, bidSize=3.0, askTime=20231130-123129.000, askExchange=P, askPrice=189.53, askSize=10.0}
```

</details>

### How to connect to dxLink
```java
// The experimental property must be enabled.
System.setProperty("dxfeed.experimental.dxlink.enable", "true")
// Set scheme for dxLink.
System.setProperty("scheme", "ext:resource:dxlink.xml")
val endpoint = DXEndpoint.newBuilder().build()
val subscription = endpoint.feed.createSubscription(Quote::class.java)
subscription?.addEventListener {
    it.forEach { event -> println(event) }

}
subscription?.addSymbols("AAPL")
endpoint.connect("dxlink:wss://demo.dxfeed.com/dxlink-ws")
```
<details>
<summary>Output</summary>
<br>

```
I 231130 124929.817 [main] QD - Using QDS-3.325+file-UNKNOWN, (C) Devexperts
I 231130 124929.821 [main] QD - Using scheme com.dxfeed.api.impl.DXFeedScheme slfwemJduh1J7ibvy9oo8DABTNhNALFQfw0KmE40CMI
I 231130 124929.824 [main] MARS - Started time synchronization tracker using multicast 239.192.51.45:5145 with sWipb
I 231130 124929.828 [main] MARS - Started JVM self-monitoring
I 231130 124929.828 [main] QD - monitoring with collectors [Ticker, Stream, History]
I 231130 124929.829 [main] QD - monitoring DXEndpoint with dxfeed.address=dxlink:wss://demo.dxfeed.com/dxlink-ws
I 231130 124929.831 [main] DxLinkClientWebSocket-Distributor - Starting DxLinkClientWebSocketConnector to wss://demo.dxfeed.com/dxlink-ws
SLF4J: Failed to load class "org.slf4j.impl.StaticLoggerBinder".
SLF4J: Defaulting to no-operation (NOP) logger implementation
SLF4J: See http://www.slf4j.org/codes.html#StaticLoggerBinder for further details.
I 231130 124929.831 [wss://demo.dxfeed.com/dxlink-ws-Writer] DxLinkClientWebSocket-Distributor - Connecting to wss://demo.dxfeed.com/dxlink-ws
I 231130 124930.153 [wss://demo.dxfeed.com/dxlink-ws-Writer] DxLinkClientWebSocket-Distributor - Connected to wss://demo.dxfeed.com/dxlink-ws
D 231130 124931.269 [oioEventLoopGroup-2-1] QD - Distributor received protocol descriptor [type=dxlink, version=0.1-0.18-20231017-133150, keepaliveTimeout=120, acceptKeepaliveTimeout=5] sending [] from wss://demo.dxfeed.com/dxlink-ws
D 231130 124931.271 [oioEventLoopGroup-2-1] QD - Distributor received protocol descriptor [type=dxlink, version=0.1-0.18-20231017-133150, keepaliveTimeout=120, acceptKeepaliveTimeout=5, authentication=] sending [] from wss://demo.dxfeed.com/dxlink-ws
Quote{AAPL, eventTime=0, time=20231130-123421.000, timeNanoPart=0, sequence=0, bidTime=20231130-123421.000, bidExchange=Q, bidPrice=189.47, bidSize=4.0, askTime=20231130-123421.000, askExchange=P, askPrice=189.53, askSize=10.0}
```

</details>

To familiarize with the dxLink protocol, please click [here](https://demo.dxfeed.com/dxlink-ws/debug/#/protocol).

## Samples
* [Quote Table](https://github.com/dxFeed/dxfeed-android-samples/tree/default/quotetableapp) - simple application that can get current quotes
* [Latency Test App](https://github.com/dxFeed/dxfeed-android-samples/tree/default/latencytestapp) - application that displays the latency in receiving data. 
Based on the difference between system time and event time
* [Perf Test App](https://github.com/dxFeed/dxfeed-android-samples/tree/default/perftestapp) - application for evaluating device performance. Metric: number of events per second that pass through the QD.
It is important that the metric also depends on the bandwidth between device and multiplexor
