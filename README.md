# Minipoly

A simplified version of the popular Monopoly board game that can be played using either the CLI or a GUI using JavaFX.


## Getting Started

Java 1.8, Maven and JavaFX 11.0 are required to run this project. As of JavaFX 11+, the version of JavaFX that comes bundled with JRE/JDK 8 is outdated.


## Installing

Once the project has been cloned, run this command:

    mvn package

on the project directory to install dependencies.


## Run tests

There are unit tests on the core of the program that can be run using:

    mvn test


## Play

To run the game with a GUI, use the following command.  
(If using bash ignore the quotation marks around `exec.mainClass`)

    mvn exec:java -D"exec.mainClass"-"view.View"

To run the game entirely in the command line, use the command:

    mvn exec:java -D"exec.mainClass"-"CLIMain"


## Built With

- [Java](https://www.java.com)
- [JavaFX](https://openjfx.io/)
- [Maven](https://maven.apache.org)
- [JUnit](http://junit.org)


## Author

Github: [@marcuslowndes](https://github.com/marcuslowndes)

