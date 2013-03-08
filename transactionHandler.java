

interface transactionHandler
{
	void depositOrWithdraw(String action);
	
	void buyOrSell(String action);

	void showBalance();
	void showTransactions();

	void showPriceAndActorProfile();

	void showMovieInfo();
	
	void showTopMovies();
}