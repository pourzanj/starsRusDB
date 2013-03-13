
/**
 * Class for assisting StarsRusMarket program with with registration
 * 
 * @author Arya Pourzanjani & Justin Phang  
 */
public class RegistrationHandler
{
	/**
	* Constructor
	*
    * @param enteredUsername username that the user type in at the command line
    * @param enteredPassword password user typed in at command line
    */
    public RegistrationHandler(String enteredUsername, String enteredPassword, ConnectionHandler C)
    {
    	//make sure username does not already exists
    	try{
    		checkIfUsernameExists( enteredUsername );
    	}
    	catch (InvalidUsernameException iue){
        	System.out.println(iue.getError() + "Quiting Program...");
    	}

    	//prompt user for taxid
    	//make sure tax id is a 4 digit integer (see other random try catch blocks in the project)
    	//make sure it's unique by querying database

    	//prompt user for name
    	//make sure it's a string

    	//tell the user they need $1000 to start the account and ask them if they have it if no exit if not continue

    	//ask the user for their email address

    	//ask the user for their phone number it can just be 10 digits with no parenthesis (you decide)

    	//ask the user for their state
    	//make sure it's 2 digits and is a string

    	//add username to database
    	//~~INSERT IN TO CUSTOMER PROFILE TABLE AS WELL AS SCREENNAME TABLE AS WELL AS BALANCES TABLE
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
    	/*
    	string queryResult = QUERY RESULT HERE;
    	if( queryResult IS NULL )
    		return;
    	else
    		throw new InvalidUsernameException("Username Already Exists. ");
    	*/
 		
    	return;
    }

}
