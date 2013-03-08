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
		System.out.println("Please enter the new date.");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String commandArg = null;
		try {
			commandArg = br.readLine();
		} catch (IOException ioe) {
			System.out.println("Error Reading Command. Exiting.");
			System.exit(0);
		}
		String newDate = commandArg;
		try {
			FileWriter fs = new FileWriter("CurrentDate.txt",false);
			BufferedWriter bw = new BufferedWriter(fs);
			bw.write(newDate);
			bw.close();
		} catch (Exception e) {
			System.out.println("Can't write to file");
			System.exit(0);
		}

		System.out.println("Changed Date.");
	}
}
