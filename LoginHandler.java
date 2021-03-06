import java.io.*;
import java.sql.*;
/**
 * Class for assisting StarsRusMarket program with with login
 * 
 * @author Arya Pourzanjani & Justin Phang  
 */
public class LoginHandler
{ 
     private ConnectionHandler myC;
    /**
     * Constructor
     *
     * @param inputMode the mode of the program user (either user, manager, or admin)
     * @param enteredUsername username that the user type in at the command line
     * @param enteredPassword password user typed in at command line
     */

    public LoginHandler(String inputMode,String enteredUsername, String enteredPassword, ConnectionHandler C) throws SQLException
    {
		myC = C;
    	String correctUserPass = "";

    	//LOGIN AS USER
        if(inputMode == "user"){

        	//check that the username exists and if so get its associated password
        	try{
        		correctUserPass = pullPassword(enteredUsername);
        	}
        	catch (InvalidUsernameException iue){
        		System.out.println(iue.getError() + "Quiting Program...");
        		System.exit(1);
        	}
        }

        //LOGIN AS MANAGER
        else if(inputMode == "manager"){
        	correctUserPass = "secret";
        }

        //LOGIN AS ADMIN
        else{
        	correctUserPass = "secret";
        }
        //check if the password is the correct match for the login mode and person
        try{
        	checkPassword(correctUserPass, enteredPassword);
        }
        catch (WrongPasswordException wpe){
         	System.out.println("Incorrect Password. Quitting Program...");
         	System.exit(1);
        }

        //if we've made it this far the login is successful
    }
    
    /**
    * Given a username as a string, queries the database to return the corresponding password for that
    * string that is stored in the database OR throws an exception if that username does not exist.
    *
    * @param the username of the user
    */
    private String pullPassword(String username) throws InvalidUsernameException, SQLException
    {
    	//Query database
    	String queryResult = "select * from screenname where username = '" + username + "'";

		Statement stmt = myC.getConnection().createStatement();
		ResultSet rs = stmt.executeQuery(queryResult);
		
		String pulledPassword = null;
    	while (rs.next())
    		pulledPassword = (rs.getString("password")).replaceAll("\\s","");
    	return pulledPassword;
    }

    /**
    * Check whether an entered password matches the correct password and throw an exception and abort otherwise.
    *
    *@param correctUserPass the correct user password as retrieved from the database using pull password
    *@param the password that the user entered
    */
    private void checkPassword(String correctUserPass, String enteredUserPass) throws WrongPasswordException
    {
    	if( correctUserPass.equals(enteredUserPass) )
    		return;
    	else
    		throw new WrongPasswordException();
    }
}
