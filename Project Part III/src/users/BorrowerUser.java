package users;

import gui.Main;

import java.io.IOException;
import java.sql.*;

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
				//System.out.print("2.  Check account\n");
				//System.out.print("3.  Place a hold request\n");
				//System.out.print("4.  Pay fines\n");
				System.out.print("5.  Quit\n>>");

				choice = Integer.parseInt(Main.in.readLine());

				System.out.println(" ");

				switch(choice) {
				case 1:  searchBook(); break;
				case 2:  checkAccount(); break;
				case 3:  ; break; // TODO requestHold()
				case 4:  ; break; // TODO payFine()
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
	 * Keyword book search on titles, authors, and/or subjects.
	 */
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
			case 1:  searchBookByTitle(); break;
			case 2:  searchBookByAuthor(); break;
			case 3:  searchBookBySubject(); break;
			case 4:  return;
			}
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
	}

	private static void searchBookByTitle() {	

		// searched title
		String             sTitle;

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
			sTitle = Main.in.readLine();

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

				// # in library
				ResultSet rsi = s.executeQuery("SELECT COUNT(*) "
						+ "FROM BookCopy "
						+ "WHERE callNumber=" + callNumber + " AND status='in'");
				while (rsi.next()) {
					inLib = rsi.getInt(1);
				}
				System.out.printf("%-15.15s", inLib);

				// # out of library or on hold
				ResultSet rso = s.executeQuery("SELECT COUNT(*) "
						+ "FROM BookCopy "
						+ "WHERE callNumber=" + callNumber + " AND (status='out' OR status='on hold')");
				while (rso.next()) {
					outLib = rso.getInt(1);
				}
				System.out.printf("%-15.15s", outLib);
			}

			System.out.println("\n No more search results");
			s.close();
		}

		catch (IOException e) {
			System.out.println("IOException!");
		}
		catch (SQLException ex) {
			System.out.println("Message: " + ex.getMessage());
		}
	}

	private static void searchBookByAuthor() {

		// searched author
		String             sAuthor;

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
			sAuthor = Main.in.readLine();

			s = Main.con.createStatement();

			ResultSet rs = s.executeQuery("SELECT DISTINCT B.callNumber, B.isbn, B.title, B.mainAuthor "
					+ "FROM HasAuthor A, Book B "
					+ "WHERE (A.callNumber=B.callNumber AND A.name LIKE '%" + sAuthor + "%') "
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

				// # in library
				ResultSet rsi = s.executeQuery("SELECT COUNT(*) "
						+ "FROM Book B, BookCopy C "
						+ "WHERE B.callNumber=" + callNumber + "B.callNumber=C.callNumber AND C.status='in'");
				while (rsi.next()) {
					inLib = rsi.getInt(1);
				}
				System.out.printf("%-15.15s", inLib);

				// # out of library or on hold
				ResultSet rso = s.executeQuery("SELECT COUNT(*) "
						+ "FROM BookB, BookCopy C "
						+ "WHERE B.callNumber=" + callNumber + "B.callNumber=C.callNumber "
						+ "AND (C.status='out' OR C.status='on hold'");
				while (rso.next()) {
					outLib = rso.getInt(1);
				}
				System.out.printf("%-15.15s", outLib);
			}

			System.out.println("No more search results");
			s.close();
		}

		catch (IOException e) {
			System.out.println("IOException!");
		}
		catch (SQLException ex) {
			System.out.println("Message: " + ex.getMessage());
		}

	}

	private static void searchBookBySubject() {

		// searched subject
		String             sSubject;

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
			System.out.print("\n Subject: ");
			sSubject = Main.in.readLine();

			s = Main.con.createStatement();

			ResultSet rs = s.executeQuery("SELECT DISTINCT B.callNumber, B.isbn, B.title, B.mainAuthor "
					+ "FROM HasSubject S, Book B "
					+ "WHERE S.callNumber=B.callNumber AND S.subject='" + sSubject + "'");

			// for each of the results
			while (rs.next()) {
				// parse the book
				callNumber = rs.getInt(1);
				isbn = rs.getString(2);
				title = rs.getString(3);
				mainAuthor = rs.getString(4);

				// # in library
				s.executeQuery("SELECT COUNT(*) "
						+ "FROM Book B, BookCopy C "
						+ "WHERE B.callNumber=" + callNumber + "B.callNumber=C.callNumber AND C.status='in'");
				while (rs.next()) {
					inLib = rs.getInt(1);
				}

				// # out of library or on hold
				s.executeQuery("SELECT COUNT(*) "
						+ "FROM BookB, BookCopy C "
						+ "WHERE B.callNumber=" + callNumber + "B.callNumber=C.callNumber "
						+ "AND (C.status='out' OR C.status='on hold')");
				while (rs.next()) {
					outLib = rs.getInt(1);
				}

				// print out search results
				System.out.println("\n Call number: " + callNumber 
						+ "\n ISBN: " + isbn
						+ "\n Title: " + title + "\n"
						+ "\n Main Author: " + mainAuthor 
						+ "\n Copies in library: " + inLib 
						+ "\n Copies out of library or on hold: " + outLib + "\n");
			}
			System.out.println("No more search results");
			s.close();
		}

		catch (IOException e) {
			System.out.println("IOException!");
		}
		catch (SQLException ex) {
			System.out.println("Message: " + ex.getMessage());
		}
	}

	/*
	 * Displays the items the borrower has currently borrowed and
	 * not yet returned, any outstanding fines and 
	 * the hold requests that have been placed by the borrower.
	 */
	private static void checkAccount() {
		String userBid;

		String title;
		String isbn;
		String mainAuthor;
		int amount;
		int totalAmount = 0;
		Date issuedDate;
		Statement stmt;
		ResultSet rs;


		try {
			System.out.printf("Put your Borrower ID: ");
			userBid = Main.in.readLine(); //TODO: I should put constraint.

			stmt = Main.con.createStatement();

			// query of title, isbn and mainAuthor when borrowing's inDate is null.
			System.out.println("List of items you currently borrowed:");
			rs = stmt.executeQuery("SELECT A.title, A.isbn, A.mainAuthor "
					+ "FROM Book A, Borrowing B, BookCopy C, Borrower D "
					+ "WHERE B.bid = D.bid AND B.callNumber = C.callNumber "
					+ "AND B.copyNo = C.copyNo AND C.callNumber = A.callNumber AND B.inDate IS NULL "
					+ "AND D.bid=" + userBid
					+ "GROUP BY A.title");

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
					System.out.printf("%-50.50s", " ");
				} else {
					System.out.printf("%-50.50s", title);
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

			//total outstanding fine
			System.out.println("Outstanding fine:");
			rs = stmt.executeQuery("SELECT A.amount, A.issuedDate, D.title "
					+ "FROM Fine A, Borrowing B, Borrower C, BookCopy D, Book E "
					+ "WHERE A.borid=B.borid AND B.bid=C.bid AND D.callNumber=E.callNumber "
					+ "AND B.callNumber=D.callNumber AND B.copyNo=D.copyNo "
					+ "AND A.payDate IS NULL AND C.bid=" + userBid);
			// get info on ResultSet
			rsmd = rs.getMetaData();

			// get number of columns
			numCols = rsmd.getColumnCount();

			System.out.println(" ");

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
			System.out.println("HOLD REQUEST LIST placed by you:");
			rs = stmt.executeQuery("SELECT B.title, A.issuedDate "
					+ "FROM HoldRequest A, Book B, Borrower C "
					+ "WHERE A.callNumber=B.callNumber AND A.bid=C.bid "
					+ "AND C.bid=" + userBid);
			// get info on ResultSet
			rsmd = rs.getMetaData();

			// get number of columns
			numCols = rsmd.getColumnCount();

			System.out.println(" ");

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
					System.out.printf("%-50.50s", " ");
				} else {
					System.out.printf("%-50.50s", title);
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
			stmt.close();
		} catch (SQLException ex) {
			System.out.println("Message: " + ex.getMessage());
		} catch (IOException e) {
			System.out.println("Message: " + e.getMessage());
		}
	}
}