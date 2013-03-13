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

	private String getLastInterestDate()
	{	
		String line = "";
		try{
			BufferedReader br = new BufferedReader(new FileReader("lastInterestDay.txt"));
			line = br.readLine();
			br.close();
		} catch (Exception e) {
			System.out.println("Error retrieving today's date. Exiting");
	    	System.exit(0);
		}
		return line;
	}

	public ManagerInterfaceHandler(ConnectionHandler C)
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

	//gets called whenever date changes
	public void AddInterest()
	{
		//set balance history for all customers and for all days
		String query_allCustomers = "select * from customerProfile";

	    int customerIDiter = 0;
	    try{
			Statement stmt_AllCustomers = myC.getConnection().createStatement();
			ResultSet rs = stmt_AllCustomers.executeQuery(query_allCustomers);
			while(rs.next())
			{
				customerIDiter = rs.getInt("taxid");

				Date todaysDate = getTodaysDate();
				int startingDay = todaysDate.getDate();
				int startingYear = todaysDate.getYear() - 100;
				int startingMonth = todaysDate.getMonth();
				String monthString = null;
					switch( startingMonth ){
						case 0:
							monthString = "JAN";
							break;
						case 1:
							monthString = "FEB";
							break;
						case 2:
							monthString = "MAR";
							break;
						case 3:
							monthString = "APR";
							break;
						case 4:
							monthString = "MAY";
							break;
						case 5:
							monthString = "JUN";
							break;
						case 6:
							monthString = "JUL";
							break;
						case 7:
							monthString = "AUG";
							break;
						case 8:
							monthString = "SEP";
							break;
						case 9:
							monthString = "OCT";
							break;
						case 10:
							monthString = "NOV";
							break;
						case 11:
							monthString = "DEC";
							break;
					}

				//get customer's average balance for this whole month up to today
				String balancesQuery = "select * from balances where To_Char(bdate,'MON') = " + "'" + monthString + "'"
					+ " AND taxid = " + customerIDiter;

				//System.out.println(balancesQuery);

				double averageBalance = 0;
				int nDays = 0;
			    try{
					Statement stmt_balance = myC.getConnection().createStatement();
					ResultSet balanceRS = stmt_balance.executeQuery(balancesQuery);
					while( balanceRS.next() )
					{
						averageBalance += balanceRS.getDouble("price");
						nDays++;
					}
					
				} catch(Exception e){
					System.out.println("Error checking account balance when adding interest. Exiting.");
					System.exit(0);
				}

				//System.out.println("average: " + averageBalance + " nDays: " + nDays);

				//divide to get true averageBalance
				averageBalance = averageBalance/nDays;

				double interestAmount = 0.03 * averageBalance;

				//System.out.println("interest amount is: " + interestAmount);

				//add interest amount
				String update = "update customerProfile SET acctbalance = acctbalance + " + interestAmount + " WHERE taxid=" + customerIDiter;
		    	try{
			    	Statement stmt_update = myC.getConnection().createStatement();
					int ex = stmt_update.executeUpdate(update);
				} catch (Exception e){
					System.out.println("Error depositing interest. Exiting.");
					System.exit(0);
				}

				//update balances table as well
				String updateB = "update balances SET price = price + " + interestAmount + " WHERE taxid=" + customerIDiter + " AND "
					+ "bdate='" + startingDay + "-" + monthString + "-" + startingYear + "'";

		    	try{
			    	Statement stmt_update = myC.getConnection().createStatement();
					int ex = stmt_update.executeUpdate(updateB);
				} catch (Exception e){
					System.out.println("Error depositing interest. Exiting.");
					System.exit(0);
				}

				System.out.println("Added " + interestAmount + " interest to " + customerIDiter);

			}
		} catch(Exception e){
			System.out.println("Error adding interest. Exiting.");
			System.exit(0);
		}


	}

	public void generateMonthlyStatement()
	{
		//prompt manager for user id
		//get users name to print
		//get email address to print
		//get current balance for month to print
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String commandArg = null;
		System.out.println("Please enter a valid customer tax id:");
	    	try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error reading command. Exiting.");
			System.exit(0);
		}
		int taxid = 0;
		taxid = Integer.valueOf(commandArg);

		Date todaysDate = getTodaysDate();
		double currentBalance = 0;
		try {
			Statement st = myC.getConnection().createStatement();
			ResultSet rs = st.executeQuery("select * from customerProfile where taxid = '" + taxid + "'");
			rs.next();
			//get the customers acctBalance and print it
			System.out.println("User's Name: " + rs.getString("name"));
			System.out.println("User's Email: " + rs.getString("email"));
			currentBalance = rs.getDouble("acctBalance");
			System.out.println("User's Current Balance: " + currentBalance);
		}
		catch (SQLException e) {
			System.out.println("Unable to generate monthly statement. Exiting.");
			System.exit(0);
		}

		//get balance at the beginning of the month
		int startingMonth = todaysDate.getMonth();
				String monthString = null;
					switch( startingMonth ){
						case 0:
							monthString = "JAN";
							break;
						case 1:
							monthString = "FEB";
							break;
						case 2:
							monthString = "MAR";
							break;
						case 3:
							monthString = "APR";
							break;
						case 4:
							monthString = "MAY";
							break;
						case 5:
							monthString = "JUN";
							break;
						case 6:
							monthString = "JUL";
							break;
						case 7:
							monthString = "AUG";
							break;
						case 8:
							monthString = "SEP";
							break;
						case 9:
							monthString = "OCT";
							break;
						case 10:
							monthString = "NOV";
							break;
						case 11:
							monthString = "DEC";
							break;
					}

		String balancesQuery = "select min(bdate) from balances where To_Char(bdate,'MON') = " + "'" + monthString + "'"
					+ " AND taxid = " + taxid;

		System.out.println(balancesQuery);

		double beginBalance = 0;
		try{
			Statement stmt_balance = myC.getConnection().createStatement();
			ResultSet balanceRS = stmt_balance.executeQuery(balancesQuery);
			beginBalance = balanceRS.getDouble("min(bdate)");
					
		} catch(Exception e){
			System.out.println("Error checking account balance when print monthly statement. Exiting.");
			System.exit(0);
		}
		System.out.println("User's First Month was Balance: " + beginBalance);

		//get total loss/earning to print (should just be final-initial balance)
		double lossEarning = currentBalance - beginBalance;
		System.out.println("Loss/Earning is: " + lossEarning);

		//print transactions, tally up how many there were total to count how mauch commision was paid
		String transactionsQuery = "select * from transactions where taxid = " + taxid;

		int nTransactions = 0;
		try{
			Statement trans = myC.getConnection().createStatement();
			ResultSet tRS = trans.executeQuery(transactionsQuery);
			//iterate through transactions of user
			while( tRS.next() )
			{
				nTransactions++;
				//print the information about each transaction here
			}
					
		} catch(Exception e){
			System.out.println("Error listing transactions when generating monthly statement. Exiting.");
			System.exit(0);
		}


	}

	public void ListActiveCustomers()
	{
		//get list of all users, iterate through them, iterate through
		//each of their transactions and keep a running sum of the total quantity traded

		String query_allCustomers = "select * from customerProfile";

	    int customerIDiter = 0;
	    try{
			Statement stmt_AllCustomers = myC.getConnection().createStatement();
			ResultSet rs = stmt_AllCustomers.executeQuery(query_allCustomers);
			while(rs.next())
			{
				customerIDiter = rs.getInt("taxid");

				//get customer's balance
				String transQuery = "select * from transactions where taxid = " + customerIDiter;

				int totalQtyTraded = 0;
			    try{
					Statement stmt_trans = myC.getConnection().createStatement();
					ResultSet quantityRS = stmt_trans.executeQuery(transQuery);
					while( quantityRS.next() )
					{
						totalQtyTraded += quantityRS.getInt("quantity");
					}
					
				} catch(Exception e){
					System.out.println("Error going through transactions when listing active customers. Exiting.");
					System.exit(0);
				}

				if( totalQtyTraded >= 1000 )
				{
					System.out.println("" + customerIDiter);
				}
		
			}
		} catch(Exception e){
			System.out.println("Error listing active customers. Exiting.");
			System.exit(0);
		}


	}

	public void generateTaxReport()
	{
		//get list of all users, iterate through them

			//get balance at the beginning of the month
			//get balance today
			//compare to see if the person earned 10,000

			//if so print out the users taxid and state of residence


	}

	public void generateCustomerReport()
	{
		//prompt manager for any customers taxid
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String commandArg = null;
		System.out.println("Please enter a valid customer tax id:");
	    	try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error reading command. Exiting.");
			System.exit(0);
		}
		int taxid = 0;
		taxid = Integer.valueOf(commandArg);
		try {
			Statement st = myC.getConnection().createStatement();
			ResultSet rs = st.executeQuery("select * from customerProfile where taxid = '" + taxid + "'");
			rs.next();
			//get the customers acctBalance and print it
			System.out.println("Current account balance: " + rs.getDouble("acctbalance"));
		}
		catch (SQLException e) {
			System.out.println("Unable to show the balance. Exiting.");
			System.exit(0);
		}
		//do query of all stock account associated with the taxid and iterate
		//through them with a while loop while print out they're total quantity and symbol
		System.out.println("Now attempting to print the total quantity and symbol of each stock corresponding to this tax id...");
		try {
			Statement st = myC.getConnection().createStatement();
			ResultSet rs = st.executeQuery("select totalQuantity, symbol from stockAccount where taxid = " + taxid);
			while (rs.next()) {
				System.out.println("Stock:");
				System.out.println(" Quantity: " + rs.getInt(1));
				System.out.println(" Symbol: " + rs.getString(2));
			}
		} catch (SQLException e) {
			System.out.println("Unable to list the stocks. Exiting.");
			System.exit(0);
		}

	}

	public void deleteTransactions()
	{
		//select all transactions and delete all
		System.out.println("Deleting the transactions...");
		try {
			Statement st = myC.getConnection().createStatement();
			int ex = st.executeUpdate("delete from transactions");
		} catch (SQLException e) {
			System.out.println("Unable to delete the transactions. Exiting.");
			System.exit(0);
		}
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