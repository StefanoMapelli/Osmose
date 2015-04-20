package it.txt.tellme.toolboEeventService.core.common;

public class Constants {
	    
	    public static final String RABBIT_MQ_PATH = "192.168.234.5";
	    public static final int RABBIT_MQ_PORT = 15672;
	    public static final String RABBIT_MQ_VHOST = "TellMeVhost";
	    public static final String RABBIT_MQ_USER = "guest";
	    public static final String RABBIT_MQ_PSW = "guest";

	    public static final String ERROR_LOG_EXCHANGE = "ErrorLogExChange";
	    public static final String TEMPERATURE_EXCHANGE = "TellMeRaspberryPIExchange";
	    public static final String ERROR_LOG_KEY = "CEPkey";
	    public static final String TEMPERATURE_KEY = "temperatureKey";
	
	    
	    public static final String DB_PATH ="WIN7VM"; //192.168.233.128
	    public static final String DB_PORT ="1433";
	    public static final String DB_USER ="sa";
	    public static final String DB_PSW = "pippo13579";
	    public static final String DB_NAME = "tellme";
	    public static final String DB_CLASS = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
	    
	    public static final String JOB_ID = "jobId";
	    public static final String MIX_ID = "mixId";
	    public static final String ASSIGN_DATE = "assignDate";
	    public static final String CONTEXT = "context";
	    public static final String DELETE = "delete";
	    
	    //postgres connection
	    public static final String DB_PG_PATH = "jdbc:postgresql://localhost:5432/";
	    public static final String DB_PG_NAME = "TellmeMaintenanceDB";
	    public static final String DB_PG_ERROR = "tellme";
	    public static final String DB_PG_USER = "postgres";
	    public static final String DB_PG_PWD = "postgres";
	    public static final String DB_PG_CLASS = "org.postgresql.Driver";
	    
	    
	    
	    
	    //OSMOSE constant
	    public static final String SESSION_ID="sessionId";
	    public static final String SIMULATOR_ID="simulatorId";
	    public static final String USER_ID = "userId";
	    public static final String ISSUE_ID="issueId";
	    public static final String DESCRIPTION="description";
	    public static final String ISSUE_OPERATION="opIssue";
	    public static final String UPDATE_DESCRIPTION="updateDescription";
	    public static final String UPDATE_QUESTIONNAIRE="updateQuestionnaire";
	    public static final String GET_IMAGES="getImages";
	    
	    
	    
	    public static final String SESSION_DATA_INIT="sessionData";
	    
	    public static final String PILOT_ROLE="pilot";
		public static final String INSTRUCTOR_ROLE="instructor";
		public static final String NONE="none";
		
		public static final String PICTURES_FOLDER_PATH="C:\\Users\\Administrator\\workspace\\ToolBoxEventService\\pictures\\";
	    
}


