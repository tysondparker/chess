package client;

import chess.ChessGame;
import model.GameData;
import model.requestandresult.*;
import ui.BoardPrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class ChessClient {

    private State state = State.SIGNEDOUT;
    private GameState gameState = GameState.OUTGAME;
    private String userName = null;
    private String authToken = null;
    private List<GameData> lastListedGames = new ArrayList<>();
    private final ServerFacade server;

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
    }

    public void run(){
        System.out.println(" Welcome to Chess 240. Sign in to start.");
        System.out.print(help());

        Scanner scanner = new Scanner(System.in);
        var result = "";

        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
    }

    public String login(String... params) throws ClientException {
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            LoginRequest request = new LoginRequest(username,password);
            LoginResult result = server.login(request);
            userName = username;
            authToken = result.authToken();
            state = State.SIGNEDIN;
            return String.format("You signed in as %s.", username);
        }
        throw new ClientException("Remember Login <username> <password>");
    }

    public String logout() throws ClientException {
        if(state.equals(State.SIGNEDIN)){
            state = State.SIGNEDOUT;
            server.logout(authToken);
            return String.format("%s logged out", userName);
        } else {
            return "Not logged in";
        }
    }

    public String register(String... params) throws ClientException {
        if (params.length == 3) {
            String username = params[0];
            String password = params[1];
            String email = params[2];

            RegisterRequest newUser = new RegisterRequest(username,password,email);
            RegisterResult result = server.register(newUser);

            userName = result.username();
            authToken = result.authToken();
            state = State.SIGNEDIN;

            return String.format("You're successfully signed in as %s.", username);
        }
        throw new ClientException("To Register Enter: register <username> <password> <email>");
    }

    public String createGame(String... params) throws ClientException {
        if(!notSignedIn()){
            if(params.length == 1) {
                String gameName = params[0];
                CreateGameRequest gameRequest = new CreateGameRequest(gameName);
                CreateGameResult gameResult = server.createGame(gameRequest,authToken);
                return String.format("Game ID is: %d",gameResult.gameID());
            } else {
                return "Bad Request";
            }

        } else {
            return "Not Logged in";
        }
    }

    public String listGame() throws ClientException{
        if(notSignedIn()){
            throw new ClientException("Hey, you're not signed in!");
        }

        ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
        List<GameData> games = server.listGame(listGamesRequest).games();
        lastListedGames = games;
        StringBuilder out = new StringBuilder("Game # | Game Name | White Name | Black Name");

        for (int i = 0; i < games.size(); i++) {
            String result = gameListLoop(games, i);
            out.append(result);
        }
        out.append("\n");
        return out.toString();
    }

    public String joinGame(String... params) throws ClientException{
        if(gameState.equals(GameState.INGAME)){
            throw new ClientException("You're already playing a game");
        }
        if(params.length == 2) {
            if (notSignedIn()) {
                throw new ClientException("Sign in first!");
            }
            String gameIndex = params[0];
            String colorString = params[1];
            String color = colorString.toUpperCase();

            int gameIndexInt = Integer.parseInt(gameIndex)-1;

            if(!color.equals("WHITE") && !color.equals("BLACK")) {
                throw new ClientException("Enter either White or Black");
            }

            GameData game = lastListedGames.get(gameIndexInt);
            String whiteUser = game.whiteUsername();
            String blackUser = game.blackUsername();

            if((color.equals("WHITE") && whiteUser != null) || (color.equals("BLACK") && blackUser != null)) {
                throw new ClientException("Color already taken in this game. Try again.");
            }

            JoinGameRequest joinGameRequest = new JoinGameRequest(color,game.gameID());
            server.joinGame(joinGameRequest, authToken);
            gameState = GameState.INGAME;
            if(color.equals("WHITE")) {
                BoardPrinter.drawBoard(game.game(), ChessGame.TeamColor.WHITE);
            } else {
                BoardPrinter.drawBoard(game.game(), ChessGame.TeamColor.BLACK);
            }

            return String.format("Successfully joined game: %s", game.gameName());

        } throw new ClientException("Whats the game number and what do you want to play as?");
    }

    public String observeGame (String... params) throws ClientException{
        if(!gameState.equals(GameState.OUTGAME)){
            throw new ClientException("You're playing a game right now, can't observe");
        }
        if(params.length == 1) {
            if (notSignedIn()) {
                throw new ClientException("Sign in first!");
            }

            String gameIndex = params[0];
            int gameIndexInt = Integer.parseInt(gameIndex)-1;

            GameData game = lastListedGames.get(gameIndexInt);

            gameState = GameState.OBSERVE;

            BoardPrinter.drawBoard(game.game(), ChessGame.TeamColor.WHITE);

            return String.format("Successfully observing game: %s", game.gameName());

        } throw new ClientException("To observe enter observe and the game number");
    }

    public String clear() throws ClientException {
        if(notSignedIn()){
            Scanner scanner2 = new Scanner(System.in);
            System.out.print("Password: ");
            String line = scanner2.nextLine();
            if(line.equals("tp123")){
                logout();
                server.clear();
                return "Database Cleared";
            } else {
                return "Wrong Password";
            }
        }
        return "Not Signed in";
    }

    public String help() {
        if (notSignedIn()) {
            return """
                    
                    - help (Actions you can take)
                    - quit (Exit the Program)
                    - Login <username> <password> (Login to play Chess)
                    - register <username> <password> <email> (To Register)
                    """;
        }
        return """
                
                - help (Actions you can take)
                - Logout (Gets you out of here)
                - create <GAMENAME> (Creates a game of Chess)
                - list (Lists all the Games)
                - join <ID> <WHITE|BLACK> (Allows you to Play a Game)
                - observe <ID> (Allows you to observe a game)
                - quit
                """;
    }

    private static String gameListLoop(List<GameData> gameList, int i) {
        GameData curGame = gameList.get(i);
        int gameNumber = i +1;
        String white = " ? ";
        String black = " ? ";
        String name = curGame.gameName();

        if (curGame.whiteUsername() != null){
            white = curGame.whiteUsername();
        }
        if (curGame.blackUsername() != null){
            black = curGame.blackUsername();
        }
        return String.format("\n%d.) Game: %s | White: %s | Black: %s",gameNumber,name,white,black);
    }

    private void printPrompt() {
        System.out.print("\n" + ">>> ");
    }

    public String eval(String input) {
        try {
            String[] tokens = input.split(" ");
            String cmd;
            if(tokens.length > 0) {
                cmd = tokens[0].toLowerCase();
            } else {
                cmd = "help";
            }
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout();
                case "clear" -> clear();
                case "quit" -> "quit";
                case "create" -> createGame(params);
                case "join" -> joinGame(params);
                case "list" -> listGame();
                case "observe" -> observeGame(params);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private boolean notSignedIn() {
        return state == State.SIGNEDOUT;
    }
}

