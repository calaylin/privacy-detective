package upa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;

public class convertTweets {

	/**da
	 * @param args
	 * @throws SQLException 
	 */
	public static void main(String[] args) throws SQLException {
		
		 TwitterDatabase db = null;
	        TwitterService ts = null;
	        LangTweetConverter converter = null;
	        try {
	            db = new TwitterDatabase();
	            ts = new TwitterService(db);
	            converter = new LangTweetConverter( db );
	        } catch ( SQLException sqle ) {
	            System.out.println( "DB Connection error!" + sqle.getMessage() );
	            System.exit(2);
	        }
	    
	        
	       LinkedList<UPUser> users = db.queryAllUsers();
	       for(UPUser u : users){
	    	   LinkedList<UPTweet> userTweets = db.queryTweetsForUser(u);
	    	   		for(UPTweet t : userTweets){
	    	   			//System.out.println(t.getTweet()+" "+t.getTweetId());
	    	   			converter.convertTweet(t);
	    	   			
	    	   		}
	       }
	       
	}
	

   

}
