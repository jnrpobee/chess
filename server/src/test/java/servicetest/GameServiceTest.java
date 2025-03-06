package servicetest;

import dataaccess.DataAccessException;
import dataaccess.memory.MemoryAuthDAO;
import model.*;
import dataaccess.handler.*;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.service.GameService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GameServiceTest {
    static final MemoryAuthDAO AUTH_DAO = new MemoryAuthDAO();
    static final MemoryGameDAO GAME_DAO = new MemoryGameDAO();
    static final GameService SERVICE = new GameService(GAME_DAO, AUTH_DAO);

    @BeforeEach
    void clear() throws DataAccessException {
        GAME_DAO.clear();
    }

    @Test
    void createGamePositive() throws DataAccessException {
        // Test creating a new game successfully
        CreateRequest newGame = new CreateRequest("Game");

        GameData gameID = null;
        try {
            gameID = SERVICE.createGame(newGame);
        } catch (DataAccessException e) {
            Assertions.fail();
        }

        Assertions.assertNotNull(GAME_DAO.getGame(gameID.gameID()));
    }

    @Test
    void createGameNegative() throws DataAccessException {
        // Test creating a game with an invalid ID
        Assertions.assertNull(GAME_DAO.getGame(1000));
    }

    @Test
    void joinGamePositive() {
        // Test joining a game successfully
        GameData gameID = null;
        try {
            gameID = SERVICE.createGame(new CreateRequest("GameTest"));
        } catch (DataAccessException e) {
            Assertions.fail();

            JoinRequest request = new JoinRequest(gameID.gameID(), "WHITE");

            AuthData authData = new AuthData("1000", "User");

            Assertions.assertDoesNotThrow(() -> SERVICE.joinGame(request, authData));

            // Test joining the same game again should throw an exception
            Assertions.assertThrows(DataAccessException.class, () -> SERVICE.joinGame(request, authData));
        }
    }

    @Test
    void joinGameNegative() {
        // Test joining a game with invalid parameters
        try {
            GameData gameID;
            gameID = SERVICE.createGame(new CreateRequest("GameTest"));

            AuthData authData = new AuthData("1234", "User");

            // Test joining a non-existent game
            Assertions.assertThrows(DataAccessException.class, () -> SERVICE.joinGame(
                    new JoinRequest(0, "BLACK"), authData));

            // Test joining a game with an invalid color
            GameData thisGameID = gameID;
            Assertions.assertThrows(DataAccessException.class, () -> SERVICE.joinGame(
                    new JoinRequest(thisGameID.gameID(), "GREEN"), authData));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void listGamePositive() {
        // Test listing all games successfully
        try {
            SERVICE.createGame(new CreateRequest("firstGame"));
            SERVICE.createGame(new CreateRequest("secondGame"));
            SERVICE.createGame(new CreateRequest("thirdGame"));
        } catch (DataAccessException e) {
            Assertions.fail();
        }

        try {
            Assertions.assertEquals(3, SERVICE.listGame().size());
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void listGameNegative() {
        // Test listing games when there are no games
        try {
            Assertions.assertTrue(SERVICE.listGame().isEmpty());
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }
}