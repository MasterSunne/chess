package server;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import request.*;
import result.CreateGameResult;
import result.ListGamesResult;
import result.RegLogResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class ServerFacade {

    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public RegLogResult registerUser(RegisterRequest rr) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, rr, RegLogResult.class,null);
    }

    public RegLogResult loginUser(LoginRequest lr) throws ResponseException {
        var path = "/session";
        return this.makeRequest("POST", path, lr, RegLogResult.class,null);
    }

    public void logoutUser(LogoutRequest lr) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null,lr.authToken());
    }

    public CreateGameResult createGame (String authToken, CreateGameRequest cgr) throws ResponseException {
        var path = "/game";
        return this.makeRequest("POST", path, cgr, CreateGameResult.class, authToken);
    }

    public ListGamesResult listGames(ListGamesRequest lgr) throws ResponseException {
        var path = "/game";
        return this.makeRequest("GET", path, null, ListGamesResult.class, lgr.authToken());
    }

    public void joinGame(JoinGameRequest jgr) throws ResponseException {
        var path = "/game";
        this.makeRequest("PUT",path, jgr,null,jgr.authToken());
    }

    public void clearAll() throws ResponseException {
        var path = "/db";
        this.makeRequest("DELETE", path, null, null, null);
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authHeader) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);

            // Add authorization header if provided
            if (authHeader != null && !authHeader.isEmpty()) {
                http.setRequestProperty("Authorization", authHeader);
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
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

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
