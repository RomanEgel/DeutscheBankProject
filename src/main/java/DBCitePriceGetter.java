import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

public class DBCitePriceGetter implements PriceGetter, Runnable {

    private final URL dbUrl = new URL("https://www.xmarkets.db.com/DE/ENG/Underlying-Detail/XC0009677409");

    private final Consumer<Map.Entry<Timestamp,Double>> consumer;

    DBCitePriceGetter(Consumer<Map.Entry<Timestamp,Double>> consumer) throws MalformedURLException{
        this.consumer = consumer;
    }

    @Override
    public Map.Entry<Timestamp, Double> getLatestPrice() {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(dbUrl.openStream()));
        } catch (IOException ex){
            return null;
        }
        List<String> strings = new ArrayList<>(2);
        in.lines().filter(item -> { return item.contains("<span class=\"instrumentDetail-push\"")||item.contains("<span class=\"dateToUpdate\"");})
                               .forEach(item ->{
                                   strings.add(item);
                               });
        Timestamp dateTime = getTimeStamp(strings.get(0));
        Map<Timestamp,Double> resultMap = new HashMap<>();
        resultMap.put(dateTime, getPrice(strings.get(1)));
        return resultMap.entrySet().iterator().next();
    }

    @Override
    public Map<Timestamp, Double> getIntradayStocks() {
        return null;
    }

    private Timestamp getTimeStamp(String findIn){
        String stringDate = findIn.substring(findIn.lastIndexOf("\"dd.MM.yyyy\""), findIn.indexOf(", </span><span source"));
        String stringTime = findIn.substring(findIn.indexOf("\"instrumentDetail-push\">"), findIn.lastIndexOf("</span>"));
        Date date;
        try {
            date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH).parse(stringDate.substring(stringDate.indexOf('>') + 1) + " " + stringTime.substring(stringTime.indexOf('>') + 1));
        } catch (ParseException ex){
            return null;
        }
        return new Timestamp(date.getTime());
    }
    private double getPrice(String findIn){
        String result = findIn.substring(findIn.indexOf('>'), findIn.lastIndexOf("</span><span")).substring(1);
        return Double.valueOf(result);
    }

    @Override
    public void run() {
        while (true){
            Map.Entry<Timestamp, Double> price = getLatestPrice();
            if(price != null){
                consumer.accept(price);
            }
        }
    }
}
