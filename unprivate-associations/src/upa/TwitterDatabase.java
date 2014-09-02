package upa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.LinkedList;
import java.util.HashMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * This class abstracts all the Twitter database stuff.  Any function which 
 * begins with query will query the DB.  Same thing for insert. Functions 
 * starting with has simply check to see if the DB has an entry maching the 
 * parameters.
 *
 * @author Jonathan Walsh (jdw74@drexel.edu)
 */
public class TwitterDatabase {

    /** The Default DB URL String (depends on your setup) */
    public static final String DEFAULT_DB_URL = "jdbc:postgresql://localhost/twitter";

    /** The Default DB User String (depends on your setup) */
    public static final String DEFAULT_DB_USER = "";

    /** The Default DB Password String (depends on your setup) */
    public static final String DEFAULT_DB_PASS = "";

    /** A flag which sets no limit on rate limited calls*/
    private static final int NO_LIMIT = -1;

    /** The database connection */
    private Connection conn;

    /** If we are using debugging or not*/
    private boolean debug;

    /** Default configuratil file path */
    public static final String DEFAULT_CONFIG_PATH = "config/db.config";

    /**
     * Create the Database object with default connection strings
     *
     * @throws SQLException - When the database connection fails
     */
    public TwitterDatabase() throws SQLException {
        this( DEFAULT_DB_URL,
              DEFAULT_DB_USER,
              DEFAULT_DB_PASS );
    }

    /**
     * Create the Database object with a configuration file
     *
     * @throws SQLException - When the DB connection fails
     * @throws IOException - When there is an IO error reading the file
     */
    public TwitterDatabase( String configFile ) throws SQLException,IOException {
        String line;
        BufferedReader in = new BufferedReader( new FileReader( configFile ) );

        String db_url = DEFAULT_DB_URL,
            db_user = DEFAULT_DB_USER, 
            db_pass = DEFAULT_DB_PASS;
        boolean debug = false;

        while ( ( line = in.readLine() ) != null ) {
            
            if ( line.indexOf( "=" ) == -1 ) {
                System.out.println( "WARNING: Lines must be format key=value" );
                System.out.println( "WARNING: Invalid line: " + line );
                continue;
            }

            String key = line.substring( 0, line.indexOf( "=" ) ).trim();
            String value = line.substring( line.indexOf( "=" ) + 1, 
                                           line.length() ).trim();
            if ( key.equals( "db_url" ) ) {
                db_url = value;
            } else if ( key.equals( "db_user" ) ) {
                db_user = value;
            } else if ( key.equals( "db_pass" ) ) {
                db_pass = value;
            } else if ( key.equals( "debug" ) ) {
                debug = value.equals("true");
            } else {
                System.out.println( "WARNING: In TwitterDatabase() Unknown configuration key = " + key );
            }
        }
        in.close();

        conn = DriverManager.getConnection ( db_url, db_user, db_pass );
        this.debug = debug;
    }

   /**
     * Create the Database object with specified connection strings
     *
     * @param dburl - The DB URL
     * @param dbuser - The DB user
     * @param dbpass - The DB password
     * @throws SQLException - When the database connection fails
     */
    public TwitterDatabase( String dburl, String dbuser, String dbpass ) throws SQLException {
        conn = DriverManager.getConnection( dburl, dbuser, dbpass );
        debug = false;
    }

    /**
     * Close the SQL database connection
     *
     * @throws SQLException - When the database connection fails
     */
    public void close() throws SQLException {
        conn.close();
    }

    /**
     * Close the SQL database connection without complaining
     */
    public void quietClose() {
        try {
            conn.close();
        } catch ( Exception e ) {}
    }

    /**
     * Enable debug printing of SQL statements
     */
    public void enableDebug() {
        debug = true;
    }
    /**
     * Disable debug printing of SQL statements
     */
    public void disableDebug() {
        debug = false;
    }

    /**
     * Print out a debug message of the statement given; intended for UPDATE or
     * INSERT statements.
     * 
     * @param stmt - The statement to print out
     */
    private void printChangeStatement( String stmt ) {
        if ( debug ) System.out.println( "EXECUTING INSERT: " + stmt );
    }

    /**
     * Print out a debug message of the statement given
     * 
     * @param stmt - The statement to print out
     */
    private void printQueryStatement( String stmt ) {
        if ( debug ) System.out.println( "EXECUTING QUERY: " + stmt );
    }

    /* ********************* USER FUNCTIONS ***********************/
    /**
     * Get a user from the database
     *
     * @param handle - The user to get
     * @return The user object for the handle
     * @throws SQLException - When the database connection fails
     */
    public UPUser queryUserByHandle( String handle ) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        UPUser ret = null;
        
        st = conn.createStatement();
        String stmtStr = "SELECT person_id,person_name,location,verified,url,created FROM person WHERE handle='"+handle+"'";
        printQueryStatement( stmtStr );
        rs = st.executeQuery( stmtStr );
        
        if ( rs != null && rs.next() ) {
            ret = new UPUser( rs.getLong(1),
                              handle,
                              rs.getString(2),
                              rs.getString(3),
                              rs.getBoolean(4),
                              rs.getString(5),
                              rs.getString(6) );
        }

        if ( rs != null ) rs.close();
        if ( st != null ) st.close();
        
        return ret;
    }

   /**
     * Get a user from the database
     *
     * @param personId - The user to get
     * @return The user object for the id, or null if the user is not found
     * @throws SQLException - When the database connection fails
     */
    public UPUser queryUserByPersonId( long personId ) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        UPUser ret = null;
        
        st = conn.createStatement();
        String stmtStr = "SELECT handle,person_name,location,verified,url,created FROM person WHERE person_id='"+personId+"'";
        printQueryStatement( stmtStr );
        rs = st.executeQuery( stmtStr );
        
        if ( rs != null && rs.next() ) {
            ret = new UPUser( personId,
                              rs.getString(1),
                              rs.getString(2),
                              rs.getString(3),
                              rs.getBoolean(4),
                              rs.getString(5),
                              rs.getString(6) );
        }

        if ( rs != null ) rs.close();
        if ( st != null ) st.close();
        
        return ret;
    }
   
   /**
     * Get all users from the Database. Caution, this might be a lot!
     *
     * @return A list of all users
     * @throws SQLException - When the database connection fails
     */
    public LinkedList<UPUser> queryAllUsers() throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        
        LinkedList<UPUser> ret = new LinkedList<UPUser>();

        st = conn.createStatement();
        String stmtStr = "SELECT person_id,handle,person_name,location,verified,url,created FROM person";
        printQueryStatement( stmtStr );
        rs = st.executeQuery( stmtStr );
        
        while ( rs != null && rs.next() ) {

            ret.add( new UPUser( rs.getLong(1),
                                 rs.getString(2),
                                 rs.getString(3),
                                 rs.getString(4),
                                 rs.getBoolean(5),
                                 rs.getString(6),
                                 rs.getString(7) ) );
        }

        if ( rs != null ) rs.close();
        if ( st != null ) st.close();
        
        return ret;
    }



    /**
     * Returns true if the handle exists in the database already
     * 
     * @param handle - The handle to check
     * @return true if the handle exists, false otherwise
     * @throws SQLException - When the database connection fails
     */
    public boolean hasUser( String handle ) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        
        st = conn.createStatement();
        String stmtStr = "SELECT COUNT(*) FROM person WHERE handle='"+handle+"'";
        printQueryStatement( stmtStr );
        rs = st.executeQuery( stmtStr );
        
        int count = 0;
        if ( rs != null && rs.next() ) {
            count = rs.getInt(1);
        }

        if ( rs != null ) rs.close();
        if ( st != null ) st.close();

        return (count != 0);
    }
    /**
     * Returns true if the person id exists in the database already
     * 
     * @param personId - The person id to check
     * @return true if the person id exists, false otherwise
     * @throws SQLException - When the database connection fails
     */
    public boolean hasUser( long personId ) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        
        st = conn.createStatement();
        String stmtStr = "SELECT COUNT(*) FROM person WHERE person_id="+personId;
        printQueryStatement( stmtStr );
        rs = st.executeQuery( stmtStr );
        
        int count = 0;
        if ( rs != null && rs.next() ) {
            count = rs.getInt(1);
        }

        if ( rs != null ) rs.close();
        if ( st != null ) st.close();

        return (count != 0);
    }

    /**
     * Add a new user to the database with a user object
     * 
     * @param u - The user object to add
     * @throws SQLException - When the database connection fails
     */
    public void insertUser( UPUser u ) throws SQLException {
        insertUser( u.getPersonId(), u.getHandle(), u.getPersonName(),
                    u.getLocation(), u.getVerified(), u.getURL(),
                    u.getCreatedDate() );        
    };

    /**
     * Add a new user to the database with values only
     * @throws SQLException - When the database connection fails
     */
    public void insertUser( long personId, String handle, String personName, 
                            String location, boolean verified, String url, 
                            String createdDate ) throws SQLException {
        if ( hasUser(personId) ) {
            System.out.println( "Skipping insert of known user: " + handle );
            return;
        }
        
        personName = fixupString(personName);
        location = fixupString(location);

        Statement st = conn.createStatement();
        String stmtStr = "INSERT INTO person (person_id,handle,person_name,location,verified,url,created) VALUES ("+personId+",'"+handle+"',E'"+personName+"',E'"+location+"','"+verified+"','"+url+"','"+createdDate+"')";
        printChangeStatement( stmtStr );
        st.executeUpdate( stmtStr );
        
        if ( st != null ) st.close(); 
    };


    /* ************* FOLLOWER FUNCTIONS ***********************/

    /**
     * Add a follower for a user
     * 
     * @param u - The user being followed
     * @param f - The user doing the following
     * @throws SQLException - When the database connection fails
     */
    public void insertFollower( UPUser u, UPUser f ) throws SQLException {
        insertFollower( u.getPersonId(), f.getPersonId() );
    }

    /**
     * Add a follower for a user
     * 
     * @param uId - The user id being followed
     * @param fId - The user id doing the following
     * @throws SQLException - When the database connection fails
     */
    public void insertFollower( long uId, long fId ) throws SQLException {
        if ( !hasFollower( uId, fId ) ) {
            Statement st = conn.createStatement();
            String stmtStr = "INSERT INTO followers (person_id,follower) VALUES ("+uId+","+fId+")";
            printChangeStatement( stmtStr );
            st.executeUpdate( stmtStr );
            
            if ( st != null ) st.close(); 
        }
    }

    /**
     * Get the list of followers for a particular user
     *
     * @param u - The user object to fill with followers
     * @return the number of followers
     * @throws SQLException - When the database connection fails
     */
    public LinkedList<UPUser> queryUserFollowers( UPUser u ) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        LinkedList<UPUser> ret = new LinkedList<UPUser>();
        
        st = conn.createStatement();
        String stmtStr = "SELECT follower FROM followers WHERE person_id=+"+u.getPersonId();
        printQueryStatement( stmtStr );
        rs = st.executeQuery( stmtStr );
        
        if ( rs != null ) {
            while ( rs.next() ) {
                long fid = rs.getLong(1);
                //System.out.println("Found follower fid = " + fid );
                ret.add( queryUserByPersonId( fid ) );                
            }
        }
        
        if ( rs != null ) rs.close();
        if ( st != null ) st.close();
        
        return ret;
    }

    /**
     * Get a list of users that this user is following.
     *
     * @param u - User to get follows for
     * @return A list of users that the user is following
     * @throws SQLException - When the database connection fails
     */
    public LinkedList<UPUser> queryUserFollowing( UPUser u ) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        LinkedList<UPUser> ret = new LinkedList<UPUser>();
        
        st = conn.createStatement();
        String stmtStr = "SELECT person_id FROM followers WHERE follower=+"+u.getPersonId();
        printQueryStatement( stmtStr );
        rs = st.executeQuery( stmtStr );
        
        if ( rs != null ) {
            while ( rs.next() ) {
                long fid = rs.getLong(1);
                //System.out.println("Found followed fid = " + fid );
                ret.add( queryUserByPersonId( fid ) );                
            }
        }
               
        if ( rs != null ) rs.close();
        if ( st != null ) st.close();
        
        return ret;
    }


    /**
     * Returns true if user u is followed by user f
     *
     * @param uId - User u's id
     * @param fId - User f's id
     * @return true if f follows u
     * @throws SQLException - When the database connection fails
    */
    public boolean hasFollower( long uId, long fId ) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        
        st = conn.createStatement();
        String stmtStr = "SELECT COUNT(*) FROM followers WHERE person_id="+uId+" AND follower="+fId;
        printQueryStatement( stmtStr );
        rs = st.executeQuery( stmtStr );
        
        int count = 0;
        if ( rs != null && rs.next() ) {
            count = rs.getInt(1);
        }

        if ( rs != null ) rs.close();
        if ( st != null ) st.close();

        return (count != 0);

    }

    /* ************** Tweet Functions ************************/
    /**
     * Get a single tweet given a tweet id
     *
     * @param tweetId - The tweet id to look up a tweet for
     * @return The tweet found for the id, or null if not found
     * @throws SQLException - When the database connection fails
     */
    public UPTweet queryTweetById( long tweetId ) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        UPTweet ret = null;
        
        st = conn.createStatement();
        String stmtStr = "SELECT person_id,tweet,reply_to_user,reply_to_status,location,retweet_count,favorite_count,created FROM tweet WHERE tweet_id="+tweetId;
        printQueryStatement( stmtStr );
        rs = st.executeQuery( stmtStr );
        
        if ( rs != null && rs.next() ) {

            ret = new UPTweet( tweetId,
                               rs.getLong(1),
                               rs.getString(2),
                               rs.getLong(3),
                               rs.getLong(4),
                               rs.getString(5),
                               rs.getInt(6),
                               rs.getInt(7),
                               rs.getString(8) );
        }


        if ( rs != null ) rs.close();
        if ( st != null ) st.close();
        
        return ret;
    }

    /**
     * Get a list of all tweet string content for a specific user
     *
     * @param user - The user to query all tweet strings for
     * @return The list of all tweet strings for a user; null if user not found
     * @throws SQLException - When the database connection fails
     */   
    public LinkedList<String> queryTweetStringsForUser( UPUser u ) throws SQLException {
        return queryTweetStringsForUser( u, NO_LIMIT );
    }


    /**
     * Get a list of all tweet string content for a specific user with a limit
     *
     * @param user - The user to query all tweet strings for
     * @param limit - The limit on the number of tweets to get
     * @return The list of all tweet strings for a user; null if user not found
     * @throws SQLException - When the database connection fails
     */   
    public LinkedList<String> queryTweetStringsForUser( UPUser u, int limit ) throws SQLException {

        Statement st = null;
        ResultSet rs = null;

        LinkedList<String> tweetStrings = new LinkedList<String>();

        st = conn.createStatement();
        String queryString = "SELECT tweet FROM tweet WHERE person_id="+u.getPersonId();
        if ( limit > 0 ) queryString += " LIMIT " + limit;
        printQueryStatement(queryString);
        rs = st.executeQuery( queryString );

        if ( rs != null ) {
            while ( rs.next() ) {
                tweetStrings.add( rs.getString(1) );
            }
        }

        if ( rs != null ) rs.close();
        if ( st != null ) st.close();

        return tweetStrings;
    }

    /**
     * Get a list of all tweets for a user with a limit
     *
     * @param userHandle - The user to query all tweets for
     * @param limit - The maximum number of tweets to return
     * @return The list of all tweets for a user; null if the user is not found
     * @throws SQLException - When the database connection fails
     */  
    public LinkedList<UPTweet> queryTweetsForUser( String userHandle, int limit ) throws SQLException {
        UPUser u = queryUserByHandle( userHandle );
        if ( u == null ) return null;
        return queryTweetsForUser( u, limit );
    }

    /**
     * Get a list of all tweets for a user
     *
     * @param userHandle - The user to query all tweets for
     * @return The list of all tweets for a user; null if the user is not found
     * @throws SQLException - When the database connection fails
     */  
    public LinkedList<UPTweet> queryTweetsForUser( String userHandle ) throws SQLException {
        UPUser u = queryUserByHandle( userHandle );
        if ( u == null ) return null;
        return queryTweetsForUser( u, NO_LIMIT );
    }

   /**
     * Get a list of all tweets for a user with a limit
     *
     * @param u - The user to query all tweets for
     * @return The list of all tweets for a user
     * @throws SQLException - When the database connection fails
     */
    public LinkedList<UPTweet> queryTweetsForUser( UPUser u ) throws SQLException {
        return queryTweetsForUser( u, NO_LIMIT );
    }

    /**
     * Get a list of all tweets for a user with a limit
     *
     * @param u - The user to query all tweets for
     * @param limit - A limit on the number of tweets to return
     * @return The list of all tweets for a user
     * @throws SQLException - When the database connection fails
     */
    public LinkedList<UPTweet> queryTweetsForUser( UPUser u, int limit ) throws SQLException {

        Statement st = null;
        ResultSet rs = null;
        LinkedList<UPTweet> ret = new LinkedList<UPTweet>();
        
        st = conn.createStatement();
        String queryString = "SELECT tweet_id,tweet,reply_to_user,reply_to_status,location,retweet_count,favorite_count,created FROM tweet WHERE person_id="+u.getPersonId();
        if ( limit > 0 ) queryString += " LIMIT " + limit;
        printQueryStatement(queryString);
        rs = st.executeQuery( queryString );

        if ( rs != null ) {
            while ( rs.next() ) {
                
                ret.add( new UPTweet( rs.getLong(1),
                                      u.getPersonId(),
                                      rs.getString(2),
                                      rs.getLong(3),
                                      rs.getLong(4),
                                      rs.getString(5),
                                      rs.getInt(6),
                                      rs.getInt(7),
                                      rs.getString(8) ) );
            }
        }


        if ( rs != null ) rs.close();
        if ( st != null ) st.close();
        
        return ret;
    }


    /** 
     * Returns a count of the number of tweets we have for a user
     *
     * @param u - The user to query number of tweets for
     * @return number of tweets for the user
     * @throws SQLException - When the database connection fails
     */
    public int queryNumTweetsForUser( UPUser u ) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        int ret = 0;
        
        st = conn.createStatement();
        String stmtStr = "SELECT COUNT(*) FROM tweet WHERE person_id="+u.getPersonId();
        printQueryStatement( stmtStr );
        rs = st.executeQuery( stmtStr );
        
        if ( rs != null && rs.next() ) {
            ret = rs.getInt(1);
        }

        if ( rs != null ) rs.close();
        if ( st != null ) st.close();
        
        return ret;     
    }

    /**
     * Returns true if a tweet id is in the database
     *
     * @param tweetId - The tweet id to query for
     * @return true if a tweet id is in the database
     * @throws SQLException - When the database connection fails
     */
    public boolean hasTweet( long tweetId ) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        
        st = conn.createStatement();
        String stmtStr = "SELECT COUNT(*) FROM tweet WHERE tweet_id="+tweetId;
        printQueryStatement( stmtStr );
        rs = st.executeQuery( stmtStr );
        
        int count = 0;
        if ( rs != null && rs.next() ) {
            count = rs.getInt(1);
        }

        if ( rs != null ) rs.close();
        if ( st != null ) st.close();

        return (count != 0);
    }

    /**
     * Get a list of tweets matching a certain regex string
     *
     * WARNING: This, as other functions are particularily ripe for injection
     *
     * @param regex - The regular experssion (PSQL format) to query
     * @return List of tweets matching the regex
     * @throws SQLException - When the database connection fails
     */
    public LinkedList<UPTweet> queryTweetsByRegex( String regex ) throws SQLException { 
        return queryTweetsByRegex( regex, NO_LIMIT );
    }

    /**
     * Get a list of tweets matching a certain regex string, with a limit
     *
     * WARNING: This, as other functions are particularily ripe for injection
     *
     * @param regex - The regular experssion (PSQL format) to query
     * @param limit - Limit on the number of tweets to return
     * @return List of tweets matching the regex
     * @throws SQLException - When the database connection fails
     */
    public LinkedList<UPTweet> queryTweetsByRegex( String regex, int limit )
        throws SQLException {

        Statement st = null;
        ResultSet rs = null;
        LinkedList<UPTweet> ret = new LinkedList<UPTweet>();
        
        st = conn.createStatement();

        String queryString = "SELECT tweet_id,person_id,tweet,reply_to_user,reply_to_status,location,retweet_count,favorite_count,created FROM tweet WHERE tweet LIKE '"+regex+"'";
        if ( limit > 0 ) queryString += " LIMIT " + limit;
        printQueryStatement(queryString);
        rs = st.executeQuery(queryString);
        
        if ( rs != null ) {
            while ( rs.next() ) {
                ret.add( new UPTweet( rs.getLong(1),
                                      rs.getLong(2),
                                      rs.getString(3),
                                      rs.getLong(4),
                                      rs.getLong(5),
                                      rs.getString(6),
                                      rs.getInt(7),
                                      rs.getInt(8),
                                      rs.getString(9) ) );
            }
        }

        if ( rs != null ) rs.close();
        if ( st != null ) st.close();
        
        return ret;
    }

    /** 
     * Insert a tweet without much information. This is generally used in the 
     * protected or error case so that we can have DB consistancy
     * 
     * @param tweetId - The tweet ID
     * @param personId - Person associated with this tweet
     * @param tweet - The tweet contents
     * @throws SQLException - When the database connection fails
     */
    public void insertTweet( long tweetId, long personId, String tweet ) throws SQLException {
        /* If the tweet is in the DB, we skip it */
        if ( hasTweet( tweetId ) ) {
            return;
        }
        
        Statement st = conn.createStatement();
        if ( st != null ) {
            String query = "INSERT INTO tweet (tweet_id,person_id,tweet) VALUES ("+tweetId+","+personId+",E'"+fixupString(tweet)+"')";
            printChangeStatement( query );
            st.executeUpdate( query );
            st.close();
        }
    }

    /**
     * Insert a tweet, which includes parsing out various metadata.
     *
     * @param tweet - The tweet to insert
     * @throws SQLException - When the database connection fails
     */
    public void insertTweet( UPTweet tweet ) throws SQLException {

        /*If the tweet is in the DB, we skip it*/
        if ( hasTweet( tweet.getTweetId() ) ) {
            return;
        }

        /* Insert the tweet into the DB */
        Statement st = conn.createStatement();
        String stmtStr = "INSERT INTO tweet (tweet_id,person_id,tweet,reply_to_user,reply_to_status,location,retweet_count,favorite_count,created) VALUES (";
        stmtStr += tweet.getTweetId() + ",";
        stmtStr += tweet.getPersonId() + ",";
        stmtStr += "E'" + fixupString(tweet.getTweet()) + "',";

        long replyToUserId = tweet.getReplyToUserId();
        if ( replyToUserId <= 0 ) stmtStr += "NULL,";
        else stmtStr += replyToUserId + ",";
        
        long replyToStatusId = tweet.getReplyToStatusId();
        if ( replyToStatusId <= 0 ) stmtStr += "NULL,";
        else stmtStr += tweet.getReplyToStatusId() + ",";
        
        stmtStr += "E'" + fixupString(tweet.getLocation()) + "',";
        stmtStr += tweet.getRetweetCount() + ",";
        stmtStr += tweet.getFavoriteCount() + ",";
        stmtStr += "'" + tweet.getCreatedDate() + "')";        

        printChangeStatement( stmtStr );
        st.executeUpdate( stmtStr );
        
        if ( st != null ) st.close(); 
    }

    /**
     * Tag a tweet as being in a non-English language
     *
     * @param tweet - The tweet to flag
     * @throws SQLException - When the database connection fails
     */
    public void setTweetNotEnglish( UPTweet tweet ) throws SQLException {
        if ( hasTweet( tweet.getTweetId() ) ) {
            Statement st = conn.createStatement();
            if ( st != null ) {
                st.executeUpdate( "UPDATE tweet SET not_english=1 WHERE tweet_id="+tweet.getTweetId() );
                st.close();
            }            
        }
    }

    /**
     * Tag a tweet as being in the English language
     *
     * @param tweet - The tweet to flag
     * @throws SQLException - When the database connection fails
     */
    public void setTweetEnglish( UPTweet tweet ) throws SQLException {
        if ( hasTweet( tweet.getTweetId() ) ) {
            Statement st = conn.createStatement();
            if ( st != null ) {
                st.executeUpdate( "UPDATE tweet SET not_english=0 WHERE tweet_id="+tweet.getTweetId() );
                st.close();
            }            
        }
    }

    /**
     * Find out if a tweet is not english
     *
     * @param tweet - The tweet to query
     * @throws SQLException - When the database connection fails
     */
    public boolean isTweetNotEnglish( UPTweet tweet ) throws SQLException {

        Statement st = null;
        ResultSet rs = null;
        
        st = conn.createStatement();
        String stmtStr = "SELECT not_english FROM tweet WHERE tweet_id="+tweet.getTweetId();
        printQueryStatement( stmtStr );
        rs = st.executeQuery( stmtStr );
        
        int result = 0;
        if ( rs != null && rs.next() ) {
            result = rs.getInt(1);
        }

        if ( rs != null ) rs.close();
        if ( st != null ) st.close();

        return (result != 0);
    }


    /* *********** HASHTAG FUNCTIONS ***************/

    /**
     * Associate a hash tag with a tweet
     *
     * @param tweet - The tweet to associate the hash tag with
     * @param hashTag - The hashtag to associate
     * @throws SQLException - When the database connection fails
     */
    public void insertHashTag( UPTweet tweet, String hashTag ) throws SQLException {
        insertHashTag( tweet.getTweetId(), hashTag );
    }

    /**
     * Associate a hash tag with a tweet
     *
     * @param tweetId - The tweet ID to associate the hash tag with
     * @param hashTag - The hashtag to associate
     * @throws SQLException - When the database connection fails
     */
    public void insertHashTag( long tweetId,
                               String hashTag ) throws SQLException {

        /*Make sure it isn't in the DB already*/
        Statement st = null;
        ResultSet rs = null;
        
        st = conn.createStatement();
        String queryString = "SELECT COUNT(*) FROM hashtags WHERE tweet_id="+tweetId + " AND hashtag='" + hashTag + "'";
        printQueryStatement(queryString);
        rs = st.executeQuery( queryString );
        
        int count = 1;
        if ( rs != null && rs.next() ) {
            count = rs.getInt(1);
        }

        if ( rs != null ) rs.close();
        if ( st != null ) st.close();
        
        /*Do the insert*/
        if ( count == 0 ) {
            st = conn.createStatement();
            String stmtStr = "INSERT INTO hashtags (tweet_id,hashtag) VALUES ("+tweetId+",'"+hashTag+"')";
            printChangeStatement( stmtStr );
            st.executeUpdate( stmtStr );
            
            if ( st != null ) st.close(); 
        }
    }

    /**
     * Get a list of the hashtags for a specific tweet with a limit
     *
     * @param tweet - The tweet to get a list of hashtags for
     * @return List of hashtags for the tweet
     * @throws SQLException - When the database connection fails
     */
    public LinkedList<String> queryHashTagsForTweet( UPTweet tweet, int limit ) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        LinkedList<String> ret = new LinkedList<String>();
        
        st = conn.createStatement();

        String queryString = "SELECT hashtag FROM hashtags WHERE tweet_id="+tweet.getTweetId();
        printQueryStatement(queryString);
        rs = st.executeQuery( queryString );
        
        if ( rs != null ) {
            while ( rs.next() ) {
                ret.add( rs.getString(1) );
            }
        }

        if ( rs != null ) rs.close();
        if ( st != null ) st.close();
        
        return ret;
    }

    /**
     * Get a list of the tweets for a specific hashtag
     *
     * @param hashTag - The hashtag to get the list of tweets for
     * @return List of tweets with the hashtag
     * @throws SQLException - When the database connection fails
     */
    public LinkedList<UPTweet> queryTweetsForHashTag( String hashTag ) throws SQLException {
        return queryTweetsForHashTag( hashTag, NO_LIMIT );
    }

    /**
     * Get a list of the tweets for a specific hashtag with a limit
     *
     * @param hashTag - The hashtag to get the list of tweets for
     * @param limit - Limit on the number of tweets to return
     * @return List of tweets with the hashtag
     * @throws SQLException - When the database connection fails
     */
    public LinkedList<UPTweet> queryTweetsForHashTag( String hashTag, int limit ) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        LinkedList<UPTweet> ret = new LinkedList<UPTweet>();
        
        st = conn.createStatement();

        String queryString = "SELECT tweet_id FROM hashtags WHERE hashtag='"+hashTag+"'";
        if ( limit > 0 ) queryString += " LIMIT " + limit;
        printQueryStatement( queryString );
        rs = st.executeQuery( queryString );
        
        if ( rs != null ) {
            while ( rs.next() ) {
                ret.add( queryTweetById( rs.getLong(1) ) );
            }
        }

        if ( rs != null ) rs.close();
        if ( st != null ) st.close();
        
        return ret;
    }


    /* *********** MENTIONED USERS FUNCTIONS ***************/

    /**
     * Insert a reference to a user in a tweet
     *
     * @param tweet - The tweet with the reference
     * @param user - The user who is referenced
     * @throws SQLException - When the database connection fails
     */
    public void insertUserMention( UPTweet tweet, UPUser user ) throws SQLException {
        insertUserMention( tweet.getTweetId(), user.getPersonId() );
    }

    /**
     * Insert a reference to a user in a tweet
     *
     * @param tweetId - The tweet ID with the reference
     * @param userId - The user ID who is referenced
     * @throws SQLException - When the database connection fails
     */
    public void insertUserMention( long tweetId, long userId ) throws SQLException {
        Statement st = conn.createStatement();
        String stmtStr = "INSERT INTO mentioned_users (tweet_id,person_id) VALUES ("+tweetId+","+userId+")";
        printChangeStatement( stmtStr );
        st.executeUpdate( stmtStr );
        
        if ( st != null ) st.close(); 
    }

    /**
     * Get a list of the mentioned users for a specific tweet
     *
     * @param tweet - The tweet to get a list of mentioned users for
     * @return List of mentioned users for the tweet
     * @throws SQLException - When the database connection fails
     */
    public LinkedList<UPUser> queryMentionedUsersForTweet( UPTweet tweet ) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        LinkedList<UPUser> ret = new LinkedList<UPUser>();
        
        st = conn.createStatement();

        String stmtStr = "SELECT person_id FROM mentioned_users WHERE tweet_id="+tweet.getTweetId();
        printQueryStatement( stmtStr );
        rs = st.executeQuery( stmtStr );
        
        if ( rs != null ) {
            while ( rs.next() ) {
                ret.add( queryUserByPersonId( rs.getLong(1) ) );
            }
        }

        if ( rs != null ) rs.close();
        if ( st != null ) st.close();
        
        return ret;
    }

    /**
     * Get a list of the tweets for a mentioned user
     *
     * @param user - The mentioned user
     * @return List of tweets with the mentioned user
     * @throws SQLException - When the database connection fails
     */
    public LinkedList<UPTweet> queryTweetsForMentionedUser( UPUser user ) throws SQLException {
        return queryTweetsForMentionedUser( user, NO_LIMIT );
    }

    /**
     * Get a list of the tweets for a mentioned user with a limit
     *
     * @param user - The mentioned user
     * @param limit - Limit on the number of tweets to return
     * @return List of tweets with the mentioned user
     * @throws SQLException - When the database connection fails
     */
    public LinkedList<UPTweet> queryTweetsForMentionedUser( UPUser user, int limit ) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        LinkedList<UPTweet> ret = new LinkedList<UPTweet>();
        
        st = conn.createStatement();

        String queryString = "SELECT tweet_id FROM mentioned_users WHERE person_id='"+user.getPersonId()+"'";
        if ( limit > 0 ) queryString += " LIMIT " + limit;
        printQueryStatement(queryString);
        rs = st.executeQuery( queryString );
        
        if ( rs != null ) {
            while ( rs.next() ) {
                ret.add( queryTweetById( rs.getLong(1) ) );
            }
        }

        if ( rs != null ) rs.close();
        if ( st != null ) st.close();
        
        return ret;
    }



    /* ********* Converted Tweet FUNCTIONS ***********/

    /**
     * Insert converted tweet text.  This is used when we are translating the
     *  tweet into english, removing acronyms, etc.
     *
     * @param tweet - The tweet to associate converted text with
     * @param conversionType - The string name of the type of conversion
     * @param convertedText - The converted text to insert
     * @throws SQLException - When the database connection fails
     */
    public void insertConvertedTweet( UPTweet tweet, String conversionType, 
                                      String convertedText ) throws SQLException {

        /*First we need to get the id for the conversion method*/
        int convMethodId = -1;
        Statement st = null;
        ResultSet rs = null;
        
        st = conn.createStatement();
        String stmtStr = "SELECT conv_method_id FROM conv_methods WHERE conv_method_name='"+conversionType+"'";
        printQueryStatement( stmtStr );
        rs = st.executeQuery( stmtStr );
        if ( rs.next() ) {
            convMethodId = rs.getInt(1);
        } else {
            throw new SQLException("Unknown conversion method: " + 
                                   conversionType);
        }
        if ( rs != null ) rs.close();
        if ( st != null ) st.close();

        /*Second we actually do the insert*/
        if ( queryConvertedTweet( tweet, convMethodId ) == null ) {
            st = conn.createStatement();
            stmtStr = "INSERT INTO convtweets (tweet_id,tweet,conv_method_id) VALUES ("+tweet.getTweetId()+",E'"+fixupString(convertedText)+"',"+convMethodId+")";
            printChangeStatement( stmtStr );
            st.executeUpdate( stmtStr );
            
            if ( st != null ) st.close();

        } else {//else it is an update
            st = conn.createStatement();
            stmtStr = "UPDATE convtweets SET tweet=E'"+fixupString(convertedText)+"' WHERE tweet_id="+tweet.getTweetId()+" AND conv_method_id="+convMethodId;
            st.executeUpdate( stmtStr );
            
            if ( st != null ) st.close();
        }
    }

    /**
     * Query for the converted text of a given tweet by conversionType
     *
     * @param tweet - The tweet to get converted text for
     * @param conversionType - The string name of the type of conversion
     * @return The converted tweet string, or null if it can't be found or the 
     *          conversion type is invalid.
     * @throws SQLException - When the database connection fails
     */
    public String queryConvertedTweet( UPTweet tweet, String conversionType ) throws SQLException {
        /*First we need to get the id for the conversion method*/
        int convMethodId = -1;
        Statement st = null;
        ResultSet rs = null;
        
        st = conn.createStatement();
        String stmtStr = "SELECT conv_method_id FROM conv_methods WHERE conv_method_name='"+conversionType+"'";
        printQueryStatement( stmtStr );
        rs = st.executeQuery( stmtStr );
        if ( rs.next() ) {
            convMethodId = rs.getInt(1);
        } else {
            throw new SQLException("Unknown conversion method: " + 
                                   conversionType);
        }
        if ( rs != null ) rs.close();
        if ( st != null ) st.close();

        /*Run with the id*/
        return queryConvertedTweet( tweet, convMethodId );
    }

    /**
     * Query for the converted text of a given tweet by conversionType
     *
     * @param tweet - The tweet to get converted text for
     * @param conversionType - The string name of the type of conversion
     * @return The converted tweet string, or null if it can't be found or the 
     *          conversion type is invalid.
     * @throws SQLException - When the database connection fails
     */
    public String queryConvertedTweet( UPTweet tweet, long conversionType ) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        String ret = null;
        
        st = conn.createStatement();
        String stmtStr = "SELECT tweet FROM convtweets WHERE tweet_id="+tweet.getTweetId()+" AND conv_method_id ="+conversionType;
        printQueryStatement( stmtStr );
        rs = st.executeQuery( stmtStr );
        
        if ( rs != null && rs.next() ) {
            ret = rs.getString(1);            
        }
        
        if ( rs != null ) rs.close();
        if ( st != null ) st.close();
        
        return ret;
    }
    
    /**
     * Query the integer id for a conversion type.
     *
     * @param conversionType - The string name of the conversion type
     * @return The integer id for the conversion type
     * @throws SQLException - When the database connection fails
     */
    public int queryConversionTypeId( String conversionType ) throws SQLException {
       /*First we need to get the id for the conversion method*/
        int convMethodId = -1;
        Statement st = null;
        ResultSet rs = null;
        
        st = conn.createStatement();
        String stmtStr = "SELECT conv_method_id FROM conv_methods WHERE conv_method_name='"+conversionType+"'";
        printQueryStatement( stmtStr );
        rs = st.executeQuery( stmtStr );
        if ( rs.next() ) {
            convMethodId = rs.getInt(1);
        } else {
            throw new SQLException("Unknown conversion method: " + 
                                   conversionType);
        }
        if ( rs != null ) rs.close();
        if ( st != null ) st.close();
     
        return convMethodId;
    }


    /* ********** Topic FUNCTIONS ****************/
    /**
     * Add a new topic to the database.  May also be used to update an 
     * existing topic to set the private flag.
     *
     * @param topicName - The String name of the topic
     * @param privacyRelated - true if this topic signifies private information
     * @throws SQLException - When the database connection fails
     */
    public void insertTopic( String topicName, boolean privacyRelated ) throws SQLException {
        int val = 0;
        if ( privacyRelated ) val = 1;

        Statement st = conn.createStatement();
        String stmtStr;
        if ( queryTopicId(topicName) != -1 ) {
            stmtStr = "UPDATE topic_assignment SET privacy_related="+val+" WHERE topic_name='"+topicName+"'";
        } else {
            stmtStr = "INSERT INTO topic_assignment (topic_name,privacy_related) VALUES ("+topicName+"," + val+")";
        }
        printChangeStatement( stmtStr );
        st.executeUpdate( stmtStr );
        
        if ( st != null ) st.close(); 
    }

    /**
     * Get the topic id for a given topic name.
     *
     * @param topicName - The topic name to get the ID for
     * @return The topic id, or -1 if the topic is not in the database
     */
    public int queryTopicId( String topicName ) throws SQLException {
        int ret = -1;
        
        Statement st = null;
        ResultSet rs = null;
        
        st = conn.createStatement();
        String stmtStr = "SELECT topic_id FROM topics WHERE topic_name='"+topicName + "'";
        printQueryStatement( stmtStr );
        rs = st.executeQuery( stmtStr );

        if ( rs.next() ) {
            ret = rs.getInt(1);
        }

        if ( rs != null ) rs.close();
        if ( st != null ) st.close();
        
        return ret;
    }

    /**
     * Get the topic name for a given topic id.
     *
     * @param topicId - The topic id to get the name for
     * @return The topic name, or null if the topic is not in the database
     */
    public String queryTopicNameById( int topicId ) throws SQLException {
        String ret = null;
        
        Statement st = null;
        ResultSet rs = null;
        
        st = conn.createStatement();
        String stmtStr = "SELECT topic_name FROM topics WHERE topic_id="+topicId;
        printQueryStatement( stmtStr );
        rs = st.executeQuery( stmtStr );

        if ( rs.next() ) {
            ret = rs.getString(1);
        }

        if ( rs != null ) rs.close();
        if ( st != null ) st.close();
        
        return ret;
    }

    /**
     * Associate a topic with a tweet.  The topic must already exist in the 
     *  database, or it is an error.
     *
     * @param tweet - The tweet to associate with the topic
     * @param topicName - The String name of the topic
     * @param convtweetId - The converted string id
     * @throws SQLException - When the database connection fails
     */
    public void insertTopicTweetAssociation( UPTweet tweet, String topicName,
                                             long convtweetId ) throws SQLException {
        int topicId = queryTopicId( topicName );
        if ( topicId == -1 ) {
            throw new SQLException( "Unset topic name given: " + topicName );
        }

        Statement st = conn.createStatement();
        String stmtStr = "INSERT INTO topic_assignment (tweet_id,topic_id,convtweetId) VALUES ("+tweet.getTweetId()+","+topicId+","+convtweetId+")";
        printChangeStatement( stmtStr );
        st.executeUpdate( stmtStr );
        
        if ( st != null ) st.close();
    }

    /**
     * Associate a topic with a tweet.  The topic must already exist in the 
     *  database, or it is an error.
     *
     * @param tweet - The tweet to associate with the topic
     * @param topicName - The String name of the topic
     * @throws SQLException - When the database connection fails
     */
    public void insertTopicTweetAssociation( UPTweet tweet, String topicName ) throws SQLException {
        int topicId = queryTopicId( topicName );
        if ( topicId == -1 ) {
            throw new SQLException( "Unset topic name given: " + topicName );
        }

        Statement st = conn.createStatement();
        String stmtStr = "INSERT INTO topic_assignment (tweet_id,topic_id,convtweetId) VALUES ("+tweet.getTweetId()+","+topicId+",NULL)";
        printChangeStatement( stmtStr );
        st.executeUpdate( stmtStr );
        
        if ( st != null ) st.close();
    }

    /**
     * Get the list of topics for a tweet.
     *
     * @param tweet - The tweet to get a list of associated topics for
     * @return The list of topics associated with the tweet
     * @throws SQLException - When the database connection fails
     */
    public LinkedList<String> queryTopicsForTweet( UPTweet tweet ) throws SQLException {
        Statement st = null;
        ResultSet rs = null;
        LinkedList<String> ret = new LinkedList<String>();
        
        st = conn.createStatement();

        String stmtStr = "SELECT topics.topic_name FROM topics LEFT JOIN topic_assignment ON topics.topic_id=topic_assignment.topic_id WHERE topic_assignment.tweet_id=" + tweet.getTweetId();
        printQueryStatement( stmtStr );
        rs = st.executeQuery( stmtStr );
       
        if ( rs != null ) {
            while ( rs.next() ) {
                ret.add( rs.getString(1) );
            }
        }

        if ( rs != null ) rs.close();
        if ( st != null ) st.close();
        
        return ret;
    }

    /* *********** Tweet Privacy Score FUNCTIONS *****************/

    /**
     * Set the privacy score for a person.  May also be used to update the
     *  score.
     *
     * @param person - The person to set a score for
     * @param score - The score to apply to the tweet
     * @throws SQLException - When the database connection fails
     */    
    public void insertPersonPrivacyScore( UPUser person, int score ) throws SQLException {
        if ( !hasUser( person.getPersonId() ) ) {
            System.err.println( "Skipping not inserted user: " + person.getHandle() );
            return;
        }

        /*See if the privacy score is set yet*/
        if ( queryPersonPrivacyScore( person ) != -1 ) {
            Statement st = conn.createStatement();
            String stmtStr = "UPDATE person_privacy_score SET score="+score+" WHERE person_id="+person.getPersonId();
               st.executeUpdate( stmtStr );
        
            if ( st != null ) st.close();
   
        } else { /*Not yet set*/
            Statement st = conn.createStatement();
            String stmtStr = "INSERT INTO person_privacy_score (person_id,score) VALUES ("+person.getPersonId()+","+score+")";
            printChangeStatement( stmtStr );
            st.executeUpdate( stmtStr );
        
            if ( st != null ) st.close();
        }
    }

    /**
     * Get the privacy score for a person
     *
     * @param person - The person to get the privacy score for
     * @return The privacy score for the person, -1 if it is not set
     * @throws SQLException - When the database connection fails
     */
    public int queryPersonPrivacyScore( UPUser person ) throws SQLException {
        int ret = -1;
        
        Statement st = null;
        ResultSet rs = null;
        
        st = conn.createStatement();
        String stmtStr = "SELECT score FROM person_privacy_score WHERE person_id="+person.getPersonId();
        printQueryStatement( stmtStr );
        rs = st.executeQuery( stmtStr );

        if ( rs.next() ) {
            ret = rs.getInt(1);
        }

        if ( rs != null ) rs.close();
        if ( st != null ) st.close();
        
        return ret;
    }

    /**
     * Return a map of all scored users in the database to their score
     *
     * @return A list of all scored users in the database to their score
     */
    public HashMap<UPUser,Integer> queryAllScoredPerson() throws SQLException {
        HashMap<UPUser,Integer> ret = new HashMap<UPUser,Integer>();
        
        Statement st = null;
        ResultSet rs = null;
        
        st = conn.createStatement();//TODO
        String stmtStr = "SELECT person_id,score FROM person_privacy_score";
        printQueryStatement( stmtStr );
        rs = st.executeQuery( stmtStr );

        if ( rs != null ) {
            while ( rs.next() ) {
                ret.put( queryUserByPersonId( rs.getLong(1) ),
                         rs.getInt(2) );
            }
        }

        if ( rs != null ) rs.close();
        if ( st != null ) st.close();
        
        return ret;
    }


    /* ********** Scoring Helper Functions ***********/
    /**
     * Return a list of tweets by user u which mentions user m.
     * 
     * @param u - The user who posted the tweet
     * @param r - The user who we want to find mentions of
     * @return a list of tweets by user u which mentions user m
     * @throws SQLException - When the database connection fails
     */
    public LinkedList<UPTweet> queryTweetsForMentionedUser( UPUser u, UPUser r ) throws SQLException {
        
        Statement st = null;
        ResultSet rs = null;
        LinkedList<UPTweet> ret = new LinkedList<UPTweet>();
        
        st = conn.createStatement();

        String stmtStr = "SELECT tweet.tweet_id FROM mentioned_users LEFT JOIN tweet ON mentioned_users.tweet_id=tweet.tweet_id WHERE tweet.person_id="+u.getPersonId()+" AND mentioned_users.person_id="+r.getPersonId();
        printQueryStatement( stmtStr );
        rs = st.executeQuery( stmtStr );
       
        if ( rs != null ) {
            while ( rs.next() ) {
                long curTweetId = rs.getLong(1);
                ret.add( queryTweetById(curTweetId) );
            }
        }

        if ( rs != null ) rs.close();
        if ( st != null ) st.close();
        
        return ret;
    }

   /**
     * Return a list of tweets by user u which is in reply to user m.
     * 
     * @param u - The user who posted the tweet
     * @param r - The user who we want to find mentions of
     * @return a list of tweets by user u which mentions user m
     * @throws SQLException - When the database connection fails
     */
    public LinkedList<UPTweet> queryTweetsForReplyUser( UPUser u, UPUser r ) throws SQLException {

        Statement st = null;
        ResultSet rs = null;
        LinkedList<UPTweet> ret = new LinkedList<UPTweet>();
        
        st = conn.createStatement();
        
        String stmtStr = "SELECT tweet_id FROM tweet WHERE person_id="+u.getPersonId()+" AND reply_to_user="+r.getPersonId();
        printQueryStatement( stmtStr );
        rs = st.executeQuery( stmtStr );
       
        if ( rs != null ) {
            while ( rs.next() ) {
                long curTweetId = rs.getLong(1);
                ret.add( queryTweetById(curTweetId) );
            }
        }

        if ( rs != null ) rs.close();
        if ( st != null ) st.close();
        
        return ret;
    }

    /* *********** HELPER FUNCTIONS ***************/
    /**
     * Add escapes to various characters for insertion into the database
     */
    private String fixupString( String in ) {
        String ret = in.replace("\\","\\\\");
        ret = ret.replace("'","\\'");
        return ret;
    }

}
