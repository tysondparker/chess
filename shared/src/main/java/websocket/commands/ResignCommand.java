package websocket.commands;

public class ResignCommand extends UserGameCommand{

    public ResignCommand(String authToken, Integer gameID) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
    }

}
