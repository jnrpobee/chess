package client;

import chess.ChessGame;
import dataaccess.handler.JoinRequest;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import exception.ResponseException;
import model.*;
import result.*;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() throws ResponseException {
        server = new Server();
        var port = server.run(0);
        String serverUrl = "http://localhost:" + port;
        System.out.println(serverUrl);
        serverFacade = new ServerFacade(serverUrl);
        serverFacade.clear();
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {

        server.stop();
    }

    @BeforeEach
    public void clearDatabase() throws ResponseException {
        serverFacade.clear();
    }

    @Test
    void registerPositive() {
        try {
            serverFacade.registerUser(new UserData("player", "password", "p@email.com"));
        } catch (ResponseException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    public void registerNegative() {
        try {
            serverFacade.registerUser(new UserData("player1", "password", "p1"));
            assertThrows(ResponseException.class, () -> serverFacade.registerUser(new UserData("player1", "password", "p1")));
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
    }

    @Test
    void loginPositive() {
        try {
            serverFacade.registerUser(new UserData("player", "player", "plater"));

            serverFacade.loginUser(new LoginRequest("player", "player"));
        } catch (ResponseException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    @Test
    void loginNegative() {
        try {
            serverFacade.loginUser(new LoginRequest("player", "player"));
            assertThrows(ResponseException.class, () -> serverFacade.loginUser(new LoginRequest("player", "player")));
        } catch (ResponseException e) {
            System.out.println(e.getMessage());
        }
    }


    @Test
    void listGamesPositive() throws Exception {
        var authData = serverFacade.registerUser(new UserData("player3", "password", "p3@email.com"));
        Collection<GameDataResult> games = serverFacade.listGames(authData.authToken());
        Assertions.assertNotNull(games);
    }

    @Test
    void listGamesNegative() {
        assertThrows(ResponseException.class, () -> {
            serverFacade.listGames("invalidAuthToken");
        });
    }

    @Test
    void createGamePositive() throws Exception {
        var authData = serverFacade.registerUser(new UserData("player4", "password", "p4"));
        serverFacade.createGame(new GameName("TestGame"));
    }

    @Test
    void createGameNegative() {
        assertThrows(ResponseException.class, () -> {
            serverFacade.createGame(new GameName("TestGame"));
        });
    }

    @Test
    void joinGamePositive() throws Exception {
        var authData = serverFacade.registerUser(new UserData("player5", "password", "p5"));
        var gameData = serverFacade.createGame(new GameName("TestGame"));
        serverFacade.joinGame(new JoinGameRequest(gameData.gameID(), "BLACK"), authData.authToken());
    }


    @Test
    void joinGameNegative() {
        assertThrows(ResponseException.class, () -> {
            serverFacade.joinGame(new JoinGameRequest(0, "BLUE"), "invalidAuthToken");
        });

    }

    @Test
    void logoutPositive() throws Exception {
        var authData = serverFacade.registerUser(new UserData("player6", "password", "p6"));
        serverFacade.logoutUser();
    }

    @Test
    void logoutNegative() {
        assertThrows(ResponseException.class, () -> {
            serverFacade.logoutUser();
        });
    }

    @Test
    void clearPositive() throws Exception {
        serverFacade.clear();
    }

}
