import java.util.Scanner;

public class AlphaVantageKeyReqirement implements KeyRequirement {

    @Override
    public String getKey() {
        System.out.print("Alpha Vantage API key required: ");
        Scanner scanner = new Scanner(System.in);
        if(scanner.hasNext()){
            return scanner.next();
        }
        return null;
    }
}
