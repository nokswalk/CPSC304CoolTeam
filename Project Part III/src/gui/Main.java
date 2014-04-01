package gui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.JOptionPane;

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
	
	//special method, we call multiple methods, so we need to hold string values for JtextFields
	String str1 = null;
	String str2 = null;
	
	public void setString1(String str){
		this.str1 = str;
	}
	public void setString2(String str){
		this.str2 = str;
	}
	public String getString1(){
		return str1;
	}
	public String getString2(){
		return str2;
	}

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

		passwordField.addActionListener(this);
		loginButton.addActionListener(this);
		loginButton.addActionListener(new ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			mainFrame.dispose();
    			try {
    				showUserMenu();
    			} catch (ParseException e1) {
    				// TODO Auto-generated catch block
    				e1.printStackTrace();
    			}     
    		}
    	});

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

	private void setupButton(JButton button, JPanel panel){ //aligns and places button to the proper panel
		button.setAlignmentX(Component.CENTER_ALIGNMENT);
		panel.add(button);
	}

	private void showMenu(int user) throws ParseException{

		final JFrame menu = new JFrame("Menu");
		final JPanel panelLibrarianMenu = new JPanel();
		final JPanel panelLibrarianSubmenu = new JPanel();
		final JPanel panelBorrowerMenu = new JPanel();
		final JPanel panelBorrowerSubmenu = new JPanel();
		final JPanel panelClerkMenu = new JPanel();
		final JPanel panelClerkSubmenu = new JPanel();

		//setting how we want the panel to be shown on the frame
		panelLibrarianMenu.setLayout(new BoxLayout(panelLibrarianMenu, BoxLayout.Y_AXIS));
		panelLibrarianSubmenu.setLayout(new BoxLayout(panelLibrarianSubmenu, BoxLayout.Y_AXIS));
		panelBorrowerMenu.setLayout(new BoxLayout(panelBorrowerMenu, BoxLayout.Y_AXIS));
		panelBorrowerSubmenu.setLayout(new BoxLayout(panelBorrowerSubmenu, BoxLayout.Y_AXIS));
		panelClerkMenu.setLayout(new BoxLayout(panelClerkMenu, BoxLayout.Y_AXIS));
		panelClerkSubmenu.setLayout(new BoxLayout(panelClerkSubmenu, BoxLayout.Y_AXIS));

		if (user == 1){ //user is librarian
		//make addNewBook frame and panel
			int ROWS = 8;
			int COLUMNS = 2;
			final JFrame addNewBookFrame = new JFrame("Add a New Book");
			final JPanel paneladdNewBook = new JPanel();
			GridLayout layoutaddNewBook = new GridLayout(ROWS, COLUMNS);
			paneladdNewBook.setLayout(layoutaddNewBook);
			addNewBookFrame.setPreferredSize(new Dimension (500, 240));
			addNewBookFrame.setLocationRelativeTo(null);


		//make addNewBookCopy frame and panel
			int ROWSaddNewBookCopy = 2;
			int COLUMNSaddNewBookCopy = 2;
			final JFrame addNewBookCopyFrame = new JFrame("Add a New Book Copy");
			final JPanel paneladdNewBookCopy = new JPanel();
			GridLayout layoutaddNewBookCopy = new GridLayout(ROWSaddNewBookCopy, COLUMNSaddNewBookCopy);
			paneladdNewBookCopy.setLayout(layoutaddNewBookCopy);
			addNewBookCopyFrame.setPreferredSize(new Dimension (500, 80));
			addNewBookCopyFrame.setLocationRelativeTo(null);

		//make reportCheckOutBooks frame and panel
			final JFrame reportCheckedoutBooksFrame = new JFrame("Report Checked out Books");
			final JPanel toppanelreportCheckedoutBooks = new JPanel();
			final JPanel panelreportCheckedoutBooksNorth = new JPanel(); //has multiple things inside so we need to make a panel to contain it
			toppanelreportCheckedoutBooks.setLayout(new BoxLayout(toppanelreportCheckedoutBooks, BoxLayout.Y_AXIS));
			panelreportCheckedoutBooksNorth.setLayout(new FlowLayout()); //set the layout to go from left->right
			reportCheckedoutBooksFrame.getContentPane().add(toppanelreportCheckedoutBooks);			
			reportCheckedoutBooksFrame.setPreferredSize(new Dimension(800,600));
			reportCheckedoutBooksFrame.setLocationRelativeTo(null);

		//make mostPopular frame and panel
			int ROWSmostPopular = 3;
			int COLUMNSmostPopular = 2;
			final JFrame mostPopularFrame = new JFrame("Most Popular");
			final JPanel toppanelmostPopular = new JPanel();
			final JPanel panelmostPopularNorth = new JPanel();
			final JPanel panelmostPopularSouth = new JPanel();
			toppanelmostPopular.setLayout(new BoxLayout(toppanelmostPopular, BoxLayout.Y_AXIS));
			panelmostPopularNorth.setLayout(new GridLayout(ROWSmostPopular, COLUMNSmostPopular));
			panelmostPopularSouth.setLayout(new FlowLayout());
			mostPopularFrame.getContentPane().add(toppanelmostPopular);
			mostPopularFrame.setPreferredSize(new Dimension(870, 630));
			mostPopularFrame.setLocationRelativeTo(null);

		//making labels
			JLabel isbn = new JLabel("ISBN:");
			JLabel isbnaddNewBook = new JLabel("ISBN:");
			JLabel title = new JLabel("Title:");
			JLabel mainAuthor = new JLabel("Main Author:");
			JLabel publisher = new JLabel("Publisher:");
			JLabel year = new JLabel("Year:");
			JLabel subjects = new JLabel("Subjects: (place commas in between)");
			JLabel authors = new JLabel("Authors: (place commas in between)");
			JLabel reportCheckedOutBooksSubject = new JLabel("Subject:");
			JLabel mostPopularLabel1 = new JLabel("Generating a report with most popular items.");
			JLabel mostPopularLabel2 = new JLabel("Please specify how many books you wish to add into the report:");
			JLabel mostPopularLabel3 = new JLabel("Please specify the year you wish to report:");
			JLabel mostPopularLabel4 = new JLabel(""); //empty space
			
		//making buttons
			JButton addBook = new JButton("Add Book");
			JButton reportCheckedOutBooks = new JButton("Report Checked Out Books");
			JButton mostPopular = new JButton("Most Popular Books");
			JButton addNewBook = new JButton("Add New Book");
			JButton addNewBookCopy = new JButton("Add New Book Copy");
			JButton back = new JButton("Go Back");
			JButton quit = new JButton("Quit Program");
			JButton enteraddNewBook = new JButton("Enter");
			JButton enteraddNewBookCopy = new JButton("Enter");
			JButton canceladdNewBook = new JButton("Cancel");
			JButton canceladdNewBookCopy = new JButton("Cancel");
			JButton enterreportCheckedOutBooks = new JButton("Enter");
			JButton clearreportCheckedOutBooks = new JButton("Clear");
			JButton okayreportCheckedOutBooks = new JButton("Okay");
			JButton entermostPopular = new JButton("Enter");
			JButton cancelmostPopular = new JButton("Cancel");
			JButton clearmostPopular = new JButton("Clear");

		//making text field
			final JTextField isbntxtaddNewBookCopy = new JTextField();
			final JTextField isbntxtaddNewBook = new JTextField();
			final JTextField titletxt = new JTextField();
			final JTextField mainAuthortxt = new JTextField();
			final JTextField publishertxt = new JTextField();
			final JTextField yeartxt = new JTextField();
			final JTextField subjectstxt = new JTextField();
			final JTextField authorstxt = new JTextField();
			final JTextField reportCheckedOutBookstxt = new JTextField();
			final JTextField mostPopulartxt1 = new JTextField();
			final JTextField mostPopulartxt2 = new JTextField();
			
		//set size of text field
			reportCheckedOutBookstxt.setPreferredSize(new Dimension(400, 30));

		//make text area
			final JTextArea reportCheckedOutBookstxtarea = new JTextArea();
			final JTextArea mostPopulartxtarea = new JTextArea();

		//making table for Report Checked out books
			String[] columnNamesCB = {"CallNumber", "CopyNo", "Title", "Out Date", "BorrowerID", "Due Date" ,"Overdue"};
			final DefaultTableModel modelCB = new DefaultTableModel(null, columnNamesCB);
			final JTable tableCB = new JTable(modelCB);
			JScrollPane scrollPaneCB = new JScrollPane(tableCB);
			tableCB.setFillsViewportHeight(true);
			
		//making table for most popular
			String[] columnNamesMP = { "CALL NUMBER", "TITLE", "MAIN AUTHOR", "ISBN" , "COUNT" };
			final DefaultTableModel modelMP = new DefaultTableModel(null,columnNamesMP);
			final JTable tableMP = new JTable(modelMP);
			JScrollPane scrollPaneMP = new JScrollPane(tableMP);
			tableMP.setFillsViewportHeight(true);

			reportCheckedOutBookstxtarea.setPreferredSize(new Dimension(800, 500));
			mostPopulartxtarea.setPreferredSize(new Dimension(800, 500));
		//center align buttons
			addBook.setAlignmentX(Component.CENTER_ALIGNMENT);
			reportCheckedOutBooks.setAlignmentX(Component.CENTER_ALIGNMENT);
			mostPopular.setAlignmentX(Component.CENTER_ALIGNMENT);
			addNewBook.setAlignmentX(Component.CENTER_ALIGNMENT);
			addNewBookCopy.setAlignmentX(Component.CENTER_ALIGNMENT);
			back.setAlignmentX(Component.CENTER_ALIGNMENT);
			quit.setAlignmentX(Component.CENTER_ALIGNMENT);
			enteraddNewBook.setAlignmentX(Component.CENTER_ALIGNMENT);
			enteraddNewBookCopy.setAlignmentX(Component.CENTER_ALIGNMENT);
			canceladdNewBook.setAlignmentX(Component.CENTER_ALIGNMENT);
			canceladdNewBookCopy.setAlignmentX(Component.CENTER_ALIGNMENT);

			menu.setPreferredSize(new Dimension(200, 300));

		//attaching buttons to the panel
			panelLibrarianMenu.add(addBook);
			panelLibrarianMenu.add(reportCheckedOutBooks);
			panelLibrarianMenu.add(mostPopular);
			panelLibrarianMenu.add(quit);

			panelLibrarianSubmenu.add(addNewBook);
			panelLibrarianSubmenu.add(addNewBookCopy);
			panelLibrarianSubmenu.add(back);

		//attaching labels and txt field to the panel
			paneladdNewBook.add(isbnaddNewBook);
			paneladdNewBook.add(isbntxtaddNewBook);
			paneladdNewBook.add(title);
			paneladdNewBook.add(titletxt);
			paneladdNewBook.add(mainAuthor);
			paneladdNewBook.add(mainAuthortxt);
			paneladdNewBook.add(publisher);
			paneladdNewBook.add(publishertxt);
			paneladdNewBook.add(year);
			paneladdNewBook.add(yeartxt);
			paneladdNewBook.add(subjects);
			paneladdNewBook.add(subjectstxt);
			paneladdNewBook.add(authors);
			paneladdNewBook.add(authorstxt);
			paneladdNewBook.add(enteraddNewBook);
			paneladdNewBook.add(canceladdNewBook);

			paneladdNewBookCopy.add(isbn);
			paneladdNewBookCopy.add(isbntxtaddNewBookCopy);
			paneladdNewBookCopy.add(enteraddNewBookCopy);
			paneladdNewBookCopy.add(canceladdNewBookCopy);

			panelreportCheckedoutBooksNorth.add(reportCheckedOutBooksSubject);
			panelreportCheckedoutBooksNorth.add(reportCheckedOutBookstxt);
			panelreportCheckedoutBooksNorth.add(enterreportCheckedOutBooks);
			panelreportCheckedoutBooksNorth.add(clearreportCheckedOutBooks);
			//append panels tgt
			toppanelreportCheckedoutBooks.add(panelreportCheckedoutBooksNorth);
			toppanelreportCheckedoutBooks.add(scrollPaneCB);
//			toppanelreportCheckedoutBooks.add(reportCheckedOutBookstxtarea);
			toppanelreportCheckedoutBooks.add(okayreportCheckedOutBooks);
			
			panelmostPopularNorth.add(mostPopularLabel1);
			panelmostPopularNorth.add(mostPopularLabel4);
			panelmostPopularNorth.add(mostPopularLabel2);
			panelmostPopularNorth.add(mostPopulartxt1);
			panelmostPopularNorth.add(mostPopularLabel3);
			panelmostPopularNorth.add(mostPopulartxt2);
//			toppanelmostPopular.add(mostPopulartxtarea);
			panelmostPopularSouth.add(entermostPopular);
			panelmostPopularSouth.add(clearmostPopular);
			panelmostPopularSouth.add(cancelmostPopular);
			//append panels tgt
			toppanelmostPopular.add(panelmostPopularNorth);
			toppanelmostPopular.add(scrollPaneMP);
//			toppanelmostPopular.add(mostPopulartxtarea);
			toppanelmostPopular.add(panelmostPopularSouth);

		//add the panel into JFrame

			menu.getContentPane().add(panelLibrarianMenu);

			//add listeners to the buttons
			addBook.addActionListener(new ActionListener() { //go into submenu
	    		public void actionPerformed(ActionEvent e) {
	    			menu.getContentPane().add(panelLibrarianSubmenu); //attach submenu to the frame
					panelLibrarianMenu.setVisible(false);
					panelLibrarianSubmenu.setVisible(true);
	    		}
	    	});
			reportCheckedOutBooks.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			//we set standard output stream to printstream instead so that it can go to the GUI now
	    			TextAreaOutputStream taOutputStream = new TextAreaOutputStream(reportCheckedOutBookstxtarea, "Console output");
	    			System.setOut(new PrintStream(taOutputStream));
	    			reportCheckedoutBooksFrame.getContentPane().add(toppanelreportCheckedoutBooks);
	    			reportCheckedoutBooksFrame.pack();
	    			reportCheckedoutBooksFrame.setVisible(true);
	    		}
	    	});
			enterreportCheckedOutBooks.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			String subject =  reportCheckedOutBookstxt.getText();
	    			LibrarianUser.reportCheckedOutBooks(subject);
	    			List<String[]> rows = LibrarianUser.getCheckedoutReportData();
	    			for(String[] row:rows){
//	    				System.out.println(row[]);
		    			modelCB.addRow(row);
	    			}
	    			reportCheckedOutBookstxt.setText(null);
	    		}
	    	});
			okayreportCheckedOutBooks.addActionListener(new ActionListener() { //kill the frame
	    		public void actionPerformed(ActionEvent e) {
	    			reportCheckedOutBookstxt.setText(null);
	    			reportCheckedoutBooksFrame.dispose();
	    		}
	    	});
			clearreportCheckedOutBooks.addActionListener(new ActionListener() { //kill the frame
	    		public void actionPerformed(ActionEvent e) {
	    			reportCheckedOutBookstxtarea.setText(null);
	    		}
	    	});
			mostPopular.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			//we set standard output stream to printstream instead so that it can go to the GUI now
	    			TextAreaOutputStream taOutputStream = new TextAreaOutputStream(mostPopulartxtarea, "Console output");
	    			System.setOut(new PrintStream(taOutputStream));
//	    			mostPopularFrame.getContentPane().add(toppanelmostPopular);
	    			mostPopularFrame.pack();
	    			mostPopularFrame.setVisible(true);

	    		}
	    	});
			entermostPopular.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			String howManyBooks = mostPopulartxt1.getText();
	    			String year = mostPopulartxt2.getText();
	    			LibrarianUser.mostPopular(howManyBooks, year);
	    			String[][] rows = LibrarianUser.getMostPopularData();
	    			for(int i = 0; i< Integer.parseInt(howManyBooks); i++){
		    			modelMP.addRow(rows[i]);
	    			}
	    			mostPopulartxt1.setText(null);
	    			mostPopulartxt2.setText(null);
	    		}
	    	});
			clearmostPopular.addActionListener(new ActionListener() { //kill the frame
	    		public void actionPerformed(ActionEvent e) {
	    			int rowCount=modelMP.getRowCount();
	    			for (int i = 0;i<rowCount;i++) {
	    			    modelMP.removeRow(i);
	    			}
	    			mostPopulartxtarea.setText(null);
	    		}
	    	});
			cancelmostPopular.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			mostPopulartxt1.setText(null);
	    			mostPopulartxt2.setText(null);
	    			mostPopulartxtarea.setText(null);
	    			mostPopularFrame.dispose();
	    		}
	    	});
			addNewBook.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			addNewBookFrame.getContentPane().add(paneladdNewBook);
	    			addNewBookFrame.pack();
	    			addNewBookFrame.setVisible(true);
	    		}
	    	});
			addNewBookCopy.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			addNewBookCopyFrame.getContentPane().add(paneladdNewBookCopy);
	    			addNewBookCopyFrame.pack();
	    			addNewBookCopyFrame.setVisible(true);
	    		}
	    	});
			back.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			panelLibrarianSubmenu.setVisible(false);
	    			panelLibrarianMenu.setVisible(true);
	    		}
	    	});
			quit.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			menu.dispose();
	    			System.exit(0);
	    		}
	    	});
			enteraddNewBook.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			String isbnstr = isbntxtaddNewBook.getText();
	    			String titlestr = titletxt.getText();
	    			String mainAuthorstr = mainAuthortxt.getText();
	    			String publisherstr = publishertxt.getText();
	    			String yearstr = yeartxt.getText();
	    			String subjectsstr= subjectstxt.getText();
	    			String authorsstr = authorstxt.getText();
	    			LibrarianUser.addNewBook(isbnstr, titlestr, mainAuthorstr, publisherstr, yearstr, subjectsstr, authorsstr);
	    			isbntxtaddNewBook.setText(null);
	    			titletxt.setText(null);
	    			mainAuthortxt.setText(null);
	    			publishertxt.setText(null);
	    			yeartxt.setText(null);
	    			subjectstxt.setText(null);
	    			authorstxt.setText(null);
	    			addNewBookFrame.dispose();
	    		}
	    	});
			enteraddNewBookCopy.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			String isbnstr = isbntxtaddNewBookCopy.getText();
	    			LibrarianUser.addNewBookCopy(isbnstr);
	    			isbntxtaddNewBookCopy.setText(null);
	    			addNewBookCopyFrame.dispose();
	    		}
			});
			canceladdNewBook.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			isbntxtaddNewBook.setText(null);
	    			titletxt.setText(null);
	    			mainAuthortxt.setText(null);
	    			publishertxt.setText(null);
	    			yeartxt.setText(null);
	    			addNewBookFrame.dispose();
	    		}
	    	});
			canceladdNewBookCopy.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			isbntxtaddNewBookCopy.setText(null);
	    			addNewBookCopyFrame.dispose();
	    		}
	    	});

			//display the window
			menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			menu.pack();
			menu.setVisible(true);		
		}
		else if (user == 2){ //user is borrower
		//make searchbyauthor
			final JFrame searchAuthorFrame = new JFrame("Search Book by Author");
			final JPanel toppanelsearchAuthor = new JPanel();
			final JPanel panelsearchAuthorNorth = new JPanel();
			final JPanel panelsearchAuthorSouth = new JPanel();
			toppanelsearchAuthor.setLayout(new BoxLayout(toppanelsearchAuthor, BoxLayout.Y_AXIS));
			panelsearchAuthorNorth.setLayout(new FlowLayout());
			panelsearchAuthorSouth.setLayout(new FlowLayout());
			searchAuthorFrame.getContentPane().add(toppanelsearchAuthor);
			searchAuthorFrame.setPreferredSize(new Dimension(800, 600));
			searchAuthorFrame.setLocationRelativeTo(null);
			
		//make searchbysubject
			final JFrame searchSubjectFrame = new JFrame("Search Book by Subject");
			final JPanel toppanelsearchSubject = new JPanel();
			final JPanel panelsearchSubjectNorth = new JPanel();
			final JPanel panelsearchSubjectSouth = new JPanel();
			toppanelsearchSubject.setLayout(new BoxLayout(toppanelsearchSubject, BoxLayout.Y_AXIS));
			panelsearchSubjectNorth.setLayout(new FlowLayout());
			panelsearchSubjectSouth.setLayout(new FlowLayout());
			searchSubjectFrame.getContentPane().add(toppanelsearchSubject);
			searchSubjectFrame.setPreferredSize(new Dimension(800, 600));
			searchSubjectFrame.setLocationRelativeTo(null);
			
		//make searchbytitle
			final JFrame searchTitleFrame = new JFrame("Search Book by Title");
			final JPanel toppanelsearchTitle = new JPanel();
			final JPanel panelsearchTitleNorth = new JPanel();
			//final JPanel panelsearchTitleSouth = new JPanel();
			toppanelsearchTitle.setLayout(new BoxLayout(toppanelsearchTitle, BoxLayout.Y_AXIS));
			panelsearchTitleNorth.setLayout(new FlowLayout());
			//panelsearchTitleSouth.setLayout(new FlowLayout());
			searchTitleFrame.getContentPane().add(toppanelsearchTitle);
			searchTitleFrame.setPreferredSize(new Dimension(800, 600));
			searchTitleFrame.setLocationRelativeTo(null);
			
		//make checkaccount
			final JFrame checkAccountFrame = new JFrame("Check Account");
			final JPanel toppanelcheckAccount = new JPanel();
			final JPanel panelcheckAccountNorth = new JPanel();
			toppanelcheckAccount.setLayout(new BoxLayout(toppanelcheckAccount, BoxLayout.Y_AXIS));
			panelcheckAccountNorth.setLayout(new FlowLayout());
			checkAccountFrame.getContentPane().add(toppanelcheckAccount);
			checkAccountFrame.setPreferredSize(new Dimension(800, 600));
			checkAccountFrame.setLocationRelativeTo(null);
			
		//make holdrequest
			int ROWholdrequest = 3;
			int COLUMNholdrequest = 2;
			final JFrame holdRequestFrame = new JFrame("Hold Request");
			final JPanel panelholdRequest = new JPanel();
			panelholdRequest.setLayout(new GridLayout(ROWholdrequest, COLUMNholdrequest));
			holdRequestFrame.getContentPane().add(panelholdRequest);
			holdRequestFrame.setPreferredSize(new Dimension(500, 100));
			holdRequestFrame.setLocationRelativeTo(null);
			
		//make payfines
			final JFrame payFinesFrame = new JFrame("Pay Fines");
			final JPanel toppayFinespanel = new JPanel();
			final JPanel panelpayFinesNorth = new JPanel();
			final JPanel panelpayFinesSouth = new JPanel();
			toppayFinespanel.setLayout(new BoxLayout(toppayFinespanel, BoxLayout.Y_AXIS));
			panelpayFinesNorth.setLayout(new FlowLayout());
			panelpayFinesSouth.setLayout(new FlowLayout());
			payFinesFrame.getContentPane().add(toppayFinespanel);
			payFinesFrame.setPreferredSize(new Dimension(500, 600));
			payFinesFrame.setLocationRelativeTo(null);
			
		//making labels
			//Search Book
			JLabel titleLabel = new JLabel("Title keyword:");
			JLabel subjectLabel = new JLabel("Subject keyword:");
			JLabel authorLabel = new JLabel("Author keyword:");
			//Check Account
			JLabel checkAccountLabel = new JLabel("Borrower ID:");
			//Hold Request
			JLabel holdRequestBorrowerLabel = new JLabel("Borrower ID:");
			JLabel holdRequestBookLabel = new JLabel("Book call number:");
			//Pay Fines
			JLabel payFinesBorrowerLabel = new JLabel("Borrower ID:");
			JLabel payFinesFinesLabel = new JLabel("Pay fine: (Input fine ID)");
		
		//make some buttons
			JButton searchBook = new JButton("Search Book");
			JButton searchBookbyTitle = new JButton("Search Book by Title");
			JButton searchBookbySubject = new JButton("Search Book by Subject");
			JButton searchBookbyAuthor = new JButton("Search Book by Author");
			JButton checkAccount = new JButton("Check Account");
			JButton requestHold = new JButton("Place a Hold Request");
			JButton payFines = new JButton("Pay Fines");
			JButton back = new JButton("Go Back");
			JButton quit = new JButton("Quit Program");
			//Search Book buttons
			JButton searchAuthorSearch = new JButton("Search");
			JButton searchSubjectSearch = new JButton("Search");
			JButton searchTitleSearch = new JButton("Search");
			JButton searchAuthorClear = new JButton("Clear");
			JButton searchSubjectClear = new JButton("Clear");
			JButton searchTitleClear = new JButton("Clear");
			JButton searchAuthorCancel = new JButton("Cancel");
			JButton searchSubjectCancel = new JButton("Cancel");
			JButton searchTitleCancel = new JButton("Cancel");
			//Check Account buttons
			JButton checkAccountSearch = new JButton("Search");
			JButton checkAccountClear = new JButton("Clear");
			JButton checkAccountCancel = new JButton("Cancel");
			//Request Hold buttons
			JButton requestHoldHold = new JButton("Hold");
			JButton requestHoldCancel = new JButton("Cancel");
			//Pay Fines buttons
			JButton payFinesSearch = new JButton("Search");
			JButton payFinesCancel = new JButton("Cancel");
			JButton payFinesPay = new JButton("Pay");

		//making text field
			//Search Book
			final JTextField authortxt = new JTextField();
			final JTextField subjecttxt = new JTextField();
			final JTextField titletxt = new JTextField();
			//Check Account
			final JTextField checkAccounttxt = new JTextField();
			//Request Hold
			final JTextField requestHoldBorrowertxt = new JTextField();
			final JTextField requestHoldBooktxt = new JTextField();
			//Pay Fines
			final JTextField payFinesBorrowertxt = new JTextField();
			final JTextField payFinesFinetxt = new JTextField();
			
		//set size of text field
			//Search Book
			authortxt.setPreferredSize(new Dimension(400, 30));
			subjecttxt.setPreferredSize(new Dimension(400, 30));
			titletxt.setPreferredSize(new Dimension(400, 30));
			//Check Account
			checkAccounttxt.setPreferredSize(new Dimension(400,30));
			//Pay Fines
			payFinesBorrowertxt.setPreferredSize(new Dimension(200, 30));
			payFinesFinetxt.setPreferredSize(new Dimension(150,30));
			
		//making text area
			//Search Book
			final JTextArea authortxtarea = new JTextArea();
			final JTextArea subjecttxtarea = new JTextArea();
			final JTextArea titletxtarea = new JTextArea();
			//Check Account
			final JTextArea checkAccounttxtarea = new JTextArea();
			//Pay FIne
			final JTextArea payFinestxtarea = new JTextArea();

		//center align buttons
			searchBook.setAlignmentX(Component.CENTER_ALIGNMENT);
			searchBookbyTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
			searchBookbySubject.setAlignmentX(Component.CENTER_ALIGNMENT);
			searchBookbyAuthor.setAlignmentX(Component.CENTER_ALIGNMENT);
			checkAccount.setAlignmentX(Component.CENTER_ALIGNMENT);
			requestHold.setAlignmentX(Component.CENTER_ALIGNMENT);
			payFines.setAlignmentX(Component.CENTER_ALIGNMENT);
			back.setAlignmentX(Component.CENTER_ALIGNMENT);
			quit.setAlignmentX(Component.CENTER_ALIGNMENT);

		//set the menu frame's size
			menu.setPreferredSize(new Dimension(200, 300));

		//attaching buttons to the frame
			//Menu
			panelBorrowerMenu.add(searchBook);
			panelBorrowerMenu.add(checkAccount);
			panelBorrowerMenu.add(requestHold);
			panelBorrowerMenu.add(payFines);
			panelBorrowerMenu.add(quit);
			//Submenu
			panelBorrowerSubmenu.add(searchBookbyAuthor);
			panelBorrowerSubmenu.add(searchBookbySubject);
			panelBorrowerSubmenu.add(searchBookbyTitle);
			panelBorrowerSubmenu.add(back);
			//Search by Author
			panelsearchAuthorNorth.add(authorLabel);
			panelsearchAuthorNorth.add(authortxt);
			panelsearchAuthorNorth.add(searchAuthorSearch);
			panelsearchAuthorNorth.add(searchAuthorClear);
			panelsearchAuthorNorth.add(searchAuthorCancel);
			toppanelsearchAuthor.add(panelsearchAuthorNorth);
			toppanelsearchAuthor.add(authortxtarea);
			//Search by Subject
			panelsearchSubjectNorth.add(subjectLabel);
			panelsearchSubjectNorth.add(subjecttxt);
			panelsearchSubjectNorth.add(searchSubjectSearch);
			panelsearchSubjectNorth.add(searchSubjectClear);
			panelsearchSubjectNorth.add(searchSubjectCancel);
			toppanelsearchSubject.add(panelsearchSubjectNorth);
			toppanelsearchSubject.add(subjecttxtarea);
			//Search by Title
			panelsearchTitleNorth.add(titleLabel);
			panelsearchTitleNorth.add(titletxt);
			panelsearchTitleNorth.add(searchTitleSearch);
			panelsearchTitleNorth.add(searchTitleClear);
			panelsearchTitleNorth.add(searchTitleCancel);
			toppanelsearchTitle.add(panelsearchTitleNorth);
			toppanelsearchTitle.add(titletxtarea);
			//Check Account
			panelcheckAccountNorth.add(checkAccountLabel);
			panelcheckAccountNorth.add(checkAccounttxt);
			panelcheckAccountNorth.add(checkAccountSearch);
			panelcheckAccountNorth.add(checkAccountClear);
			panelcheckAccountNorth.add(checkAccountCancel);
			toppanelcheckAccount.add(panelcheckAccountNorth);
			toppanelcheckAccount.add(checkAccounttxtarea);
			//Request Hold
			panelholdRequest.add(holdRequestBorrowerLabel);
			panelholdRequest.add(requestHoldBorrowertxt);
			panelholdRequest.add(holdRequestBookLabel);
			panelholdRequest.add(requestHoldBooktxt);
			panelholdRequest.add(requestHoldHold);
			panelholdRequest.add(requestHoldCancel);
			//Pay Fines
			panelpayFinesNorth.add(payFinesBorrowerLabel);
			panelpayFinesNorth.add(payFinesBorrowertxt);
			panelpayFinesNorth.add(payFinesSearch);
			panelpayFinesSouth.add(payFinesFinesLabel);
			panelpayFinesSouth.add(payFinesFinetxt);
			panelpayFinesSouth.add(payFinesPay);
			panelpayFinesSouth.add(payFinesCancel);
			toppayFinespanel.add(panelpayFinesNorth);
			toppayFinespanel.add(payFinestxtarea);
			toppayFinespanel.add(panelpayFinesSouth);
			
		//add borrower menu to the frame
			menu.getContentPane().add(panelBorrowerMenu);

		//add the button listeners
			searchBook.addActionListener(new ActionListener() { //go into submenu
	    		public void actionPerformed(ActionEvent e) {
	    			menu.getContentPane().add(panelBorrowerSubmenu); //attach submenu to the frame
					panelBorrowerMenu.setVisible(false);
					panelBorrowerSubmenu.setVisible(true);
	    		}
	    	});
			searchBookbyTitle.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			TextAreaOutputStream taOutputStream = new TextAreaOutputStream(titletxtarea, "Console output");
	    			System.setOut(new PrintStream(taOutputStream));
	    			searchTitleFrame.getContentPane().add(toppanelsearchTitle);
	    			searchTitleFrame.pack();
	    			searchTitleFrame.setVisible(true);
	    		}
	    	});
			searchTitleSearch.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			String title = titletxt.getText();
	    			BorrowerUser.searchBookByTitle(title);
	    			titletxt.setText(null);
	    		}
	    	});
			searchTitleClear.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			titletxtarea.setText(null);
	    		}
	    	});
			searchTitleCancel.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			titletxt.setText(null);
	    			titletxtarea.setText(null);
	    			searchTitleFrame.dispose();
	    		}
	    	});
			searchBookbySubject.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			TextAreaOutputStream taOutputStream = new TextAreaOutputStream(subjecttxtarea, "Console output");
	    			System.setOut(new PrintStream(taOutputStream));
	    			searchSubjectFrame.getContentPane().add(toppanelsearchSubject);
	    			searchSubjectFrame.pack();
	    			searchSubjectFrame.setVisible(true);
	    		}
	    	});
			searchSubjectSearch.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			String subject = subjecttxt.getText();
	    			BorrowerUser.searchBookBySubject(subject);
	    			subjecttxt.setText(null);
	    		}
	    	});
			searchSubjectClear.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			subjecttxtarea.setText(null);
	    		}
	    	});
			searchSubjectCancel.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			subjecttxt.setText(null);
	    			subjecttxtarea.setText(null);
	    			searchSubjectFrame.dispose();
	    		}
	    	});
			searchBookbyAuthor.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			TextAreaOutputStream taOutputStream = new TextAreaOutputStream(authortxtarea, "Console output");
	    			System.setOut(new PrintStream(taOutputStream));
	    			searchAuthorFrame.getContentPane().add(toppanelsearchAuthor);
	    			searchAuthorFrame.pack();
	    			searchAuthorFrame.setVisible(true);
	    		}
	    	});
			searchAuthorSearch.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			String author = authortxt.getText();
	    			BorrowerUser.searchBookByAuthor(author);
	    			authortxt.setText(null);
	    		}
	    	});
			searchAuthorClear.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			authortxtarea.setText(null);
	    		}
	    	});
			searchAuthorCancel.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			authortxt.setText(null);
	    			authortxtarea.setText(null);
	    			searchAuthorFrame.dispose();
	    		}
	    	});
			checkAccount.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			checkAccountFrame.getContentPane().add(toppanelcheckAccount);
	    			checkAccountFrame.pack();
	    			checkAccountFrame.setVisible(true);
	    		}
	    	});
			checkAccountSearch.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			String bid = checkAccounttxt.getText();
	    			BorrowerUser.checkAccount(bid);;
	    			checkAccounttxt.setText(null);
	    		}
	    	});
			checkAccountClear.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			checkAccounttxtarea.setText(null);
	    		}
	    	});
			checkAccountCancel.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			checkAccounttxt.setText(null);
	    			checkAccounttxtarea.setText(null);
	    			checkAccountFrame.dispose();
	    		}
	    	});
			requestHold.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			holdRequestFrame.getContentPane().add(panelholdRequest);
	    			holdRequestFrame.pack();
	    			holdRequestFrame.setVisible(true);
	    		}
	    	});
			requestHoldHold.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			String borrower = requestHoldBorrowertxt.getText();
	    			String book = requestHoldBooktxt.getText();
	    			BorrowerUser.requestHold(borrower, book);
	    			requestHoldBorrowertxt.setText(null);
	    			requestHoldBooktxt.setText(null);
	    			holdRequestFrame.dispose();
	    		}
	    	});
			requestHoldCancel.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			requestHoldBorrowertxt.setText(null);
	    			requestHoldBooktxt.setText(null);
	    			holdRequestFrame.dispose();
	    		}
	    	});
			payFines.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			payFinesFrame.getContentPane().add(toppayFinespanel);
	    			payFinesFrame.pack();
	    			payFinesFrame.setVisible(true);
	    		}
	    	});
			payFinesSearch.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			String borrower = payFinesBorrowertxt.getText();
	    			setString1(borrower);
	    			BorrowerUser.payFineSearch(borrower);
	    			
	    		}
	    	});
			payFinesCancel.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			payFinesBorrowertxt.setText(null);
	    			payFinesFinetxt.setText(null);
	    			payFinesFrame.dispose();
	    		}
	    	});
			payFinesPay.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			String fineID = payFinesFinetxt.getText();
	    			BorrowerUser.payFine(fineID);
	    		}
	    	});
			back.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			panelBorrowerSubmenu.setVisible(false);
	    			panelBorrowerMenu.setVisible(true);
	    		}
	    	});
			quit.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			menu.dispose();
	    			System.exit(0);
	    		}
	    	});

			//display the window
			menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			menu.pack();
			menu.setVisible(true);
		}
		else if (user == 3){ //user is clerk
			
		//make addNewBook frame and panel
			int ADDBORROWERROWS = 8;
			int COLUMNS = 2;

			final JFrame addBorrowerFrame = new JFrame("Add a New Borrower");
			final JPanel paneladdBorrower = new JPanel();
			final JPanel paneladdBorrowerResult = new JPanel();
			GridLayout layout = new GridLayout(ADDBORROWERROWS, COLUMNS);
			paneladdBorrower.setLayout(layout);
			addBorrowerFrame.setPreferredSize(new Dimension (400, 250));
			addBorrowerFrame.setLocationRelativeTo(null);

		//make checkOutItems frame and panel
			int ROWScheckOutItems = 2;
			int COLUMNScheckOutItems = 2;
			final JFrame checkOutItemsFrame = new JFrame("Checkout Items");
			final JPanel panelcheckOutItems = new JPanel();
			final JPanel toppanelcheckOutItems = new JPanel();
			final JPanel panelcheckOutItemsrNorth = new JPanel();
			final JPanel panelcheckOutItemsSouth = new JPanel();
			toppanelcheckOutItems.setLayout(new BoxLayout(toppanelcheckOutItems, BoxLayout.Y_AXIS));
			panelcheckOutItemsrNorth.setLayout(new GridLayout(ROWScheckOutItems, COLUMNScheckOutItems));
			panelcheckOutItemsSouth.setLayout(new FlowLayout());
			checkOutItemsFrame.getContentPane().add(toppanelcheckOutItems);
			checkOutItemsFrame.setPreferredSize(new Dimension(800, 600));
			checkOutItemsFrame.setLocationRelativeTo(null);
			
		//make processReturn frame and panel and setup layout, size of frame, location of frame
			int ROWSprocessReturn = 3;
			int COLUMNprocessReturn = 2;
			final JFrame processReturnFrame = new JFrame("Process a return");
			final JPanel panelprocessReturnBooks = new JPanel();
			panelprocessReturnBooks.setLayout(new GridLayout(ROWSprocessReturn, COLUMNprocessReturn));
			processReturnFrame.getContentPane().add(panelprocessReturnBooks);
			processReturnFrame.setPreferredSize(new Dimension(400, 110));
			processReturnFrame.setLocationRelativeTo(null);
			
			
		//make checkOverdueItems frame and panel
			final JFrame checkOverdueItemsFrame = new JFrame("Check overdue items");
			final JPanel panelcheckOverdueItems = new JPanel();

		//making labels for addBorrower
			JLabel password = new JLabel("Password:");
			JLabel name = new JLabel("Name:");
			JLabel address = new JLabel("Address:");
			JLabel phone = new JLabel("Phone Number:");
			JLabel email = new JLabel("Email Address:");
			JLabel sinOrStno = new JLabel("SIN or Student Number:");
			JLabel type = new JLabel("Type(student, staff, etc):");
			final JLabel addBorrowerResultLabel = new JLabel("");
			
		//making labels
			//checkOutItems
			JLabel checkOutItemsBorrowerlabel = new JLabel("Borrower ID:");
			JLabel checkOutItemsCallCopylabel = new JLabel("Call Number and Copy Number: (Each entry separated by a ,)");
			//JLabel checkOutItems4 = new JLabel(""); //empty space
			//JLabel checkOutItems5 = new JLabel("Please list out copy number of books in order of specified call numbers:");
			JLabel checkOutItemsResultLabel = new JLabel("");
			//processReturn
			JLabel processReturncallNumberlabel = new JLabel("Call Number:");
			JLabel processReturncopyNumberlabel = new JLabel("Copy Number:");

		//making textarea for addBorrower
			//addBorrower
			final JTextField passwordtxt = new JTextField();
			final JTextField nametxt = new JTextField();
			final JTextField addresstxt = new JTextField();
			final JTextField phonetxt = new JTextField();
			final JTextField emailtxt = new JTextField();
			final JTextField sinOrStnotxt = new JTextField();
			final JTextField typetxt = new JTextField();
			//checkOutItems
			final JTextField bidtxt = new JTextField();
			final JTextField callncopyNumberstxt = new JTextField();
			//processReturn
			final JTextField processReturncallNumbertxt = new JTextField();
			final JTextField processReturncopyNumbertxt = new JTextField();
		
		//setSize
		//bidtxt.setPreferredSize(new Dimension(400, 30));
		//callncopyNumberstxt.setPreferredSize(new Dimension(400, 30)); 
		
		//make buttons
			JButton addBorrower = new JButton("Add Borrower");
			JButton checkOutItems = new JButton("Check out Items");
			JButton processReturn = new JButton("Process Return");
			JButton checkOverdueItems = new JButton("Check Overdue Items");
			JButton back = new JButton("Go Back");
			JButton quit = new JButton("Quit Program");
			JButton enter = new JButton("Enter");
			JButton cancel = new JButton("Cancel");
			final JButton close = new JButton("Close");
			//checkOutItems
			JButton enterCheckOutItems = new JButton("Enter");
			JButton cancelcheckOutItems = new JButton("Cancel");
			//processReturn
			JButton enterProcessReturn = new JButton("Enter");
			JButton cancelProcessReturn = new JButton("Cancel");
			
		//make txt area
			JTextArea checkOutItemstxtarea = new JTextArea();
			checkOutItemstxtarea.setPreferredSize(new Dimension(800, 400));

		//center align buttons
			addBorrower.setAlignmentX(Component.CENTER_ALIGNMENT);
			checkOutItems.setAlignmentX(Component.CENTER_ALIGNMENT);
			processReturn.setAlignmentX(Component.CENTER_ALIGNMENT);
			checkOverdueItems.setAlignmentX(Component.CENTER_ALIGNMENT);
			back.setAlignmentX(Component.CENTER_ALIGNMENT);
			quit.setAlignmentX(Component.CENTER_ALIGNMENT);

		//set the menu frame's size
			menu.setPreferredSize(new Dimension(200, 300));

		//attaching buttons to the frame
			panelClerkMenu.add(addBorrower);
			panelClerkMenu.add(checkOutItems);
			panelClerkMenu.add(processReturn);
			panelClerkMenu.add(checkOverdueItems);
			panelClerkMenu.add(quit);


		//attaching 
		//attach panel to the frame
			menu.getContentPane().add(panelClerkMenu);


		//attaching labels and txt field to the panel for addBorrower
			paneladdBorrower.add(password);
			paneladdBorrower.add(passwordtxt);
			paneladdBorrower.add(name);
			paneladdBorrower.add(nametxt);
			paneladdBorrower.add(address);
			paneladdBorrower.add(addresstxt);
			paneladdBorrower.add(phone);
			paneladdBorrower.add(phonetxt);
			paneladdBorrower.add(email);
			paneladdBorrower.add(emailtxt);
			paneladdBorrower.add(sinOrStno);
			paneladdBorrower.add(sinOrStnotxt);
			paneladdBorrower.add(type);
			paneladdBorrower.add(typetxt);
			paneladdBorrower.add(enter);
			paneladdBorrower.add(cancel);
			
		//attaching labels and txt field to the panel for checkOutItems

			panelcheckOutItemsrNorth.add(checkOutItemsBorrowerlabel);
			panelcheckOutItemsrNorth.add(bidtxt);
			panelcheckOutItemsrNorth.add(checkOutItemsCallCopylabel);
			panelcheckOutItemsrNorth.add(callncopyNumberstxt);
			
			panelcheckOutItemsSouth.add(enterCheckOutItems);
			panelcheckOutItemsSouth.add(cancelcheckOutItems);
			
			toppanelcheckOutItems.add(panelcheckOutItemsrNorth);
			toppanelcheckOutItems.add(checkOutItemstxtarea);
			toppanelcheckOutItems.add(panelcheckOutItemsSouth);
			//processReturn
			panelprocessReturnBooks.add(processReturncallNumberlabel);
			panelprocessReturnBooks.add(processReturncallNumbertxt);
			panelprocessReturnBooks.add(processReturncopyNumberlabel);
			panelprocessReturnBooks.add(processReturncopyNumbertxt);
			panelprocessReturnBooks.add(enterProcessReturn);
			panelprocessReturnBooks.add(cancelProcessReturn);
			
		//add listeners to the buttons
			addBorrower.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			addBorrowerFrame.getContentPane().add(paneladdBorrower);
	    			addBorrowerFrame.pack();
	    			addBorrowerFrame.setVisible(true);
	    			passwordtxt.setText(null);
	    			nametxt.setText(null);
	    			addresstxt.setText(null);
	    			phonetxt.setText(null);
	    			emailtxt.setText(null);
	    			sinOrStnotxt.setText(null);
	    			typetxt.setText(null);
	    		}
	    	});
			checkOutItems.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
	    			//we set standard output stream to printstream instead so that it can go to the GUI now
	    			checkOutItemsFrame.getContentPane().add(toppanelcheckOutItems);
	    			checkOutItemsFrame.pack();
	    			checkOutItemsFrame.setVisible(true);
	    			bidtxt.setText(null);
	    			callncopyNumberstxt.setText(null);
				}
	    	});
			processReturn.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			processReturnFrame.getContentPane().add(panelprocessReturnBooks);
	    			processReturnFrame.pack();
	    			processReturnFrame.setVisible(true);
	    		}
	    	});
			enterProcessReturn.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			String call = processReturncallNumbertxt.getText();
	    			String copy = processReturncopyNumbertxt.getText();
	    			System.out.println(call + " " + copy);
	    			//ClerkUser.processReturn(call, copy);
	    		}
	    	});
			cancelProcessReturn.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			processReturncallNumbertxt.setText(null);
	    			processReturncopyNumbertxt.setText(null);
	    			processReturnFrame.dispose();
	    		}
	    	});
			checkOverdueItems.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {

	    		}
	    	});
			back.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			panelClerkSubmenu.setVisible(false);
	    			panelClerkMenu.setVisible(true);
	    		}
	    	});
			quit.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			menu.dispose();
	    			System.exit(0);
	    		}
	    	});
			
			enterCheckOutItems.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			String bidstr = bidtxt.getText();
	    			String callncopyNumberstr = callncopyNumberstxt.getText();
	    			ClerkUser.checkOutItems(bidstr, callncopyNumberstr);
	    		}
	    	});
			
			enter.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			String passwordstr = passwordtxt.getText();
	    			String namestr = nametxt.getText();
	    			String addressstr = addresstxt.getText();
	    			String phonestr = phonetxt.getText();
	    			String emailstr = emailtxt.getText();
	    			String sinOrStnostr = sinOrStnotxt.getText();
	    			String typestr = typetxt.getText();
	    			ClerkUser.addBorrower(passwordstr, namestr, addressstr, phonestr, emailstr, sinOrStnostr, typestr);
	    			int bid = ClerkUser.getNewBid(sinOrStnostr);
	    			if(bid == -1){
	    				addBorrowerResultLabel.setName("Not able to retrieve Borrower ID with given SIN or Student Number.");
	    			}else
	    				addBorrowerResultLabel.setName(namestr + "'s Borrower ID (bid) is : " + bid);
	    			paneladdBorrowerResult.add(addBorrowerResultLabel);
	    			paneladdBorrowerResult.add(close);
	    			addBorrowerFrame.getContentPane().add(paneladdBorrowerResult);
	    			paneladdBorrower.setVisible(false);
	    			paneladdBorrowerResult.setVisible(true);
	    		}
	    	});
	    	close.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			addBorrowerFrame.dispose();
	    			paneladdBorrowerResult.setVisible(false);
	    			paneladdBorrower.setVisible(true);
	    		}
	    	});

	    	cancel.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			passwordtxt.setText(null);
	    			nametxt.setText(null);
	    			addresstxt.setText(null);
	    			phonetxt.setText(null);
	    			emailtxt.setText(null);
	    			sinOrStnotxt.setText(null);
	    			typetxt.setText(null);
	    			addBorrowerFrame.dispose();
	    		}
	    	});
	    	cancelcheckOutItems.addActionListener(new ActionListener() {
	    		public void actionPerformed(ActionEvent e) {
	    			bidtxt.setText(null);
	    			callncopyNumberstxt.setText(null);
	    			checkOutItemsFrame.dispose();
	    		}
	    	});


			//add the panel into JFrame
			//menu.getContentPane().add(panel);

			//display the window
			menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			menu.pack();
			menu.setVisible(true);
			//ClerkUser.main();
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
					showMenu(1);//this is librarian
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
    }

    public static void main(String args[])
    {
    	Main m = new Main();
    }
}
