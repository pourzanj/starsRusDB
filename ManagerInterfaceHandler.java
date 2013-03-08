import java.io.*;
import java.sql.*;

/**
 * Class for assisting StarsRusMarket program
 * with manager interaction with the database
 * 
 * @author Arya Pourzanjani & Justin Phang  
 */
public class ManagerInterfaceHandler implements transactionHandler
{
	public ManagerInterfaceHandler(ConnectionHandler C)
	{

		System.out.println("Welcome Manager!");

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
	      		case "interest":
	      			AddInterest();
	      			break;

	      		case "statement":
	      			generateMonthlyStatement();
	      			break;

	      		case "activecustomers":
	      			ListActiveCustomers();
	      			break;

	      		case "taxreport":
	      			generateTaxReport();
	      			break;

	      		case "customerreport":
	      			generateCustomerReport();
	      			break;

	      		case "cleartransactions":
	      			deleteTransactions();
	      			break;

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
	      			break;

	      		default:
	      			System.out.println("Command Not Recognized. Try again.");
	      			break;
	      	}

		}
	}	

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

	public void generateCustomerReport()
	{

	}

	public void deleteTransactions()
	{
		
	}


	//METHODS FROM TRANSACTION HANDLER

	public void depositOrWithdraw(String action)
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String commandArg = null;

		System.out.println("Please enter the act. ID number of the account you'd like to deposit or withdraw from.");

		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error Reading Command. Exiting.");
			System.exit(0);
		}

		int id = 0;
		try {
	    	id = Integer.valueOf( commandArg );
	    } catch (NumberFormatException nfe) {
	    	System.out.println("Entered value is not an integer. Exiting.");
			System.exit(0);
	    }

	    //~~QUERY DATABASE TO ENSURE ACCOUNT EXISTS AND THROW INVALIDACCT EXCEPTION

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
	    	System.out.println("Deposit and Withdraw Values must be positive . Exiting.");
			System.exit(0);
	    }

	    //~~INSERT INTO DB HERE 
	    //MAKE SURE IF WITHDRAW THAT CHECK THERE IS ENOUGH IN THE ACCOUNT TO WITHDRAW MY SPECIFIED AMOUNT
	    
	    if(action.equals("deposit"))
	    	System.out.println("Deposited " + amount + " dollars.");
	    else
	    	System.out.println("Withdrew " + amount + " dollars.");
	}
	
	public void buyOrSell(String action)
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String commandArg = null;

		System.out.println("Please enter the act. ID number of the account you'd like to deposit or withdraw from.");

		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error Reading Command. Exiting.");
			System.exit(0);
		}

		int id = 0;
		try {
	    	id = Integer.valueOf( commandArg );
	    } catch (NumberFormatException nfe) {
	    	System.out.println("Entered value is not an integer. Exiting.");
			System.exit(0);
	    }

	    //~~QUERY DATABASE TO ENSURE ACCOUNT EXISTS AND THROW INVALIDACCT EXCEPTION

		System.out.println("Please enter what symbol you would like to buy or sell.");
		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error Reading Command. Exiting.");
			System.exit(0);
		}
		String stockSymbol = commandArg;

		//~~CHECK IF SYMBOL EXISTS IN DATABASE HERE THROW INVALIDSTOCK EXCEPTION IF IT DOESNT

		System.out.println("Please enter the amount you would like to buy or sell.");
		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error Reading Command. Exiting.");
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

	   	if(action.equals("buy"))
	    	System.out.println("Bought " + amount + " of " + stockSymbol);
	    else
	    	System.out.println("Sold " + amount + " of " + stockSymbol);
	}

	public void showBalance()
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String commandArg = null;

		System.out.println("Please enter the act. ID number of the account you'd like to deposit or withdraw from.");

		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error Reading Command. Exiting.");
			System.exit(0);
		}

		int id = 0;
		try {
	    	id = Integer.valueOf( commandArg );
	    } catch (NumberFormatException nfe) {
	    	System.out.println("Entered value is not an integer. Exiting.");
			System.exit(0);
	    }

	    //~~QUERY DATABASE TO ENSURE ACCOUNT EXISTS AND THROW INVALIDACCT EXCEPTION

		//~~EXECUTE QUERY TO SHOW BALANCE
	}

	public void showTransactions()
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String commandArg = null;
		
		System.out.println("Please enter the act. ID number of the account you'd like to deposit or withdraw from.");

		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error Reading Command. Exiting.");
			System.exit(0);
		}

		int id = 0;
		try {
	    	id = Integer.valueOf( commandArg );
	    } catch (NumberFormatException nfe) {
	    	System.out.println("Entered value is not an integer. Exiting.");
			System.exit(0);
	    }

	    //~~QUERY DATABASE TO ENSURE ACCOUNT EXISTS AND THROW INVALIDACCT EXCEPTION

		//~~EXECUTE QUERY TO SHOW ALL TRANSACTIONS
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

		//~~QUERY THROW INVALID STOCK EXCEPTION IF STOCK DOESNT EVEN EXIST
	}

	public void showMovieInfo()
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String commandArg = null;

		System.out.println("Please enter what movie you'd like to see info for.");
		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error Reading Command. Exiting.");
			System.exit(0);
		}
		String movieName = commandArg;

		//~~QUERY THROW INVALID MOVIE EXCEPTION IF MOVIE DOESNT EVEN EXIST
	}
	
	public void showTopMovies()
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String commandArg = null;

		System.out.println("Please enter the starting year of the period you'd like to find top movies in.");

		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error Reading Command. Exiting.");
			System.exit(0);
		}

		int amount1 = 0;
		try {
	    	amount1 = Integer.valueOf( commandArg );
	    } catch (NumberFormatException nfe) {
	    	System.out.println("Entered value is not an integer. Exiting.");
			System.exit(0);
	    }

	    System.out.println("Please enter the ending year of the period you'd like to find top movies in.");
	    
		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error Reading Command. Exiting.");
			System.exit(0);
		}

	    int amount2 = 0;
		try {
	    	amount2 = Integer.valueOf( commandArg );
	    } catch (NumberFormatException nfe) {
	    	System.out.println("Entered value is not an integer. Exiting.");
			System.exit(0);
	    }

		//~~QUERY
	}
}
