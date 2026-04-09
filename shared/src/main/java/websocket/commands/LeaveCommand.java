package websocket.commands;

public class LeaveCommand extends UserGameCommand{

    public LeaveCommand(String authToken, Integer gameID) {
        super(CommandType.MAKE_MOVE, authToken, gameID);
    }

}
