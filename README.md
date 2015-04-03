# Structuring Concurrent Applications

### Objective

<p>The Objective of this project was to successfully limit the number of simultaneous client connections to a server. The ability to limit the number of connections needed to be implemented with the use of a semaphore in combination with the Executor interface provided by the Java Concurrency Library. The application also required the use of Callable and Future in order to retrieve information about the running threads as well as interrupting them when necessary. These requirements aim to prevent concurrency issues such as deadlocks, starvation, and race conditions.</p>

### Architecture

<p>The application has three components — Client, ServerPool, and WorkerThread — which are used to implement the requirements listed in the objective.</p>

<p>The Client component is runs on the client-side and is responsible for connecting as well as sending string messages to the server using the Transmission Control Protocol. The Client runs in a terminal and provides an input prompt, allowing the user to input any string. The string is subsequently written to an output buffer stream and waits for a response from the server. If the server pool is full, the Client will notify the user that a response was not received. The Client will continue to run until the user types “stop” in the prompt. This will trigger a return and the client object will terminate along with the socket connection. If the Client is able to connect to the server, the Client will output the response and wait for more input from the user.</p>

<p>The ServerPool component runs on the server-side and is responsible for listening for incoming messages on a given port. (The default port is set to 9000). When a new client arrives, the ServerPool will use a WorkerThread which handles the incoming and outgoing messages. The WorkerThreads are created using an Executor object. This object calls the submit() method which runs the Callable thread and returns a Future object. The Future object is added to a list and then analyzed when the ServerPool stops running (i.e. upon exit). A semaphore is used to limit the number of client-server connections. Before a WorkerThread can be used, the ServerPool must acquire a semaphore key. Otherwise, it must wait. The WorkerThread is then responsible for releasing the semaphore upon completion of its task. Once the ServerPool is terminated, all connections are closed and the collected data is printed to the terminal.</p>

<p>The WorkerThread is responsible for handling the incoming client messages. For the purposes of this project, the object simply echo’s the incoming message. The thread continues to listen for incoming messages until the client disconnects. once disconnected, the thread continues to run, releases the semaphore, and returns to the thread pool. Once in the thread pool, it can be used again. When a client chooses to disconnect, any other client that was previously waiting, will be automatically connected.</p>



### Testing 

<p>The application was tested manually with several terminals to ensure that no more than the stated number of connections could be established. The ServerPool can take up to two arguments. The first is to set the server port and the second is to set the maximum number of simultaneous connections. The Client only takes the server port as an argument or none at all.</p>

#### Steps to run

<p>Open two terminals and navigate to the source directory.</p>

<p>In the first terminal, enter the following:</p>

	java ServerPool <serverPort> <number of connections>

<p>In the second terminal, enter the following:</p>

	java Client <serverPort>

<p>Hit Ctrl+C to terminate the Client and/or ServerPool</p>

<em>Only one ServerPool can run at a time, however there is no limit on the number of running Clients.
Results</em>


### Conclusion

<p>Overall, the project was successful. The concurrent application does not suffer from any concurrency issues such as deadlocks, starvation or race conditions. The application is capable of limiting the number of connections and meets all the requirements set in the objectives section.</p>
