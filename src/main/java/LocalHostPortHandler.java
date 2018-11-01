import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class LocalHostPortHandler implements Runnable {

    private ServerSocket serverSocket;

    LocalHostPortHandler(int port){
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException ex){
            return;
        }
    }
    @Override
    public void run() {
        System.out.println("Listen started at port: " + serverSocket.getLocalPort());
        SocketRequestHandler socketRequestHandler = new SocketRequestHandler();
        while (true) {
            try (
                    Socket clientSocket = serverSocket.accept();
            ) {
                System.out.println("Client " + clientSocket.getInetAddress() + ":" + clientSocket.getPort() + " connected");
                socketRequestHandler.handle(clientSocket);
            } catch (IOException ex) {
                System.err.println("Something went wrong stopping listener");
                close();
                return;
            }
        }
    }

    private void close(){
        try{
            serverSocket.close();
        } catch (IOException ex){
            System.err.println(ex.getMessage());
        }
    }
}
