package client;

import model.UserData;
import model.requestandresult.LoginRequest;
import model.requestandresult.RegisterResult;

import java.util.Arrays;
import java.util.Scanner;

public class ChessClient {

    private State state = State.SIGNEDOUT;
    private String userName = null;
    private String authToken = null;
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
            server.login(request);
            userName = username;
            state = State.SIGNEDIN;
            return String.format("You signed in as %s.", username);
        }
        throw new ClientException("Remember Login <username> <password>");
    }

    public String logout(String... params) throws ClientException {
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

            UserData newUser = new UserData(username,password,email);
            RegisterResult result = server.register(newUser);

            userName = result.username();
            authToken = result.authToken();
            state = State.SIGNEDIN;

            return String.format("You're successfully signed in as %s.", username);
        }
        throw new ClientException("To Register Enter: register <username> <password> <email>");
    }

    public String clear() throws ClientException {
        if(assertSignedIn()){
            Scanner scanner2 = new Scanner(System.in);
            System.out.print("\n" + "Password: ");
            String line = scanner2.nextLine();
            if(line.equals("tp123")){
                server.clear();
                return "Database Cleared";
            } else {
                return "Wrong Password";
            }
        }
        return "Not Signed in";
    }

    public String help() {
        if (!assertSignedIn()) {
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

    private void printPrompt() {
        System.out.print("\n" + ">>> ");
    }

    public String eval(String input) {
        try {
            String[] tokens = input.split(" ");
            String cmd = "";
            if(tokens.length > 0) {
                cmd = tokens[0].toLowerCase();
            } else {
                cmd = "help";
            }
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "logout" -> logout(params);
                case "clear" -> clear();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    private boolean assertSignedIn() {
        return state != State.SIGNEDOUT;
    }
}

