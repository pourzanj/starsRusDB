/**
 * Class for assisting StarsRusMarket program with with login
 * 
 * @author Arya Pourzanjani & Justin Phang  
 */
public class LoginHandler
{ 
	private boolean loginSuccessful;

	/**
	* Public method intended for StarsRusMarket to check if login was successful
	*/
	public boolean isLoginSuccessful()
	{
		if(loginSuccessful == true) return true;
		else return false;
	}

    /**
     * Constructor
     *
     * @param inputMode the mode of the program user (either user, manager, or admin)
     */
    public LoginHandler(String inputMode,String enteredUsername, String enteredPassword)
    {
    	//LOGIN AS USER
        if(inputMode == "user"){

        	//check that the username exists and if so get its associated password
        	String correctUserPass = "";
        	try{
        		correctUserPass = pullPassword("enteredUsername");
        	}
        	catch (InvalidUsernameException iue){
        		System.out.println("Username does not exist. Quiting Program...");
        	}

        	//check if the password is the correct match for the username
        	try{
        		checkPassword(correctUserPass, enteredPassword);
        	}
        	catch (WrongPasswordException wpe){
         		System.out.println("Incorrect Password. Quiting Program...");
        	}

        	//if we've made it this far the login is successful
        	loginSuccessful = true;
        }

        //LOGIN AS MANAGER
        else if(inputMode == "manager"){
        	if( enteredPassword.equals("egg") )
        		loginSuccessful = true;
        	else
        		loginSuccessful = false;
        }

        //LOGIN AS ADMIN
        else{
        	if( enteredPassword.equals("egg") )
        		loginSuccessful = true;
        	else
        		loginSuccessful = false;
        }
    }
    
    /**
    * Given a username as a string, queries the database to return the corresponding password for that
    * string that is stored in the database OR throws an exception if that username does not exist.
    *
    * @param the username of the user
    */
    private String pullPassword(String username) throws InvalidUsernameException
    {
    	//~~QUERY DATABASE HERE
    	/*
    	string queryResult = QUERY RESULT HERE;
    	if( queryResult IS NOT NULL )
    		return queryResult;
    	else
    		throw new InvalidUsernameException;
    	*/
 		
    	return "fortyTWO";
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

class InvalidUsernameException extends Exception
{
   public InvalidUsernameException(){
      super();
   }
}

class WrongPasswordException extends Exception
{
   public WrongPasswordException(){
      super();
   }
}
