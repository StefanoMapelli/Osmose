package it.txt.tellme.toolboEeventService.core.common;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class PostgresConnection {

	private Connection con;
	private Statement stmt;

	public PostgresConnection() throws Exception{

	  	try {
			  Class.forName(Constants.DB_PG_CLASS);
			  con = DriverManager.getConnection(Constants.DB_PG_PATH, Constants.DB_PG_USER, Constants.DB_PG_PWD);
			  stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);

			}
			catch (java.lang.ClassNotFoundException e)
			  {System.err.print("ClassNotFoundException: ");
			   System.err.println(e.getMessage());}
			catch (SQLException ex)
			  {System.err.print("SQLException: ");
			   System.err.println(ex.getMessage());}

	}

	public ResultSet executeQuery(String query)throws SQLException
	{
		ResultSet rs=null;
		rs = stmt.executeQuery(query);

		return rs;
		}

	public boolean insert(String query)
	{
		boolean ok = false;
		try {
			  ok = stmt.execute(query);
			}
			catch (SQLException ex)
			  {System.err.print("SQLException: ");
		       System.err.println(ex.getMessage());
			  }
		return !ok;
	}


	public ResultSet select(String query)
	{
		ResultSet result=null;
		try	{
			  result = stmt.executeQuery(query);
			}
			catch (SQLException ex)
			{System.err.print("SQLException: ");
	         System.err.println(ex.getMessage());}
	    return result;
	}


	public boolean update(String query)throws SQLException
	{
		return stmt.execute(query);
	}

	public void commit()throws SQLException
	{
		con.commit();
	}

	public void rollback()throws SQLException
	{
		con.rollback();
	}

	public void closeConnection ()
	{
		try
		{
			stmt.close();
			con.close();
		}
		catch (SQLException ex)
		{System.err.print("SQLException: ");
		 System.err.println(ex.getMessage());}
	}

	public int delete(String query)throws SQLException
	{
		return stmt.executeUpdate(query);
	}



}