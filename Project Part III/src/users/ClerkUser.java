package users;

import gui.Main;

import java.io.IOException;
import java.sql.*;


/**
 * Loads Clerk user interface.
 * Executes Clerk transactions.
 */
public class ClerkUser {

	/*
	 * Loads clerk's side of application.
	 * Uses buffer line reader and connection established in Main class.
	 */
	public static void main() {
		int choice;
		boolean quit;

		quit = false;
		
		try 
		{
		    while (!quit)
		    {
			System.out.print("\n\nPlease choose one of the following: \n");
			System.out.print("1.  Add Borrower\n");
			//System.out.print("2.  Clerk\n");
			//System.out.print("3.  Librarian\n");
			System.out.print("4.  Quit\n>>");

			choice = Integer.parseInt(Main.in.readLine());
			
			System.out.println(" ");

			switch(choice)
			{
			   case 1:  addBorrower(); break;
			   case 2:  ; break; // TODO checkOutItems()
			   case 3:  ; break; // TODO processReturn()
			   case 4:  ; break; // TODO checkOverdueItems()
			   case 5:  quit = true; 
			}
		    }

		    Main.con.close();
	        Main.in.close();
		    System.out.println("\nGood Bye!\n\n");
		    System.exit(0);
		}
		catch (IOException e)
		{
		    System.out.println("IOException!");
		    try
		    {
			Main.con.close();
			System.exit(-1);
		    }
		    catch (SQLException ex)
		    {
			 System.out.println("Message: " + ex.getMessage());
		    }
		}
		catch (SQLException ex)
		{
		    System.out.println("Message: " + ex.getMessage());
		}
	}
	
	
	/*
	 * Add a new borrower to the library.  
	 * User should provide all required info.
	 */
	private static void addBorrower() {
		int                bid;
		String             password; 
		String             name;
		String             address;
		String                phone;
		String             emailAddress;
		int                sinOrStNo;
		Date               expiryDate;
		String             type;
		
		PreparedStatement  ps;
		
		try {
			ps = Main.con.prepareStatement("INSERT INTO Borrower VALUES (?,?,?,?,?,?,?,?,?");
			
			System.out.print("\n Borrower ID: ");
			bid = Integer.parseInt(Main.in.readLine());
			ps.setInt(1, bid);
			
			System.out.print("\n Borrower password: ");
			password = Main.in.readLine();
			ps.setString(2, password);
			
			System.out.print("\n Borrower name: ");
			name = Main.in.readLine();
			ps.setString(3, name);
			
			System.out.print("\n Borrower address: ");
			address = Main.in.readLine();
			ps.setString(4, address);
			
			System.out.print("\n Borrower phone number: ");
			phone = Main.in.readLine();
			ps.setString(5,  phone);
			
			System.out.print("\n Borrower email address: ");
			emailAddress = Main.in.readLine();
			ps.setString(6, emailAddress);
			
			System.out.print("\n Borrower SIN or student number: ");
			sinOrStNo = Integer.parseInt(Main.in.readLine());
			ps.setInt(7, sinOrStNo);
			
			System.out.print("\n Borrower expiry date: ");  // Clerk should set to 2 years from today
			expiryDate = Date.valueOf(Main.in.readLine());  // Must be in format yyyy-mm-dd
			ps.setDate(8, expiryDate);
			
			System.out.print("\n Borrower type: ");
			type = Main.in.readLine();
			ps.setString(9, type);
			
			ps.executeUpdate();
			// commit work 
			Main.con.commit();
			ps.close();
		}
		
		catch (IOException e) {
			System.out.println("IOException!");
		}
		catch (SQLException ex) {
			System.out.println("Message: " + ex.getMessage());
		    try 
		    {
			// undo the insert
			Main.con.rollback();	
		    }
		    catch (SQLException ex2)
		    {
			System.out.println("Message: " + ex2.getMessage());
			System.exit(-1);
		    }
		}
	}
	
}
