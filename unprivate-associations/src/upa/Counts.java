package upa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Object to print  out a count of data stored in the database.  This is
 *  really mostly that I could check progress as the fill process ran.
 *
 * @author Jonathan Walsh (jdw74@drexel.edu)
 */
public class Counts {
    
    /** The database connection */
    private Connection conn;

    /**
     * Make a new counts object with default DB connection
     *
     * @throws SQLException - When the database connection fails
     */
    public Counts() throws SQLException {
        this( TwitterDatabase.DEFAULT_DB_URL,
              TwitterDatabase.DEFAULT_DB_USER,
              TwitterDatabase.DEFAULT_DB_PASS );
    }

    /**
     * Make a new counts object with the specified DB connection
     *
     * @param url - The DB URL
     * @param user - The DB user
     * @param password - The DB password
     * @throws SQLException - When the database connection fails
     */
    public Counts( String url, String user, String password ) throws SQLException {
        conn = DriverManager.getConnection( url, user, password );
    }

    /**
     * Close the SQL database connection
     * @throws SQLException - When the database connection fails
     */
    public void close() throws SQLException {
        conn.close();
    }

    /**
     * Return the number of entries in the person table
     *
     * @return The number of entries in the tweet table, -1 for failure
     */
    public int getPersonCount() { 
        int ret = -1;
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM person");
            rs.next();
            ret = rs.getInt(1);
            rs.close();
            st.close();
        } catch ( SQLException sqle ) {
            System.err.println( "SQLException: " + sqle.getMessage() );
        }
        return ret;
    }

    /**
     * Return the number of entries in the tweet table
     *
     * @return The number of entries in the tweet table, -1 for failure
     */
    public int getTweetCount() {
        int ret = -1;
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM tweet");
            rs.next();
            ret = rs.getInt(1);
            rs.close();
            st.close();
        } catch ( SQLException sqle ) {
            System.err.println( "SQLException: " + sqle.getMessage() );
        }
        return ret;
    }

    /**
     * Return the number of entries in the follower table
     *
     * @return The number of entries in the tweet table, -1 for failure
     */
    public int getFollowersCount() {
        int ret = -1;
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM followers");
            rs.next();
            ret = rs.getInt(1);
            rs.close();
            st.close();
        } catch ( SQLException sqle ) {
            System.err.println( "SQLException: " + sqle.getMessage() );
        }
        return ret;
    }

    /**
     * Return the number of entries in the hashtag table
     *
     * @return The number of entries in the hashtag table, -1 for failure
     */
    public int getHashtagsCount() {
        int ret = -1;
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM hashtags");
            rs.next();
            ret = rs.getInt(1);
            rs.close();
            st.close();
        } catch ( SQLException sqle ) {
            System.err.println( "SQLException: " + sqle.getMessage() );
        }
        return ret;
    }

    /**
     * The main() function simply runs through each of the counting functions 
     * and prints the results.
    */
    public static void main( String argv[] ) throws SQLException {

        Counts c = new Counts();
        System.out.println( "Person\t" + c.getPersonCount() );   
        System.out.println( "Tweet\t" + c.getTweetCount() );
        System.out.println( "Followers\t" + c.getFollowersCount() );
        System.out.println( "Hashtags\t" + c.getHashtagsCount() );
        c.close();
    }

}