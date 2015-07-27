import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;

public class Symbol extends Stock {
	
	// Keep historical data that we have already pulled to speed up getHistory method
	private List<HistoricalQuote> history;
	private int lastDay = 0;
	
	public Symbol(String symbol) throws IOException {
		super(symbol);
		Stock stock = YahooFinance.get(symbol);
		setQuote(stock.getQuote());
		setStats(stock.getStats());
		setDividend(stock.getDividend());
	}
	 
	//Returns History
	public List<HistoricalQuote> getHistory(int months) throws IOException{
		Calendar from = Calendar.getInstance();
		Calendar to = Calendar.getInstance();
		from.add(Calendar.MONTH, - months); //4 Months Ago
		List<HistoricalQuote> quotes = getHistory(from, to, Interval.DAILY);
		return quotes;
	}
	
	/**
	 * Returns list of historical quotes using days
	 * @author Michael Bick
	 * @param daysAgo days ago the first quote is from
	 * @param days amount of days of historical quotes
	 * @return list of historical quotes from time period
	 * @throws IOException
	 */
	public List<HistoricalQuote> getHistory(int daysAgo, int days) throws IOException {
		
		// Create an integer to hold the farthest back day requested
		int fromDay = daysAgo + days;
		// Create an integer to hold what the new last day will be
		int newDay = 2 * fromDay;
		
		// Only get more historical data if we don't have enough stored
		if (lastDay < newDay) {			
			Calendar from = Calendar.getInstance();
			
			// Grab history of more days than necessary. We'll filter out what we don't need later
			from.add(Calendar.DAY_OF_MONTH, - newDay);
			
			// Add neccessary information to the list of history
			List<HistoricalQuote> newHistory = new ArrayList<HistoricalQuote>();
			newHistory.addAll(getHistory(from, Interval.DAILY));
			newHistory.addAll(history);
			history = newHistory;
			
			// Update lastDay since we now have more information
			lastDay = newDay;
		}
		
		// Filter the list down to what we need
		return history.subList(daysAgo, fromDay);
	}
	
	/**
	 * Returns one day of historical information
	 * @author Michael Bick
	 * @param daysAgo amount of days ago to get information from
	 * @return historical quote from a given amount of days ago
	 * @throws IOException
	 */
	public HistoricalQuote getDay(int daysAgo) throws IOException {
		return getHistory(daysAgo, 1).get(0);
	}

	/**
	 * Returns a historical adjusted closing price of a stock
	 * @author Michael Bick
	 * @param daysAgo amount of days ago to get the closing price from
	 * @return historical adjusted closing price
	 * @throws IOException
	 */
	public BigDecimal getAdjClose(int daysAgo) throws IOException {
		return getDay(daysAgo).getAdjClose();
	}
	
	/**
	 * Returns a historical volume of shares traded
	 * @author Michael Bick
	 * @param daysAgo amount of days ago to get the volume from
	 * @return volume of shares traded
	 * @throws IOException
	 */
	public long getVolume(int daysAgo) throws IOException {
		return getDay(daysAgo).getVolume();
	}
	
	/**
	 *  Returns a moving average from a stock's history
	 * @author Michael Bick
	 * @param daysAgo amount of days ago the moving average is from
	 * @param days amount of days to use in the moving average
	 * @return moving average
	 * @throws IOException
	 */
	public BigDecimal getMA(int daysAgo, int days) throws IOException {
		// Gets historical quotes
		List<HistoricalQuote> quotes = getHistory(daysAgo, days);
		
		// Calculate the moving average
		BigDecimal ma = new BigDecimal(0);
		for (HistoricalQuote quote : quotes) {
			ma = ma.add(quote.getAdjClose());
			// System.out.println(quote.getAdjClose());
			// System.out.println(ma);
		}
		ma = ma.divide(new BigDecimal(quotes.size()), 2, RoundingMode.HALF_UP); // Rounds the "regular" way to 2 decimal places
		
		return ma;
	}
	
	/**
	 * Returns an array containing different features to use in a stock prediction
	 * @author Michael Bick
	 * @param daysAgo amount of days ago to get the features from
	 * @return array containing features
	 * @throws IOException
	 */
	public double[] getFeatures(int daysAgo) throws IOException {
		int NUM_FEATURES = 3;
		
		double[] features = new double[NUM_FEATURES + 1];
		
		features[0] = 1.0;
		features[1] = getMA(daysAgo, 50).doubleValue();
		features[2] = getAdjClose(daysAgo).doubleValue();
		features[3] = (double)getVolume(daysAgo);
		// Need function to get EPS
		// Need function to get year high
		
		return features;
	}
	
	public BigDecimal getPrice() {
		return getQuote().getPrice();
	}
	/*
	public BigDecimal getDayHigh() {
		return getQuote().getDayHigh();
	}
	
	public BigDecimal getDayLow() {
		return getQuote().getDayLow();
	}
	public BigDecimal getFtWkHigh(){
		return getQuote().getYearHigh();
	}
	public BigDecimal getFtWkLow(){
		return getQuote().getYearLow();
	}
	public BigDecimal getEPS() {
		return getStats().getEps();
	}
	public long getNumberOfShares() {
		return getStats().getSharesOutstanding();
	}
	public long getVolume() {
		return getQuote().getVolume();
	}
	*/
}
