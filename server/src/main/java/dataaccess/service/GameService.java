package dataaccess.service;

// Import statements

import chess.ChessGame;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.handler.CreateRequest;
import dataaccess.handler.JoinRequest;
import result.GameDataResult;
import model.AuthData;
import model.GameData;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class GameService {
    // DAO instances for accessing game and authentication data
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    private int gameID;

    // Constructor to initialize DAOs
    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
        try {
            gameID = 100000;
            int numGame = gameDAO.getAllGame().size();
            if (numGame != 0) {
                gameID += numGame;
            }
        } catch (DataAccessException e) {
            gameID = 100000;
        }
    }

    // Method to create a new game
    public GameData createGame(CreateRequest createNewGame) throws DataAccessException {
        ChessGame game = new ChessGame();
        GameData newGameData = new GameData(gameID += 1, null, null, createNewGame.gameName(), game);
        // Adding the game to the Database
        this.gameDAO.addGame(newGameData);
        return newGameData;
    }

    // Method to join an existing game
    public void joinGame(JoinRequest joinRequest, AuthData authData) throws DataAccessException {
        if (joinRequest.gameID() == null || joinRequest.playerColor() == null) {
            throw new DataAccessException("Error: bad request");
        }
        GameData game = gameDAO.getGame(joinRequest.gameID());
        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }

        String whiteUsername = game.whiteUsername();
        String blackUsername = game.blackUsername();

        if (Objects.equals(joinRequest.playerColor().toUpperCase(), "WHITE")) {
            if (whiteUsername != null) {
                throw new DataAccessException("Error: already taken");
            }
            whiteUsername = authData.username();
        } else if (Objects.equals(joinRequest.playerColor().toUpperCase(), "BLACK")) {
            if (blackUsername != null) {
                throw new DataAccessException("Error: already taken");
            }
            blackUsername = authData.username();
        } else {
            throw new DataAccessException("Error: bad request");
        }
        GameData createNewGame = new GameData(joinRequest.gameID(), whiteUsername,
                blackUsername, game.gameName(), game.game());
        gameDAO.updateGame(createNewGame);
    }

    // Method to list all games
    public List<GameDataResult> listGame() throws DataAccessException {
        Collection<GameData> allGames = gameDAO.getAllGame();
        List<GameDataResult> gameDataResult = new ArrayList<>();
        for (var game : allGames) {
            gameDataResult.add(new GameDataResult(
                    game.gameID(), game.whiteUsername(),
                    game.blackUsername(), game.gameName()
            ));
        }
        return gameDataResult;
    }

    // Method to authenticate a user
    public void authentication(String authToken) throws DataAccessException {
        if (!this.authDAO.authExists(authToken)) {
            throw new DataAccessException("Error: unauthorized");
        }
    }

    // Method to get authentication data
    public AuthData getAuthData(String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken);
    }
}

