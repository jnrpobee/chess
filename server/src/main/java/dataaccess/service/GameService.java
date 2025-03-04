package dataaccess.service;

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
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;
    private int gameID = 100000;


    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }


    public GameData createGame(CreateRequest createNewGame) throws DataAccessException {

        ChessGame game = new ChessGame();
        GameData newGameData = new GameData(gameID += 1, null, null, createNewGame.gameName(), game);
        //this.gameID ;

        //adding the game to the Database
        this.gameDAO.addGame(newGameData);

        return new GameData(gameID, null, null, null, null);


    }

//    public GameData getGame(Integer GameID) throws DataAccessException {
//        return gameDAO.getGame(GameID);
//    }

//    public void updateGame(GameData game) throws DataAccessException {
//        gameDAO.updateGame(game);
//    }

    public void JoinGame(JoinRequest joinRequest, AuthData authData) throws DataAccessException {
        if (joinRequest.gameID() == null || joinRequest.playerColor() == null) {
            throw new DataAccessException("Error: bad request");
        }
        GameData game = gameDAO.getGame(joinRequest.gameID());
        if (game == null) {
            throw new DataAccessException("Error: bad request");
        }


        String whiteUsername = game.whiteUsername();
        String blackUsername = game.blackUsername();

        if (Objects.equals(joinRequest.playerColor(), "WHITE")) {
            if (whiteUsername != null) {
                throw new DataAccessException("Error: already taken");
            }
            whiteUsername = authData.username();
        } else if (Objects.equals(joinRequest.playerColor(), "BLACK")) {
            if (blackUsername != null) {
                throw new DataAccessException("Error: already taken");
            }
            blackUsername = authData.username();
        } else if (joinRequest.playerColor() != null) {
            throw new DataAccessException("Error: bad request");

        }
        GameData createNewGame = new GameData(joinRequest.gameID(), whiteUsername,
                blackUsername, game.gameName(), game.game());

        gameDAO.updateGame(createNewGame);


    }

    public List<GameDataResult> ListGame() throws DataAccessException {
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

    public boolean authentication(String authToken) throws DataAccessException {
        if (!this.authDAO.authExists(authToken)) {
            throw new DataAccessException("Error: unauthorized");
        }
        return true;
    }

    public AuthData getAuthData(String authToken) throws DataAccessException {
        return authDAO.getAuth(authToken);
    }


}

