package client;

import com.google.gson.Gson;
import model.GameData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import model.UserData;
import model.requestandresult.LoginRequest;
import model.requestandresult.LogoutRequest;
import model.requestandresult.RegisterResult;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String finalUrl;

    public ServerFacade(String url) {
        finalUrl = url;
    }

    public RegisterResult register(UserData data) throws ClientException {
        var request = buildRequest("POST", "/user",data);
        var response = sendRequest(request);
        return handleResponse(response, RegisterResult.class);
    }

    public void login(LoginRequest loginRequest) throws ClientException {
        var request = buildRequest("POST", "/session",loginRequest);
        var response = sendRequest(request);
        handleResponse(response, LoginRequest.class);
    }

    public void logout(String authToken) throws ClientException {
        LogoutRequest logRequest = new LogoutRequest(authToken);
        var request = buildRequest("DELETE", "/session", logRequest);
        var response = sendRequest(request);
    }

    public void clear() throws ClientException {
        var request = buildRequest("DELETE", "/db",null);
        var response = sendRequest(request);
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ClientException {
        try {
            return client.send(request,HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ClientException("Bad Request");
        }
    }

    private HttpRequest buildRequest(String method, String path, Object body) {
        var request = HttpRequest.newBuilder().uri(URI.create(finalUrl + path)).method(method,makeRequestBody(body));
        if (body != null) {
            request.setHeader("Content-Type", "application/json");
        }
        return request.build();
    }

    private HttpRequest.BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return HttpRequest.BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return HttpRequest.BodyPublishers.noBody();
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass) throws ClientException {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
                throw new ClientException("Bad input, try again");
            }
            throw new ClientException("Bad input, try again");
        }

        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }

        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}
