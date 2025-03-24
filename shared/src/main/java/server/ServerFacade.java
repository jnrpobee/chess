package server;

import com.google.gson.Gson;
import exception.ResponseException;
import result.*;
import model.*;

import java.io.*;
import java.net.*;
import java.util.Collection;

public class ServerFacade {
    private final String serverUrl;
    private String authToken;


    public ServerFacade(String url) {
        serverUrl = url;
    }

    public AuthData registerUser(UserData userData) throws ResponseException {
        var path = "/user";
        AuthData authData = this.makeRequest("POST", path, userData, AuthData.class, null);
        if (authData != null) {
            authToken = authData.authToken();
        }
        return authData;
    }

    public AuthData loginUser(LoginRequest loginRequest) throws ResponseException {
        var path = "/session";
        AuthData authData = this.makeRequest("POST", path, loginRequest, AuthData.class, null);
        if (authData != null) {
            authToken = authData.authToken();
        }
        return authData;
    }

    public void logoutUser() throws ResponseException {
        var path = "/session";

        this.makeRequest("DELETE", path, null, null, authToken);
        authToken = null;
    }

    public Collection<GameDataResult> listGames() throws ResponseException {
        var path = "/game";
        return this.makeRequest("GET", path, null, ListGameRequest.class, authToken).games();

    }

    public GameData createGame(GameName gameName) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, gameName, GameData.class, authToken);
    }

    public void joinGame(JoinGameRequest joinRequest) throws ResponseException {
        var path = "/game";
        this.makeRequest("PUT", path, joinRequest, null, authToken);
    }

    public void clear() throws ResponseException {
        var path = "db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String AuthToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
//            try (InputStream respErr = http.getErrorStream()) {
//                if (respErr != null) {
//                    throw ResponseException.fromJson(respErr);
//                }
//            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
