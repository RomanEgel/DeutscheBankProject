import javafx.util.Pair;
import org.patriques.AlphaVantageConnector;
import org.patriques.TimeSeries;
import org.patriques.input.timeseries.Interval;
import org.patriques.input.timeseries.OutputSize;
import org.patriques.output.timeseries.IntraDay;
import org.patriques.output.timeseries.data.StockData;
import javax.annotation.Nonnull;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlphaVantagePriceGetter implements PriceGetter {

    final String apiKey;
    final AlphaVantageConnector apiConnector;
    final String symbol;

    AlphaVantagePriceGetter(@Nonnull AlphaVantageKeyReqirement keyReqirement,@Nonnull String symbol){
        apiKey = keyReqirement.getKey();
        if(apiKey == null){
            throw new IllegalArgumentException("Required valid key");
        }
        int timeout = 700;
        apiConnector = new AlphaVantageConnector(apiKey, timeout);
        this.symbol = symbol;
    }

    @Override
    public Map.Entry<Timestamp, Double> getLatestPrice() {
        IntraDay series = new TimeSeries(apiConnector).intraDay(symbol, Interval.ONE_MIN, OutputSize.COMPACT);
        List<StockData> stockData = series.getStockData();
        Map<Timestamp,  Double> result = new HashMap<>();
        result.put(Timestamp.valueOf(stockData.get(0).getDateTime()),stockData.get(0).getClose());
        return result.entrySet().iterator().next();
    }

    @Override
    public Map<Timestamp, Double> getIntradayStocks() {
        Map<Timestamp, Double> priceMap = new HashMap<>();
        IntraDay series = new TimeSeries(apiConnector).intraDay(symbol, Interval.ONE_MIN, OutputSize.COMPACT);
        List<StockData> stockData = series.getStockData();
        stockData.forEach(item ->{
            priceMap.put(Timestamp.valueOf(item.getDateTime()), item.getClose());
        });
        return priceMap;
    }
}
