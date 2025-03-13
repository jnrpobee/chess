package dataaccess;

import chess.ChessGame;
import dataaccess.mysql.MySQLGameDAO;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameDAOTests {

    private static final GameDAO game_DAO;

    static {
        try {
            game_DAO = new MySQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void init() {
        GameData gameData = new GameData(1, "first", "second", "theGame", new ChessGame());
        try {
            game_DAO.addGame(gameData);
        } catch (DataAccessException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Test
    void positiveAddGame() {
        GameData gameData = new GameData(1, "first", "second", "theGame", new ChessGame());
        Assertions.assertDoesNotThrow(() -> game_DAO.addGame(gameData));
    }

    @Test
    void negativeAddGame() {
        GameData gameData = new GameData(1, null, null, "noGame", new ChessGame());
        Assertions.assertDoesNotThrow(() -> game_DAO.addGame(gameData));
    }

    @Test
    void positiveGetGame() {
        GameData gameData = new GameData(1, "first", "second", "theGame", new ChessGame());

        try {
            var gotGame = game_DAO.getGame(1);
            Assertions.assertEquals(gameData, gotGame);
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void negativeGetGame() {
        try {
            Assertions.assertNull(game_DAO.getGame(2));
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
            Assertions.assertTrue(game_DAO.getAllGame().contains(gameData));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

    @Test
    void NegativeGetAllGame() {
        Assertions.assertDoesNotThrow(game_DAO::getAllGame);
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
            game_DAO.updateGame(gameData);
            Assertions.assertEquals(gameData, game_DAO.getGame(1));
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
        Assertions.assertDoesNotThrow(() -> game_DAO.updateGame(gameData));
    }

    @Test
    void positiveClear() {
        try {
            game_DAO.clear();

            Assertions.assertNull(game_DAO.getGame(1));
            Assertions.assertNull(game_DAO.getGame(2));
        } catch (DataAccessException e) {
            Assertions.fail();
        }
    }

}
