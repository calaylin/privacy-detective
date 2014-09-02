package upa.ui;

import java.io.*;

/**
 * Implements the write command to output the last command to a file.
 *
 * @author Jonathan Walsh (jdw74@drexel.edu)
 */
public class CommandWrite {

    public static String help( String operation ) {
        String ret = "  write <filename>  - Writes the last command output to a file.";
        //System.out.println( ret );
        return ret;
    }

    /**
     * Run the actual command. For write, the operation is really the filename
     */
    public static String run( String operation, String[] args ) {
        
        if ( operation == null ) return help(null);

        String ret = "Writing file: " + operation;
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter( operation ) );
            out.write( ui.lastCmdOutput );
            out.close();
        } catch ( IOException ioe ) {
            System.err.println( "Failed to write to file: " + operation );
            System.err.println( ioe.getMessage() );
            ret = "Failed to write file: " + operation;
        }
    
        return ret;
    }


}