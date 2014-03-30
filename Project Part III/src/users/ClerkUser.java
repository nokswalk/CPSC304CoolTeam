package users;

import gui.Main;

import java.io.IOException;
import java.sql.*;
import java.util.GregorianCalendar;


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
				System.out.print("4.  Check overdue items\n");
				System.out.print("5.  Quit\n>>");

				choice = Integer.parseInt(Main.in.readLine());

				System.out.println(" ");

				switch (choice) {
				case 1:  addBorrower(); break;
				case 2:  ; break; // TODO checkOutItems()
				case 3:  ; break; // TODO processReturn()
				case 4:  checkOverdueItems(); break; // TODO checkOverdueItems()
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
		String             sinOrStNo;
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
			sinOrStNo = Main.in.readLine();
			
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
			
			ps.setString(7, sinOrStNo);

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

	


	/*
	 * Displays a list of the items that are overdue and the borrowers who have checked them out. 
	 * The clerk may decide to send an email messages to any of them (or to all of them).
	 */
	private static void checkOverdueItems() {
		Statement statement;
		ResultSet rs;
		try {
			statement = Main.con.createStatement();

			System.out.println("List of items overdue and the borrowers who have checked them out:");
			rs = statement.executeQuery("SELECT E.bid, E.name, E.emailAddress, A.callNumber, C.copyNo, A.title, B.outDate, D.bookTimeLimit"
							+ "FROM Book A, Borrowing B, BookCopy C, BorrowerType D, Borrower E "
							+ "WHERE B.callNumber = C.callNumber AND B.copyNo = C.copyNo AND D.type = E.type AND E.bid = B.bid "
							+ "AND C.callNumber = A.callNumber AND B.inDate IS NULL "//(OR C.status = 'out')B.indate is null means item has not been returned.
							+ "ORDER BY E.bid, E.name ASC");
			// get info on ResultSet
			ResultSetMetaData rsmd = rs.getMetaData();

			// get number of columns
			int numCols = rsmd.getColumnCount();

			System.out.println(" ");

			// display column names;
			for (int i = 0; i < numCols-2; i++) {
				// get column name and print it
				System.out.printf("%-25s", rsmd.getColumnName(i + 1));
			}
			System.out.printf("%-25s", "DUEDATE");
			
			System.out.println(" ");

			while (rs.next()) {
				Integer bid = rs.getInt("bid");
				Date outDate = rs.getDate("outDate");
				Integer bookTimeLimit = rs.getInt("bookTimeLimit");
				// pseudo code: Date dueDate = outDate + bookTimeLimit;
				// System.out.printf("%-20.20s\n", dueDate);
				if (overDue(getDueDate(bid, outDate))) {
					System.out.printf("%-9.9s", bid);
					String name = rs.getString("name");
					if(rs.wasNull())
						System.out.printf("%-20.20s", " ");
					else
						System.out.printf("%-20.20s", name);
					String email = rs.getString("emailAddress");
					if(rs.wasNull())
						System.out.printf("%-30.30s", " ");
					else
						System.out.printf("%-30.30s", email);
					
					Integer callNumber = rs.getInt("callNumber");
					if (rs.wasNull()) {
						System.out.printf("%-9.9s", " ");
					} else {
						System.out.printf("%-9.9s", callNumber);
					}

					Integer copyNo = rs.getInt("copyNo");
					if (rs.wasNull()) {
						System.out.printf("%-20.20s", " ");
					} else {
						System.out.printf("%-20.20s", copyNo);
					}

					String title = rs.getString("title");
					if (rs.wasNull()) {
						System.out.printf("%-30.30s", " ");
					} else {
						System.out.printf("%-30.30s", title);
					}
				}
			}

			// close the statement;
			// the ResultSet will also be closed
			statement.close();
		} catch (SQLException ex) {
			System.out.println("Message: " + ex.getMessage());
		}
	}
	
	private static boolean overdue(Date dueDate){
		GregorianCalendar gregCalendar = new GregorianCalendar();
		if(gregCalendar.after(dueDate))
			return true;
		else
			return false;
	}
}
