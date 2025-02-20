# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)


## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
## [Sequence design for phase 3](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5ks9FMr73H0XwfksKyTAC5wFrKaooOUCAHjysL7oeqLorEmIwI6zr5K6LJkhSBq0uWo4mkS4YWhyMDcryBqCsKMDAe++ivEsmg6IWBGknaAraKGlFup2CYIcxZFvjA4HvLhiYEmGbpEWAsb+sBFFMsJkY0XRMZBg6xhJvJQmETAirKoJGnSoZRTwQqSoqqJGoKSZnrekGpEjqM6lmhGhSWjA0YBXpAk8RGcFOvKQVxgJomGf+aZoTy2a5pg8UiVeabDrco4LpOfTTrOzbjq2fTfhlnbZD2MD9oOvQDBJY4Tl8BWNkVTUHKYy6rt4fiBF4KDoHuB6+Mwx7pJkmCVReNklOUFTSAAoruC31AtzQtA+qhPt0LVzu2UAFPFiHDb6AZBoVaDYml4XgjASH2CNsL3SNGEYtisVGZZvEcCg3CZCpZ11q1l0WT51HlJEwwQDQUXBjFcooNZR1DQ9vrJQgeYwR21k-lci77RV55gH2A5DjAew4V1ng9RukK2ru0IwAA4qOrJjaek1E2yuOVEzK3rfYo47edwP7YdWNpsgsQs6MqiAzOwNXRL3MI4h0KodCr1YbJiP4c5vHklSNK7eg3nhoZonlMBOsFKFavS6zHnZV5oNUVp5Q6czo76Vx1l217owOqF6W2QHKDxgjnbIwzsTo5jQLYwUPOAX0guy+MlT9GnKAAJLSBnACMvYAMwACxPCemQGhWExfDoCCgA21c5bXTzZwAcqOtcU40ZXUInhRTcT1Wk0kKfZ6oGcVFno554XJfl1Mlf6g1kFPPXjfN6Ma9TB3XegT3S6cN166BNgPhQNg3DwLqmRhyk41njkzDhdetQNALQvBCLc5Dnvox92OMmCW5RXKZBligeWF1Zjj1HJ3betdyZKwTr+Ga4IwGQLgLfFAWssQ2z1sZA2xFjY-1Nq7N0FtVbiU8mID6-sMEQPliGYO7sAo2jDj7Tsdts553IVZPyNFAoMW0E7ekswIGMlhDw6QZtNI3UitInWUcQFYK9JkOOqVlY4wylcXes987lCLmXcmBNrJDxJrVUsejRhz0MQvExnVj7U1PgESwv0kLJBgAAKQgDye+AQN4gAbJzZ+Kt+5pmqJSO8LRs7CyBr-XoV9gBuKgHACASEoCzGkYA7Gf4QEwAAFa+LQIwk2aBpgpySSktJGSVgAHUWA51Wi0AAQruBQcAADSXxpHz2MXsZBqYwkRTEkUlCPikooDRG9JRLp9buhgIbEiZTZFWXkWJa2jk5mEPdIbRh0jVnmlYZ7CR2ghRhGzpxP28zyinPkHwo56zbne3hvBJyOylIQIeb5dkHseS2mEfIc51DnYoHES8oFTFFEGW2V9BZ1sWFPJBXcG2wCUHlAmWgDR11X6ZRMT+Qmz8LEwG6KWA+ZgnFrl6gELwySuxelgMAbAV9CDxESA-DmQ9hnXkWstVa61jCmLyeimAIBuB4AUMy5AIA2VoFhIM4ESKxUMvlbMz6PkIQbGhigBQyoIFy0OT8-y0hfoUkMHc4AaqZA3JgJCKGNBdUIANAa754MZCmrvjcDQH1rUfNtVqh1yoAaGrdSav6hgVIXVmWi1M5REAMuxVo3FuickggKOYkeTgSVjxMV1IAA)

