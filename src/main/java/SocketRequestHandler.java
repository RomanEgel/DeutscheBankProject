import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.regex.Matcher;

public class SocketRequestHandler {

    private Map<String, ClientHandler> rules;

    private Socket client = null;

    private Map<Socket, Thread> tasks;

    SocketRequestHandler() {
        rules = new HashMap<>();
        rules.put("TICKER", new TickerClientHandler());
    }

    public void handle(Socket clientSocket){
        client = clientSocket;
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                ){
            List<String> arguments = parseArguments(reader);
            ClientHandler handler = rules.get(arguments.iterator().next());
            if(handler != null){
                handler.handle(clientSocket, arguments);
            } else{
                noCommandResponse();
            }
        } catch (IOException ex){
            System.err.println("Unable to read client arguments: " + ex.getMessage());
        }
    }
    private List<String> parseArguments(BufferedReader in){
        String commandString = in.lines().findFirst().get();
        return Arrays.asList(commandString.split("\\s+"));
    }

    private void noCommandResponse(){
        try {
            System.out.println("No such command");
            PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
            writer.println("No such command\n");
            writer.close();
        } catch (IOException ex){
            System.err.println("Unable to send response: " + ex.getMessage());
        }
    }
}
