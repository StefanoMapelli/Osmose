package it.txt.tellme.toolboEeventService.core.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class SessionCheckThread extends Thread{

	public void run(){  		
		while(true)
		{
			try 
			{
				updateEffectiveTimeTrainingSession();
				updateEffectiveTimeMaintenanceSession();
				Thread.sleep(600000);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			
		}		
	  }  
	
	/**
	 * This method updates the effective start/finish time of the maintenance session when they are terminated since 
	 * 1 hour
	 */
	
	private void updateEffectiveTimeMaintenanceSession() {
		
		try {
			ResultSet session = null;
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
			//session to be updated
			String query = "SELECT * FROM maintenance"
					+" WHERE maintenance.effective_start_time is NULL AND maintenance.effective_finish_time is NULL"
					+" AND maintenance.scheduled_finish_time<DATE_SUB(NOW(), INTERVAL 1 HOUR)";
			Statement st = conn.createStatement();
			Statement st1 = conn.createStatement();
			Statement st2 = conn.createStatement();
			session=st.executeQuery(query);
			
			//maintenance to be updated
			query="UPDATE maintenance SET maintenance.effective_start_time=maintenance.scheduled_start_time,"
					+ " maintenance.effective_finish_time=maintenance.scheduled_finish_time"
					+ " WHERE maintenance.effective_start_time is NULL AND maintenance.effective_finish_time is NULL"
					+ " AND maintenance.scheduled_finish_time<DATE_SUB(NOW(), INTERVAL 1 HOUR)";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.executeUpdate();	
			System.out.println("Effective maintenance time updated");
			
			while(session.next())
			{
				query="SELECT * FROM components WHERE components.id_component="+session.getString("component")
						+ " AND components.simulator="+session.getString("simulator");
				ResultSet component=st1.executeQuery(query);
				component.next();
				query="SELECT COUNT(*) AS counter FROM maintenance WHERE component="+session.getString("component");
				ResultSet numberOfMaintenanceRS=st2.executeQuery(query);
				numberOfMaintenanceRS.next();
				int numberOfMaintenance=numberOfMaintenanceRS.getInt("counter");
				float newMtbf=(component.getFloat("life_time")+(component.getFloat("mtbf")*(numberOfMaintenance-1)))/(numberOfMaintenance);
				//the life time of the component will be 0 when a maintenance on the component ends
				query="UPDATE components SET"
						+ " components.life_time=0, components.mtbf="+newMtbf+", components.installation_date='"+session.getString("scheduled_start_time")
						+ "' WHERE components.id_component="+session.getString("component")
						+ " AND components.simulator="+session.getString("simulator");
				preparedStmt = conn.prepareStatement(query);
				preparedStmt.executeUpdate();
				
				System.out.println("Component life time reset");
			}
			preparedStmt.close(); 
			session.close();
			DatabaseManager.disconnectFromDatabase(conn);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		
	}


	/**
	 * This method updates the effective start/finish time of the sessions that are finished since one hour
	 * It updates also the life time of the components of the simulator involved in the updated session.
	 */
	private void updateEffectiveTimeTrainingSession()
	{

		try {
			ResultSet session = null;
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();
			//session to be updated
			String query = "SELECT * FROM sessions"
					+" WHERE sessions.effective_start_time is NULL AND sessions.effective_finish_time is NULL"
					+" AND sessions.scheduled_finish_time<DATE_SUB(NOW(), INTERVAL 1 HOUR)";
			Statement st = conn.createStatement();
			session=st.executeQuery(query);

			//query to find issues of the specified session
			query="UPDATE sessions SET sessions.effective_start_time=sessions.scheduled_start_time,"
					+ " sessions.effective_finish_time=sessions.scheduled_finish_time"
					+ " WHERE sessions.effective_start_time is NULL AND sessions.effective_finish_time is NULL"
					+ " AND sessions.scheduled_finish_time<DATE_SUB(NOW(), INTERVAL 1 HOUR)";
			PreparedStatement preparedStmt = conn.prepareStatement(query);
			preparedStmt.executeUpdate();	
			
			System.out.println("Effective session time updated");
			
			while(session.next())
			{
				//aggiorno il life time delle componenti del simulatore dato che una session è terminata
				
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
				
				Date startDate = format.parse(session.getString("scheduled_start_time"));
				Date finishDate = format.parse(session.getString("scheduled_finish_time"));
				double lifeTimeIncrement=((double)finishDate.getTime()-(double)startDate.getTime())/3600000;
				
				//query to update the life time
				query="UPDATE components SET"
						+ " components.life_time=components.life_time+"+lifeTimeIncrement
						+ " WHERE (components.component_state='Installed' OR components.component_state='Broken')"
						+ " AND components.simulator="+session.getString("simulator");
				preparedStmt = conn.prepareStatement(query);
				preparedStmt.executeUpdate();
				
				System.out.println("Component life time updated");
			}
			preparedStmt.close(); 
			session.close();
			DatabaseManager.disconnectFromDatabase(conn);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
