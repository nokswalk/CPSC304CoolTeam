package users;

import gui.Main;

import java.io.IOException;
import java.sql.*;
import java.util.GregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
				System.out.print("3.  Process a return\n");
				System.out.print("4.  Check overdue items\n");
				System.out.print("5.  Quit\n>>");

				choice = Integer.parseInt(Main.in.readLine());

				System.out.println(" ");

				switch (choice) {
				case 1:  addBorrower(); break;
				case 2:  checkOutItems(); break;
				case 3:  processReturn(); break;
				case 4:  checkOverdueItems(); break;
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
		String             password; 
		String             name;
		String             address;
		String             phone;
		String             emailAddress;
		int                sinOrStNo;
		String             type;

		PreparedStatement  ps;

		try {
			ps = Main.con.prepareStatement("INSERT INTO Borrower VALUES (bid_c.nextval,?,?,?,?,?,?,TO_DATE(?, 'YYYY-MM-DD'),?)");

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

			// expiry date set to 5 years from now
			GregorianCalendar gregToday = new GregorianCalendar();
			gregToday.add(Calendar.YEAR, 5);
			java.sql.Date expiryDate = new java.sql.Date(gregToday.getTime().getTime());
			ps.setDate(7, expiryDate);

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


	/*
	 * Checks out items borrowed by a borrower.  Borrowers provide their bid and a list
	 * of callNumbers and copyNos of the items they wish to check out. 
	 */
	private static void checkOutItems() throws ParseException {
		int 			   bid;
		List<String>	   callNumbers;
		Statement  		   s;

		// today's date
		GregorianCalendar gregToday = new GregorianCalendar();
		java.sql.Date sqlToday = new java.sql.Date(gregToday.getTime().getTime());

		try {
			System.out.print("Borrower ID: ");
			bid = Integer.parseInt(Main.in.readLine());

			System.out.print("List of call numbers to be checked out: ");
			callNumbers = Arrays.asList(Main.in.readLine().split(","));

			s = Main.con.createStatement();
			ResultSet rs = s.executeQuery("SELECT bid "
					+ "FROM Borrower "
					+ "WHERE bid = " + bid);
			// check that bid is valid
			if (!rs.next()){
				System.out.println("Invalid borrower ID.");
				return;
			}
			
			// check out all items that borrower listed
			for (String c: callNumbers){
				int callNumber = Integer.parseInt(c.trim());
				checkOutItem(bid, callNumber, sqlToday);
			}

			// after checking out items using helper function, 
			// display all that were successfully checked out
			ResultSet rs2 = s.executeQuery("SELECT callNumber, copyNo "
					+ "FROM Borrowing "
					+ "WHERE bid=" + bid + " AND outDate=" + sqlToday.toString());

			// display columns
			ResultSetMetaData rsmd = rs2.getMetaData();
			int numCols = rsmd.getColumnCount();
			System.out.println(" ");

			for (int i = 0; i < numCols; i++)
			{
				System.out.printf("%-15s", rsmd.getColumnName(i+1));    
			}

			System.out.println(" ");

			// display checked out items
			while(rs2.next())
			{
				int iCallNumber = rs.getInt(1);
				System.out.printf("%-10.10s", iCallNumber);

				int iCopyNo = rs.getInt(2);
				System.out.printf("%-20.20s", iCopyNo); 
			}
			System.out.println("Items due " + sqlToday);
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

	private static void checkOutItem(int bid, int callNumber, Date outDate) throws ParseException {

		Statement			s;

		try 
		{
			//check if book is in library
			s = Main.con.createStatement();
			ResultSet rs = s.executeQuery("SELECT callNumber "
					+ "FROM BookCopy  "
					+ "WHERE status = 'in' "
					+ "AND callNumber = " + callNumber);

			if (!rs.next()){
				System.out.println("Book not available for borrowing at this time.");

			}
			else {
				addBorrowingHelper(bid, callNumber, outDate);
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

	private static void addBorrowingHelper(int bid, int callNumber, Date outDate) throws ParseException {

		int				   copyNo;
		PreparedStatement  ps;

		try {
			ps = Main.con.prepareStatement("INSERT INTO Borrowing VALUES (borid_c.nextval,?,?,?,TO_DATE(?, 'YYYY-MM-DD'),null)");

			ps.setInt(1, bid);
			ps.setInt(2, callNumber);
			
			System.out.print("Copy number of item " + callNumber + ": ");
			copyNo = Integer.parseInt(Main.in.readLine());

			ps.setInt(3, copyNo);
			ps.setDate(4, outDate);		
			System.out.println("got past insert");

			ps.executeUpdate();

			updateBookCopyStatus(callNumber, copyNo);

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
			System.out.println("Message: Date must be in format dd/MM/yy.");
			return null;
		}		
	}

	public static Date getDueDate(int bid, Date outDate){

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
	 * Process a return.  Clerk provides item's callNumber and copyNo, and the system 
	 * registers the item as 'in'.  
	 * If the item is overdue, a fine is placed on the borrower.
	 * If there is a hold request on this item, the system registers the item as 'on hold'.
	 */
	private static void processReturn() {
		
		// provided by clerk
		int 				callNumber;
		int					copyNo;

		// determined by system
		int					borid;
		int					bid;
		Date				outDate;

		Statement			s;
		PreparedStatement   ps1;
		PreparedStatement   ps2;
		PreparedStatement   ps3;
		PreparedStatement   ps4;

		try {
			ps1 = Main.con.prepareStatement("UPDATE Borrowing SET inDate= ? WHERE callNumber= ? AND copyNo= ?");
			ps2 = Main.con.prepareStatement("INSERT INTO Fine VALUES (fid_c.nextval,?,?,?,?)");
			ps3 = Main.con.prepareStatement("UPDATE BookCopy SET status='on hold' WHERE callNumber= ? AND copyNo= ?");
			ps4 = Main.con.prepareStatement("UPDATE BookCopy SET status='in' WHERE callNumber= ? AND copyNo= ?");

			// first enter callNumber and copyNo
			System.out.print("Book call number: ");
			callNumber = Integer.parseInt(Main.in.readLine());
			System.out.print("Book copy number: ");
			copyNo = Integer.parseInt(Main.in.readLine());

			s = Main.con.createStatement();

			// get borid and bid of borrowing
			ResultSet rs = s.executeQuery("SELECT A.borid, A.bid, A.outDate, C.bookTimeLimit "
					+ "FROM Borrowing A, Borrower B, BorrowerType C "
					+ "WHERE A.callNumber=" + callNumber + " AND A.copyNo=" + copyNo
					+ " AND A.bid=B.bid AND B.type=C.type");
			if (!rs.next()) {
				System.out.println("This item has not been borrowed, please check the call number and copy number.");
				return;
			}
			borid = rs.getInt(1);
			bid = rs.getInt(2);
			outDate = rs.getDate(3);

			// update borrowing so that inDate is set
			GregorianCalendar gregToday = new GregorianCalendar();
			java.sql.Date sqlToday = new java.sql.Date(gregToday.getTime().getTime());
			ps1.setDate(1, sqlToday);
			ps1.setInt(2, callNumber);
			ps1.setInt(3, copyNo);
			ps1.execute();
			System.out.println("Item has been checked in.");

			// if item is overdue, place a fine on the borrower
			if (overdue(getDueDate(bid, outDate))) {
				double fine = overdueBy(outDate) * 0.10;  // charged 10 cents a day
				ps2.setDouble(1, fine);
				ps2.setDate(2, sqlToday);
				ps2.setDate(3, null);
				ps2.setInt(4, borid);
				ps2.execute();
				System.out.println("A fine has been placed on borrower " + bid + ".");
			}

			ResultSet rs2 = s.executeQuery("SELECT bid "
					+ "FROM HoldRequest "
					+ "WHERE callNumber=" + callNumber);
			// if there is a hold request on the book, register as on hold
			if (rs2.next()) {
				ps3.setInt(1, callNumber);
				ps3.setInt(2, copyNo);
				ps3.execute();

				int bidHold = rs2.getInt(1);

				// notify borrower who requested hold
				ResultSet rs3 = s.executeQuery("SELECT name, emailAddress "
						+ "FROM Borrower "
						+ "WHERE bid=" + bidHold);
				String nameHold = rs3.getString(1);
				String emailAddrHold = rs3.getString(2);

				System.out.println("Item has been registered as 'on hold' for borrower " + bidHold);
				System.out.println("Borrower "+ bid + ", " + nameHold + " (" + emailAddrHold + 
						"), has been notified about their held item.");
			}
			// otherwise update book copy so that it's registered as 'in'
			else {
				ps4.setInt(1, callNumber);
				ps4.setInt(2, copyNo);
				ps4.execute();
				System.out.println("Item has been registered as 'in'.");
			}

			Main.con.commit();
			ps1.close();
			ps2.close();
			ps3.close();

		}
		catch (IOException e) {
			System.out.println("IOException!");
		}
		catch (SQLException ex) {
			System.out.println("Message: " + ex.getMessage());
		}
	}


	/*
	 * Displays a list of the items that are overdue and the borrowers who have checked them out. 
	 * The clerk may decide to send an email messages to any of them (or to all of them).
	 */
	private static void checkOverdueItems() {
		Statement statement;
		ResultSet rs;
		List<String> bidsS = null;
		try {
			statement = Main.con.createStatement();

			System.out.println("List of items overdue and the borrowers who have checked them out:");

			rs = statement.executeQuery("SELECT E.bid, E.name, E.emailAddress, A.callNumber, C.copyNo, A.title, B.outDate "
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
			for (int i = 0; i < numCols-1; i++) {
				// get column name and print it
				System.out.printf("%-25s", rsmd.getColumnName(i + 1));
			}
			System.out.printf("%-25s", "DUEDATE");

			System.out.println(" ");

			while (rs.next()) {
				Integer bid = rs.getInt("bid");
				Date outDate = rs.getDate("outDate");
				Date duedate = getDueDate(bid, outDate);
				
				if (overdue(duedate)) {
					
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
					System.out.printf("%-20.20s", duedate);
				}
			}


			System.out.print("\n\nList of borrower IDs you would like to send an overdue email to (or type in 'all' to send to all borrowers): ");
			try {
				bidsS = Arrays.asList(Main.in.readLine().split(","));
			} catch (IOException e) {
				System.out.println("Message: " + e.getMessage());
				e.printStackTrace();
			}
			
			if (bidsS.get(0).equals("all")){
				
				ResultSet rs2 = statement.executeQuery("SELECT E.bid "
						+ "FROM Book A, Borrowing B, BookCopy C, BorrowerType D, Borrower E "
						+ "WHERE B.callNumber = C.callNumber AND B.copyNo = C.copyNo AND D.type = E.type AND E.bid = B.bid "
						+ "AND C.callNumber = A.callNumber AND B.inDate IS NULL "//(OR C.status = 'out')B.indate is null means item has not been returned.
						+ "ORDER BY E.bid, E.name ASC");
				
				while (rs2.next()) {
					Integer bid = rs2.getInt("bid");
					sendEmailOverdue(bid);
				}
			}
			else {
			for (String b: bidsS){
				int bid = Integer.parseInt(b.trim());
				sendEmailOverdue(bid);
			}
			}
			// close the statement;
			// the ResultSet will also be closed
			statement.close();
		} catch (SQLException ex) {
			System.out.println("Message: " + ex.getMessage());
		}
	}

	private static void sendEmailOverdue(int bid) {
		Statement 				s;
		String 					emailAddrHold;
		String					nameHold;

		try 
		{
			s = Main.con.createStatement();
			ResultSet rs = s.executeQuery("SELECT emailAddress, name "
					+ "FROM Borrower "
					+ "WHERE bid = " + bid);
			
			while (rs.next()){
				emailAddrHold = rs.getString(1);
				nameHold = rs.getString(2);
				System.out.println("\nBorrower "+ bid + ", " + nameHold + " (" + emailAddrHold + 
						"), has been notified about their overdue item.");
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


	public static boolean overdue(Date dueDate){
		String dueDateString = dueDate.toString();
		//System.out.println("Item due: " + dueDateString);

		String[] tokens = dueDateString.split("-");

		GregorianCalendar gregCalendar2 = new GregorianCalendar(Integer.parseInt(tokens[0]), 
				Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));

		GregorianCalendar gregCalendar = new GregorianCalendar();
		if(gregCalendar.after(gregCalendar2))
			return true;
		else
			return false;
	}

	public static int overdueBy(Date dueDate) {
		String dueDateString = dueDate.toString();
		String[] tokens = dueDateString.split("-");

		GregorianCalendar gregDueDate = new GregorianCalendar(Integer.parseInt(tokens[0]), 
				Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
		GregorianCalendar gregToday = new GregorianCalendar();

		java.util.Date dueDate1 = gregDueDate.getTime();
		java.util.Date today = gregToday.getTime();

		double diff = (double) ((today.getTime() - dueDate1.getTime()))/(1000*60*60*24);
		return (int) diff;
	}
}

