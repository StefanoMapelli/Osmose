package it.txt.tellme.toolboEeventService.core.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.print.attribute.standard.Finishings;

public class SessionCheckThread extends Thread{

	public void run(){  		
		while(true)
		{
			try 
			{
				System.out.println("Effective session updated");
				updateEffectiveTimeTrainingSession();
				updateEffectiveTimeMaintenanceSession();
				Thread.sleep(10000);
			} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			}
			
		}		
	  }  
	
	
	private void updateEffectiveTimeMaintenanceSession() {
		// TODO Auto-generated method stub
		
	}


	private void updateEffectiveTimeTrainingSession()
	{

		try {System.out.println("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ma che oooooh");
			ResultSet rs = null;
			//connection to db
			Connection conn=DatabaseManager.connectToDatabase();

			//query to find issues of the specified session
			String query="UPDATE sessions SET sessions.effective_start_time=sessions.scheduled_start_time,"
					+ " sessions.effective_finish_time=sessions.scheduled_finish_time"
					+ " WHERE sessions.effective_start_time is NULL AND sessions.effective_finish_time is NULL"
					+ " AND sessions.scheduled_finish_time<DATE_SUB(NOW(), INTERVAL 1 HOUR)";
			PreparedStatement preparedStmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			preparedStmt.execute();	
			rs=preparedStmt.getGeneratedKeys();
			System.out.println("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+cambiato la data");
			while(rs.next())
			{
				System.out.println("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ciclo le sessioni");
				//aggiorno il life time delle componenti del simulatore dato che una session è terminata
				ResultSet session = null;
				//query to find session with specified id
				query = "SELECT * FROM sessions WHERE id_session="+rs.getString("id_session");
				Statement st = conn.createStatement();
				session=st.executeQuery(query);
				session.next();
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
				
				Date startDate = format.parse(session.getString("effective_start_time"));
				Date finishDate = format.parse(session.getString("effective_finish_time"));
				float lifeTimeIncrement=(finishDate.getTime()-startDate.getTime())/3600000;
				System.out.println("-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+tempo: "+lifeTimeIncrement);
				
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
			rs.close();
			DatabaseManager.disconnectFromDatabase(conn);
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	

}
