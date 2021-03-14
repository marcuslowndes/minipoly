# Minipoly

A simplified version of the popular Monopoly board game that can be played using either the CLI or a GUI using JavaFX.

Originally created between February and June 2020, without any VCS.


## Rules of the game

There are two players. During a player's turn they may roll a die and move their counter. If they land on a property space, that player may purchase the property. When each property on a road has been purchased by the same player, that player can then purchase improvements on that property. Rent is payed to their opponent by any player who lands on their opponent's property. The first player to run out of money loses the game.


## Getting Started

Java 1.8, Maven and JavaFX 11.0 are required to run this project. As of JavaFX 11+, the version of JavaFX that comes bundled with JRE/JDK 8 is outdated, and must be installed from OpenJFX.


## Installing

Once the project has been cloned, run this command:

    mvn package

on the project directory to install dependencies.


## Run tests

There are unit tests on the core of the program that can be run using:

    mvn test


## Play


To run the game with a GUI, run the command below.

    java -jar target\MinipolyGame-gui.jar

To run the game entirely in the command line use the command:

    java -jar target\MinipolyGame-cli.jar


## Known Issues

- Depending on the platform you are using, the GUI version may display differently, with the buttons and outer padding of the game board not displaying properly 


## Built With

- [Java](https://www.java.com)
- [JavaFX](https://openjfx.io/)
- [Maven](https://maven.apache.org)
- [JUnit](http://junit.org)


## Author

Github: [@marcuslowndes](https://github.com/marcuslowndes)

