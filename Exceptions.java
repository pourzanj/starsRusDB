package StarsRusMarket;

class ArgLengthException extends Exception
{
   public ArgLengthException(){
      super();
   }
}

class LoginModeException extends Exception
{
   public LoginModeException(){
      super();
   }
}

class InvalidUsernameException extends Exception
{
	private String usernameError;

	public InvalidUsernameException(String e){
    	super();
    	usernameError = e;
   }

   public String getError()
   {
   		return usernameError;
   }
}

class WrongPasswordException extends Exception
{
   public WrongPasswordException(){
      super();
   }
}
