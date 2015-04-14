package it.txt.tellme.toolboEeventService.core.common;

/**
*
* @author zheng
*/
public class DB {

   private static DB db=null;
   private String systemPath = null;
   private DB(String systemPath) {
	   this.systemPath = systemPath;
   }

   public static DB getInstance(String systemPath) {
       if (db == null) {
           db = new DB(systemPath);
       }
       return db;
   }


}