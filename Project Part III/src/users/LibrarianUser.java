package users;

import gui.Main;

import java.io.IOException;
import java.sql.*;
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
				//System.out.print("2.  Check out items\n");
				//System.out.print("3.  Process a return\n");
				//System.out.print("4.  Check overdue items\n");
				System.out.print("5.  Quit\n>>");

				choice = Integer.parseInt(Main.in.readLine());

				System.out.println(" ");

				switch (choice) {
				case 1:  addBook(); break;
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
		catch (SQLException ex)	{
			System.out.println("Message: " + ex.getMessage());
		}
	}


	/*
	 * Add a new book to the library.
	 * Librarian should provide all info for book.
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

	private static void addNewBook() {

		// attributes of new book
		int                callNumber;
		String 	           isbn;
		String             title;
		String             mainAuthor;
		String             publisher;
		int                year;
		
		//
		List<String>       subjects;

		Statement          s;    // to check if added book already exists in database

		PreparedStatement  ps1;  // for adding Book
		PreparedStatement  ps2;  // for adding BookCopy
		PreparedStatement  ps3;  // for adding HasSubject

		try {

			ps1 = Main.con.prepareStatement("INSERT INTO Book VALUES (?,?,?,?,?,?)");
			ps2 = Main.con.prepareStatement("INSERT INTO BookCopy VALUES (?,?,?)");
			ps3 = Main.con.prepareStatement("INSERT INTO HasSubject VALUES (?,?)");

			// new book
			// TODO use a sequence 
			System.out.print("Book call number: ");
			callNumber = Integer.parseInt(Main.in.readLine());
			ps1.setInt(1, callNumber);

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
			
			ps1.setString(2, isbn);

			System.out.print("Book title: ");
			title = Main.in.readLine();
			ps1.setString(3, title);

			System.out.print("Book main author: ");
			mainAuthor = Main.in.readLine();
			ps1.setString(4, mainAuthor);

			System.out.print("Book publisher: ");
			publisher = Main.in.readLine();
			ps1.setString(5,  publisher);

			System.out.print("Book published year: ");
			year = Integer.parseInt(Main.in.readLine());
			ps1.setInt(6, year);

			// new book copy
			ps2.setInt(1, callNumber);
			ps2.setInt(2, 1);
			ps2.setString(3, "in");
			
			// add subjects of book
			System.out.print("Book subjects: ");
			String temp = Main.in.readLine();
			subjects = Arrays.asList(temp.split(","));
			
			for (String subject : subjects) {
				ps3.setInt(1, callNumber);
				ps3.setString(2, subject.trim());
			}

			// add book and book copy and subjects to database tables
			ps1.executeUpdate();
			ps2.executeUpdate();
			ps3.executeUpdate();

			// commit work 
			Main.con.commit();
			System.out.println("Book has been added successfully.");
			
			ps1.close();
			ps2.close();
			s.close();
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

	private static void addNewBookCopy() {
		// TODO Auto-generated method stub

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
