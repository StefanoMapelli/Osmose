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
	    public static final String COMPONENT_ID = "componentId";
	    public static final String MAINTENANCE_ID = "maintenanceId";
	    public static final String SYSTEM_NAME="systemName";
	    public static final String SUBSYSTEM_NAME = "subsystemName";
	    public static final String DESCRIPTION="description";
	    public static final String START_DATE = "startDate";
	    public static final String FINISH_DATE = "finishDate";
	    
	    public static final String ISSUE_OPERATION="opIssue";
	    public static final String UPDATE_DESCRIPTION="updateDescription";
	    public static final String UPDATE_QUESTIONNAIRE="updateQuestionnaire";
	    public static final String UPDATE_TAG="updateTag";
	    public static final String GET_IMAGES="getImages";
	    public static final String GET_AUDIOS="getAudio";
	    public static final String DELETE_ISSUE="deleteIssue";
	    public static final String PUT_UNDER_MAINTENANCE="putUnderMaintenance";
	    public static final String FIX_ISSUE = "fixIssue";
	    public static final String REJECT_ISSUE = "rejectIssue";
	    public static final String UPDATE_FINISH_TIME = "updateFinishTime";
	    
	    public static final String SESSION_OPERATION="sessionOperation";
	    public static final String TYPE_OPERATION="opType";
	    public static final String GET_SEVERITIES = "getSeverities";
	    public static final String GET_TYPES = "getTypes";
		public static final String GET_TAGS = "getTags";
	    public static final String GET_PRIORITIES = "getPriorities";
	    public static final String GET_SYSTEMS = "getSystems";
	    public static final String GET_SUBSYSTEMS = "getSubsystems";
	    public static final String GET_COMPONENTS = "getComponents";
		public static final String GET_STRUCTURE = "getStructure";
		public static final String GET_SCHEDULING = "getScheduling";
		public static final String GET_USER_ROLE = "userRole";
		public static final String LAST_SESSION = "lastSession";
		public static final String DATE_NOW = "dateNow";
		public static final String CHECK_DATE="checkDate";
		public static final String CHECK_DATE_EQUALS="checkDateEquals";
	    
	    
	    public static final String SESSION_DATA_INIT="sessionData";
	    
	    //id of the not classified system
	    public static final String NOT_CLASSIFIED = "7";
	    public static final String PILOT_ROLE="pilot";
		public static final String INSTRUCTOR_ROLE="instructor";
		public static final String NONE="none";
		public static final String PRIORITY_LOW="Low";
		public static final String TYPE_GENERIC = "Generic";
		public static final String SEVERITY_MODERATE = "Moderate";
		public static final String STATE_OPEN="open";
		public static final String STATE_FIXED = "fixed";
		public static final String STATE_REJECTED = "rejected";
		
		public static final String PICTURES_FOLDER_PATH="C:\\OsmoseMedia\\pictures\\";
		public static final String AUDIO_FOLDER_PATH="C:\\OsmoseMedia\\audios\\";
		public static final String USERNAME = "username";
		public static final String PASSWORD = "password";
		
		public static final String DEFAULT_INSTRUCTOR_ID = "7";
		public static final String DEFAULT_PILOT_ID = "8";
		public static final String DEFAULT_TAG = "1";
		
		public static final String SYSTEM_OPEN_COUNTERS = "systemOpenCounters";
		public static final String NOT_YET_CLASSIFIED_COUNTER = "notYetClassifiedCounter";
		public static final String SYSTEM_ID = "systemId";
		public static final String ISSUE_STATUS = "issueStatus";
		public static final String ISSUE_TYPE = "issueType";
		public static final String NEW_DESCRIBED_ISSUES = "newDescribedIssues";
		
		
		
		
		
		
		
		
		
		
		
	
		
		
		
}



