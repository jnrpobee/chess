package dataaccess.service;

import dataaccess.GameDAO;


public class ListGameService {
    private final GameDAO gameDAO;

    public ListGameService(GameDAO gameDAO) {
        this.gameDAO = gameDAO;
    }

    public GameDAO getGameDAO() {
        return gameDAO;
    }
}
