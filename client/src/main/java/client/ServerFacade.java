package client;

import com.google.gson.Gson;
import model.GameData;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import model.UserData;

public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String finalUrl;

    public ServerFacade(String url) {
        finalUrl = url;
    }

    public void register(UserData data) throws ClientException {
        var request = buildRequest("POST", "/user",data);
        var response = sendRequest(request);
        handleResponse(response, UserData.class);
    }

    private HttpResponse<String> sendRequest(HttpRequest request) throws ClientException {
        try {
            return client.send(request,HttpResponse.BodyHandlers.ofString());
        } catch (Exception ex) {
            throw new ClientException();
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
                throw new ClientException();
            }
            throw new ClientException();
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
