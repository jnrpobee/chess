package dataaccess.service;

import dataaccess.GameDAO;

public class JoinGameService {
    private final GameDAO gameDAO;

    public JoinGameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public GameDAO getGameDAO() {
        return gameDAO;
    }
}
