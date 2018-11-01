import pl.zankowski.iextrading4j.api.stocks.Quote;
import pl.zankowski.iextrading4j.client.*;
import pl.zankowski.iextrading4j.client.rest.request.stocks.QuoteRequestBuilder;

import java.sql.Timestamp;
import java.util.Map;

public class IEXPriceGettet implements PriceGetter {

    final String symbol;
    final IEXTradingClient iexTradingClient;

    IEXPriceGettet(String symbol){
        this.symbol = symbol;
        this.iexTradingClient = IEXTradingClient.create();
    }

    public Map.Entry<Timestamp, Double> getLatestPrice() {
        final Quote quote = iexTradingClient.executeRequest(new QuoteRequestBuilder()
                .withSymbol("BNO")
                .build());
        System.out.println(quote);
        return null;
    }

    public Map<Timestamp, Double> getIntradayStocks() {
        return null;
    }
}
