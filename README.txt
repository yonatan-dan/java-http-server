Web Server README
Overview
This is a multi-threaded web server implemented in Java as part of the Computer Networks Lab at Reichman University. The goal of the project is to build a web server that supports various HTTP methods, handles different types of HTTP responses, and ensures proper configuration through a config.ini file.

Major Milestones
Config.ini File:

Port: 8080
Root directory: ~/www/lab/html/
Default page: index.html
Max threads: 10
HTTP Response Codes:

200 OK
404 Not Found
501 Not Implemented
400 Bad Request
500 Internal Server Error
Content Types:

HTML: "content-type: text/html"
Image files: "content-type: image"
Icons: "content-type: icon"
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
www/lab/html/: Root directory for the web server.
Contributors
Yonatan Dan
Maya Levi
