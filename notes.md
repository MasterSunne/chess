# Class Design Guidelines
Keep data private.
Only write getters/setters when other classes need access.
Use standard structure to define classes.
Break up classes w/too much responsibility.
Names of classes and methods should reflect their purpose.
Classes have noun names, methods have verb names.
Use static methods as an exception.

Use inheritance for Is-A relationships.

Use references for Has-A or Uses-A relationships.

# Overriding equals method
Overriding equals method for Classes
When overriding the equals method for a class check to see if either instance is null first
if it's not then use the built-in isInstance method to check that the 2nd is an instance of the
same class as the first object. If it is then continue to compare internal variables for equality.

# Anonymous Inner Classes
The code looks weird and unconventional but these were invented for event handlers
and are really useful for keeping code close to where it's needed/defined.

# I/O
Input stream is used to read bytes sequentially from a data src
FileInputStream
PipedInputStream (read data from a thread)
URLConnection.getInputStream() allows you to connect a client and read data from server
HttpExchange.getRequestBody() allows the server to read data from a client
ResultSet.getBinaryStream(int columnIndex)

# Phase 3 Implementation Tips
What do Web API handlers do?
Put logic for validating an auth token in a handler base class so it can be shared by multiple handlers or a 
Service class that can be shared by multiple services. pick whether to do it in the service or handler
-deserialize JSON request body to Java request object
-call service class to perform the requested function passing it to the Java request object
-call service class to perform the requested function, passing it the Java request object
-wait and receive the Java response object from service
-serialize response obj to JSON
-send HTTP response back to client w/status code and response body


Questions for Phase 5:
need to start SQL server in the background to test ServerFacade?
How are the request/response objects supposed to reach the facade if they're in Server module?
How are is ServerFacadeTests supposed to reach ServerFacade if it's in the shared module?

need to implement clear function for phase 5?
is it an all or nothing pass off?
need a way to quit from being in a game?
how to get it to remember the list of games given last? (should it be persistent for different instances of clientMain?)
debugging join
how to clear before each test

note to self: make it so that joining games will ask to list games first and then list games will say if there are any




Questions for Phase 6:
- how to store a variable in the client that updates anytime a LOAD_GAME msg is 
received from the server [going to try storing it inside of ClientData] (this way I have the game/board stored on the client-side to redraw at any time)
- - how do I represent no more moves possible within the game (how to mark the game as over when resigning a player?)

- how to get the gameID and username data to create the CONNECT userGameCommand
- where do I call the connect method on wsf so that it only happens when desired?

- do we have to serialize and deserialize every serverMessage and UserGameCommand between server and client?

- - - is there a way to know what type of notification you're receiving and cast it to the right class before deserialization?

- ideas for how to pass the valid moves to the client to highlight the board spaces?

- how to handle promotion pieces? add it as an optional parameter when calling makeMove?






