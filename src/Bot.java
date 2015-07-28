import java.io.IOException;
import java.util.Scanner;

public class Bot {
	static final String ds = "$";
	
	public static void main(String[] args) throws IOException {
		Symbol symbol;
		Scanner sc = new Scanner(System.in);
		
		/*
		// Get a stock sybol to check
		System.out.print("Stock: ");
		String stock = sc.next();
		symbol = new Symbol(stock);
		*/
		
		symbol = new Symbol("AAPL");
		
		System.out.println("Symbol: " + symbol.getSymbol());
		System.out.println("Price: " + ds + symbol.getPrice());
		// System.out.println("Shares: " + symbol.getNumberOfShares());
		System.out.println("Moving Average: " + symbol.getMA(0, 200));
		System.out.println("Moving Average: " + symbol.getMA(0, 50));
		
		sc.close();
	}
}
