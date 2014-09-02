package upa;

import java.sql.SQLException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Quick program to load in privacy scores for users from output from Aylin's
 * program of magic scoring!
 *
 * @author Jonathan
 */

public class LoadUserScore {
    

    public static void main( String argv[] ) throws SQLException,IOException {

        if ( argv.length != 1 ) {
            System.err.println( "USAGE: GraphGenerator <input>" );
            System.exit(1);
        }
     
        TwitterDatabase db = new TwitterDatabase("config/db.config");
        BufferedReader in = new BufferedReader( new FileReader( argv[0] ) );
        
        String cline;
        
        boolean inside = false;
        int count = 0;

        while ( ( cline = in.readLine() ) != null ) {
            
            if ( cline.startsWith( "inst#" ) ) {
                inside = true;
            } else if ( inside && cline.length() == 0 ) {
                inside = false;
            } else if ( inside ) {
                // So the line below removes the error column which sometimes
                // exists and is a '+', the splits by column
                String[] sp = cline.replace("+","").split("\\s+");
                
                String personId = sp[7].substring(1,sp[7].length()-1); //user id is formated as '(XYZ)'
                long personIdL = Long.parseLong(personId);

                String score = sp[3].split(":")[0];//score is formated as 'N:NN'
                int scoreI = Integer.parseInt(score);
                //System.out.println( personIdL + " = " + scoreI );
                
                
                db.insertPersonPrivacyScore( db.queryUserByPersonId(personIdL),
                                             scoreI );
                count++;
                

                /*
                for ( int i = 0; i < sp.length; i++ ) 
                    System.out.print( sp[i] + "," );
                System.out.println("");
                */
            }
        }
        
        System.out.println( "Read in " + count + " scores" );
        
        in.close();
        db.close();
    }


}