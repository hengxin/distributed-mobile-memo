Project distributed-mobile-memo
=======================

mobile shared memory on Android system

## What is the project about?


## Key components in the `"sharedmemory"` package

### Client/Server architecture

Each mobile phone holds a complete copy/replica of all data items and plays as a server. Clients issue their requests (read/write operations) to servers. All servers collectively process the requests and provide an illusion of a shared memory to the clients. In this project, the shared memory conforms to the atomic consistency. 

Clients should implement the `IRegisterClient` interface to issue the `get(key:Key)` and `put(key:Key, val:String)` operations.

### Data model (See package `"sharedmemory.data"`)

We use the simple key-value data model. Each data item is associated with a key, which is simply a string. Values are also strings. To fit in most algorithms for emulating a distributed shared memory, each key-value pair is further associated with a version. Specifically, in the algorithm for emulating atomic registers in this project, a version is comprised of an integer `process id` (of a client, as described above) and an integer local `seqno`. For each key, all versions are totally ordered.

Each replica 

### Communication (See package `"sharedmemory.communication"`)


### Server replicas


## How to run the project?



## Experimental Evaluation
