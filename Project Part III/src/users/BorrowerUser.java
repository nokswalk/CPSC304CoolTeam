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
				case 2:  ; break; // TODO checkAccount()
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
		// TODO Auto-generated method stub

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
		String             sTitle;
		Statement          s;

		try {
			System.out.print("\n Title keyword: ");
			sTitle = Main.in.readLine();

			s = Main.con.createStatement();
			ResultSet rs = s.executeQuery("SELECT B.callNumber, B.isbn, B.title, B.mainAuthor, C.copyNo, c.status"
										+ "FROM HasAuthor A, Book B, BookCopy C"
										+ "WHERE B.callNumber = C.callNumber AND B.callNumber = A.callNumber AND B.title LIKE '%" + sTitle + "%'"
										+ "GROUP BY B.callNumber");

			// TODO How to get # of copies in or out of library?
			int           callNumber = 0;
			String        isbn = null;
			String        title = null;
			String        mainAuthor = null;
			int           copyNo = 0;
			String        status = null;
			
			// keep track of book copies
			int inLib = 0;
			int outLib = 0;

			while (rs.next()) {
				// if this callNumber same as previous tuple's, 
				// only difference in fields are copyNo and status
				if (rs.getInt(1) == callNumber) {
					if (rs.getString(5).equalsIgnoreCase("in")) {
						inLib++;
					}
					else {
						outLib++;
					}
				}
				// if not the same callNumber as previous tuple
				// must be next book (because of use of GROUP BY in query)
				else {
					// first print out previous book
					System.out.println("Call number: " + callNumber + "\n"
									+ "ISBN: " + isbn + "\n"
									+ "Title: " + title + "\n"
									+ "Main Author: " + mainAuthor + "\n"
									+ "Copies in library: " + inLib + "\n"
									+ "Copies out of library or on hold: " + outLib + "\n\n");
					
					// then reset copy counters
					inLib = 0;  outLib = 0;
					
					// then parse in the new book
					callNumber = rs.getInt(1);
					isbn = rs.getString(2);
					title = rs.getString(3);
					mainAuthor = rs.getString(4);
					copyNo = rs.getInt(5);
					status = rs.getString(5);
				}
			}

		}
		catch (IOException e) {
			System.out.println("IOException!");
		}
		catch (SQLException ex) {
			System.out.println("Message: " + ex.getMessage());
		}
	}

	private static void searchBookByAuthor() {
		// TODO Auto-generated method stub

	}

	private static void searchBookBySubject() {
		// TODO Auto-generated method stub

	}
}
