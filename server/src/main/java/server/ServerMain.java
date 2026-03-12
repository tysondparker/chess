
package server;

import dataaccess.DatabaseManager;

import javax.xml.crypto.Data;

public class ServerMain {
    public static void main(String[] args) {

        Server server = new Server();
        server.run(8080);

        System.out.println("♕ 240 Chess Server");
    }
}
