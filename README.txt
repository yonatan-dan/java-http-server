Web src.Server README
Overview
This is a multi-threaded web server implemented in Java as part of the Computer Networks Lab at Reichman University. The goal of the project is to build a web server that supports various HTTP methods, handles different types of HTTP responses, and ensures proper configuration through a config.ini file.

Major Milestones
Config.ini File:

Port: 8080
Root directory: server-root
Default page: index.html
Max threads: 10
HTTP Response Codes:

200 OK
404 Not Found
501 Not Implemented
400 Bad Request
500 Internal src.Server Error
Content Types:

HTML: "content-type: text/html"
Image files: "content-type: image"
Icons: "content-type: icon"
Trace: "content-type: message/http"
Other: "content-type: application/octet-stream"

Supported Image Files:

.bmp, .gif, .png, .jpg
HTTP Methods:

GET, POST, HEAD, TRACE
Transfer-Encoding:

Supports "transfer-encoding: chunked" based on client request header
Default Page:

Returns HTML page with a form on the default page
Parameter Handling:

Supports GET and POST methods for getting parameters
Prints HTTP request header and response header to the console
Server Reliability:

Handles exceptions gracefully
Shuts down gracefully in case of errors during startup
Thread Limit:

Limits the number of threads to maxThreads
Directory Restriction:

Users cannot surf outside the server's root directory
Favicon.ico:

Added favicon.ico to the website
How to Run and Test
Compile the code using the provided compile.sh script.
Run the server using the run.sh script.
Open a web browser and navigate to http://localhost:8080/.
Test various scenarios, including requesting non-existing pages and using different HTTP methods.
Check the console for HTTP request and response headers.
Bonus Features
No additional bonus features have been implemented.

Project Structure
src: Contains all Java source files.
config.ini: Configuration file for the server.
compile.sh: Bash script to compile the code.
run.sh: Bash script to run the server.
server-root: Root directory for the web server.

Implementation Details:
Server: This class is responsible for starting the server, listening for incoming connections, and spawning new threads to handle each connection.
RequestHandler: This class is responsible for handling each individual client request. It parses the HTTP request, processes it, and sends back an appropriate HTTP response.
HTTPRequest: This class represents an HTTP request. It parses the request line, headers, and body (if any) from the input stream and provides methods to access the request's properties.
ResponseBuilder: This class is responsible for building an HTTP response based on the request and the server's configuration.

Server Design:
The server is designed around a multi-threaded model, where each client connection is handled by a separate thread. This design allows the server to handle multiple simultaneous connections without blocking, improving the server's performance and responsiveness. The server uses a thread pool to manage these threads, limiting the number of concurrent threads to a maximum specified in the configuration file. This prevents resource exhaustion under heavy load. The server also uses a semaphore to control access to the thread pool, ensuring that if the maximum number of threads is reached, additional connections are queued and handled only after one of the initial connections has been closed. This design ensures that the server remains responsive and efficient under a variety of load conditions. The server is also designed to be robust and fault-tolerant, with exception handling mechanisms in place to ensure that the server continues to run even if an error occurs while processing a request.

Contributors
Yonatan Dan
Maya Levi
