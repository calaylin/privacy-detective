package upa;

import java.sql.SQLException;
import java.util.LinkedList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Generate the graph output for a particular user.
 *
 * @author Jonathan Walsh (jdw74@drexel.edu)
 */
public class GraphGenerator {

    /**
     * Returns a DOT file for the network of a user, colored to show risk from 
     * other associated users.
     *
     * @param handle - The handle for the user
     * @return The DOT file for the user
     */
    public static String getDotFormatGraph( String handle, 
                                            TwitterDatabase db ) throws SQLException {
        return getDOTFormatGraph( db.queryUserByHandle( handle ), db );
    }

    /**
     * Returns a DOT file for the network of a user, colored to show risk from 
     * other associated users.
     *
     * @param user - The user to center the graph on
     * @return The DOT file for the user
     */
    public static String getDOTFormatGraph( UPUser user,
                                            TwitterDatabase db ) throws SQLException {
        
        long rootId = user.getPersonId();

        String ret = "digraph " + user.getHandle() + "_network {\n";
        String nodeList = "";
        String edgeList = "";
        
        LinkedList<UPUser> nodeUsers = new LinkedList<UPUser>();

        // Add a node for the root user
        //nodeList += rootId + " [label = \""+user.getHandle()+"\"];\n";
        int score = db.queryPersonPrivacyScore( user );
        String color = getScoreColor( score ); 

        nodeList += rootId + " [label = \""+user.getHandle()+"\\n("+score+")\" fillcolor=\""+color+"\" style=\"filled\"];\n";

        //Get followers and add them as nodes, plus links
        LinkedList<UPUser> followers = db.queryUserFollowers( user );
        System.out.println( followers.size() + " Followers" );
        for ( UPUser f : followers ) {
            nodeList += getNodeText( f, db );
            edgeList += getEdgeTextFollower( user, f, db );
        }
        
        // Get users being followed and add them as nodes, plus links
        LinkedList<UPUser> followed = db.queryUserFollowing( user );
        System.out.println( followed.size() + " Followed" );
        for ( UPUser f : followed ) {
            nodeList += getNodeText( f, db );
            edgeList += getEdgeTextFollowed( user, f, db );
        }

        /*Write the output*/
        ret += nodeList;
        ret += edgeList;
        ret += "}\n";

        return ret;
    }
    
    private static String getScoreColor( int score ) {
        String color = "#ffffff";//low risk
        if ( score > 90 ) { //high risk
            color = "#ff0000";
        } else if ( score > 70 ) { //medium risk
            color = "#ffff00";
        } else if ( score < 0 ) { //unable to calculate
            color = "#222222";
        }
        return color;
    }

    private static String getEdgeTextFollower( UPUser u, UPUser f, 
                                                TwitterDatabase db ) throws SQLException {
        return f.getPersonId() + " -> " + u.getPersonId() + ";\n";
    }
    
    private static String getEdgeTextFollowed( UPUser u, UPUser f, 
                                               TwitterDatabase db ) throws SQLException {
        return u.getPersonId() + " -> " + f.getPersonId() + ";\n";
    }
    
    private static String getNodeText( UPUser u, TwitterDatabase db ) throws SQLException {
        int score = db.queryPersonPrivacyScore( u );
        String color = getScoreColor( score ); 

        return u.getPersonId() + " [label = \""+u.getHandle()+"\\n("+score+")\" fillcolor=\""+color+"\" style=\"filled\"];\n";

    }
    
    /**
     * The main function for the program.
     *
     * USAGE: GraphGenerator <handle> <output.dot>
     */
    public static void main( String argv[] ) throws SQLException,IOException {

        if ( argv.length != 2 ) {
            System.err.println( "USAGE: GraphGenerator <handle> <output.dot>" );
            System.exit(1);
        }

        TwitterDatabase db = new TwitterDatabase("config/db.config");
        String contents = getDotFormatGraph( argv[0], db );
        
        //System.out.println( contents );

        BufferedWriter out = new BufferedWriter( new FileWriter( argv[1] ) );
        out.write( contents );
        out.close();
    }
}
