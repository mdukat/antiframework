package mdukat.antiframework;

/*

Example Handler

 */

import java.util.HashMap;
import java.util.Map;

public class ExampleHandler extends Worker {
    public ExampleHandler(){
        super();
        addEndpoint("GET", "/test", this::testEndpoint);
        addEndpoint("GET", "/useragent", this::getUserAgentEndpoint);
        addEndpoint("GET", "/debug", this::debugEndpoint);
        addEndpoint("POST", "/debug", this::debugEndpoint);
        addEndpoint("GET", "/htmltest", this::htmlTest);
        addEndpoint("GET", "/htmlerror", this::htmlError);
        addEndpoint("GET", "/htmlstatic", this::htmlStatic);
        addEndpointRedirect("GET", "/redirect", "/debug");
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

    // Debug endpoint with some data (very developer, much data)
    String debugEndpoint(String input){
        System.out.println("Called debug endpoint");
        String output = "Debug endpoint\r\n";
        output += "User-Agent: " + getHeaderValue("User-Agent") + "\n"
            + "Request path: " + getRequestPath() + "\n"
            + "Request type: " + getRequestType() + "\n"
            + "HTTP Version: " + getRequestVersion() + "\n";

        if(hasArguments())
            output += "Has arguments: " + getRequestArguments().toString() + "\n";

        return output;
    }

    // Builds example HTML file on the fly
    String htmlTest(String input){
        System.out.println("Called htmltest");
        Map<String, String> htmlThings = new HashMap<>();

        htmlThings.put("useragent", getHeaderValue("User-Agent"));

        HTMLBuilder html = new HTMLBuilder("examplefiles/test.html", htmlThings);
        return html.getHTML();
    }

    // Throw HTML file error
    String htmlError(String input){
        System.out.println("Called htmlerror");
        Map<String, String> htmlThings = new HashMap<>();
        HTMLBuilder html = new HTMLBuilder("this_file_does_not_exist.html", htmlThings);
        return html.getHTML();
    }

    // Show static website
    String htmlStatic(String input){
        System.out.println("Called htmlstatic");
        HTMLBuilder html = new HTMLBuilder("examplefiles/static.html");
        return html.getHTML();
    }
}
