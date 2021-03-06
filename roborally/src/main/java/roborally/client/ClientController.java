package roborally.client;

import client_server.IGameService;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Simple HTTP request that communicates with server
 */
public class ClientController implements IGameService {

    String myIP;
    String targetIP;

    ClientController(String myIP) {
        // Update Own IP
        this.myIP = myIP;
        targetIP = myIP;
    }

    private static final java.net.http.HttpClient httpClient = java.net.http.HttpClient.newBuilder()
            .version(java.net.http.HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /**
     * Get the gameState as JSON, from the player requesting.
     * @param id gameId
     * @param playerName
     * @return gameState as JSON String
     * @author Mads Sørensen (S215805)
     */
    @Override
    public String getGameById(int id, String playerName) {
        String result;
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://" + targetIP + ":8080/game/" + id + "/" + playerName))
                .setHeader("User-Agent", "Game Client")
                .header("Content-Type", "application/json")
                .build();

        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        try {
            result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
        } catch (IllegalArgumentException e) {
            result = null;
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }

        return result;
    }

    /**
     * Update the gameState of the player on the server
     * @param id gameId
     * @param playerName
     * @param gameData
     * @return Response status code
     * @author Mads Sørensen (S215805)
     */
    @Override
    public String updateGame(int id, String playerName, String gameData) {
        String result;
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(gameData))
                .uri(URI.create("http://" + targetIP + ":8080/game/join/" + id + "/" + playerName))
                .setHeader("User-Agent", "Game Client")
                .setHeader("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        try {
            result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
            // Ignore response
        } catch (IllegalArgumentException e) {
            result = null;
        } catch (Exception e) {
            e.printStackTrace();
            result = "null";
        }
        return result;
    }

    /**
     * Create a new game on the server from a board template name.
     * @param boardName Name of the board
     * @param numOfPlayers Number of players in the game
     * @return gameId of game for further communication
     * @author Mads Sørensen (S215805)
     */
    @Override
    public String createGame(String boardName, int numOfPlayers) {
        String result;
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(numOfPlayers)))
                .uri(URI.create("http://" + targetIP + ":8080/game/" + boardName + "/" + numOfPlayers))
                .setHeader("User-Agent", "Game Client")
                .header("Content-Type", "application/json")
                .build();

        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        try {
            result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
        } catch (IllegalArgumentException e) {
            result = null;
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    /**
     * Get a list of board names on the server
     * @return list of boards
     * @author Mark Nielsen
     */
    @Override
    public String getListOfGames() {
        String result;
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://" + targetIP + ":8080/game"))
                .setHeader("User-Agent", "Game Client")
                .header("Content-Type", "application/json")
                .build();

        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        try {
            result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
        } catch (IllegalArgumentException e) {
            result = null;
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    /**
     * Get a list of board names on the server
     * @return list of boards
     * @author Mark Nielsen
     */
    @Override
    public String getListOfBoards() {
        String result;
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://" + targetIP + ":8080/board"))
                .setHeader("User-Agent", "Game Client")
                .header("Content-Type", "application/json")
                .build();

        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        try {
            result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
        } catch (IllegalArgumentException e) {
            result = null;
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    /**
     * Get the boardState of a board template in JSON.
     * @param gameId
     * @return boardState
     * @author Mark Nielsen
     */
    @Override
    public String getBoardState(int gameId) {
        String result;
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://" + targetIP + ":8080/board/" + gameId))
                .setHeader("User-Agent", "Game Client")
                .header("Content-Type", "application/json")
                .build();

        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        try {
            result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
        } catch (IllegalArgumentException e) {
            result = null;
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    /**
     * Join a game and get the gameId for further communications
     * @param gameId the id of the game
     * @param playername the name of the player to join
     * @return Response statuscode
     * @author Mads Sørensen (S215805)
     */
    @Override
    public String joinGame(int gameId, String playername) {
        String result;
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(playername)))
                .uri(URI.create("http://" + targetIP + ":8080/game/join/" + gameId))
                .setHeader("User-Agent", "Game Client")
                .header("Content-Type", "application/json")
                .build();

        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());


        try {
            result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
        } catch (IllegalArgumentException e) {
            result = null;
        } catch (Exception e) {
            e.printStackTrace();
            result = null;
        }
        return result;
    }

    /**
     * Join a game and get the gameId for further communications
     * @param id the id of the game
     * @param playerName the name of the player to join
     * @return Response statuscode
     * @author Mads Sørensen (S215805)
     */
    @Override
    public String leaveGame(int id, String playerName) {
        String result;
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://" + targetIP + ":8080/game/" + id + "/" + playerName))
                .setHeader("User-Agent", "Game Client")
                .header("Content-Type", "application/json")
                .build();

        CompletableFuture<HttpResponse<String>> response =
                httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());

        try {
            result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
        } catch (IllegalArgumentException e) {
            result = null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return result;
    }

    /**
     * The player submits it's playerData to tell the server that it has finished programming phase.
     * @param id gameId to join
     * @param playername name of the player
     * @param playerData JSON player object
     * @return Response statuscode
     * @author Mads Sørensen (S215805)
     */
    @Override
    public String setPlayerState(int id, String playername, String playerData) {
        String result;
            HttpRequest request = HttpRequest.newBuilder()
                    .PUT(HttpRequest.BodyPublishers.ofString(playerData))
                    .uri(URI.create("http://" + targetIP + ":8080/game/" + id + "/" + playername))
                    .setHeader("User-Agent", "Game Client")
                    .setHeader("Content-Type", "application/json")
                    .build();
            CompletableFuture<HttpResponse<String>> response =
                    httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            try {
                result = response.thenApply(HttpResponse::body).get(5, TimeUnit.SECONDS);
            } catch (IllegalArgumentException e) {
                result = null;
            } catch (Exception e) {
                e.printStackTrace();
                result = null;
            }
        return result;
    }

    public void setTargetIP(String newIP) {
        targetIP = newIP;
    }
}
