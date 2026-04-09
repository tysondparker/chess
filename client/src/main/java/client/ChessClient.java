package client;

import chess.*;
import client.websocket.ServiceMessageHandler;
import client.websocket.WebSocketFacade;
import model.GameData;
import model.requestandresult.*;
import ui.BoardPrinter;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;
import java.util.*;

public class ChessClient implements ServiceMessageHandler {

//user states:
    private UserState userState = UserState.SIGNEDOUT;
    private GameState gameState = GameState.OUTGAME;
//user info:
    private String userName = null;
    private String authToken = null;
    private String userColor = null;
    private GameData userGame = null;
//Game info:
    private List<GameData> lastListedGames = new ArrayList<>();
//Server:
    private final ServerFacade server;
    private final WebSocketFacade ws;

//    Creates Chess Client
    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        ws = new WebSocketFacade(serverUrl, this);
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


    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage loadGameMessage = (LoadGameMessage) message;
                ChessGame game = loadGameMessage.getGame();
                userGame = new GameData(userGame.gameID(), userGame.whiteUsername(), userGame.blackUsername(), userGame.gameName(), game);

                    if("BLACK".equals(userColor)) {
                        BoardPrinter.drawBoard(game, ChessGame.TeamColor.BLACK);
                    } else {
                        BoardPrinter.drawBoard(game, ChessGame.TeamColor.WHITE);
                    }
            }
            case NOTIFICATION -> {
                NotificationMessage notificationMessage = (NotificationMessage) message;
                System.out.println(notificationMessage.getMessage());
            }
            case ERROR -> {
                ErrorMessage errorMessage = (ErrorMessage) message;
                System.out.println(errorMessage.getErrorMessage());
            }
        }
        printPrompt();
    }

//    pre-login UI
    public String login(String... params) throws ClientException {
        if (params.length == 2) {
            String username = params[0];
            String password = params[1];
            LoginRequest request = new LoginRequest(username,password);
            try {
                LoginResult result = server.login(request);
                userName = username;
                authToken = result.authToken();
                userState = UserState.SIGNEDIN;
                return String.format("You signed in as %s.", username);
            } catch (Exception e) {
                throw new ClientException("Wrong Password or Username");
            }
        }
        throw new ClientException("Remember Login <username> <password>");
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
            userState = UserState.SIGNEDIN;

            return String.format("You're successfully signed in as %s.", username);
        }
        throw new ClientException("To Register Enter: register <username> <password> <email>");
    }
    public String help() {
        if (notSignedIn()) {
            return """
                    
                    - help (Actions you can take)
                    - quit (Exit the Program)
                    - Login <username> <password> (Login to play Chess)
                    - register <username> <password> <email> (To Register)
                    """;
        } else if (inGame()) {
            return """
                    
                    - help (Actions you can take)
                    - redraw (redraws the board)
                    - leave (leaves the game)
                    - move <starting square> <ending square> (moves a chess piece)
                    - resign (forfeits the game)
                    - find <chess piece> (finds all possible moves)
                    """;
        } else {
            return """
                    
                    - help (Actions you can take)
                    - Logout (Gets you out of here)
                    - create <Game Name> (Creates a game of Chess)
                    - list (Lists all the Games)
                    - join <ID> <WHITE|BLACK> (Allows you to Play a Game)
                    - observe <ID> (Allows you to observe a game)
                    - quit
                    """;
        }
    }

//    post-login UI
    public String logout() throws ClientException {
        if(userState.equals(UserState.SIGNEDIN)){
            userState = UserState.SIGNEDOUT;
            server.logout(authToken);
            return String.format("%s logged out", userName);
        } else {
            return "Not logged in";
        }
    }
    public String createGame(String... params) throws ClientException {
        if(!notSignedIn()){
            if(params.length == 1) {
                String gameName = params[0];
                CreateGameRequest gameRequest = new CreateGameRequest(gameName);
                server.createGame(gameRequest,authToken);
                return String.format("Created game: %s",gameName);
            } else {
                return "Remember create <Game Name>";
            }

        } else {
            return "Not Logged in";
        }
    }
    public String listGame() throws ClientException{
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

        if(params.length == 2) {
            String gameIndex = params[0];
            String color = params[1].toUpperCase();

            GameData game = getGameData(gameIndex, color);

            String whiteUser = game.whiteUsername();
            String blackUser = game.blackUsername();

            if((color.equals("WHITE") && whiteUser != null) ||
                    (color.equals("BLACK") && blackUser != null)) {
                throw new ClientException("Color already taken in this game. Try again.");
            }

            JoinGameRequest joinGameRequest = new JoinGameRequest(color,game.gameID());
            server.joinGame(joinGameRequest, authToken);

            userGame = game;
            gameState = GameState.INGAME;
            userColor = color;

            ws.connect(authToken,game.gameID());

            return String.format("Successfully joined game: %s", game.gameName());

        } throw new ClientException("Whats the game number and what do you want to play as?");
    }
    public String observeGame (String... params) throws ClientException{
        if(params.length == 1) {
            String gameIndex = params[0];
            int gameIndexInt = Integer.parseInt(gameIndex)-1;

            try {
                GameData game = lastListedGames.get(gameIndexInt);

                gameState = GameState.OBSERVE;
                userColor = "WHITE";
                userGame = game;

                ws.connect(authToken,game.gameID());

                return String.format("Successfully observing game: %s", game.gameName());
            } catch (Exception e) {
                throw new ClientException("Make sure you enter observe <Game Number from List Games>");
            }

        } throw new ClientException("To observe enter observe and the game number");
    }
    public String clear() throws ClientException {
        Scanner clearScanner = new Scanner(System.in);
        System.out.print("Password: ");
        String line = clearScanner.nextLine();
        if (line.equals("tp123")) {
            logout();
            server.clear();
            return "Database Cleared";
        } else {
            return "Wrong Password";
        }
    }

//    game UI
    public String resign() throws ClientException {
        Scanner leaveScanner = new Scanner(System.in);
        System.out.print("Are you sure you want to resign? <yes/no>: ");
        String line = leaveScanner.nextLine().toUpperCase();
        if(line.equals("YES")) {
            ws.resign(authToken, userGame.gameID());
        } else if (line.equals("NO")) {
            System.out.print("I guess you didn't want to resign, alright then.");
        } else {
            throw new ClientException("If you want to resign, you need to type either yes or no!");
        }
        return "resigned";
    }
    public String makeMove(String... params) throws Exception {
//        get and verify the start and end positions
        String startPositionString = params[0];
        String endPositionString = params[1];
        ChessPiece.PieceType promotionPiece = null;

        if (params.length == 3) {
            promotionPiece = ChessPiece.PieceType.valueOf(params[2]);
        }

        ChessMove chessMove = verifyUserChessMove(startPositionString, endPositionString, promotionPiece);
        ws.makeMove(authToken, userGame.gameID(), chessMove);
        return "Websocket sent?";
    }
    public String leave() {
        ws.leave(authToken,userGame.gameID());

        userColor = null;
        userGame = null;
        gameState = GameState.OUTGAME;
        return "you're not in the game anymore";
    }

    public String findMoves(String... params) {
        return null;
    }
    public String redraw() {
        if(userColor.equals("WHITE")) {
            BoardPrinter.drawBoard(userGame.game(), ChessGame.TeamColor.WHITE);
        } else {
            BoardPrinter.drawBoard(userGame.game(), ChessGame.TeamColor.BLACK);
        }
        return "";
    }

//    helpers
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
    private boolean notSignedIn() {
        return userState == UserState.SIGNEDOUT;
    }
    private boolean inGame() {
        return gameState != GameState.OUTGAME;
    }
    private GameData getGameData(String gameIndex, String color) throws ClientException {
        int gameIndexInt = Integer.parseInt(gameIndex)-1;

        if(!color.equals("WHITE") && !color.equals("BLACK")) {
            throw new ClientException("Enter either White or Black");
        }

        try {
            return lastListedGames.get(gameIndexInt);
        } catch (Exception e) {
            throw new ClientException("Make sure you enter join <Game Number from List Games>");
        }
    }

    private ChessMove verifyUserChessMove(String startPositionString, String endPositionString, ChessPiece.PieceType promotionPiece) throws ClientException {

        String startPositionColString = String.valueOf(startPositionString.charAt(0)).toUpperCase();
        int startPositionRow = Integer.parseInt(String.valueOf(startPositionString.charAt(1)));
        
        String endPositionColString = String.valueOf(endPositionString.charAt(0)).toUpperCase();
        int endPositionRow = Integer.parseInt(String.valueOf(endPositionString.charAt(1)));

        Map<String, Integer> numbermap = Map.of(
                "A", 1, "B", 2, "C", 3,
                "D", 4, "E", 5, "F", 6,
                "G", 7, "H", 8
        );

        boolean startPosBoolRow = startPositionRow <= 8 && startPositionRow >= 1;
        boolean endPosBoolRow = endPositionRow <= 8 && endPositionRow >= 1;
        boolean startPosBoolCol = numbermap.containsKey(startPositionColString);
        boolean endPosBoolCol = numbermap.containsKey(endPositionColString);

        if (startPosBoolCol && startPosBoolRow && endPosBoolCol && endPosBoolRow) {
            int startPositionCol = numbermap.get(startPositionColString);
            int endPositionCol = numbermap.get(endPositionColString);

            ChessPosition startPosition = new ChessPosition(startPositionRow,startPositionCol);
            ChessPosition endPosition = new ChessPosition(endPositionRow,endPositionCol);

//            check to see if it's a valid move
            ChessBoard chessBoard = userGame.game().getBoard();
            ChessPiece chessPiece = chessBoard.getPiece(startPosition);
            Collection<ChessMove> possibleMoves = chessPiece.pieceMoves(chessBoard,startPosition);

            ChessMove desiredMove = new ChessMove(startPosition,endPosition, promotionPiece);

            if(possibleMoves.contains(desiredMove)) {
                return desiredMove;
            } else {
                throw new ClientException("Remember, make move uses letters from A-H and numbers 1-8 to make moves \n" +
                        "Example: move <starting>a2 <ending>a3 <piece you want to promote>(optional)");
            }

        } else {
            throw new ClientException("Remember, make move uses letters from A-H and numbers 1-8 to make moves \n" +
                    "Example: move <starting>a2 <ending>a3 <piece you want to promote>(optional)");
        }
    }

//    main eval loop
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
            if(notSignedIn()) {
                return switch (cmd) {
                    case "quit" -> "quit";
                    case "login" -> login(params);
                    case "register","r" -> register(params);
                    case "help" -> help();
                    default -> String.format("Sorry, %s isn't a valid command, try using one of the following:",cmd)+help();
                };
            } else if (inGame()) {
                return switch (cmd) {
                    case "redraw" -> redraw();
                    case "leave" -> leave();
                    case "move","m" -> makeMove(params);
                    case "resign" -> resign();
                    case "find" -> findMoves(params);
                    case "help" -> help();
                    case "logout","create","list","join","observe", "clear", "login","register","quit" -> "You need to leave the game to do that.";
                    default -> String.format("Sorry, %s isn't a valid command, try using one of the following:",cmd)+help();
                };
            } else {
                return switch (cmd) {
                    case "logout" -> logout();
                    case "create","c" -> createGame(params);
                    case "list","l" -> listGame();
                    case "join","j" -> joinGame(params);
                    case "observe" -> observeGame(params);
                    case "help" -> help();
                    case "clear" -> clear();
                    case "find","resign","move","leave","redraw" -> "You must join a game to use those commands.";
                    case "login","register","quit" -> "In order to do this, you must first logout.";
                    default -> String.format("Sorry, %s isn't a valid command, try using one of the following:",cmd)+help();
                };
            }
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }
}

