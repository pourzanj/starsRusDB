

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

//USER INTERFACE EXCEPTIONS

class NegativeNumberException extends Exception
{
	public NegativeNumberException(){
		super();
	}
}

class InvalidStockException extends Exception
{
	public InvalidStockException(){
		super();
	}
}

class InvalidMovieException extends Exception
{
	public InvalidMovieException(){
		super();
	}
}

class InvalidAcctNumber extends Exception
{
	public InvalidAcctNumber(){
		super();
	}
}