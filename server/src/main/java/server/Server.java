package server;

import dataaccess.*;
import service.LoginRequest;
import service.RegisterRequest;
import com.google.gson.Gson;
import io.javalin.*;
import io.javalin.http.Context;
import service.*;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private final UserService service;
    private final DataAccess dataAccess;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        this.dataAccess = new MemoryDataAccess();
        this.service = new UserService(dataAccess);

        javalin.exception(AlreadyTakenException.class,(ex, ctx)-> {
            var body = new Gson().toJson(Map.of("message",String.format(ex.getMessage())));
            ctx.status(403);
            ctx.json(body);
        });

        javalin.exception(BadRequestException.class,(ex, ctx)-> {
            var body = new Gson().toJson(Map.of("message",String.format(ex.getMessage())));
            ctx.status(401);
            ctx.json(body);
        });

        // Register your endpoints and exception handlers here.
        javalin.post("/user", this::register);
        javalin.post("/session", this::login);
        javalin.delete("/db", this::clear);
        javalin.delete("/session", this::logout);
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
        LogoutRequest request = new Gson().fromJson(ctx.body(), LogoutRequest.class);
        service.logout(request);
        ctx.status(200);
        ctx.result("{}");
    }

    private void clear(Context ctx) throws Exception {
        ClearService clearService = new ClearService(dataAccess);
        clearService.clear();
        ctx.status(200);
        ctx.result("{}");
    }
}
