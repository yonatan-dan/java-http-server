# java-http-server

## Overview

This project involves building a multi-threaded web server over TCP, following the specified guidelines provided by Reichman University Lab for the Computer Networks course.

## Project Structure

- `src/`: Contains the source code for the web server.
- `config.ini`: Configuration file with server parameters.
- `compile.sh`: Bash script to compile the project on a Linux system.
- `run.sh`: Bash script to execute the web server on a Linux system.
- `server-root/`: Directory containing server root files.
- `bonus.txt`: File explaining any implemented bonus features.
- `readme.txt`: Explanation of classes, roles, and design choices.

## Getting Started

1. Clone the repository: `git clone <https://github.com/yonatan-dan/java-http-server`
2. Navigate to the project directory: `cd java-http-server`
3. Compile the project: `./compile.sh`
4. Run the server: `./run.sh`
5. Open your browser and navigate to [http://localhost:8080/](http://localhost:8080/) to test the server.

## Configuration

Edit the `config.ini` file to adjust server parameters such as port, root directory, default page, and maximum threads.
