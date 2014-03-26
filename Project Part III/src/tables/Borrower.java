package tables;

import java.io.IOException;
import java.sql.*;

public class Borrower {

	private Connection con;

	/*
	 * inserts a branch
	 */ 
	private void insertBranch(
			int bid,
			String password, 
			String name,
			String address,
			int phone,
			String emailAddress,
			int sinOrStNo,
			Date expiryDate,
			String type)
	{
		PreparedStatement  ps;

		try
		{
			ps = con.prepareStatement("INSERT INTO branch VALUES (?,?,?,?,?,?,?,?,?)");

			ps.setInt(1, bid);
			ps.setString(2, password);
			ps.setString(3, name);
			ps.setString(4, address);
			ps.setInt(5,phone);
			ps.setString(6, emailAddress);
			ps.setInt(7, sinOrStNo);
			ps.setDate(8, expiryDate);
			ps.setString(9, type);

			ps.executeUpdate();

			// commit work 
			con.commit();

			ps.close();
		}
		
		catch (SQLException ex)
		{
			System.out.println("Message: " + ex.getMessage());
			try 
			{
				// undo the insert
				con.rollback();	
			}
			catch (SQLException ex2)
			{
				System.out.println("Message: " + ex2.getMessage());
				System.exit(-1);
			}
		}
	}
}
