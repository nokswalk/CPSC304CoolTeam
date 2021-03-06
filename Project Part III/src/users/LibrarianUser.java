package users;

import gui.Main;

import java.io.IOException;
import java.sql.*;
import java.sql.Date;
import java.util.*;

public class LibrarianUser {

	/*
	 * Loads librarian's side of application.
	 * Uses buffer line reader and connection established in Main class.
	 */
	public static void main() {

		int choice;
		boolean quit;

		quit = false;
		try {
			while (!quit) {
				System.out.print("\n\nPlease choose one of the following: \n");
				System.out.print("1.  Add book\n");
				System.out.print("2.  Generate a report of all checked out books\n"); 
				System.out.print("3.  Generate a report of the most popular items for a given year\n");
				System.out.print("4.  Quit\n>>");

				choice = Integer.parseInt(Main.in.readLine());

				System.out.println(" ");

				switch (choice) {
				case 1:  addBook(); break;
				case 2:  reportCheckedOutBooks(); break;
				case 3:  mostPopular(); break; 
				case 4:  quit = true; 
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
		catch (SQLException ex)	{
			System.err.println("Message: " + ex.getMessage());
		}
	}


	/*
	 * Adds a new book or new copy of an existing book to the library. The librarian provides 
	 * the information for the new book, and the system adds it to the library.
	 */
	private static void addBook() {
		// Search by title or author or subject
		int choice;

		try {
			System.out.print("\n\nPlease choose one of the following to add: \n");
			System.out.print("1.  New book\n");
			System.out.print("2.  New copy of an existing book\n");
			System.out.print("3.  Back\n>>");

			choice = Integer.parseInt(Main.in.readLine());

			System.out.println(" ");

			switch (choice) {
			case 1:  addNewBook(); break;
			case 2:  addNewBookCopy(); break;
			case 3:  return;
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

	private static void addNewBook() {

		// attributes of new book
		String 	           isbn;
		String             title;
		String             mainAuthor;
		String             publisher;
		int                year;

		//
		List<String>       subjects;
		List<String>       authors;

		Statement          s;    // to check if added book already exists in database

		PreparedStatement  ps1;  // for adding Book
		PreparedStatement  ps2;  // for adding BookCopy
		PreparedStatement  ps3;  // for adding HasSubject
		PreparedStatement  ps4;  // for adding HasAuthor

		try {

			ps1 = Main.con.prepareStatement("INSERT INTO Book VALUES (callNumber_c.nextval,?,?,?,?,?)");
			ps2 = Main.con.prepareStatement("INSERT INTO BookCopy VALUES (callNumber_c.currval,?,?)");
			ps3 = Main.con.prepareStatement("INSERT INTO HasSubject VALUES (callNumber_c.currval,?)");
			ps4 = Main.con.prepareStatement("INSERT INTO HasAuthor VALUES (callNumber_c.currval,?)");

			// new book
			System.out.print("Book ISBN: ");
			isbn = Main.in.readLine();

			// check if this book already in database
			s = Main.con.createStatement();
			ResultSet rs = s.executeQuery("SELECT * "
					+ "FROM Book "
					+ "WHERE isbn=" + isbn);
			if (rs.next()) {
				System.out.println("This book already exists in the library database."
						+ "Please select 'New copy' in the 'Add book' menu.");
				s.close();
				ps1.close();
				ps2.close();
				return;
			}

			ps1.setString(1, isbn);

			System.out.print("Book title: ");
			title = Main.in.readLine();
			ps1.setString(2, title);

			System.out.print("Book main author: ");
			mainAuthor = Main.in.readLine();
			ps1.setString(3, mainAuthor);

			System.out.print("Book publisher: ");
			publisher = Main.in.readLine();
			ps1.setString(4,  publisher);

			System.out.print("Book published year: ");
			year = Integer.parseInt(Main.in.readLine());
			ps1.setInt(5, year);

			ps1.executeUpdate();


			// new book copy
			ps2.setInt(1, 1);
			ps2.setString(2, "in");
			
			ps2.executeUpdate();


			// add subjects of book
			System.out.print("Book subjects: ");
			String temp = Main.in.readLine();
			subjects = Arrays.asList(temp.split(","));

			for (String subject : subjects) {				
				if (subject.trim().length() == 0) {
					ps3.setString(1, null);
				} else {
					ps3.setString(1, subject.trim());
				}
				
				ps3.executeUpdate();
			}


			// add other authors of book
			System.out.print("Book's other authors: ");
			String temp2 = Main.in.readLine();
			
			if (temp2.length() != 0) {
				authors = Arrays.asList(temp2.split(","));
				
				for (String author: authors) {					
					if (author.length() == 0) {
						ps4.setString(1, null);
					} else {
						ps4.setString(1, author.trim());
					}
					
					ps4.executeUpdate();
				}
			}

			
			// commit work 
			Main.con.commit();
			System.out.println("Book has been added successfully.");

			ps1.close();
			ps2.close();
			ps3.close();
			ps4.close();
			s.close();
		}

		catch (IOException e) {
			System.err.println("IOException!");
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

	private static void addNewBookCopy() {

		// attributes of new copy
		int                callNumber = 0;
		int                copyNo     = 0;
		String             status     = "in";

		// to get existing callNumber of this book
		String             isbn; 
		Statement          s; 

		// to add new copy into database
		PreparedStatement  ps;

		try {
			// use ISBN to get existing callNumber
			System.out.print("Book ISBN: ");
			isbn = Main.in.readLine();

			s = Main.con.createStatement();
			ResultSet rs1 = s.executeQuery("SELECT callNumber "
					+ "FROM Book "
					+ "WHERE isbn=" + isbn);

			while (rs1.next()) {
				callNumber = rs1.getInt(1);
			}

			// use callNumber to get #copies already existing in database
			ResultSet rs2 = s.executeQuery("SELECT COUNT(C.callNumber) "
					+ "FROM BookCopy C "
					+ "WHERE C.callNumber=" + callNumber);

			while (rs2.next()) {
				copyNo = rs2.getInt(1);
			}

			// use callNumber and copyNo to add book copy to database
			ps = Main.con.prepareStatement("INSERT INTO BookCopy VALUES (?,?,?)");

			if (callNumber != 0 || copyNo != 0) {
				ps.setInt(1, callNumber);
				ps.setInt(2, copyNo+1);
				ps.setString(3, status);

				// add copy to database table
				ps.execute();

				// commit work 
				Main.con.commit();
				System.out.println("Book copy has been added successfully.");
			}
			else {
				System.out.println("This book does not exist in the database yet."
						+ "  Please select 'New book' in the 'Add book' menu.");
			}

			ps.close();
			s.close();
		}

		catch (IOException e) {
			System.err.println("IOException!");
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
	

	/*
	 * Generate a report with all the books that have been checked out. For each
	 * book the report shows the date it was checked out and the due date. The
	 * system flags the items that are overdue. The items are ordered by the
	 * book call number. If a subject is provided the report lists only books
	 * related to that subject, otherwise all the books that are out are listed
	 * by the report.
	 */
	private static void reportCheckedOutBooks() {
		
		String subject;
		
		try {
			Statement s = Main.con.createStatement();
			ResultSet rs;
			
			System.out.println("Please enter a subject to report. \n "
					+ "If no subject is inputted, the report will contain all subjects.): \n>> ");
			subject = Main.in.readLine();
			
			// check that this is a valid subject
			if (!subject.trim().equals("")) {
				rs = s.executeQuery("SELECT * " 
										  + "FROM HasSubject "
										  + "WHERE subject='" + subject+"'");
				if (!rs.next()) {
					System.out.println("This subject does not exist in the library database.");
					s.close();
					return;
				}
			}
			
			// query callNumber, copyNo, title, outDate and bookTimeLimit
			// when borrowing's inDate is null.
			System.out.println("List of items that are out :");
			
			//if subject is inputed
			if(!subject.trim().equals("")) {
				rs = s.executeQuery("SELECT A.callNumber, C.copyNo, A.title, TO_CHAR(B.outDate, 'YYYY-MM-DD') as outDate, B.bid "
							+ "FROM Book A, Borrowing B, BookCopy C, BorrowerType D, Borrower E, HasSubject F "
							+ "WHERE B.callNumber = C.callNumber AND B.copyNo = C.copyNo AND D.type = E.type AND E.bid = B.bid AND F.callNumber = A.callNumber "
							+ "AND C.callNumber = A.callNumber AND B.inDate IS NULL AND F.subject='" + subject + "' "
							+ "ORDER BY A.callNumber, C.copyNo, A.title ASC");
			}
			
			//empty subject, all of the items will be selected
			else {
				rs = s.executeQuery("SELECT A.callNumber, C.copyNo, A.title, TO_CHAR(B.outDate, 'YYYY-MM-DD') as outDate, B.bid "
							+ "FROM Book A, Borrowing B, BookCopy C, BorrowerType D, Borrower E "
							+ "WHERE B.callNumber = C.callNumber AND B.copyNo = C.copyNo AND D.type = E.type AND E.bid = B.bid "
							+ "AND C.callNumber = A.callNumber AND B.inDate IS NULL "
							+ "ORDER BY A.callNumber, C.copyNo, A.title ASC");
			}
			
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
			System.out.printf("%-25s", "OVERDUE");  // for flagging
			
			System.out.println(" ");

			while (rs.next()) {
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
				
				Date outDate = rs.getDate("outDate");
				if(rs.wasNull()){
					System.out.printf("%-20.20s", " ");
				} else {
					System.out.printf("%-20.20s", outDate);
				}
				
				Integer bid = rs.getInt("bid");
				Date duedate = ClerkUser.getDueDate(bid,outDate);			
				System.out.printf("%-20.20s", duedate);
				
				// if item overdue, system flags it
				if(ClerkUser.overdue(duedate)){
					System.out.printf("%-20.20s\n", "*");
				}
				else
					System.out.printf("%-20.20s\n", " ");
			}
			
			// close the statement;
			// the ResultSet will also be closed
			s.close();
			
		} catch (SQLException ex) {
			System.err.println("Message: " + ex.getMessage());
		} catch (IOException e) {
			System.err.println("IOException!");
			try {
				Main.con.close();
				System.exit(-1);
			}
			catch (SQLException ex) {
				System.err.println("Message: " + ex.getMessage());
			}
		}
	}
	
	
	/*
	 * Generate a report with the most popular items in a given year. The
	 * librarian provides a year and a number n. The system lists out the top n
	 * books that where borrowed the most times during that year. The books are
	 * ordered by the number of times they were borrowed.
	 */
	private static void mostPopular() {
		try {
			System.out.println("Generating a report with most popular items.");
			System.out.println("Please specify how many books you wish to add into the report:\n>>");
			int amount = Integer.parseInt(Main.in.readLine());
			if(amount < 0){
				System.out.println("Negatives are not allowed.");
				return;
			}
			System.out.println("Please specify the year you wish to report:\n>>");
			String year = Main.in.readLine();
			Statement statement = Main.con.createStatement();

			ResultSet query = statement.executeQuery("SELECT A.callNumber, A.title, A.mainAuthor, A.isbn , COUNT(B.borid) AS count "  
												+ "FROM Borrowing B "
												+ "LEFT JOIN Book A "
												+ "ON B.callNumber=A.callNumber "
												+ "WHERE B.outDate > TO_DATE('"+year+"-01-01', 'YYYY-MM-DD') AND B.outDate < TO_DATE('"+year+"-12-31', 'YYYY-MM-DD') "
												+ "GROUP BY A.callNumber, A.title, A.mainAuthor, A.isbn "
												+ "ORDER BY count desc");

			// get info on ResultSet
			ResultSetMetaData rsmd = query.getMetaData();
			// get number of columns
			int numCols = rsmd.getColumnCount();
			// display column names;
			for (int i = 0; i < numCols; i++) {
				// get column name and print it
				System.out.printf("%-20s", rsmd.getColumnName(i + 1));
			}
			System.out.println(" ");
			
			for(int i = 0; i < amount; i++){
				if (!query.next()) {
					System.out.println("End of results");
					return;
				}
				// simplified output formatting; truncation may occur
				String title = query.getString("title");
				String isbn = query.getString("isbn");
				String mainAuthor = query.getString("mainAuthor");
				int count = query.getInt("count");

				System.out.printf("%-30.30s", title);
				System.out.printf("%-20.20s", mainAuthor);
				System.out.printf("%-10.10s", isbn);
				System.out.printf("%-10.10s\n", count);
			}

			// close the statement;
			// the ResultSet will also be closed
			statement.close();
			
		} catch (SQLException e) {
			System.err.println("Message: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("Message: " + e.getMessage());
		}
		catch (NumberFormatException ne) {
			System.err.println("A required field was left blank.");
		}
	}
}
