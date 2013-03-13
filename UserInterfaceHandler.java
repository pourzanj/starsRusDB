import java.io.*;
import java.sql.*;

/**
 * Class for assisting StarsRusMarket program
 * with user interaction with the database
 * 
 * @author Arya Pourzanjani & Justin Phang  
 */
public class UserInterfaceHandler implements transactionHandler
{
	private int acctIDnum;
	private ConnectionHandler myC;
	
	private Date getTodaysDate()
	{	
		String query_lastDate = "select max(bdate) from balances";

	    Date lastUpdateDate = null;
	    try{
			Statement stmt = myC.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(query_lastDate);
			rs.next();
			lastUpdateDate = rs.getDate("max(bdate)");
		} catch(Exception e){
			System.out.println("Error getting last date. Exiting.");
			System.exit(0);
		}

		return lastUpdateDate;

	}
	
	private double getCurrentPrice(String sym)
	{
		double currentprice = -1;
		String currentPriceQuery = "select currentprice from actorStock where symbol = '" + sym + "'";
		try{
			Statement stmt = myC.getConnection().createStatement();
			ResultSet currPriceRS = stmt.executeQuery(currentPriceQuery);
			currPriceRS.next();
			currentprice =currPriceRS.getDouble("currentprice");
		} catch(Exception e){
			System.out.println("Error getting current stock price. Exiting.");
			System.exit(0);
		}

		return currentprice;
	}

	private double getCurrentBalance()
	{
		String balanceQuery = "select * from customerProfile where taxid = " + acctIDnum;

		double currentBalance = 0;
	    try{
			Statement stmt = myC.getConnection().createStatement();
			ResultSet balanceRS = stmt.executeQuery(balanceQuery);
			balanceRS.next();
			currentBalance = balanceRS.getDouble("acctbalance");
		} catch(Exception e){
			System.out.println("Error checking account balance. Exiting.");
			System.exit(0);
		}

		return currentBalance;
	}

	private int getTransactionNumber()
	{
	    int tID = -1;
	    String queryResult = "select * from transactions";

	    ResultSet rsTnum = null;
		try{
			Statement stmt = myC.getConnection().createStatement();
			rsTnum = stmt.executeQuery(queryResult);
		} catch(Exception e){
			System.out.println("Error getting new transactionid. Exiting.");
			System.exit(0);
		}

		//check if this is the first transaction of the month or not and set appropriate tID
		boolean noTransactionsThisMonth = true;
		try {
			while(rsTnum.next()) noTransactionsThisMonth = false; //if we go through this once then the query is NOT empty
		} catch (SQLException sqle) {
			System.out.println("Error getting new transactionID. Exiting.");
			System.exit(0);
		}

		if(noTransactionsThisMonth == true) tID = 1; //if so transaction ID is 1
		else{//else find highest transaction that occurred
			String lastTranNum = "select max(transactionID) from transactions";
			Statement stmt = null;
			ResultSet lastTidRS = null;
			try{
				stmt = myC.getConnection().createStatement();
			} catch(Exception e){
				System.out.println("Error getting new transactionID. Exiting.");
				System.exit(0);
			}
			try{
				lastTidRS = stmt.executeQuery(lastTranNum);
			} catch(Exception e){
				System.out.println("Error getting new transactionID. Exiting.");
				System.exit(0);
			}
			try{
				lastTidRS.next();
			} catch(Exception e){
				System.out.println("Error getting new transactionID. Exiting.");
				System.exit(0);
			}
			try{
				tID = lastTidRS.getInt("MAX(TRANSACTIONID)") + 1;
			} catch(SQLException e){
				System.out.println( "Error Code " + e.getErrorCode() );
				System.out.println( "State " + e.getSQLState() );
				System.out.println("Error getting new transactionID. Exiting.");
				System.exit(0);
			}
		}

		return tID;
	}

	public UserInterfaceHandler(String username, ConnectionHandler C)
	{

		myC = C;

		//make sure market is open
		String openline = "";
		try{
			BufferedReader br = new BufferedReader(new FileReader("OpenOrClosed.txt"));
			openline = br.readLine();
			br.close();
		} catch (Exception e) {
			System.out.println("Error checking market status. Exiting");
	    	System.exit(0);
		}
		if(openline.equals("Closed"))
		{
			System.out.println("Sorry market is close, come back later. Exiting");
	    	System.exit(0);
		}


		//get acctIDnum
		String queryResult = "select * from screenname S, customerProfile C where username = '" + username + "'" +
			" AND S.taxid = C.taxid";

		try{
			Statement stmt = myC.getConnection().createStatement();
			ResultSet rs = stmt.executeQuery(queryResult);
			rs.next();
	    	acctIDnum = rs.getInt("taxid");
	    } catch (Exception e) {
	    	System.out.println("Error retrieving acccount ID in user interface handler. Exiting");
	    	System.exit(0);
	    }

		System.out.println("Welcome to the StarsRus Market!");

		//Allow user to continually execute desired actions
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String command = null;

		boolean shouldContinue = true;

		//continue until user selects quit
		while( shouldContinue )
		{
			System.out.println("Please enter a command or type help for more options.");

	      	try {
	      	   command = br.readLine();
	      	} catch (IOException ioe) {
	      	   System.out.println("Error Reading Command. Exiting.");
	      	}
			
	      	switch( command )
	      	{
	      		case "deposit":
	      			depositOrWithdraw("deposit");
	      			break;
	      		
	      		case "withdraw":
	      			depositOrWithdraw("withdraw");
	      			break;

	      		case "buy":
	      			buyOrSell("buy");
	      			break;

	      		case "sell":
	      			buyOrSell("sell");
	      			break;

	      		case "balance":
	      			showBalance();
	      			break;

	      		case "transactions":
	      			showTransactions();
	      			break;

	      		case "stockinfo":
	      			showPriceAndActorProfile();
	      			break;

	      		case "movieinfo":
	      			showMovieInfo();
	      			break;

	      		case "topmovies":
	      			showTopMovies();
	      			break;

	      		case "quit":
	      			System.out.println("Thank you for using the StarsRus Market. We hope to see you soon");
	      			shouldContinue = false;
	      			break;

	      		case "help":
				System.out.println("Here is a list of valid commands.");
				System.out.println(" deposit: Deposits money to balance");
				System.out.println(" withdraw: Withdraws money from balance");
				System.out.println(" buy: Purchase a specified number of shares for a stock");
				System.out.println(" sell: Sell a specified number of shares for a stock");
				System.out.println(" balance: lists the current balance");
				System.out.println(" transactions: Lists the transactions for a given account");
				System.out.println(" stockinfo: Show the price and profile of a given stock symbol");
				System.out.println(" movieinfo: Displays movie reviews");
				System.out.println(" topmovies: Displays the 5-star movie titles given a time interval");
				System.out.println(" quit: Exits the program");
				System.out.println(" help: Takes you here");
	      			break;

	      		default:
	      			System.out.println("Command Not Recognized. Try again.");
	      			break;
	      	}

		}
	}

	public void depositOrWithdraw(String action)
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String commandArg = null;

		System.out.println("Please enter how much you would like to deposit in dollars.");
	      			
	    try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error Reading Command. Exiting.");
			System.exit(0);
		}

		double amount = 0;
		try {
	    	amount = Double.valueOf( commandArg );
	    } catch (NumberFormatException nfe) {
	    	System.out.println("Entered value is not a number. Exiting.");
			System.exit(0);
	    }

	    try {
	    	if( amount <= 0 ) throw new NegativeNumberException();
	    } catch (NegativeNumberException nne) {
	    	System.out.println("Deposit and withdraw Values must be positive. Exiting.");
			System.exit(0);
	    }

	    if(action.equals("deposit"))
	    {
	    	String update = "update customerProfile SET acctbalance = acctbalance + " + amount + " WHERE taxid=" + acctIDnum;
	    	try{
		    	Statement stmt = myC.getConnection().createStatement();
				int ex = stmt.executeUpdate(update);
			} catch (Exception e){
				System.out.println("Error depositing to database. Exiting.");
				System.exit(0);
			}
			System.out.println("Deposited " + amount + " dollars.");
	    }
	    else
	    {
	    	//first check if there's enough to withdraw
	    	String queryResult = "select * from customerProfile where taxid = " + acctIDnum;

	    	double currentBalance = 0;
	    	try{
				Statement stmt = myC.getConnection().createStatement();
				ResultSet rs = stmt.executeQuery(queryResult);
				rs.next();
				currentBalance = rs.getDouble("acctbalance");
			} catch(Exception e){
				System.out.println("Error checking account balance. Exiting.");
				System.exit(0);
			}

	    	if(currentBalance < amount)
	    		System.out.println("Sorry, you do not have that much cash available.");
	    	else {
	    		String update = "update customerProfile SET acctbalance = acctbalance - " + amount + " WHERE taxid=" + acctIDnum;
		    	try{
			    	Statement stmt = myC.getConnection().createStatement();
					int ex = stmt.executeUpdate(update);
				} catch (Exception e){
					System.out.println("Error depositing to database. Exiting.");
					System.exit(0);
				}
	    		System.out.println("Withdrew " + amount + " dollars.");
	    	}
	    }    
	}

	public void buyOrSell(String action)
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String commandArg = null;

		System.out.println("Please enter what symbol you would like to buy or sell.");
		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error reading command. Exiting.");
			System.exit(0);
		}
		String stockSymbol = commandArg;

		//Check if symbol exists
		String queryResult = "select * from actorStock where symbol = '" + stockSymbol + "'";

		ResultSet rs = null;
	    try{
			Statement stmt = myC.getConnection().createStatement();
			rs = stmt.executeQuery(queryResult);
		} catch(Exception e){
			System.out.println("Error looking up stock symbol. Exiting.");
			System.exit(0);
		}

		boolean empty = true;
		try{
			try {
				while(rs.next()) empty = false; //if we go through this once then the query is NOT empty
			}
			catch(SQLException sqle){
				System.out.println("Error looking up stock symbol. Exiting.");
				System.exit(0);
			}
			if(empty == true) throw new InvalidStockException();
		} catch (InvalidStockException ISE) {
			System.out.println("The stock you selected does not exist. Exiting.");
			System.exit(0);
		}

		System.out.println("Please enter the amount you would like to buy or sell.");
		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error reading command. Exiting.");
			System.exit(0);
		}

		int amount = 0;
		try {
	    	amount = Integer.valueOf( commandArg );
	    } catch (NumberFormatException nfe) {
	    	System.out.println("Entered value is not an integer. Exiting.");
			System.exit(0);
	    }

		try {
	    	if( amount <= 0 ) throw new NegativeNumberException();
	    } catch (NegativeNumberException nne) {
	    	System.out.println("You can only buy or sell positive values of stocks. Exiting.");
			System.exit(0);
	    }

	    double boughtAmount = 0;
	    if(action.equals("sell")) {
	    	System.out.println("Please enter the price you bought your stock at for tax purposes.");

		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error Reading Command. Exiting.");
			System.exit(0);
		}

		try {
	    		boughtAmount = Double.valueOf( commandArg );
	    	} catch (NumberFormatException nfe) {
	    		System.out.println("Entered value is not a number. Exiting.");
				System.exit(0);
	    	}

		try {
	    		if( boughtAmount <= 0 ) throw new NegativeNumberException();
	    	} catch (NegativeNumberException nne) {
	    		System.out.println("Stocks can not be bought at negative values. Exiting.");
				System.exit(0);
	    	}
	    }
	    //~~EXECUTE TRADE in DATABASE
	    //MAKE SURE USER HAS ENOUGH MONEY TO EXECUTE THE TRADE
	    int comissionCost = 20;
	    //SELL
	    if(action.equals("sell"))
	    {
	    	//(1)create transaction in database
	    	//make sure user has enough of the stock to sell how much he wants to
	    	String qtyQuery = "select * from stockaccount where symbol = '" + stockSymbol + "'" +
	    		" AND taxid = " + acctIDnum;
	    	int totalquantityAvailable = -1;
		    try{
				Statement stmt = myC.getConnection().createStatement();
				ResultSet rsTotQtyAvailRS = stmt.executeQuery(qtyQuery);
				rsTotQtyAvailRS.next();
				totalquantityAvailable = rsTotQtyAvailRS.getInt("totalquantity");
			} catch(Exception e){
				System.out.println("Error checking available quantity. Exiting.");
				System.exit(0);
			}
			try{
				if(totalquantityAvailable < amount) throw new QuantityTooLowException();
			} catch (QuantityTooLowException qtle){
				System.out.println("You do not have enough of this stock to sell the desired amount. Exiting.");
				System.exit(0);
			}

	    	//get transaction number
	    	int tID = getTransactionNumber();

			//get date
			Date todaysDate = getTodaysDate();

			//format date string properly
			int startingMonth = todaysDate.getMonth();
			int startingDay = todaysDate.getDate();
			int startingYear = todaysDate.getYear() - 100;
			String monthString = null;
					switch( startingMonth ){
						case 0:
							monthString = "jan";
							break;
						case 1:
							monthString = "feb";
							break;
						case 2:
							monthString = "mar";
							break;
						case 3:
							monthString = "apr";
							break;
						case 4:
							monthString = "may";
							break;
						case 5:
							monthString = "jun";
							break;
						case 6:
							monthString = "jul";
							break;
						case 7:
							monthString = "aug";
							break;
						case 8:
							monthString = "sep";
							break;
						case 9:
							monthString = "oct";
							break;
						case 10:
							monthString = "nov";
							break;
						case 11:
							monthString = "dec";
							break;
					}

					String dayString = null;
					if(startingDay < 9)
						dayString = "0" + (startingDay);
					else
						dayString = "" + (startingDay);

			String dateQueryString = "'" + dayString + "-" + monthString + "-" + startingYear + "'";


			//get current price
			double currentprice = getCurrentPrice( stockSymbol );

			//insert transaction
	    	String insertTransUpdate = "insert into transactions values(" + tID + "," + amount + "," +
	    		dateQueryString + "," + "'s'" + "," + currentprice + "," + boughtAmount + "," + acctIDnum + ",'" + stockSymbol + "')";
	    	try{
		    	Statement stmt = myC.getConnection().createStatement();
				int ex = stmt.executeUpdate(insertTransUpdate);
			} catch (Exception e){
				System.out.println("Error inserting new transaction in database. Exiting.");
				System.exit(0);
			}
			
			//(2)update balance
			String acctBalanceUpdate = "update customerProfile SET acctbalance = acctbalance + " + (amount*(currentprice-boughtAmount)-comissionCost) +
				" WHERE taxid=" + acctIDnum;
			try{
		    	Statement stmt = myC.getConnection().createStatement();
				int ex = stmt.executeUpdate(acctBalanceUpdate);
			} catch (Exception e){
				System.out.println("Error updating balance. Exiting.");
				System.exit(0);
			}

			//(3)update stock account
			String stockAcctUpdate = "update stockAccount SET totalquantity = totalquantity - " + amount +
				" WHERE taxid=" + acctIDnum + " AND symbol = '" + stockSymbol + "'";
			try{
		    	Statement stmt = myC.getConnection().createStatement();
				int ex = stmt.executeUpdate(stockAcctUpdate);
			} catch (Exception e){
				System.out.println("Error updating stock account. Exiting.");
				System.exit(0);
			}
			System.out.println("Sold " + amount + " of " + stockSymbol);
	    }
	    else //BUY
	    {
	    	//first check if there's enough to withdraw
	    	double currentBalance = getCurrentBalance();
	    	double currentPrice = getCurrentPrice(stockSymbol);

	    	if(currentBalance < (amount*currentPrice-comissionCost))
	    		System.out.println("Sorry, you do not have enough cash available for this trade.");
	    	else {
	    		//withdraw cash
	    		String update = "update customerProfile SET acctbalance = acctbalance - " + (amount*currentPrice-comissionCost) + " WHERE taxid=" + acctIDnum;
		    	//System.out.println("withdraw string: " + update);
		    	try{
			    	Statement stmt = myC.getConnection().createStatement();
				int ex = stmt.executeUpdate(update);
			} catch (Exception e){
				System.out.println("Error taking cash out of account for trade. Exiting.");
				System.exit(0);
			}

			//get transaction number
	    		int tID = getTransactionNumber();

	    		//get date
			//Date todaysDate = getTodaysDate();

			//get current price
			double currentprice = getCurrentPrice( stockSymbol );

			//insert transaction
			String insertTransUpdate = "insert into transactions values(" + tID + "," + amount + "," +
		    	"sysdate" + "," + "'b'" + "," + currentprice + "," + boughtAmount + "," + acctIDnum + ",'" + stockSymbol + "')";
		    	try {
			    	Statement stmt = myC.getConnection().createStatement();
				int ex = stmt.executeUpdate(insertTransUpdate);
			} catch (Exception e){
				System.out.println("Error inserting new transaction in database. Exiting.");
				System.exit(0);
			}

			//update stock account
			String stockAcctUpdate = "update stockAccount SET totalquantity = totalquantity + " + amount +
				" WHERE taxid=" + acctIDnum + " AND symbol = '" + stockSymbol + "'";
			try{
			    	Statement stmt = myC.getConnection().createStatement();
				int ex = stmt.executeUpdate(stockAcctUpdate);
			} catch (Exception e){
				System.out.println("Error updating stock account. Exiting.");
				System.exit(0);
			}

	    	}
	    	
	    	System.out.println("Bought " + amount + " of " + stockSymbol);
	    }
	    	
	}

	public void showBalance()
	{
		System.out.println("Here is the balance:");
		try {
			Statement st = myC.getConnection().createStatement();
			ResultSet rs = st.executeQuery("select * from customerProfile where taxid = '" + acctIDnum + "'");
			rs.next();
			System.out.println("Current account balance: " + rs.getDouble("acctbalance"));
		}
		catch (SQLException e) {
			System.out.println("Unable to show the balance. Exiting.");
			System.exit(0);
		}
	}

	public void showTransactions()
	{
		System.out.println("Here are the transactions:");
		try {
			Statement st = myC.getConnection().createStatement();
			ResultSet rs = st.executeQuery("select * from transactions");
			while (rs.next()) {
				System.out.println("Transaction info:");
				System.out.println(" Transaction ID: " + rs.getInt(1));
				System.out.println(" Quantity: " + rs.getInt(2));
				System.out.println(" Date: " + rs.getDate(3));
				System.out.println(" Buy/Sell: " + rs.getString(4));
				System.out.println(" Price: " + rs.getDouble(5));
				System.out.println(" Bought price: " + rs.getDouble(6));
				System.out.println(" Tax ID: " + rs.getInt(7));
				System.out.println(" Symbol: " + rs.getString(8));
			}
		} catch (SQLException e) {
			System.out.println("Unable to list the transactions. Exiting.");
			System.exit(0);
		}
	}

	public void showPriceAndActorProfile()
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String commandArg = null;

		System.out.println("Please enter what symbol you would like to look up the price and profile of.");
		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error Reading Command. Exiting.");
			System.exit(0);
		}
		String stockSymbol = commandArg;

		String queryResult = "select * from actorStock where symbol = '" + stockSymbol + "'";
		ResultSet rs = null;
	        try{
			Statement stmt = myC.getConnection().createStatement();
			rs = stmt.executeQuery(queryResult);
		} catch(Exception e){
			System.out.println("Error looking up stock symbol. Exiting.");
			System.exit(0);
		}

		boolean empty = true;
		try{
			try {
				while(rs.next()) empty = false; //if we go through this once then the query is NOT empty
			}
			catch(SQLException sqle){
				System.out.println("Error looking up stock symbol. Exiting.");
				System.exit(0);
			}
			if(empty == true) throw new InvalidStockException();
		} catch (InvalidStockException ISE) {
			System.out.println("The stock you selected does not exist. Exiting.");
			System.exit(0);
		}
		String query = "select * from actorStock A where symbol ='" + stockSymbol +"'";
		try {
			Statement stmt = myC.getConnection().createStatement();
			rs = stmt.executeQuery(query);
		} catch(Exception e){
			System.out.println("Error looking up customer profile. Exiting.");
			System.exit(0);
		}

		try {
			while (rs.next()) {
				System.out.println("Actor Profile:");
				System.out.println(" Symbol: " + rs.getString(1));
				System.out.println(" Name: " + rs.getString(2));
				System.out.println(" Date of birth: " + rs.getString(3));
				System.out.println(" Current price: " + rs.getDouble(4));
			}
		}
		catch (SQLException sqle) {
			System.out.println("Error looking up customer profile. Exiting.");
			System.exit(0);
		}

	}

	public void showMovieInfo()
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String commandArg = null;

		System.out.println("Please enter what movie you'd like to see info for.");
		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error reading command. Exiting.");
			System.exit(0);
		}
		String movieName = commandArg;
		boolean empty = true;
		String queryResult = "select r_id, m_id, m_name, m_year, r_author, r_review, m_ranking from cs174a.movies, cs174a.reviews where m_id = r_mid and m_name='" + movieName + "'";
		ResultSet rs = null;
		try {
			Statement stmt = myC.getConnection().createStatement();
			rs = stmt.executeQuery(queryResult);
		} catch (SQLException e) {
			System.out.println("Error looking up the movie reviews. Exiting.");
			System.exit(0);
		}
		try {
			try {
				while (rs.next()) empty = false;
			} catch (SQLException sqle) {
				System.out.println("Error looking up the movie reviews. Exiting.");
			}
			if (empty == true) throw new InvalidMovieException();
		} catch (InvalidMovieException e) {
			System.out.println("This movie has no reviews. Exiting.");
			System.exit(0);
		}
		try {
			Statement stmt = myC.getConnection().createStatement();
			rs = stmt.executeQuery(queryResult);
		} catch (Exception e) {
			System.out.println("Error looking up the movie reviews. Exiting.");
			System.exit(0);
		}
		try {
			while (rs.next()) {
				System.out.println("Movie review info:");
				System.out.println(" Review ID: " + rs.getInt(1));
				System.out.println(" Movie ID: " + rs.getInt(2));
				System.out.println(" Movie Name: " + rs.getString(3));
				System.out.println(" Movie Year: " + rs.getInt(4));
				System.out.println(" Review Author: " + rs.getString(5));
				System.out.println(" Review: " + rs.getString(6));
				System.out.println(" Ranking: " + rs.getDouble(7));
			}
		} catch(Exception e) {
			System.out.println("Error looking up the movie reviews.");
			System.exit(0);
		}
	}
	
	public void showTopMovies()
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String commandArg = null;

		System.out.println("Please enter the starting year of the period you'd like to find top movies in.");

		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error reading command. Exiting.");
			System.exit(0);
		}

		int startYear = 0;
		try {
	    		startYear = Integer.valueOf( commandArg );
	    	} catch (NumberFormatException nfe) {
	    		System.out.println("Entered value is not an integer. Exiting.");
			System.exit(0);
	    	}

	    	System.out.println("Please enter the ending year of the period you'd like to find top movies in.");
	    
		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error reading command. Exiting.");
			System.exit(0);
		}

	    	int endYear = 0;
		try {
	    		endYear = Integer.valueOf( commandArg );
	    	} catch (NumberFormatException nfe) {
	    		System.out.println("Entered value is not an integer. Exiting.");
			System.exit(0);
	    	}
		if (startYear > endYear) {
			System.out.println("Error: The starting year is earlier than the ending year. Exiting.");
			System.exit(0);
		}

		ResultSet rs = null;
		String query = "select m_name from cs174a.movies where m_year >= " + startYear + " and m_year <= " + endYear + " and (m_ranking = 5 or m_ranking = 5.0)";
		try {
			Statement stmt = myC.getConnection().createStatement();
			rs = stmt.executeQuery(query);
		} catch (SQLException e) {
			System.out.println("Error looking up the movie. Exiting.");
			System.exit(0);
		}
		try {
			while (rs.next()) {
				System.out.println("Top movies made between " + startYear + " and " + endYear + ":");
				System.out.println(" Movie name: " + rs.getString(1));
			}
		} catch (SQLException e) {
			System.out.println("Error looking up the movie. Exiting.");
			System.exit(0);
		}
	}
}
