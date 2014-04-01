package users;

import gui.Main;

import java.io.IOException;
import java.sql.*;
import java.util.GregorianCalendar;
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
	 * Add a new borrower to the library.  
	 * User should provide all required info.
	 */

	public static void addBorrower(String passwordstr, String namestr, String addressstr, String phonestr, 
			String emailstr, String sinOrStnostr, String typestr) {

		PreparedStatement  ps;
		try {
			ps = Main.con.prepareStatement("INSERT INTO Borrower VALUES (bid_c.nextval,?,?,?,?,?,?,?,?)");

			System.out.println(passwordstr);
			ps.setString(1, passwordstr);

			System.out.println(namestr);
			ps.setString(2, namestr);

			System.out.println(addressstr);
			if (addressstr.length() == 0) {
				ps.setString(3, null);
			} else {
				ps.setString(3, addressstr);
			}

			System.out.println(phonestr);
			if (phonestr.length() == 0) {
				ps.setString(4, null);
			} else {
				ps.setString(4,  phonestr);
			}

			System.out.println(emailstr);
			ps.setString(5, emailstr);

			System.out.println(sinOrStnostr);
			int sinOrStNo = Integer.parseInt(sinOrStnostr);
			// check if account already exists for this sinOrStNo
			Statement s = Main.con.createStatement();
			ResultSet rs = s.executeQuery("SELECT * "
					+ "FROM Borrower "
					+ "WHERE sinOrStNo='" + sinOrStNo + "'");
			if (rs.next()) {
				System.err.println("This SIN or student number is already associated with an account."
						+ "Please check the digits with the borrower.");
				s.close();
				ps.close();
				return;
			}
			ps.setInt(6, sinOrStNo);

			// expiry date set to 5 years from now
			GregorianCalendar gregToday = new GregorianCalendar();
			gregToday.add(Calendar.YEAR, 5);
			java.sql.Date expiryDate = new java.sql.Date(gregToday.getTime().getTime());
			ps.setDate(7, expiryDate);
			System.out.println(expiryDate);

			System.out.println(typestr);
			ps.setString(8, typestr);

			ps.executeUpdate();
			// commit work 
			Main.con.commit();
			ps.close();

			System.out.println("New borrower successfully added to database.");
		}

		catch (NumberFormatException ne) {
			System.err.println("A required field was left blank.");
		}
		catch (SQLException ex) {
			System.err.println("Message: " + ex.getMessage());
			try 
			{
				// undo the insert
				Main.con.rollback();	
			}
			catch (SQLException ex2)
			{
				System.err.println("Message: " + ex2.getMessage());
				System.exit(-1);
			}
		}
	}

	public static int getNewBid(String sinOrStNo){
		int bid = -1;
		// get new borrower's bid
		try {
			Statement s;
			
			s = Main.con.createStatement();
			ResultSet rs = s.executeQuery("SELECT bid "
					+ "FROM Borrower "
					+ "WHERE sinOrStNo=" + sinOrStNo);
			if(rs.next()) {
				bid = rs.getInt(1);
				System.out.println("Assigned borrower ID: " + bid + "\n\n");
			}
			s.close();
			
			return bid;
			
		} catch (SQLException e) {
			System.err.println("Message: " + e.getMessage());
		}
		return bid;
	}


	/*
	 * Check-out items borrowed by a borrower. To borrow items, borrowers provide their card 
	 * number and a list with the call numbers and copy numbers of the items they want to check out. 
	 * The system determines if the borrower's account is valid and if the library items are available
	 * for borrowing. Then it creates one or more borrowing records and prints a note with the items 
	 * and their due day (which is given to the borrower).  
	 */

	public static void checkOutItems(String bidS, String callAndCopyNumbersS) {

		try {
			int 			   bid = Integer.parseInt(bidS);

			List<String>	   callAndCopyNumbers = Arrays.asList(callAndCopyNumbersS.split(","));
			Statement  		   s;

			// today's date
			GregorianCalendar gregToday = new GregorianCalendar();
			java.sql.Date sqlToday = new java.sql.Date(gregToday.getTime().getTime());


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
			for (String c: callAndCopyNumbers){
				checkOutItem(bid, c.trim(), sqlToday);
			}

			// print due date
			System.out.println("Checked out items due " + getDueDate(bid, sqlToday));
		}
		catch (NumberFormatException ne) {
			System.err.println("A required field was left blank.");
		}
		catch (SQLException ex) {
			System.err.println("Message: " + ex.getMessage());
			try 
			{
				// undo the insert
				Main.con.rollback();	
			}
			catch (SQLException ex2)
			{
				System.err.println("Message: " + ex2.getMessage());
				System.exit(-1);
			}
		}
	}

	private static void checkOutItem(int bid, String callAndCopyNumber, Date outDate) {

		try {
			List<String> callAndCopyNo = Arrays.asList(callAndCopyNumber.split(" "));
			String callNumberS 		   = callAndCopyNo.get(0);
			String copyNoS 			   = callAndCopyNo.get(1);
			
			Statement          s;
			PreparedStatement  ps1;
			PreparedStatement  ps2;

			// get call number and copy number of item to be checked out
			int callNumber = Integer.parseInt(callNumberS);
			int copyNo = Integer.parseInt(copyNoS);

			//check if book is in library
			s = Main.con.createStatement();
			ResultSet rs = s.executeQuery("SELECT callNumber "
					+ "FROM BookCopy  "
					+ "WHERE status = 'in' "
					+ "AND callNumber = " + callNumber + "AND copyNo = " + copyNo);
			if (!rs.next()){
				System.err.println("Book " + callNumber + " " + copyNo + " is not available for borrowing at this time."
						+ "Please check the call number and copy number entered.");
			}

			// if book is in library
			else {		
				// create new borrowing tuple
				ps1 = Main.con.prepareStatement("INSERT INTO Borrowing VALUES (borid_c.nextval,?,?,?,?,null)");

				ps1.setInt(1, bid);
				ps1.setInt(2, callNumber);
				ps1.setInt(3, copyNo);
				ps1.setDate(4, outDate);		

				ps1.executeUpdate();
				System.out.println(callNumber + " " + copyNo + " has been checked out.");


				// update book copy status
				ps2 = Main.con.prepareStatement("UPDATE bookCopy SET status = 'out' WHERE callNumber = ? AND copyNo = ?");

				ps2.setInt(1, callNumber);
				ps2.setInt(2, copyNo);
				ps2.execute();

				// commit work 
				Main.con.commit();
				ps1.close();
				ps2.close();
				
				System.out.println("" + callNumber + " " + copyNo + "");
			}
		}

		catch (NumberFormatException ne) {
			System.err.println("A required field was left blank.");
		}
		catch (SQLException ex) {
			System.err.println("Message: " + ex.getMessage());
			try {
				// undo the insert
				Main.con.rollback();	
			}
			catch (SQLException ex2) {
				System.err.println("Message: " + ex2.getMessage());
				System.exit(-1);
			}
		}
	}


	/*
	 * Processes a return. When an item is returned, the clerk records the return by providing the 
	 * item's catalogue number. The system determines the borrower who had borrowed the 
	 * item and records that the item is "in". If the item is overdue, a fine is assessed for the 
	 * borrower. If there is a hold request for this item by another borrower, the item is 
	 * registered as "on hold" and a message is send to the borrower who made the hold request. 
	 */

	public static void processReturn(String callNumberS, String copyNoS) {

		try {
			// provided by clerk
			int callNumber = Integer.parseInt(callNumberS.trim());
			int copyNo = Integer.parseInt(copyNoS.trim());

			// determined by system
			int					borid;
			int					bid;
			Date				outDate;

			Statement			s;
			PreparedStatement   ps1;
			PreparedStatement   ps2;
			PreparedStatement   ps3;
			PreparedStatement   ps4;

			ps1 = Main.con.prepareStatement("UPDATE Borrowing SET inDate= ? WHERE callNumber= ? AND copyNo= ?");
			ps2 = Main.con.prepareStatement("INSERT INTO Fine VALUES (fid_c.nextval,?,?,?,?)");
			ps3 = Main.con.prepareStatement("UPDATE BookCopy SET status='on hold' WHERE callNumber= ? AND copyNo= ?");
			ps4 = Main.con.prepareStatement("UPDATE BookCopy SET status='in' WHERE callNumber= ? AND copyNo= ?");

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
			ps4.close();

		}
		catch (NumberFormatException ne) {
			System.err.println("A required field was left blank.");
		}
		catch (SQLException ex) {
			System.err.println("Message: " + ex.getMessage());
		}
	}


	/*
	 * Checks overdue items. The system displays a list of the items that are overdue and the 
	 * borrowers who have checked them out. The clerk may decide to send an email messages 
	 * to any of them (or to all of them). 
	 */
	//THIS METHOD TAKES IN BIDS AS INPUT TO KNOW WHICH EMAIL ADDRESSES TO SEND TO. IF 'ALL' IS INPUTTED, THEN EMAILS ALL BORROWERS

	public static void checkOverdueItems(String bidsSS) {

		try {
			Statement statement;
			ResultSet rs;

			List<Integer> overdueBids = new ArrayList<Integer>();  // for storing overdue item borrower ids, for emailing

			statement = Main.con.createStatement();

			System.out.println("List of items overdue and the borrowers who have checked them out:");

			rs = statement.executeQuery("SELECT E.bid, E.name, E.emailAddress, A.callNumber, C.copyNo, A.title, TO_CHAR(B.outDate, 'YYYY-MM-DD') as outDate "
					+ "FROM Book A, Borrowing B, BookCopy C, BorrowerType D, Borrower E "
					+ "WHERE B.callNumber = C.callNumber AND B.copyNo = C.copyNo AND D.type = E.type AND E.bid = B.bid "
					+ "AND C.callNumber = A.callNumber AND B.inDate IS NULL "
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
					overdueBids.add(bid);

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
					System.out.printf("%-20.20s\n", duedate);
				}
			}

			// Clerk can send an email to each user or all users
			List<String> bidsS = Arrays.asList(bidsSS.split(","));

			System.out.print("\n\nPlease list IDs of borrowers you would like to send an overdue email to, "
					+ "or input 'all' to send a message to all borrowers: ");
			//bidsS = Arrays.asList(Main.in.readLine().split(","));

			if (bidsS.get(0).equals("all")){
				for (int b : overdueBids) {
					sendEmailOverdue(b);
				}
			}
			else {
				for (String bs: bidsS){
					int b = Integer.parseInt(bs.trim());
					sendEmailOverdue(b);
				}
			}

			// close the statement;
			// the ResultSet will also be closed
			statement.close();
		}

		catch (SQLException ex) {
			System.err.println("Message: " + ex.getMessage());
		}

	}
	
	// Sends email to borrower with overdue item
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
				try {
					// undo the insert
					Main.con.rollback();	
				}
				catch (SQLException ex2) {
					System.out.println("Message: " + ex2.getMessage());
					System.exit(-1);
				}
			}

		}




	// Gets due date of an item given borrower's bid and out date of item
	public static Date getDueDate(int bid, Date outDate){
		Statement 				s;
		int 					bookTimeLimit = 0;

		try {
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
			try {
				// undo the insert
				Main.con.rollback();	
			}
			catch (SQLException ex2) {
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


	// Returns true of dueDate < today's date
	public static boolean overdue(Date dueDate){
		String dueDateString = dueDate.toString();
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
