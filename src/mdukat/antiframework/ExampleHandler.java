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
        addEndpoint("GET", "/setcookie", this::setCookie);
        addEndpoint("GET", "/listcookie", this::listCookie);
        addEndpoint("GET", "/setspecificcookie", this::setSpecificCookie);
        addEndpoint("GET", "/hasantiframecookie", this::hasAntiframeCookie);
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

    // Set antiframecookie=somefunnyvalue cookie
    String setCookie(String input){
        System.out.println("Called setcookie");
        addCookie("antiframecookie", "somefunnyvalue");
        return "Cookie should be set";
    }

    String hasAntiframeCookie(String input){
        System.out.println("Called hasantiframecookie");
        String value = getCookie("antiframecookie");
        if(value != null)
            return value;
        else
            return "Cookie not found";
    }

    // List all cookies sent to server
    String listCookie(String input){
        System.out.println("Called listcookie");
        if(getAllCookies() != null)
            return getAllCookies().toString();
        else
            return "No cookies found";
    }

    // Set specific cookie with GET arguments
    String setSpecificCookie(String input){
        System.out.println("Called setspecificcookie");
        if(!hasArguments())
            return "No arguments. Arguments for this endpoint are going to be sent back as cookies.";

        for(Map.Entry<String, String> argument : getRequestArguments().entrySet()){
            addCookie(argument.getKey(), argument.getValue());
        }

        return "Cookies sent back: " + getRequestArguments().toString();
    }
}
