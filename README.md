Project distributed-mobile-memo
=======================

mobile shared memory on Android system

## 1. What is the project about?


## 2. System models (See package  `sharedmemory`)

### 2.1 Client/Server architecture

Each mobile phone holds a complete copy/replica of all data items and plays as a server. Clients issue their requests (read/write operations) to servers. All servers collectively process the requests and provide an illusion of a shared memory to the clients. In this project, the shared memory conforms to the atomic consistency. 

Clients should implement the `IRegisterClient` interface to issue the `get(key:Key)` and `put(key:Key, val:String)` operations.

### 2.2 Data model (See package `sharedmemory.data.kvs`)

We use the simple key-value data model. Each server replica holds a collection of data items. Each data item is associated with a key, which is simply a string. Values are strings as well. To fit in most algorithms for emulating a distributed shared memory, each key-value pair is further associated with a version. Specifically, in the algorithm for emulating atomic registers in this project, a version is comprised of an integer `process id` (of a client, as described above) and an integer local `seqno`. For each key, all versions are totally ordered. To summarize, each server replica holds a collection of `(key:Key, val:String, version:Version)` triples. 

`KVStoreInMemory` is an implementation of a database of key-value model. It maintains a `HashMap` of `(Key, VersionValue)` pairs, where `Key` is simply a String and `VersionValue` is a value associated with a `Version`. The class `KVPair` is a compressed representation of `(Key, VersionValue)` pairs.  

### 2.3 Communication (See package `sharedmemory.architecture.communication`)

Clients and servers communicate by message-passing. Each message carries the ip of its sender, thus called `IPMessage`. The receivers (both the clients and the servers in this project) of messages implement the `IReceiver` interface to handle with received messages. 

The core of the communication modular lies at the `CommmunicationService` class. It sends an `IPMessage` to a designated destination by calling `public void sendOneWay(final String receiver_ip, final IPMessage msg)`. To start to listen to coming messages, a process (as a client or as a server) can call `public void start2Listen(String server_ip)`. In addition, the `CommunicationService` class has implemented the `onReceive()` method of the `IReceiver` interface to propagate its received messages to `AtomicityMessagingService`. The latter class is related to the algorithm for emulating atomic registers and is described below.

## 3. Algorithm for emulating atomic registers (See package `sharedmemory.atomicity`)

### 3.1 Sketch of the ABD algorithm 

### 3.1 Messages (See package `sharedmemory.atomicity.message`)

### 3.2 

## 4. How to run the project?


