package upa;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.HashMap;
import java.util.Iterator;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Generate the graph output for all users in the DB who have a privacy score
 *
 * @author Jonathan Walsh (jdw74@drexel.edu)
 */
public class GraphGeneratorNG {
   
    /**
     *Helper function for the dotFile function. I should just inline this
     */
    private static String getScoreColor( int score ) {
        String color = "#ffffff";//low risk
        if ( score == 3 ) { //high risk
            color = "#ff0000";
        } else if ( score == 2 ) { //medium risk
            color = "#ffff00";
        } else if ( score <= 0 ) { //unable to calculate
            color = "#222222";
        }
        return color;
    }
    

    /**
     * Write out dot file for graphviz
     *
     * @param outBase - Base string for the output file
     * @param users - A hashmap of user to score
     * @param db - Database connection
     */
    private static void dotFile( String outBase,
                                 HashMap<UPUser,Integer> users,
                                 TwitterDatabase db ) throws SQLException,IOException {

        String fn =  outBase+".dot";
        BufferedWriter out = new BufferedWriter( new FileWriter( fn ) );
        out.write( "digraph network {\n" );

        // Write out the nodes (ie users )
        HashMap<String,Integer> nodes = new HashMap<String,Integer>();
        Iterator<UPUser> it = users.keySet().iterator();
        while ( it.hasNext() ) {
            UPUser u = it.next();
            int score = users.get(u);
            String color = getScoreColor( score ); 
            out.write( u.getPersonId() + " [label = \""+u.getHandle()+"\\n("+score+")\" fillcolor=\""+color+"\" style=\"filled\"];\n" );
            nodes.put( u.getHandle(), score );
        }

        // For each user, find any connections to other users
        it = users.keySet().iterator();
        while ( it.hasNext() ) {
            UPUser u = it.next();
            LinkedList<UPUser> following = db.queryUserFollowing(u);
            
            Iterator<UPUser> fit = following.iterator();
            while ( fit.hasNext() ) {
                UPUser f = fit.next();
                //check to see if this is a user in our set
                if ( !nodes.containsKey( f.getHandle() ) ) continue;
                //write out the edge
                out.write( u.getPersonId() + " -> " + f.getPersonId() + "\n" );
            }

        }

        // footer stuff
        out.write( "}\n" );

        //close it out
        out.close();
        System.out.println( "Wrote: " + fn );
    }


    /**
     * Write out GEXF format XML for Gephi times
     *
     * @param outBase - Base string for the output file
     * @param users - A hashmap of user to score
     * @param db - Database connection
     */
    private static void gephiFile( String outBase,
                                   HashMap<UPUser,Integer> users,
                                   TwitterDatabase db ) throws SQLException,IOException {
        
        String fn = outBase+".gexf.xml";
        BufferedWriter out = new BufferedWriter( new FileWriter( fn ) );
        // header stuff
        out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        out.write("<gexf xmlns=\"http://www.gexf.net/1.2draft\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.gexf.net/1.2draft http://www.gexf.net/1.2draft/gexf.xsd\" version=\"1.2\">\n" );
        out.write( "    <meta lastmodifieddate=\"2009-03-20\">\n        <creator>Me</creator>\n        <description>Some Twitter People</description>\n    </meta>\n    <graph defaultedgetype=\"directed\">\n");

        // attributes (ie the score)
        out.write("        <attributes class=\"node\">\n            <attribute id=\"0\" title=\"score\" type=\"int\"/>\n        </attributes>\n");

        // nodes
        out.write("        <nodes>\n");
        HashMap<String,Integer> nodes = new HashMap<String,Integer>();
        Iterator<UPUser> it = users.keySet().iterator();
        while ( it.hasNext() ) {
            UPUser u = it.next();
            int score = users.get(u);
            out.write( "            <node id=\""+u.getPersonId()+"\" label=\""+u.getHandle()+"\">\n                <attvalues>\n                    <attvalue for=\"0\" value=\""+score+"\"/>\n                </attvalues>\n            </node>\n" );
            nodes.put( u.getHandle(), score );
        }
        out.write("        </nodes>\n");


        // edges
        out.write("        <edges>\n");
        int ecount = 0;
        it = users.keySet().iterator();
        while ( it.hasNext() ) {
            UPUser u = it.next();
            LinkedList<UPUser> following = db.queryUserFollowing(u);
            
            Iterator<UPUser> fit = following.iterator();
            while ( fit.hasNext() ) {
                UPUser f = fit.next();
                //check to see if this is a user in our set
                if ( !nodes.containsKey( f.getHandle() ) ) continue;

                //write out the edge
                out.write( "            <edge id=\""+ecount+"\" source=\""+u.getPersonId()+"\" target=\""+f.getPersonId()+"\"/>\n" );
                ecount++;
            }

        }
        out.write("        </edges>\n");

        // footer stuff
        out.write("    </graph>\n</gexf>\n");

        //close things
        out.close();
        System.out.println( "Wrote: " + fn );
    }


    /**
     * The main function for the program.
     *
     * USAGE: GraphGenerator <handle> <output.dot>
     */
    public static void main( String argv[] ) throws SQLException,IOException {

        if ( argv.length != 1 ) {
            System.err.println( "USAGE: GraphGenerator <output_base>" );
            System.exit(1);
        }

        TwitterDatabase db = new TwitterDatabase("config/db.config");
      

        // First get all the users that have scores
        HashMap<UPUser,Integer> users = db.queryAllScoredPerson();
        System.out.println( "Found " + users.size() + " scored users." );
        
        // Write the dot file
        dotFile( argv[0], users, db );

        // Write the Gephi file
        gephiFile( argv[0], users, db );

        db.quietClose();
    }
}
