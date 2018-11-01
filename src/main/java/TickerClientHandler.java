import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;

public class TickerClientHandler implements ClientHandler {
    private Socket client = null;
    @Override
    public void handle(Socket socket, List<String> arguments) {
        client = socket;
        Thread ticker = new Thread(() -> {
            try {
                PrintWriter writer = new PrintWriter(client.getOutputStream(), true);
                DBCitePriceGetter dbCitePriceGetter = new DBCitePriceGetter(writer::println);
                dbCitePriceGetter.run();
            } catch (IOException ex) {
                System.err.println("Unable to send price");
            }
        });
        ticker.start();

    }
}
