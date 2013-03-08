import java.io.*;
import java.sql.*;

/**
 * Class for assisting StarsRusMarket program
 * with admin interaction with the database
 * 
 * @author Arya Pourzanjani & Justin Phang  
 */

public class AdminInterfaceHandler
{
	public AdminInterfaceHandler(ConnectionHandler C)
	{

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

		//~~CHECK IF SYMBOL EXISTS IN DATABASE HERE THROW INVALIDSTOCK EXCEPTION IF IT DOESNT

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

	    //~~INSERT INTO DB HERE 
	}

	public void updateDate()
	{
		
	}
}
