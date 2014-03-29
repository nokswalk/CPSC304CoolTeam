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
		try {
			while (!quit) {
				System.out.print("\n\nPlease choose one of the following: \n");
				System.out.print("1.  Add borrower\n");
				//System.out.print("2.  Check out items\n");
				//System.out.print("3.  Process a return\n");
				//System.out.print("4.  Check overdue items\n");
				System.out.print("5.  Quit\n>>");

				choice = Integer.parseInt(Main.in.readLine());

				System.out.println(" ");

				switch (choice) {
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

		catch (IOException e) {
			System.out.println("IOException!");
			try {
				Main.con.close();
				System.exit(-1);
			}
			catch (SQLException ex) {
				System.out.println("Message: " + ex.getMessage());
			}
		}
		catch (SQLException ex) {
			System.out.println("Message: " + ex.getMessage());
		}
	}


	/*
	 * Add a new borrower to the library.  
	 * User should provide all required info.
	 */
	private static void addBorrower() {
		
		// attributes of new borrower
		int                bid;
		String             password; 
		String             name;
		String             address;
		String             phone;
		String             emailAddress;
		int	               sinOrStNo;
		Date               expiryDate;
		String             type;
		
		Statement          s;   // to check if borrower already exists in database

		PreparedStatement  ps;  // to add new borrower

		try {
			ps = Main.con.prepareStatement("INSERT INTO Borrower VALUES (?,?,?,?,?,?,?,{d ?},?)");

			// TODO use a sequence
			System.out.print("Borrower ID: ");
			bid = Integer.parseInt(Main.in.readLine());
			ps.setInt(1, bid);

			System.out.print("Borrower password: ");
			password = Main.in.readLine();
			ps.setString(2, password);

			System.out.print("Borrower name: ");
			name = Main.in.readLine();
			ps.setString(3, name);

			System.out.print("Borrower address: ");
			address = Main.in.readLine();
			ps.setString(4, address);

			System.out.print("Borrower phone number: ");
			phone = Main.in.readLine();
			ps.setString(5,  phone);

			System.out.print("Borrower email address: ");
			emailAddress = Main.in.readLine();
			ps.setString(6, emailAddress);

			System.out.print("Borrower SIN or student number: ");
			String tempSinOrStNo = Main.in.readLine();
			
			// TODO test when GUI is working; leaving blank in simple text console ui causes error
			if (tempSinOrStNo.length() == 0) {
				System.out.println("SIN or student number is a required field.  Please try again.");
				ps.close();
				return;
			}
			
			sinOrStNo = Integer.parseInt(tempSinOrStNo);
			
			// check if this book already in database
			s = Main.con.createStatement();
			ResultSet rs = s.executeQuery("SELECT bid "
										+ "FROM Borrower "
										+ "WHERE sinOrStNo=" + sinOrStNo);
			if (rs.next()) {
				System.out.println("An account for this borrower already exists in the library database.");
				s.close();
				ps.close();
				return;
			}
			
			ps.setInt(7, sinOrStNo);

			// TODO need to convert between JDBC and Oracle date types, doesn't run as is.
			System.out.print("Borrower expiry date: ");  // Clerk should set to 2 years from today
			expiryDate = Date.valueOf(Main.in.readLine());  // Must be in format dd-mm-yyyy format
			ps.setDate(8, expiryDate);

			System.out.print("Borrower type: ");
			type = Main.in.readLine();
			ps.setString(9, type);

			// add borrower
			ps.executeUpdate();
			
			// commit work 
			Main.con.commit();
			System.out.println("Borrower has been added successfully.");
			
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
