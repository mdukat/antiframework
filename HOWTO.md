# How to use Antiframework in Your project

## Required knowledge
 - Creating new IntelliJ IDEA project
 - Methods
 - Classes, Constructors, Inheritance
 - Server port number

## Jargon
 - Endpoint - Method that accepts client request and returns data

## IntelliJ IDEA

### 1. Download JAR library build
You can download latest build from [antiframework/Releases](https://github.com/d3suu/antiframework/releases)

### 2. Create new project
Use default "command line template" for new project. No Maven or whatever else needed!

### 3. Add Antiframework library to Your project
Check this stackoverflow answer: [Correct way to add external jars (lib/*.jar) to an IntelliJ IDEA project](https://stackoverflow.com/a/1051705/13003825)

### 4. Create new class for Your endpoints
```java
public class MyHandler {
}
```

### 5. Extend it from `Worker` class
IntelliJ should automatically import this class.
```java
import mdukat.antiframework.Worker;

public class MyHandler extends Worker{
}
```

### 6. Create Your endpoint
Endpoint method must have `String` argument, and must return `String`. Return value is document value sent back to client.
```java
import mdukat.antiframework.Worker;

public class MyHandler extends Worker{
    
    public String test(String input){
        return "Hello";
    }

}
```

### 7. Add endpoint handler
In Your handler class constructor, add endpoint with HTTP path You want to use it on, and request type (might be `GET` or `POST`).
```java
import mdukat.antiframework.Worker;

public class MyHandler extends Worker {
    public MyHandler(){
        addEndpoint("GET", "/test", this::test);
    }

    public String test(String input){
        return "Hello";
    }
}
```

### 8. Create server instance
In Your `Main`, create new server instance. IntelliJ should automatically import Server class.
```java
import mdukat.antiframework.Server;

public class Main {

    public static void main(String[] args) {
        Server server = new Server();
    }
}
```

### 9. Start the server
Specify the port, and Your handler class.
```java
import mdukat.antiframework.Server;

public class Main {

    public static void main(String[] args) {
        Server server = new Server();
        server.start(8080, MyHandler.class);
    }
}
```

### 10. Have fun!
Click green "start" button and off You go! Your server should be up and running. Simpler than all this "professional" stuff, isn't it? (I hope...)
