package StarsRusMarket;

/**
 * Class for assisting StarsRusMarket program
 * with manager interaction with the database
 * 
 * @author Arya Pourzanjani & Justin Phang  
 */
public class ManagerInterfaceHandler implements transactionHandler
{
	public void AddInterest()
	{

	}

	public void generateMonthlyStatement()
	{

	}

	public void ListActiveCustomers()
	{

	}

	public void generateTaxReport()
	{

	}

	public void generateCustomerReport(String acctIDnum)
	{

	}

	public void deleteTransactions()
	{
		
	}


	//METHODS FROM TRANSACTION HANDLER

	public void deposit(int acctIDnum, double amount)
	{

	}

	public void withdraw(int acctIDnum, double amount)
	{

	}
	
	public void buy(int acctIDnum, String symbol, int amount)
	{

	}

	public void sell(int acctIDnum, String symbol, int amount, double price)
	{

	}

	public void showBalance(int acctIDnum)
	{

	}

	public void showTransactions(int acctIDnum)
	{

	}

	public void showPriceAndActorProfile(String symbol)
	{

	}

	public void showMovieInfo(String movie)
	{

	}
	
	public void showTopMovies(int startYear, int endYear)
	{

	}
}