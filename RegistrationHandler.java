
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

    	//add username to database
    	//~~INSERT IN TO DATABASE HERE	
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
