package dataaccess;

import chess.ChessGame;
import dataaccess.mysql.MySQLGameDAO;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameDAOTests {

    private static final GameDAO GAME_DAO;

    static {
        try {
            GAME_DAO = new MySQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void init() {
        GameData gameData = new GameData(1, "first", "second", "theGame", new ChessGame());
        try {
            GAME_DAO.addGame(gameData);
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Test
    void positiveAddGame() {
        GameData gameData = new GameData(1, "first", "second", "theGame", new ChessGame());
        Assertions.assertDoesNotThrow(() -> GAME_DAO.addGame(gameData));
    }

    @Test
    void negativeAddGame() {
        GameData gameData = new GameData(1, null, null, "noGame", new ChessGame());
        Assertions.assertDoesNotThrow(() -> GAME_DAO.addGame(gameData));
    }

    @Test
    void positiveGetGame() {
        GameData gameData = new GameData(1, "first", "second", "theGame", new ChessGame());

        try {
            var getTheGame = GAME_DAO.getGame(1);
            Assertions.assertEquals(gameData, getTheGame);
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void negativeGetGame() {
        try {
            Assertions.assertNull(GAME_DAO.getGame(2));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void positiveGetAllGame() {
        GameData gameData = new GameData(
                1,
                "first",
                "second",
                "theGame",
                new ChessGame()
        );
        try {
            Assertions.assertTrue(GAME_DAO.getAllGame().contains(gameData));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void negativeGetAllGame() {
        Assertions.assertDoesNotThrow(GAME_DAO::getAllGame);
    }

    @Test
    void positiveUpdateGame() {
        GameData gameData = new GameData(
                1,
                "first",
                "second",
                "theGame",
                new ChessGame()
        );
        try {
            GAME_DAO.updateGame(gameData);
            Assertions.assertEquals(gameData, GAME_DAO.getGame(1));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void updateGame() {
        GameData gameData = new GameData(
                1,
                "first",
                "second",
                "theGame",
                new ChessGame()
        );
        Assertions.assertDoesNotThrow(() -> GAME_DAO.updateGame(gameData));
    }

    @Test
    void positiveClear() {
        try {
            GAME_DAO.clear();

            Assertions.assertNull(GAME_DAO.getGame(1));
            Assertions.assertNull(GAME_DAO.getGame(2));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

}
