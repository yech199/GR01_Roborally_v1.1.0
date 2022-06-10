package roborally.client;

import client_server.IGameService;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ClientController implements IGameService {

    private static final java.net.http.HttpClient httpClient = java.net.http.HttpClient.newBuilder()
            .version(java.net.http.HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public String getGameById(int id, String playerName) {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/game/" + id + "/" + playerName))
                .setHeader("User-Agent", "Game Client")
                .header("Content-Type", "application/json")
                .build();

        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        String result;
        try {
            result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }

        return result;
    }

    @Override
    public String updateGame(int id, String playerName, String gameData) {
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(gameData))
                .uri(URI.create("http://localhost:8080/game/" + id + "/" + playerName))
                .setHeader("User-Agent", "Game Client")
                .setHeader("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        String result;
        try {
            result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
            // Ignore response
        } catch (Exception e) {
            e.printStackTrace();
            result = "null";
        }
        return result;
    }

    @Override
    public String createGame(String boardName, int numOfPlayers) {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(numOfPlayers)))
                .uri(URI.create("http://localhost:8080/game/" + boardName + "/" + numOfPlayers))
                .setHeader("User-Agent", "Game Client")
                .header("Content-Type", "application/json")
                .build();

        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        String result;
        try {
            result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    @Override
    public String getListOfGames() {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/game"))
                .setHeader("User-Agent", "Game Client")
                .header("Content-Type", "application/json")
                .build();

        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        String result;
        try {
            result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    @Override
    public String getListOfBoards() {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/board"))
                .setHeader("User-Agent", "Game Client")
                .header("Content-Type", "application/json")
                .build();

        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        String result;
        try {
            result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    @Override
    public String getBoardState(int gameId) {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://localhost:8080/board/" + gameId))
                .setHeader("User-Agent", "Game Client")
                .header("Content-Type", "application/json")
                .build();

        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        String result;
        try {
            result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }

        return result;
    }

    @Override
    public String joinGame(int gameId, String playerName) {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(playerName)))
                .uri(URI.create("http://localhost:8080/game/" + gameId))
                .setHeader("User-Agent", "Game Client")
                .header("Content-Type", "application/json")
                .build();

        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        String result;
        try {
            result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
        } catch ( InterruptedException | ExecutionException | TimeoutException e ) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    @Override
    public String leaveGame(int id, String playerName) {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://localhost:8080/game/" + id + "/" + playerName))
                .setHeader("User-Agent", "Game Client")
                .header("Content-Type", "application/json")
                .build();

        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        String result;
        try {
            result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    @Override
    public String setPlayerState(int id, String playerName, String playerData) {
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(playerData))
                .uri(URI.create("http://localhost:8080/game/" + id + "/" + playerName))
                .setHeader("User-Agent", "Game Client")
                .setHeader("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        String result;
        try {
            result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }
}
