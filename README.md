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
[Sequence design for phase 3](phase2Design.png)(https://sequencediagram.org/index.html?presentationMode=readOnly&shrinkToFit=true#initialData=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-gUTAEA3A8AKEZcgEALK0D-PUcgjZSkxVwjWtZd5rDPmQmBjQBQyoHYiwBYddk5RpCvQpIYe5tDtmPQ+RCDYIMUC6oQAaA1MLqImrNffG4GgHoiNtTALVDqnUfUNe6mQnrDAGXGhM5FJZEB0oxfK3pwjGbmIyX3fIJih5OCJSPQxWUgA)

[sequence design .02](hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4)


