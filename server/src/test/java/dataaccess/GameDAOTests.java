package dataaccess;

import chess.ChessGame;
import dataaccess.mysql.MySQLGameDAO;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameDAOTests {

    private static final GameDAO gameDAO;

    static {
        try {
            gameDAO = new MySQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void init() {
        GameData gameData = new GameData(1, "first", "second", "theGame", new ChessGame());
        try {
            gameDAO.addGame(gameData);
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Test
    void positiveAddGame() {
        GameData gameData = new GameData(1, "first", "second", "theGame", new ChessGame());
        Assertions.assertDoesNotThrow(() -> gameDAO.addGame(gameData));
    }

    @Test
    void negativeAddGame() {
        GameData gameData = new GameData(1, null, null, "noGame", new ChessGame());
        Assertions.assertDoesNotThrow(() -> gameDAO.addGame(gameData));
    }

    @Test
    void postiveGetGame() {
        GameData gameData = new GameData(1, "first", "second", "theGame", new ChessGame());

        try {
            var gotGame = gameDAO.getGame(1);
            Assertions.assertEquals(gameData, gotGame);
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

}
