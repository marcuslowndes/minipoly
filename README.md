# Minipoly

A simplified version of the popular Monopoly board game that can be played using either the CLI or a GUI using JavaFX.

Originally created between February and June 2020, without any VCS.

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


To run the game with a GUI, run the command below.
(If using bash ignore the quotation marks around `exec.mainClass`)

    mvn exec:java -D"exec.mainClass"-"view.View"

To run the game entirely in the command line, you must comment out `<mainclass>view.View</mainclass>` from line 33 of the POM and then use the command:

    mvn exec:java -D"exec.mainClass"-"CLIMain"

<!-- This is due to a known issue that will be fixed. -->


## Rules of the game

There are two players. During a player's turn they may roll a die and move their counter. If they land on a property space, that player may purchase the property. When each property on a road has been purchased by the same player, that player can then purchase improvements on that property. Rent is payed by any player who lands on their opponent's property, to the opponent. The first player to run out of money loses the game.


## Known Bugs

- Most places where there is a `£` sign have somehow corrupted and become `Â£` in both the CLI and GUI versions of the game, this does not affect gameplay.


## Built With

- [Java](https://www.java.com)
- [JavaFX](https://openjfx.io/)
- [Maven](https://maven.apache.org)
- [JUnit](http://junit.org)


## Author

Github: [@marcuslowndes](https://github.com/marcuslowndes)

