package StarsRusMarket;

interface transactionHandler
{
	void deposit(int acctIDnum, double amount);
	void withdraw(int acctIDnum, double amount);
	
	void buy(int acctIDnum, String symbol, int amount);
	void sell(int acctIDnum, String symbol, int amount, double price);

	void showBalance(int acctIDnum);
	void showTransactions(int acctIDnum);

	void showPriceAndActorProfile(String symbol);

	void showMovieInfo(String movie);
	
	void showTopMovies(int startYear, int endYear);
}