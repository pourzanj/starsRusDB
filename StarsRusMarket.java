import java.io.*;

/** 
 *  Main interface for managers, customers,
 *  and administrators to interact with
 *  StarsRus movie star stock market.
 *  
 * @author Arya Pourzanjani & Justin Phang
 */
public class StarsRusMarket
{
   public static void main(String[] args)
   {
      //check for proper arg length in usage of program
      try{
         checkArgLength( args.length );
      }
      catch (ArgLengthException ale){
         System.out.println("Usage is StarsRusMarket MODE USERNAME PASSWORD");
      }

      //attempt login based on mode
      LoginHandler L;
      String loginMode = args[0];//set login mode to first command line arg
      switch( loginMode ){
         case "user":
            System.out.println("Attempting Login as User...");
            L = new LoginHandler("user", args[1], args[2]);
            break;
         case "manager":
            System.out.println("Attempting Login as Manager...");
            L = new LoginHandler("manager", "NA", args[2]);
            break;
         case "admin":
            System.out.println("Attempting Login as Adminstrator...");
            L = new LoginHandler("admin", "NA", args[2]);
            break;
         case "register":
            System.out.println("Starting Registration Process...");
            //ADD HERE. IMPLEMENT REGISTRATION HANDLER
         default:
            try{ wrongLoginMode(); }
            catch( LoginModeException lme ){
               System.out.println("Invalid Login Mode. Please select either user, manager, admin, or register");
            }
            break;
      }

      //check if login was successful
   }

   /**
   * Check to make sure the user specified a mode, username, and password
   */
   private static void checkArgLength(int argsLength) throws ArgLengthException
   {
    if(argsLength == 3)
      System.out.println("Checking User Mode...");
    else
      throw new ArgLengthException();
   }

   /*
   * Check to make sure the user selected a valid login mode
   */
   private static void wrongLoginMode() throws LoginModeException
   {
      throw new LoginModeException();
   }
}


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