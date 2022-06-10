package roborally.client;

import client_server.IGameService;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ClientController implements IGameService {

    String myIP;
    String targetIP;
    Scanner input = new Scanner(System.in);

    ClientController(String myIP){
        // Update Own IP
        this.myIP = myIP;
        targetIP = myIP;

        System.out.println("If you wish to host server enter: no");
        System.out.println("To Join another Server enter taget IP-Address for the LAN-Server: (ex. 123.45.678.9)");
        targetIP = input.nextLine();

        // Reset target ip
        if (targetIP.equals("no")){
            targetIP = myIP;
        }
    }

    private static final java.net.http.HttpClient httpClient = java.net.http.HttpClient.newBuilder()
            .version(java.net.http.HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public String getGameById(int id, String playerName) {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create("http://"+targetIP+":8080/game/" + id + "/" + playerName))
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
                .uri(URI.create("http://"+targetIP+":8080/game/join/" + id + "/" + playerName))
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
                .uri(URI.create("http://"+targetIP+":8080/game/" + boardName + "/" + numOfPlayers))
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
                .uri(URI.create("http://"+targetIP+":8080/game"))
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
                .uri(URI.create("http://"+targetIP+":8080/board"))
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
                .uri(URI.create("http://"+targetIP+":8080/board/" + gameId))
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
    public String joinGame(int gameId, String playername) {
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(String.valueOf(playername)))
                .uri(URI.create("http://"+targetIP+":8080/game/join/" + gameId))
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
    public String leaveGame(int id, String playerName) {
        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(URI.create("http://"+targetIP+":8080/game/" + id + "/" + playerName))
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
    public String setPlayerState(int id, String playername, String playerData) {
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(playerData))
                .uri(URI.create("http://"+targetIP+":8080/game/" + id + "/" + playername))
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
