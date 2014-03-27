package users;

import gui.Main;

import java.io.IOException;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LibrarianUser {
	
	/*
	 * Loads librarian's side of application.
	 * Uses buffer line reader and connection established in Main class.
	 */
	public static void main() {
		int choice;
		boolean quit;

		quit = false;
		
		try 
		{
		    while (!quit)
		    {
			System.out.print("\n\nPlease choose one of the following: \n");
			System.out.print("1.  Add book\n");
			//System.out.print("2.  Check out items\n");
			//System.out.print("3.  Process a return\n");
			//System.out.print("4.  Check overdue items\n");
			System.out.print("5.  Quit\n>>");

			choice = Integer.parseInt(Main.in.readLine());
			
			System.out.println(" ");

			switch(choice)
			{
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
		catch (IOException e)
		{
		    System.out.println("IOException!");
		    try
		    {
			Main.con.close();
			System.exit(-1);
		    }
		    catch (SQLException ex)
		    {
			 System.out.println("Message: " + ex.getMessage());
		    }
		}
		catch (SQLException ex)
		{
		    System.out.println("Message: " + ex.getMessage());
		}
	}

	
	/*
	 * Add a new book to the library.
	 * Librarian should provide all info for book.
	 */
	private static void addBook() {
		// TODO Auto-generated method stub
		int                callNumber;
		String 	           isbn;
		String             title;
		String             mainAuthor;
		String             publisher;
		int                year;
		int                copyNo;
		
		PreparedStatement  ps1;  // for adding Book
		PreparedStatement  ps2;  // for adding BookCopies
		
		try {
			ps1 = Main.con.prepareStatement("INSERT INTO Book VALUES (?,?,?,?,?,?");
			ps2 = Main.con.prepareStatement("INSERT INTO BookCopy VALUES (?,?,?");
			
			System.out.print("\n Book call number: ");
			callNumber = Integer.parseInt(Main.in.readLine());
			ps1.setInt(1, callNumber);
			
			System.out.print("\n Book isbn: ");
			isbn = Main.in.readLine();
			ps1.setString(2, isbn);
			
			System.out.print("\n Book title: ");
			title = Main.in.readLine();
			ps1.setString(3, title);
			
			System.out.print("\n Book main author: ");
			mainAuthor = Main.in.readLine();
			ps1.setString(4, mainAuthor);
			
			System.out.print("\n Book publisher: ");
			publisher = Main.in.readLine();
			ps1.setString(5,  publisher);
			
			System.out.print("\n Book published year: ");
			year = Integer.parseInt(Main.in.readLine());
			ps1.setInt(6, year);
			
			System.out.print("\n Book number of copies: ");
			copyNo = Integer.parseInt(Main.in.readLine());
			
			ps1.executeUpdate();
			for (int i = 1; i <= copyNo; i++) {
				ps2.setInt(1, callNumber);
				ps2.setInt(2, i);
				ps2.setString(3, "in");
				
				ps2.executeUpdate();
			}
			// commit work 
			Main.con.commit();
			ps1.close();
			ps2.close();
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
