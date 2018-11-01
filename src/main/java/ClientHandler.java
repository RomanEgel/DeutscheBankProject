import java.net.Socket;
import java.util.List;

public interface ClientHandler {
    void handle(Socket client, List<String> arguments);
}
