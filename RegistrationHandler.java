import java.io.*;
import java.sql.*;
/**
 * Class for assisting StarsRusMarket program with with registration
 * 
 * @author Arya Pourzanjani & Justin Phang  
 */
public class RegistrationHandler
{
	private ConnectionHandler myC;

	/**
	* Constructor
	*
    * @param enteredUsername username that the user type in at the command line
    * @param enteredPassword password user typed in at command line
    */
    public RegistrationHandler(String enteredUsername, String enteredPassword, ConnectionHandler C)
    {
    	//make sure username does not already exists
		myC = C;
    	try{
    		checkIfUsernameExists(enteredUsername);
    	}
    	catch(InvalidUsernameException iue) {
        	System.out.println(iue.getError() + "Quitting Program...");
		System.exit(0);
    	}
		System.out.println("This username doesn't exist. Please provide the information as follows to register into the database.");

    	//prompt user for taxid

    	//make sure tax id is a 4 digit integer
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String commandArg = null;
		System.out.println("Please enter the desired tax ID (1000-9999)");
	    	try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error reading command. Exiting.");
			System.exit(0);
		}
		int taxid = 0;
		try {
	    	taxid = Integer.valueOf(commandArg);
	    } catch (NumberFormatException nfe) {
	    	System.out.println("Entered value is not a number. Exiting.");
			System.exit(0);
	    }
	    try {
	    	if (taxid < 1000 || taxid > 9999) throw new IllegalArgumentException();
	    } catch (IllegalArgumentException ie) {
	    	System.out.println("Error: Tax ID must be between 1000 and 9999. Exiting.");
			System.exit(0);
	    }
		boolean uniqueTaxID = true;
		ResultSet rsTax = null;
		try {
			Statement stmt = myC.getConnection().createStatement();
			rsTax = stmt.executeQuery("select taxid from screenname where taxid='" + taxid + "'");
		} catch(SQLException e) {
			System.out.println("Error creating tax ID. Exiting.");
			System.exit(0);
		}
		//make sure it's unique by querying database
		try {
			while (rsTax.next()) uniqueTaxID = false; //if we go through this once then the query is NOT empty
		} catch (SQLException sqle) {
			System.out.println("Error checking if the tax ID is unique. Exiting.");
			System.exit(0);
		}
    	if (uniqueTaxID == false) {
    		System.out.println("Error: The tax ID must be unique. Exiting.");
			System.exit(0);
		}
    	//prompt user for name
		String name = "";
		System.out.println("Please enter your first name.");
		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error reading command. exiting.");
			System.exit(0);
		}
		name += commandArg;
		System.out.println("Please enter your last name.");
		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error reading command. Exiting.");
			System.exit(0);
		}
		name += " " + commandArg;
    	//ask the user for their email address
		String email = "";
		System.out.println("Please enter the local part of your email address (before the @).");
		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error reading command. Exiting.");
			System.exit(0);
		}
		email += commandArg;
		System.out.println("Please enter the domain part of your email address (after the @).");
		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error reading command. Exiting.");
			System.exit(0);
		}
		email += "@" + commandArg;
    	//ask the user for their phone number it can just be 10 digits with no parenthesis
		String phoneNumber = "";
		System.out.println("Please enter your area code (3 digits).");
		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error reading command. Exiting.");
			System.exit(0);
		}
		if (commandArg.length() != 3)  {
			System.out.println("Please enter a valid area code.");
			System.exit(0);
		}
		phoneNumber += "(" + commandArg;
		System.out.println("Please enter your phone number (7 digits).");
		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error reading command. Exiting.");
			System.exit(0);
		}
		if (commandArg.length() != 7) {
			System.out.println("Please enter a valid phone number.");
			System.exit(0);
		}
		phoneNumber += ")" + commandArg;		
    	//ask the user for their state
		String state = "";
		System.out.println("Please enter the initials for your state (two uppercase letters).");
    	//make sure it's 2 letters and is a string
		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error reading command. Exiting.");
			System.exit(0);
		}
		if (commandArg.length() != 2 || Character.isUpperCase(commandArg.charAt(0)) == false || Character.isUpperCase(commandArg.charAt(1)) == false) {
			System.out.println("Please print a valid state initial.");
			System.exit(0);
		}
		state += commandArg;
    	//tell the user they need $1000 to start the account and ask them if they have it if no exit if not continue
		System.out.println("You need $1000 to create this account. Are you sure you want to create this account? (Y/N)");
		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error reading command. Exiting.");
			System.exit(0);
		}
		while (commandArg.equals("Y") == false) {
			if (commandArg.equals("N") == false) {
				System.out.println("Please pick (Y)es or (N)o if you want to create this account or not.");
				try {
					commandArg = br.readLine();
				} catch (IOException ioe) {
					System.out.println("Error reading command. Exiting.");
					System.exit(0);
				}
			}
			else {
				System.out.println("The account wasn't created. Exiting.");
				System.exit(0);
			}
		}
		System.out.println("Now attempting to add you to the database...");
    	//add customerProfile to database
		String customerProfileQuery = "INSERT INTO customerProfile VALUES(" + taxid + ",'" + name + "',1000,'" + email + "','" + phoneNumber + "','" + state + "')";
		try {
			Statement stmt = myC.getConnection().createStatement();
			int ex = stmt.executeUpdate(customerProfileQuery);
		} catch (SQLException e){
			System.out.println("Error inserting the new customer profile into the database. Exiting.");
			System.exit(0);
		}
		//add screenname to database
		String screennameQuery = "INSERT INTO screenname VALUES('" + enteredUsername + "','" + enteredPassword + "'," + taxid + ")";
		try {
			Statement stmt = myC.getConnection().createStatement();
			int ex = stmt.executeUpdate(screennameQuery);
		} catch (SQLException e){
			System.out.println("Error inserting the new screenname into the database. Exiting.");
			System.exit(0);
		}
		//add balances to database
    	String balancesQuery = "INSERT INTO balances VALUES(" + taxid + "," + getTodaysDate() + ",1000)";
		try {
			    Statement stmt = myC.getConnection().createStatement();
				int ex = stmt.executeUpdate(balancesQuery);
		} catch (SQLException e){
				System.out.println("Error inserting the new balances into the database. Exiting.");
				System.exit(0);
		}
    }

    /**
    * Given a username as a string, queries the database to make sure that username does not
    * exist OR throws an exception if that username does indeed exists
    *
    * @param the username the user would like to register
    * @see InvalidUsernameException
    */
    private void checkIfUsernameExists(String username) throws InvalidUsernameException
    {
    	//~~QUERY DATABASE HERE
    	String queryResult = "select username from screenname where username ='" + username + "'";
		ResultSet rs = null;
		boolean noUsername = true;
		
		try{
			Statement stmt = myC.getConnection().createStatement();
			rs = stmt.executeQuery(queryResult);
		} catch(SQLException e) {
			System.out.println("Error getting the username. Exiting.");
			System.exit(0);
		}

		try {
			while (rs.next()) noUsername = false; //if we go through this once then the query is NOT empty
		} catch (SQLException sqle) {
			System.out.println("Error checking if there is a username or not. Exiting.");
			System.exit(0);
		}
    	if (noUsername == false)
    		throw new InvalidUsernameException("Username already exists. ");
    	return;
    }
	
	private String getTodaysDate() {	
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
		int startingMonth = lastUpdateDate.getMonth();
		int startingDay = lastUpdateDate.getDate();
		int startingYear = lastUpdateDate.getYear() - 100;
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
		return dateQueryString;
	}
}
