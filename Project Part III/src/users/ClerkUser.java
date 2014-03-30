package users;

import gui.Main;

import java.io.IOException;
import java.sql.*;
import java.util.GregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Loads Clerk user interface.
 * Executes Clerk transactions.
 */
public class ClerkUser {

	/*
	 * Loads clerk's side of application.
	 * Uses buffer line reader and connection established in Main class.
	 */
	public static void main() throws ParseException {
		int choice;
		boolean quit;

		quit = false;
		try {
			while (!quit) {
				System.out.print("\n\nPlease choose one of the following: \n");
				System.out.print("1.  Add borrower\n");
				System.out.print("2.  Check out item\n");;
				//System.out.print("3.  Process a return\n");
				System.out.print("4.  Check overdue items\n");
				System.out.print("5.  Quit\n>>");

				choice = Integer.parseInt(Main.in.readLine());

				System.out.println(" ");

				switch (choice) {
				case 1:  addBorrower(); break;
				case 2:  checkOutItems(); break; // TODO checkOutItems()
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
		int                bid;
		String             password; 
		String             name;
		String             address;
		String             phone;
		String             emailAddress;
		int                sinOrStNo;
		String             expiryDate;
		String             type;

		PreparedStatement  ps;

		try {
			ps = Main.con.prepareStatement("INSERT INTO Borrower VALUES (bid_c.nextval,?,?,?,?,?,?,{d ?},?)");

			System.out.print("Borrower password: ");
			password = Main.in.readLine();
			ps.setString(1, password);

			System.out.print("Borrower name: ");
			name = Main.in.readLine();
			ps.setString(2, name);

			System.out.print("Borrower address: ");
			address = Main.in.readLine();
			ps.setString(3, address);

			System.out.print("Borrower phone number: ");
			phone = Main.in.readLine();
			ps.setString(4,  phone);

			System.out.print("Borrower email address: ");
			emailAddress = Main.in.readLine();
			ps.setString(5, emailAddress);

			System.out.print("Borrower SIN or student number: ");
			sinOrStNo = Integer.parseInt(Main.in.readLine());
			ps.setInt(6, sinOrStNo);

			System.out.print("Borrower expiry date: ");  // Clerk should set to 2 years from today
			expiryDate = Main.in.readLine();  // Must be in format yyyy-mm-dd
			ps.setString(7, expiryDate);

			System.out.print("Borrower type: ");
			type = Main.in.readLine();
			ps.setString(8, type);

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

	
	private static void checkOutItems() throws ParseException {
		int 			   bid;
		List<String>	   callNumbersS;
		ArrayList<Integer> callNumbers = null;
		callNumbers = new ArrayList<Integer>();
		Statement  		   s;
		
		try {
			System.out.println("Borrower ID: ");
			bid = Integer.parseInt(Main.in.readLine());
			System.out.println("List of call numbers: ");
			callNumbersS = Arrays.asList(Main.in.readLine().split(","));
			
			if (callNumbersS.isEmpty()) {
				
			}
	
			
			else{
				
				for (String c: callNumbersS){
					int callNumber;
					callNumber = Integer.parseInt(c.trim());
					callNumbers.add(callNumber);
				}
			
				s = Main.con.createStatement();
				ResultSet rs = s.executeQuery("SELECT bid "
												+ "FROM Borrower "
												+ "WHERE bid = " + bid);

				if (rs.next() == false){
					System.out.println("Invalid ID");
				
				}
				else {
					for (int j = 0; j < callNumbers.size(); j++) {
						int i = callNumbers.get(j);
						checkOutItem(bid, i);
					}
				
				}
			}
			
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

	private static void checkOutItem(int bid, int callNumber) throws ParseException {
		
		Statement			s;
		
		try 
		{
			s = Main.con.createStatement();
			ResultSet rs = s.executeQuery("SELECT callNumber "
										+ "FROM BookCopy  "
										+ "WHERE status = 'in' "
										+ "AND callNumber = " + callNumber);

			if (rs.next() == false){
				System.out.println("Book not available for borrowing");
				
			}
			else {
				addBorrowingHelper(bid, callNumber);
			}
			
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
	
	private static void addBorrowingHelper(int bid, int callNumber) throws ParseException {

		int						borid;
		int						copyNo;
		java.sql.Date 			outDate;
		PreparedStatement  ps;

		try {
			ps = Main.con.prepareStatement("INSERT INTO Borrowing VALUES (borid_c.nextval,?,?,?,?,?)");



			System.out.println("Copy No: ");
			copyNo = Integer.parseInt(Main.in.readLine());
			ps.setInt(3, copyNo);

			System.out.println("Out Date(dd/mm/yy): ");
			outDate = stringToDate(Main.in.readLine());
			ps.setDate(4, outDate);
			
			//!!
			ps.setInt(1, bid);
			ps.setInt(2, callNumber);
			ps.setDate(5, null);

			ps.executeUpdate();
			updateBookCopyStatus(callNumber, copyNo);

			// commit work 
			Main.con.commit();
			ps.close();
			
			System.out.println("Call Number: " + callNumber);
			System.out.println("Copy Number: " + copyNo);
			System.out.println("Due Date: " + getDueDate(bid, outDate).toString());
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

    private static void updateBookCopyStatus(int callNumber, int copyNo)
    {

	String             status = "out";
	PreparedStatement  ps;
	  
	try
	{
	  ps = Main.con.prepareStatement("UPDATE bookCopy SET status = 'out' WHERE callNumber = ? AND copyNo = ?");
	

	  //ps.setString(1, status);
	  ps.setInt(1, callNumber);
	  ps.setInt(2, copyNo);
	  ps.execute();

	  Main.con.commit();

	  ps.close();
	}
	catch (SQLException ex)
	{
	    System.out.println("Message: " + ex.getMessage());
	    
	    try 
	    {
		Main.con.rollback();	
	    }
	    catch (SQLException ex2)
	    {
		System.out.println("Message: " + ex2.getMessage());
		System.exit(-1);
	    }
	}	
    }

	
	static Date stringToDate(String date) {
		try {SimpleDateFormat fm = new SimpleDateFormat("dd/MM/yy");
		java.util.Date utilDate = fm.parse(date);
		java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
		return sqlDate;
		}
		catch (ParseException p) {
			System.out.println("Message: Date must be informat dd/MM/yy");
			return null;
		}		
	}
	
	static Date getDueDate(int bid, Date outDate){
		
		Statement 				s;
		int 					bookTimeLimit = 0;
		
		try 
		{
			s = Main.con.createStatement();
			ResultSet rs = s.executeQuery("SELECT bookTimeLimit "
										+ "FROM Borrower B, BorrowerType C "
										+ "WHERE B.type = C.type "
										+ "AND B.bid = " + bid);
			while (rs.next()){
				bookTimeLimit = rs.getInt(1);
				
			}

			
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
		
		String outDateS = outDate.toString();
		String[] tokens = outDateS.split("-");
		
		GregorianCalendar gregCalendar = new GregorianCalendar(Integer.parseInt(tokens[0]), Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
		gregCalendar.add(Calendar.DAY_OF_YEAR, bookTimeLimit*7);
		java.sql.Date sqlDate = new java.sql.Date(gregCalendar.getTime().getTime());
		return sqlDate;		
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
			rs = statement.executeQuery("SELECT E.bid, E.name, E.emailAddress, A.callNumber, C.copyNo, A.title, B.outDate, D.bookTimeLimit "
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
				if (overdue(getDueDate(bid, outDate))) {
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

