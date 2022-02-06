package mdukat;

/*

Example Handler

 */

public class ExampleHandler extends Worker {
    public ExampleHandler(){
        super();
        addEndpoint("GET", "/test", this::testEndpoint);
        addEndpoint("GET", "/useragent", this::getUserAgentEndpoint);
    }

    // Returns static "Test123"
    String testEndpoint(String input){
        System.out.println("Hello from test endpoint");
        return "Test123";
    }

    // Returns value from User-Agent HTTP header
    String getUserAgentEndpoint(String input){
        return getHeaderValue("User-Agent");
    }
}
