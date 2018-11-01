import java.sql.Timestamp;
import java.util.Map;

public interface PriceGetter {
    Map.Entry<Timestamp, Double> getLatestPrice();
    Map<Timestamp, Double> getIntradayStocks();
}
