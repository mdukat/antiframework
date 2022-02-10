# Antiframework documentation
Just one document, WOOOO!

## Server class
### Starting the server
```
void start(int port, Class worker)
void start(String IP, int port, Class worker)
void start(String IP, int port, int backlog, Class worker)
```

### Server public methods
```
// Return full HTTP `Server: ` header
static String getServerHeader()

// Return server version string
static String getServerVersion()
```

## Worker class
```
// Add new GET or POST endpoint
void addEndpoint(String requestType, String requestPath, Function<String, String> endpoint)

// Add new 301 redirect endpoint
void addEndpointRedirect(String requestType, String requestPath, String destination)

// Get request header value
String getHeaderValue(String headerName)

// Get HTTP version from request
String getRequestVersion()

// Get requested HTTP path
String getRequestPath()

// Get requested HTTP type (GET or POST)
String getRequestType()

// Get requested arguments (?key=value for GET, key=value for POST)
Map<String, String> getRequestArguments()

// Check if request has arguments
boolean hasArguments()

// Add cookie
void addCookie(String cookieKey, String cookieValue)

// Get cookie
String getCookie(String cookieKey)

// Get all cookies
Map<String, String> getAllCookies()
```

## HTMLBuilder class
```
// Static HTML builder
HTMLBuilder(String templateFilePath)

// Dynamic HTML builder with values in map
HTMLBuilder(String templateFilePath, Map<String, String> parseKeys)

// Return compiled HTML
String getHTML()

// Get template file path
String getTemplateFilePath()
```