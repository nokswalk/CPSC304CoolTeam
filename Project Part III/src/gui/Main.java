package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.text.ParseException;

import javax.swing.*;

import users.BorrowerUser;
import users.ClerkUser;
import users.LibrarianUser;


/**
 * Loads application.
 * Using simple text interface like "branch" tutorial until GUI is set up
 */
public class Main implements ActionListener {

	// command line reader 
    public static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    
	public static Connection con;
	
    // user is allowed 3 login attempts
    private int loginAttempts = 0;

	// components of the login window
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JFrame mainFrame;


    /*
     * constructs login window and loads JDBC driver
     */ 
	public Main() {
		// TODO
		mainFrame = new JFrame("User Login");

		JLabel usernameLabel = new JLabel("Enter username: ");
		JLabel passwordLabel = new JLabel("Enter password: ");

		usernameField = new JTextField(10);
		passwordField = new JPasswordField(10);
		passwordField.setEchoChar('*');

		JButton loginButton = new JButton("Log In");

		JPanel contentPane = new JPanel();
		mainFrame.setContentPane(contentPane);


		// layout components using the GridBag layout manager

		GridBagLayout gb = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		contentPane.setLayout(gb);
		contentPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// place the username label 
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.insets = new Insets(10, 10, 5, 0);
		gb.setConstraints(usernameLabel, c);
		contentPane.add(usernameLabel);

		// place the text field for the username 
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(10, 0, 5, 10);
		gb.setConstraints(usernameField, c);
		contentPane.add(usernameField);

		// place password label
		c.gridwidth = GridBagConstraints.RELATIVE;
		c.insets = new Insets(0, 10, 10, 0);
		gb.setConstraints(passwordLabel, c);
		contentPane.add(passwordLabel);

		// place the password field 
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(0, 0, 10, 10);
		gb.setConstraints(passwordField, c);
		contentPane.add(passwordField);

		// place the login button
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.insets = new Insets(5, 10, 10, 10);
		c.anchor = GridBagConstraints.CENTER;
		gb.setConstraints(loginButton, c);
		contentPane.add(loginButton);

		// register password field and OK button with action event handler
		passwordField.addActionListener(this);
		loginButton.addActionListener(this);

		// anonymous inner class for closing the window
		mainFrame.addWindowListener(new WindowAdapter() 
		{
			public void windowClosing(WindowEvent e) 
			{ 
				System.exit(0); 
			}
		});

		// size the window to obtain a best fit for the components
		mainFrame.pack();

		// center the frame
		Dimension d = mainFrame.getToolkit().getScreenSize();
		Rectangle r = mainFrame.getBounds();
		mainFrame.setLocation( (d.width - r.width)/2, (d.height - r.height)/2 );

		// make the window visible
		mainFrame.setVisible(true);

		// place the cursor in the text field for the username
		usernameField.requestFocus();

		try 
		{
			// Load the Oracle JDBC driver
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		}
		catch (SQLException ex)
		{
			System.out.println("Message: " + ex.getMessage());
			System.exit(-1);
		}
	}


	/*
	 * connects to Oracle database named ug using user supplied username and password
	 */ 
	private boolean connect(String username, String password)
	{
		String connectURL = "jdbc:oracle:thin:@localhost:1522:ug"; 

		try 
		{
			con = DriverManager.getConnection(connectURL,username,password);

			System.out.println("\nConnected to Oracle!");
			return true;
		}
		catch (SQLException ex)
		{
			System.out.println("Message: " + ex.getMessage());
			return false;
		}
	}


	/*
	 * event handler for login window
	 */ 
	public void actionPerformed(ActionEvent e) 
	{
		if ( connect(usernameField.getText(), String.valueOf(passwordField.getPassword())) )
		{
			// if the username and password are valid, 
			// remove the login window and display a text menu 
			mainFrame.dispose();
			try {
				showUserMenu();
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}     
		}
		else
		{
			loginAttempts++;

			if (loginAttempts >= 3)
			{
				mainFrame.dispose();
				System.exit(-1);
			}
			else
			{
				// clear the password
				passwordField.setText("");
			}
		}             

	}
	
	private void showMenu(int user) throws ParseException{
		final JFrame menu = new JFrame("Menu");
		final JPanel panel = new JPanel();
		final JPanel panelLibrarianSubmenu = new JPanel();
		JTextArea textArea = new JTextArea();

		TextAreaOutputStream taOutputStream = new TextAreaOutputStream(textArea, "Console output");
		
		//setting how we want the panel to be shown on the frame
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		panelLibrarianSubmenu.setLayout(new BoxLayout(panelLibrarianSubmenu, BoxLayout.Y_AXIS));
		
		//panel.add(new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER)); //make a scrollbar for navigation purposes
		//System.setOut(new PrintStream(taOutputStream));
		if (user == 1){ //user is librarian
			
		//making buttons
			JButton addBook = new JButton("Add Book");
			JButton reportCheckedOutBooks = new JButton("Report Checked Out Books");
			JButton mostPopular = new JButton("Most Popular Books");
			JButton addNewBook = new JButton("Add New Book");
			JButton addNewBookCopy = new JButton("Add New Book Copy");
			JButton back = new JButton("Go Back");
			
		//center align buttons
			addBook.setAlignmentX(Component.CENTER_ALIGNMENT);
			reportCheckedOutBooks.setAlignmentX(Component.CENTER_ALIGNMENT);
			mostPopular.setAlignmentX(Component.CENTER_ALIGNMENT);
			addNewBook.setAlignmentX(Component.CENTER_ALIGNMENT);
			addNewBookCopy.setAlignmentX(Component.CENTER_ALIGNMENT);
			back.setAlignmentX(Component.CENTER_ALIGNMENT);
			
			menu.setPreferredSize(new Dimension(200, 300));
			
		//attaching buttons to the frame
			panel.add(addBook);
			panel.add(reportCheckedOutBooks);
			panel.add(mostPopular);
			
			panelLibrarianSubmenu.add(addNewBook);
			panelLibrarianSubmenu.add(addNewBookCopy);
			panelLibrarianSubmenu.add(back);
			
			
			
			//panel.add(textArea);
			
		//add the panel into JFrame
			
			menu.getContentPane().add(panel);
			
			//add listeners to the buttons
			addBook.addActionListener(new ActionListener() { //go into submenu
	    		public void actionPerformed(ActionEvent e) {
	    			menu.getContentPane().add(panelLibrarianSubmenu); //attach submenu to the frame
					panel.setVisible(false);
					panelLibrarianSubmenu.setVisible(true);
	    		}
	    	});
			reportCheckedOutBooks.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			
	    		}
	    	});
			mostPopular.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			
	    		}
	    	});
			addNewBook.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			
	    		}
	    	});
			addNewBookCopy.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			
	    		}
	    	});
			back.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			panelLibrarianSubmenu.setVisible(false);
	    			panel.setVisible(true);
	    		}
	    	});
			
			
			//panelSubmenu.setVisible(false);
			
			//display the window
			menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			menu.pack();
			menu.setVisible(true);
			
			//System.out.println("HELLOLLOLOLOOLOOOOO");
			//LibrarianUser.main();
		}
		else if (user == 2){ //user is borrower
			menu.setPreferredSize(new Dimension(700, 800));
			menu.add(textArea);
			menu.pack();
			menu.setVisible(true);
			BorrowerUser.main();
		}
		else if (user == 3){ //user is clerk
			menu.setPreferredSize(new Dimension(700, 800));
			menu.add(textArea);
			menu.pack();
			menu.setVisible(true);
			ClerkUser.main();
		}
		else{
			System.out.println("OMG THIS SHOULD NEVER HAPPEN BUT IT DID SO HAHAHA");
			System.exit(1);
		}
	}
	

	/*
     * displays simple text interface
     */ 
    private void showUserMenu() throws ParseException
    {
    	
    	final JFrame userMenu = new JFrame("Choose user");
    	JLabel welcomeUser = new JLabel("Please choose one of the following:");
    	JButton librarian = new JButton("Librarian");
    	JButton borrower = new JButton("Borrower");
    	JButton clerk = new JButton("Clerk");	
    	JPanel panel = new JPanel(new FlowLayout());
    	
    	userMenu.getContentPane().add(panel);
    	
    	userMenu.setPreferredSize(new Dimension(350, 100));
    	panel.add(librarian);
    	panel.add(borrower);
    	panel.add(clerk);
    	userMenu.setLocationRelativeTo(null);
    	librarian.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			try {
					userMenu.dispose();
					showMenu(1);
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    		}
    	});
    	borrower.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			try {
    				userMenu.dispose();
					showMenu(2);	
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    		}
    	});
    	clerk.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			try {
					userMenu.dispose();
					showMenu(3);
				} catch (ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    		}
    	});
    	
    	panel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
    	
    	
    	userMenu.pack();
    	userMenu.setVisible(true);
    	
    	
    /**
     * the old stuff using console to use the program
     */
 /*
	int choice;
	boolean quit;

	quit = false;
	
	try 
	{
	    // disable auto commit mode
	    con.setAutoCommit(false);

	    while (!quit)
	    {
		System.out.print("\n\nPlease choose one of the following: \n");
		System.out.print("1.  Borrower\n");
		System.out.print("2.  Clerk\n");
		System.out.print("3.  Librarian\n");
		System.out.print("4.  Quit\n>>");

		choice = Integer.parseInt(in.readLine());
		
		System.out.println(" ");

		switch(choice)
		{
		   case 1:  BorrowerUser.main(); break;
		   case 2:  ClerkUser.main(); break;
		   case 3:  LibrarianUser.main(); break;
		   case 4:  quit = true; 
		}
	    }

	    con.close();
        in.close();
	    System.out.println("\nGood Bye!\n\n");
	    System.exit(0);
	}
	catch (IOException e)
	{
	    System.out.println("IOException!");

	    try
	    {
		con.close();
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
*/
    }

    
    public static void main(String args[])
    {
    	Main m = new Main();
    }
}
