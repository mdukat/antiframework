package mdukat.antiframework;

public class Main {

    public static void main(String[] args) {
        Server server = new Server();
        server.start("127.0.0.1", 8080, ExampleHandler.class);
    }
}

