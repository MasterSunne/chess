import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

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

    public AuthData registerUser(String username, String password, String email) throws ResponseException {
        var path = "/user";
        Map<String, String> data = Map.of(
                "username", username,
                "password", password,
                "email", email
        );
        record registerResponse(String username, String authToken){}
        var response = this.makeRequest("POST", path, data, registerResponse.class,null);
        return new AuthData (response.username(), response.authToken());
    }

    public AuthData loginUser(String username, String password) throws ResponseException{
        var path = "/session";
        Map<String, String> data = Map.of(
                "username", username,
                "password", password
        );
        record loginResponse (String username, String authToken){}
        var response = this.makeRequest("POST", path, data, loginResponse.class,null);
        return new AuthData (response.username(), response.authToken());
    }

    public void logoutUser(String authToken) throws ResponseException{
        var path = "/session";
        this.makeRequest("DELETE", path, null, null,authToken);
    }

    public Object createGame (String gameName, String authToken) throws ResponseException {
        var path = "/game";
        record GameRequest(String gameName) {}
        record createGameResponse(int gameID){}
        return this.makeRequest("POST", path, new GameRequest(gameName), createGameResponse.class, authToken);
    }

    public GameData[] listGames(String authToken) throws ResponseException {
        var path = "/game";
        record listGamesResponse(GameData[] gameList) {
        }
        return this.makeRequest("GET", path, null, listGamesResponse.class, authToken).gameList();
    }

    public void joinGame(String authToken,String playerColor,Integer gameID) throws ResponseException {
        var path = "/game";
        record joinGameReq(String playerColor, Integer gameID){}
        this.makeRequest("PUT",path,new joinGameReq(playerColor,gameID),null,authToken);
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
