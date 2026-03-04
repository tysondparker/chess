package service;

import dataaccess.*;
import dataaccess.exception.*;
import model.*;
import service.requestandresult.*;

import java.util.List;

public class GameService {

    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public CreateGameResult createGame(CreateGameRequest request, String authToken) throws DataAccessException {
        AuthData token = dataAccess.getAuth(authToken);
        if (token == null) {
            throw new UnauthorizedException("Error: bad request");
        }
        if (request.gameName() == null) {
            throw new BadRequestException("Error: bad request");
        }

        GameData tempGame = new GameData(0,null,null, request.gameName());

        int gameId = dataAccess.createGame(tempGame);
        return new CreateGameResult(gameId);
    }

    public void joinGame(JoinGameRequest request, String authToken) throws DataAccessException {
        AuthData token = dataAccess.getAuth(authToken);
        if (token == null) {
            throw new UnauthorizedException("Error: bad request");
        }
        GameData game = dataAccess.getGame(request.gameID());
        if (game == null) {
            throw new BadRequestException("Error: bad request");
        }
        String Color = request.playerColor();
       if (Color == null || Color.isBlank()) {
            throw new BadRequestException("Error: bad request");
        }
        if(!Color.equals("WHITE") && !Color.equals("BLACK")) {
            throw new BadRequestException("Error: bad request");
        }

        String plyrColor = request.playerColor();

        if (plyrColor.equals("WHITE") && game.whiteUsername() == null) {
            GameData newGameData = new GameData(game.gameID(),token.username(),game.blackUsername(),game.gameName());
            dataAccess.updateGame(newGameData);
        } else if (plyrColor.equals("BLACK") && game.blackUsername() == null) {
            GameData newGameData = new GameData(game.gameID(),game.whiteUsername(),token.username(),game.gameName());
            dataAccess.updateGame(newGameData);
        } else {
            throw new AlreadyTakenException("Error: bad request");
        }

    }

    public List<GameData> listGames(ListGamesRequest request) throws DataAccessException {
        if (dataAccess.getAuth(request.authToken()) == null) {
            throw new UnauthorizedException("Error: bad request");
        }
        return dataAccess.listGame();
    }
}
