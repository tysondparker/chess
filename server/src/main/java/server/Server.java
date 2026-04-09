package server;

import dataaccess.*;
import dataaccess.exception.*;
import model.GameData;
import model.requestandresult.*;
import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import server.websocket.WebSocketHandler;
import service.*;
import service.UserService;

import java.util.List;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.ws;

public class Server {

    private final Javalin javalin;
    private final UserService service;
    private final GameService gameService;
    private final ClearService clearService;
    private final WebSocketHandler webSocketHandler;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        DataAccess dataAccess;
        try {
            dataAccess = new MySqlDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }

        this.service = new UserService(dataAccess);
        this.gameService = new GameService(dataAccess);
        this.clearService = new ClearService(dataAccess);

        webSocketHandler = new WebSocketHandler(dataAccess);

//        SqlError 500
        javalin.exception(SqlDataAccessException.class,(ex, ctx)-> {
            var body = new Gson().toJson(Map.of("message",String.format(ex.getMessage())));
            ctx.status(500);
            ctx.json(body);
        });

//        Already taken 403
        javalin.exception(AlreadyTakenException.class,(ex, ctx)-> {
            var body = new Gson().toJson(Map.of("message",String.format(ex.getMessage())));
            ctx.status(403);
            ctx.json(body);
        });

//        Unauthorized 401
        javalin.exception(UnauthorizedException.class,(ex, ctx)-> {
            var body = new Gson().toJson(Map.of("message",String.format(ex.getMessage())));
            ctx.status(401);
            ctx.json(body);
        });

//        Already taken 400
        javalin.exception(BadRequestException.class,(ex, ctx)-> {
            var body = new Gson().toJson(Map.of("message",String.format(ex.getMessage())));
            ctx.status(400);
            ctx.json(body);
        });

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);

        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);
        javalin.get("/game",this::listGame);

        javalin.delete("/db", this::clear);
        javalin.delete("/session", this::logout);

        javalin.ws("/ws", ws -> {
            ws.onConnect(webSocketHandler);
            ws.onMessage(webSocketHandler);
            ws.onClose(webSocketHandler);
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(Context ctx) throws Exception {
        RegisterRequest request = new Gson().fromJson(ctx.body(), RegisterRequest.class);
        RegisterResult result = service.register(request);
        ctx.result(new Gson().toJson(result));
    }

    private void login(Context ctx) throws Exception {
        LoginRequest request = new Gson().fromJson(ctx.body(), LoginRequest.class);
        LoginResult result = service.login(request);
        ctx.result(new Gson().toJson(result));
    }

    private void logout(Context ctx) throws Exception {
        String authToken = ctx.header("Authorization");
        LogoutRequest request = new LogoutRequest(authToken);
        service.logout(request);
        ctx.status(200);
        ctx.result("{}");
    }

    private void clear(Context ctx) throws Exception {
        clearService.clear();
        ctx.status(200);
        ctx.result("{}");
    }

    private void createGame(Context ctx) throws Exception {
        String authToken = ctx.header("Authorization");
        CreateGameRequest request = new Gson().fromJson(ctx.body(),CreateGameRequest.class);
        CreateGameResult result = gameService.createGame(request,authToken);
        ctx.status(200);
        ctx.result(new Gson().toJson(result));
    }

    private void joinGame(Context ctx) throws Exception {
        String authToken = ctx.header("Authorization");
        JoinGameRequest request = new Gson().fromJson(ctx.body(),JoinGameRequest.class);
        gameService.joinGame(request,authToken);
        ctx.status(200);
        ctx.result("{}");
    }

    private void listGame(Context ctx) throws Exception {
        String authToken = ctx.header("Authorization");
        ListGamesRequest request = new ListGamesRequest(authToken);
        List<GameData> games = gameService.listGames(request);
        ctx.result(new Gson().toJson(Map.of("games",games)));
        ctx.status(200);
    }
}
