package users;

import gui.Main;

import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.util.GregorianCalendar;

public class BorrowerUser {

	/*
	 * Loads borrower's side of application.
	 * Uses buffer line reader and connection established in Main class.
	 */
	public static void main() {

		int choice;
		boolean quit;

		quit = false;
		try {
			while (!quit) {
				System.out.print("\n\nPlease choose one of the following: \n");
				System.out.print("1.  Book search\n");
				System.out.print("2.  Check account\n");
				System.out.print("3.  Place a hold request\n");
				System.out.print("4.  Pay fines\n");
				System.out.print("5.  Quit\n>>");

				choice = Integer.parseInt(Main.in.readLine());

				System.out.println(" ");

				switch(choice) {
				case 1:  searchBook(); break;
//				case 2:  checkAccount(); break;
//				case 3:  requestHold(); break;
//				case 4:  payFine(); break;
				case 5:  quit = true; 
				}
			}
			Main.con.close();
			Main.in.close();
			System.out.println("\nGood Bye!\n\n");
			System.exit(0);

		}
		catch (IOException e) {
			System.err.println("IOException!");
			try {
				Main.con.close();
				System.exit(-1);
			}
			catch (SQLException ex) {
				System.err.println("Message: " + ex.getMessage());
			}
		}
		catch (NumberFormatException ne) {
			System.err.println("Please select an option.");
		}
		catch (SQLException ex) {
			System.err.println("Message: " + ex.getMessage());
		}
	}


	/*
	 * Search for books using keyword search on titles, authors and subjects. The result is a list 
	 * of books that match the search together with the number of copies that are in and out. 
	 */
	 //DON'T USE THIS ONE FOR MAIN!!! D: OMG
	private static void searchBook() {

		// Search by title or author or subject
		int choice;

		try {
			System.out.print("\n\nPlease choose one of the following to search by: \n");
			System.out.print("1.  Title\n");
			System.out.print("2.  Author\n");
			System.out.print("3.  Subject\n");
			System.out.print("4.  Back\n>>");

			choice = Integer.parseInt(Main.in.readLine());

			System.out.println(" ");

			switch (choice) {
//			case 1:  searchBookByTitle(); break;
//			case 2:  searchBookByAuthor(); break;
//			case 3:  searchBookBySubject(); break;
			case 4:  return;
			}
		}

		catch (IOException e) {
			System.err.println("IOException!");
			try {
				Main.con.close();
				System.exit(-1);
			}
			catch (SQLException ex) {
				System.err.println("Message: " + ex.getMessage());
			}
		}
		catch (NumberFormatException ne) {
			System.err.println("Please select an option.");
		}
	}

	public static void searchBookByTitle(String titleS) {	

		// searched title
		String             sTitle = titleS;

		// search results
		int          	   callNumber;
		String             isbn;
		String             title;
		String             mainAuthor;

		// book copies in/out
		int inLib=0;
		int outLib=0;

		// to execute queries
		Statement          s;

		try {
			// first search Book table based on title keyword
			System.out.print("\n Title keyword: ");
			//sTitle = Main.in.readLine();

			s = Main.con.createStatement();
			ResultSet rs = s.executeQuery("SELECT DISTINCT B.callNumber, B.isbn, B.title, B.mainAuthor "
					+ "FROM Book B "
					+ "WHERE B.title LIKE '%" + sTitle + "%'");

			// get info on ResultSet
			ResultSetMetaData rsmd = rs.getMetaData();

			// get number of columns
			int numCols = rsmd.getColumnCount();

			System.out.println(" ");

			// display column names;
			for (int i = 0; i < numCols; i++)
			{
				// get column name and print it

				System.out.printf("%-15s", rsmd.getColumnName(i+1));    
			}
			// add columns for in/out count
			System.out.printf("%-15s", "in library");
			System.out.printf("%-15s", "out/on hold");

			System.out.println(" ");

			while(rs.next())
			{
				// simplified output formatting; truncation may occur

				callNumber = rs.getInt(1);
				System.out.printf("%-15.15s", callNumber);

				isbn = rs.getString(2);
				System.out.printf("%-15.15s", isbn);

				title = rs.getString(3);
				System.out.printf("%-15.15s", title);

				mainAuthor = rs.getString(4);
				System.out.printf("%-15.15s", mainAuthor);

				int[] statusCounts = statusCounts(callNumber);
				inLib = statusCounts[0];
				outLib = statusCounts[1] + statusCounts[2];
				System.out.printf("%-15.15s", inLib);
				System.out.printf("%-15.15s\n", outLib);
			}

			System.out.println("\n No more search results");
			s.close();
		}
		catch (SQLException ex) {
			System.err.println("Message: " + ex.getMessage());
		}
	}

	public static void searchBookByAuthor(String authorS) {

		// searched author
		String             sAuthor = authorS;

		// search results
		int          	   callNumber;
		String             isbn;
		String             title;
		String             mainAuthor;

		// book copies in/out
		int inLib=0;
		int outLib=0;

		// to execute queries
		Statement          s;

		try {
			// first search Book table based on author name
			System.out.print("\n Author name: ");
			//sAuthor = Main.in.readLine();

			s = Main.con.createStatement();

			ResultSet rs = s.executeQuery("SELECT DISTINCT B.callNumber, B.isbn, B.title, B.mainAuthor "
					+ "FROM HasAuthor A, Book B "
					+ "WHERE (A.callNumber=B.callNumber AND A.name LIKE '%" + sAuthor + "%' ) "
					+ "OR B.mainAuthor LIKE '%" + sAuthor + "%'");

			// get info on ResultSet
			ResultSetMetaData rsmd = rs.getMetaData();

			// get number of columns
			int numCols = rsmd.getColumnCount();

			System.out.println(" ");

			// display column names;
			for (int i = 0; i < numCols; i++)
			{
				// get column name and print it

				System.out.printf("%-15s", rsmd.getColumnName(i+1));    
			}
			// add columns for in/out count
			System.out.printf("%-15s", "in library");
			System.out.printf("%-15s", "out/on hold");

			System.out.println(" ");

			while(rs.next())
			{
				// simplified output formatting; truncation may occur

				callNumber = rs.getInt(1);
				System.out.printf("%-15.15s", callNumber);

				isbn = rs.getString(2);
				System.out.printf("%-15.15s", isbn);

				title = rs.getString(3);
				System.out.printf("%-15.15s", title);

				mainAuthor = rs.getString(4);
				System.out.printf("%-15.15s", mainAuthor);

				int[] statusCounts = statusCounts(callNumber);
				inLib = statusCounts[0];
				outLib = statusCounts[1] + statusCounts[2];
				System.out.printf("%-15.15s", inLib);
				System.out.printf("%-15.15s\n", outLib);
			}

			System.out.println("\n No more search results");
			s.close();
		}

		catch (SQLException ex) {
			System.err.println("Message: " + ex.getMessage());
		}

	}

	public static void searchBookBySubject(String subjectS) {

		// searched subject
		String             sSubject = subjectS;

		// search results
		int          	   callNumber;
		String             isbn;
		String             title;
		String             mainAuthor;

		// book copies in/out
		int inLib;
		int outLib;

		// to execute queries
		Statement          s;

		try {
			// first search Book table based on subject name
			System.out.print("\n Subject: ");
			//sSubject = Main.in.readLine();

			s = Main.con.createStatement();

			ResultSet rs = s.executeQuery("SELECT B.callNumber, B.isbn, B.title, B.mainAuthor "
					+ "FROM HasSubject S, Book B "
					+ "WHERE S.callNumber=B.callNumber AND S.subject LIKE '%" + sSubject + "%'");

			// get info on ResultSet
			ResultSetMetaData rsmd = rs.getMetaData();

			// get number of columns
			int numCols = rsmd.getColumnCount();

			System.out.println(" ");

			// display column names;
			for (int i = 0; i < numCols; i++)
			{
				// get column name and print it

				System.out.printf("%-15s", rsmd.getColumnName(i+1));    
			}
			// add columns for in/out count
			System.out.printf("%-15s", "in library");
			System.out.printf("%-15s", "out/on hold");

			System.out.println(" ");

			while(rs.next())
			{
				// simplified output formatting; truncation may occur

				callNumber = rs.getInt(1);
				System.out.printf("%-15.15s", callNumber);

				isbn = rs.getString(2);
				System.out.printf("%-15.15s", isbn);

				title = rs.getString(3);
				System.out.printf("%-15.15s", title);

				mainAuthor = rs.getString(4);
				System.out.printf("%-15.15s", mainAuthor);

				int[] statusCounts = statusCounts(callNumber);
				inLib = statusCounts[0];
				outLib = statusCounts[1] + statusCounts[2];
				System.out.printf("%-15.15s", inLib);
				System.out.printf("%-15.15s\n", outLib);
			}

			System.out.println("\n No more search results");
			s.close();
		}

		catch (SQLException ex) {
			System.err.println("Message: " + ex.getMessage());
		}
	}


	/*
	 * Check his/her account. The system will display the items the borrower has currently 
	 * borrowed and not yet returned, any outstanding fines and the hold requests that have been 
	 * placed by the borrower. 
	 */
	public static void checkAccount(String userBidS) {

		int userBid = Integer.parseInt(userBidS);

		String title;
		String isbn;
		String mainAuthor;
		int amount;
		int totalAmount = 0;
		Date issuedDate;

		Statement s;

		try {
			s = Main.con.createStatement();

			System.out.printf("Please enter your Borrower ID: ");
			//userBid = Integer.parseInt(Main.in.readLine());

			// check that this is a valid Borrower account
			ResultSet rs = s.executeQuery("SELECT * "
					+ "FROM Borrower "
					+ "WHERE bid=" + userBid);
			if (rs.next() == false) {
				System.out.println("This is not a valid borrower ID.");
				s.close();
				return;
			}

			// Items on loan
			System.out.println("\nList of items currently on loan:");
			rs = s.executeQuery("SELECT A.title, A.isbn, A.mainAuthor "
					+ "FROM Book A, Borrowing B, BookCopy C, Borrower D "
					+ "WHERE B.bid = D.bid AND B.callNumber = C.callNumber "
					+ "AND B.copyNo = C.copyNo AND C.callNumber = A.callNumber AND B.inDate IS NULL "
					+ "AND D.bid=" + userBid
					+ "ORDER BY A.title ASC");
			// get info on ResultSet
			ResultSetMetaData rsmd = rs.getMetaData();

			// get number of columns
			int numCols = rsmd.getColumnCount();
			System.out.println(" ");
			// display column names;
			for (int i = 0; i < numCols; i++) {
				// get column name and print it
				System.out.printf("%-25s", rsmd.getColumnName(i + 1));
			}
			System.out.println(" ");

			while (rs.next()) {
				// for display purposes get everything from Oracle
				// as a string

				// simplified output formatting; truncation may occur

				title = rs.getString("title");
				if (rs.wasNull()) {
					System.out.printf("%-25.25s", " ");
				} else {
					System.out.printf("%-25.25s", title);
				}

				isbn = rs.getString("isbn");
				if (rs.wasNull()) {
					System.out.printf("%-9.9s", " ");
				} else {
					System.out.printf("%-9.9s", isbn);
				}

				mainAuthor = rs.getString("mainAuthor");
				if (rs.wasNull()) {
					System.out.printf("%-20.20s\n", " ");
				} else {
					System.out.printf("%-20.20s\n", mainAuthor);
				}
			}


			// Total outstanding fine
			System.out.println("\nOutstanding fines:");
			rs = s.executeQuery("SELECT A.amount, TO_CHAR(A.issuedDate, 'YYYY-MM-DD') as issuedDate, E.title "
					+ "FROM Fine A, Borrowing B, Borrower C, BookCopy D, Book E "
					+ "WHERE A.borid=B.borid AND B.bid=C.bid AND D.callNumber=E.callNumber "
					+ "AND B.callNumber=D.callNumber AND B.copyNo=D.copyNo "
					+ "AND A.paidDate IS NULL AND C.bid=" + userBid);
			// get info on ResultSet
			rsmd = rs.getMetaData();

			// get number of columns
			numCols = rsmd.getColumnCount();
			// display column names;
			for (int i = 0; i < numCols; i++) {
				// get column name and print it
				System.out.printf("%-20s", rsmd.getColumnName(i + 1));
			}
			System.out.println(" ");

			while (rs.next()) {
				// for display purposes get everything from Oracle
				// as a string

				// simplified output formatting; truncation may occur

				amount = rs.getInt("amount");
				if (rs.wasNull()) {
					System.out.printf("%-20.20s", " ");
				} else {
					System.out.printf("%-20.20s", amount);
				}
				totalAmount += amount;

				issuedDate = rs.getDate("issuedDate");
				if (rs.wasNull()) {
					System.out.printf("%-15.15s", " ");
				} else {
					System.out.printf("%-15.15s", issuedDate);
				}

				title = rs.getString("title");
				if (rs.wasNull()) {
					System.out.printf("%-50.50s\n", " ");
				} else {
					System.out.printf("%-50.50s\n", title);
				}
			}
			System.out.println("Total amount of outstanding fine is: " + totalAmount);
			System.out.println(" ");


			//Hold Request List
			System.out.println("\nPlaced hold requests:");
			rs = s.executeQuery("SELECT B.title, TO_CHAR(A.issuedDate, 'YYYY-MM-DD') as issuedDate "
					+ "FROM HoldRequest A, Book B, Borrower C "
					+ "WHERE A.callNumber=B.callNumber AND A.bid=C.bid "
					+ "AND C.bid=" + userBid);
			// get info on ResultSet
			rsmd = rs.getMetaData();

			// get number of columns
			numCols = rsmd.getColumnCount();
			// display column names;
			for (int i = 0; i < numCols; i++) {
				// get column name and print it
				System.out.printf("%-20s", rsmd.getColumnName(i + 1));
			}
			System.out.println(" ");

			while (rs.next()) {
				// for display purposes get everything from Oracle
				// as a string

				// simplified output formatting; truncation may occur
				title = rs.getString("title");
				if (rs.wasNull()) {
					System.out.printf("%-25.25s", " ");
				} else {
					System.out.printf("%-25.25s", title);
				}
				issuedDate = rs.getDate("issuedDate");
				if (rs.wasNull()) {
					System.out.printf("%-15.15s\n", " ");
				} else {
					System.out.printf("%-15.15s\n", issuedDate);
				}
			}

			// close the statement;
			// the ResultSet will also be closed
			s.close();

		} catch (SQLException ex) {
			System.err.println("Message: " + ex.getMessage());
		}
		catch (NumberFormatException ne) {
			System.err.println("A required field was left blank.");
		}
	}


	/*
	 * Place a hold request for a book that is out. When the item is returned, the system sends an 
	 * email to the borrower and informs the library clerk to keep the book out of the shelves. 
	 */
	public static void requestHold(String bidS, String callNumberS) {

		int					bid = Integer.parseInt(bidS);
		int					callNumber = Integer.parseInt(callNumberS);
		Statement			s;
		PreparedStatement   ps;

		try {
			s = Main.con.createStatement();

			System.out.println("Borrower ID: ");
			//bid = Integer.parseInt(Main.in.readLine());
			// check that this is a valid Borrower account
			ResultSet rs = s.executeQuery("SELECT * "
					+ "FROM Borrower "
					+ "WHERE bid=" + bid);
			if (rs.next() == false) {
				System.out.println("This is not a valid borrower ID.");
				s.close();
				return;
			}

			System.out.println("Book call number: ");
			//callNumber = Integer.parseInt(Main.in.readLine());
			// check that this is a valid Book call number
			rs = s.executeQuery("SELECT * "
					+ "FROM Borrower "
					+ "WHERE bid=" + bid);
			if (rs.next() == false) {
				System.out.println("This is not a valid item call number.");
				s.close();
				return;
			}

			// check that book is not in library
			rs = s.executeQuery("SELECT copyNo  "
					+ "FROM BookCopy  "
					+ "WHERE callNumber = " + callNumber
					+ " AND status = 'in'");
			if (rs.next()){
				System.out.println("Book is currently available in the library.");
				return;
			}


			// Place a hold request
			ps = Main.con.prepareStatement("INSERT INTO holdRequest VALUES (hid_c.nextval,?,?,?)");

			java.sql.Date 			issuedDate;
			GregorianCalendar gregCalendar = new GregorianCalendar();  // issuedDate is today
			issuedDate = new java.sql.Date(gregCalendar.getTime().getTime());

			ps.setInt(1, bid);
			ps.setInt(2, callNumber);
			ps.setDate(3, issuedDate);

			ps.executeUpdate();

			// commit work 
			Main.con.commit();
			ps.close();

			System.out.println("Hold request placed. You will be notified by email when the book is available.");
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
		catch (NumberFormatException ne) {
			System.err.println("A required field was left blank.");
		}
	}


	/*
	 * Pay a fine.
	 */
	 //Tony! this one is tricky theres a string input -> display fines -> input fid string
	public static void payFine(String bidS, String fidS)
	{
		int		   bid = Integer.parseInt(bidS);
		Statement  s;

		int		   sfid;
		String	   ans;

		try
		{
			s = Main.con.createStatement();

			System.out.print("Borrower ID: ");
			//bid = Integer.parseInt(Main.in.readLine());
			// check that this is a valid Borrower account
			ResultSet rs = s.executeQuery("SELECT * "
					+ "FROM Borrower "
					+ "WHERE bid=" + bid);
			if (rs.next() == false) {
				System.out.println("This is not a valid borrower ID.");
				s.close();
				return;
			}

			// Get all fines owed by borrower
			rs = s.executeQuery("SELECT fid, amount, TO_CHAR(issuedDate, 'YYYY-MM-DD') as issDate "
					+ "FROM Fine F, Borrowing B "
					+ "WHERE F.borid = B.borid "
					+ "AND B.bid = " + bid);

			// get info on ResultSet
			ResultSetMetaData rsmd = rs.getMetaData();
			// get number of columns
			int numCols = rsmd.getColumnCount();

			System.out.println(" ");
			// display column names;
			for (int i = 0; i < numCols; i++)
			{
				// get column name and print it
				System.out.printf("%-15s", rsmd.getColumnName(i+1));    
			}
			System.out.println(" ");

			while(rs.next())
			{
				// simplified output formatting; truncation may occur

				int fid = rs.getInt(1);
				System.out.printf("%-15.15s", fid);

				double amount = rs.getDouble(2);
				System.out.printf("%-15.15s", amount);

				Date issuedDate = rs.getDate(3);
				System.out.printf("%-15.15s", issuedDate);
			}


			// Ask borrower to select which fine to pay for
			System.out.println("\n\nSelect ID of fine you wish to pay: ");
			//String ans1 = Main.in.readLine();
			String ans1 = fidS;

			if (ans1.trim().equals("")){
				return;
			}
			//this is where the chosen fid is inputted
			sfid = Integer.parseInt(ans1);

			System.out.println("Proceed with payment?(y/n)");
//			ans = Main.in.readLine();

//			if (ans.equals("y")){
				updateFine(sfid);
//				System.out.println("Fine has been paid.");
//			}
//			else {
//				System.out.println("Payment cancelled.");
//			}
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
		catch (NumberFormatException ne) {
			System.err.println("A required field was left blank.");
		}
	}

	private static void updateFine(int fid)
	{
		Date			   paidDate;
		PreparedStatement  ps;

		try
		{
			ps = Main.con.prepareStatement("UPDATE fine SET paidDate = ? WHERE fid = ?");

			GregorianCalendar gregCalendar = new GregorianCalendar();
			paidDate = new java.sql.Date(gregCalendar.getTime().getTime());
			
			ps.setDate(1, paidDate);
			ps.setInt(2, fid);
			
			ps.execute();
			Main.con.commit();

			ps.close();
		}
		
		catch (SQLException ex) {
			System.err.println("Message: " + ex.getMessage());
			try {
				Main.con.rollback();	
			}
			catch (SQLException ex2) {
				System.err.println("Message: " + ex2.getMessage());
				System.exit(-1);
			}
		}	
	}



	//Helper method to get # book copies in/out/on hold.
	public static int[] statusCounts (int callNumber) {

		// result array
		int[] statusCounts = new int[3];

		try {
			Statement s = Main.con.createStatement();

			// # in library
			ResultSet rsi = s.executeQuery("SELECT COUNT(*) "
					+ "FROM BookCopy "
					+ "WHERE callNumber=" + callNumber + " AND status='in'");
			if (rsi.next()) {
				statusCounts[0] = rsi.getInt(1);
			}

			// # out of library 
			ResultSet rso = s.executeQuery("SELECT COUNT(*) "
					+ "FROM BookCopy "
					+ "WHERE callNumber=" + callNumber + " AND status='out'");
			if (rso.next()) {
				statusCounts[1] = rso.getInt(1);
			}

			// # on hold
			ResultSet rsh = s.executeQuery("SELECT COUNT(*) "
					+ "FROM BookCopy "
					+ "WHERE callNumber=" + callNumber + "AND status='on hold'");
			if (rsh.next()) {
				statusCounts[2] = rsh.getInt(1);
			}

			return statusCounts;
		}

		catch (SQLException ex) {
			System.err.println("Message: " + ex.getMessage());
			return null;
		}
	}

}
