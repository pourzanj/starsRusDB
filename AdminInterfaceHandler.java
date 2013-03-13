import java.io.*;
import java.util.Date;
import java.sql.*;

/**
 * Class for assisting StarsRusMarket program
 * with admin interaction with the database
 * 
 * @author Arya Pourzanjani & Justin Phang  
 */

public class AdminInterfaceHandler
{
	private ConnectionHandler myC;

	public AdminInterfaceHandler(ConnectionHandler C)
	{

		myC = C;

		System.out.println("Welcome Admin!");

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
	      		case "open":
	      			openOrCloseMarket("open");
            		System.out.println("Market Open!");
	      			break;
	      		
	      		case "close":
	      			openOrCloseMarket("close");
	      			System.out.println("Market Closed");
	      			break;

	      		case "setprice":
	      			setNewStockPrice();
	      			break;

	      		case "date":
	      			updateDate();
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

	public void openOrCloseMarket(String action)
	{
		File f=new File("OpenOrClosed.txt");

        FileInputStream fs = null;
        InputStreamReader in = null;
        BufferedReader br = null;

        StringBuffer sb = new StringBuffer();

        String textinLine;

        try {
            fs = new FileInputStream(f);
            in = new InputStreamReader(fs);
            br = new BufferedReader(in);

            while(true)
            {
                textinLine=br.readLine();
                if(textinLine==null)
                    break;
                sb.append(textinLine);
            }

            if(action.equals("open")){
            	String textToEdit1 = "Closed";
            	int cnt1 = sb.indexOf(textToEdit1);
            	sb.replace(cnt1,cnt1+textToEdit1.length(),"Open");
            }
            else{
            	String textToEdit1 = "Open";
            	int cnt1 = sb.indexOf(textToEdit1);
            	sb.replace(cnt1,cnt1+textToEdit1.length(),"Closed");
            }

            fs.close();
            in.close();
            br.close();

        } catch (FileNotFoundException e) {
              e.printStackTrace();
          } catch (IOException e) {
              e.printStackTrace();
          }

        try{
            FileWriter fstream = new FileWriter(f);
            BufferedWriter outobj = new BufferedWriter(fstream);
            outobj.write(sb.toString());
            outobj.close();
        }catch (Exception e){
            System.err.println("Error: " + e.getMessage());
        }
	}

	public void setNewStockPrice()
	{
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String commandArg = null;

		System.out.println("Please enter what symbol who's price you'd like to change.");
		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error Reading Command. Exiting.");
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

		System.out.println("Enter new price.");

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

	    //update
	    String update = "update actorStock SET currentPrice = " + amount + " WHERE symbol= '" + stockSymbol + "'";
		try{
			Statement stmt = myC.getConnection().createStatement();
			int ex = stmt.executeUpdate(update);
		} catch (Exception e){
			System.out.println("Error changing price. Exiting.");
			System.exit(0);
		}
	    System.out.println("Changed Price of " + stockSymbol + " to " + amount);
	}

	public void updateDate()
	{
		System.out.println("Please enter the new date DD-MON-YY, where MON is the first 3 letters of the month.");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String newDateS = null;
		try {
			newDateS = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error Reading Command. Exiting.");
			System.exit(0);
		}

		Date newDate = new Date(newDateS);
		System.out.println(newDate.toString());

		/*
		//make sure day and month is an integer and not negative
		int dateInt = 0;
		try {
	    	amount = Integer.valueOf( commandArg );
	    } catch (NumberFormatException nfe) {
	    	System.out.println("Entered value is not an integer. Exiting.");
			System.exit(0);
	    }

	    try {
	    	if( amount <= 0 ) throw new NegativeNumberException();
	    } catch (NegativeNumberException nne) {
	    	System.out.println("Dates must be positive integers. Exiting.");
			System.exit(0);
	    }

	    try {
	    	if( newDate.length() != 6 ) throw new Exception();
	    } catch (NegativeNumberException nne) {
	    	System.out.println("Dates must be in 6 digit format. Exiting.");
			System.exit(0);
	    }

	    //extract date from string to test for correctness 
	    int year, month, day;

	    //add 100 to year because it's date minus 1900
	    //year = 100 + 10*Integer.valueOf(newDate[4]) + Integer.valueOf(newDate[5]);
	    month = 10*Integer.valueOf(newDate[0]) + Integer.valueOf(newDate[1]);
	    day = 10*Integer.valueOf(newDate[2]) + Integer.valueOf(newDate[3]);

	    //check bounds of day and month
	    try {
	    	if( month < 1 || month > 12 ) throw new Exception;
	    	if( day < 1 || day > 31) throw new Exception;
	    } catch (Exception d) {
	    	System.out.println("Your date is not in the bounds of a normal date. Exiting.");
			System.exit(0);
	    }
	    */

	   	//get the last date where anything happened
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

		//make sure new date is after old date
		try{
			if (newDate.compareTo( lastUpdateDate ) <= 0) throw new Exception();
		} catch(Exception e) {
			System.out.println("Error: The date you entered is today or before today. Exiting.");
			System.exit(0);
		}

		//figure out how many days we need to insert balance history for
		int daysBetween = 0;

		int startingDay = 0;
		int startingMonth = newDate.getMonth();
		int startingYear = newDate.getYear() - 100;
		//if this is the first new date of the month set past balances for the entire new month
		if( newDate.getMonth() != lastUpdateDate.getMonth() || newDate.getYear() != lastUpdateDate.getYear() )
		{
			daysBetween = newDate.getDate();
			startingDay = 1;
		}

		//else set past balances for days in between last update day and today
		else
		{
			daysBetween = newDate.getDate() - lastUpdateDate.getDate();
			startingDay = lastUpdateDate.getDate() + 1;
		}
			

		//set balance history for all customers and for all days
		String query_allCustomers = "select * from customerProfile";

	    int customerIDiter = 0;
	    try{
			Statement stmt_AllCustomers = myC.getConnection().createStatement();
			ResultSet rs = stmt_AllCustomers.executeQuery(query_allCustomers);
			while(rs.next())
			{
				customerIDiter = rs.getInt("taxid");

				//get customer's balance
				String balanceQuery = "select * from customerProfile where taxid = " + customerIDiter;

				double currentBalance = 0;
			    try{
					Statement stmt_balance = myC.getConnection().createStatement();
					ResultSet balanceRS = stmt_balance.executeQuery(balanceQuery);
					balanceRS.next();
					currentBalance = balanceRS.getDouble("acctbalance");
				} catch(Exception e){
					System.out.println("Error checking account balance when updating past balances. Exiting.");
					System.exit(0);
				}

				//insert past balances for all missed days
				for(int i=0; i<daysBetween; i++)
				{
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
						dayString = "0" + (startingDay+i);
					else
						dayString = "" + (startingDay+i);

					String insertPastBalanceStatement = "insert into balances values(" + customerIDiter
						+ ",'" + dayString + "-" + monthString + "-" + startingYear + "'," + currentBalance + ")";

			    	try{
				    	Statement stmt_insertPastBalances = myC.getConnection().createStatement();
						int ex = stmt_insertPastBalances.executeUpdate(insertPastBalanceStatement);
					} catch (Exception e){
						System.out.println("Error inserting new past balance in database. Exiting.");
						System.exit(0);
					}

				}
			}
		} catch(Exception e){
			System.out.println("Error updating date. Exiting.");
			System.exit(0);
		}


		System.out.println("Changed Date to " + newDateS);

	}
}
