package upa.ui;

import java.util.Arrays;
import java.io.*;
import upa.*;
import java.sql.SQLException;

/**
 * The main command line parsing UI program.
 *
 * @author Jonathan Walsh (jdw74@drexel.edu)
 */
public class ui {

    /** Storage for the last command output, used to write to file output*/
    public static String lastCmdOutput;

    /** The database connection used by the ui compoenents.*/
    public static TwitterDatabase db;

    /** The connection to Twitter used by the ui components*/
    public static TwitterService twitterService;

    private static final String PROMPT = "TUI9000> ";
        
    /*The main function! */
    public static void main( String argv[] ) {

        boolean going = true;
        BufferedReader in = new BufferedReader( new InputStreamReader(System.in) );
        String line = "";
        lastCmdOutput = "";
      
        /* Set up the database */
        try {
            db = new TwitterDatabase( TwitterDatabase.DEFAULT_CONFIG_PATH );
        } catch ( SQLException sqle ) {
            System.err.println( "ERROR: Failed to set up the DB!" );
            System.err.println( sqle.getMessage() );
            return;
        } catch ( IOException ioe ) {
            System.out.println( "Failed to open configuration file: " + ioe.getMessage() );
            System.exit(2);
        }

        try {
            twitterService = new TwitterService( db, TwitterService.DEFAULT_CONFIG_PATH );
        } catch ( IOException ioe ) {
            System.out.println( "Failed to open configuration file: " + ioe.getMessage() );
            System.exit(2);
        }

        /*Keep reading until we get something that tells us to stop*/
        while ( going ) {
            System.out.print(PROMPT);
            System.out.flush();

            try {
                line = in.readLine();
            } catch ( IOException ioe ) {
                System.err.println( "IO Error reading input: " + ioe.getMessage() );
                System.exit(1);
            }
            
            // EOF means stop running
            if ( line == null ) {
                break;
            }

            //Tokenize the line
            String[] parts = line.split(" ");
            
            /*  //DEBUGGING OUTPUT!
          System.out.println( "TEMP: I read \""+line+"\"");
            System.out.print( "TEMP: " );
            for ( int i = 0; i < parts.length; i++ ) {
                System.out.print( parts[i] + "," );
            }
            System.out.println( "" );*/

            // Make sure we actually have a command
            if ( parts.length == 0 ) {
                continue;
            }

            // Split apart everything into command, operation, and arguments
            String command = parts[0];
            String operation = null;
            if ( parts.length > 1 ) {
                operation = parts[1];
            }
            String[] args = null;
            if ( parts.length > 2 ) {
                args = Arrays.copyOfRange( parts, 2, parts.length );
            }

            // Figure out what our command is
            if ( command.equals( "exit" ) || command.equals( "quit" ) ) {
                System.out.println( "Bye!" );
                System.exit(0);


            } else if ( command.equals( "help" ) ) {
                lastCmdOutput = "Valid Commands:\n  showtweet\tDisplay tweet information\n  showuser\tDisplay user information\n  getuser\tPull user information from the Twitter service\n  gettweet\tPull tweet information from the Twitter service\n  getfollowers\tPull followers from the Twitter Service\n  getfollowing\tPull users being followed from the Twitter service\n  write\t\tWrite the output of the last command to a file\n  help\t\tDisplay this help message\n  quit,exit\tExit the UI\n";
                System.out.println( lastCmdOutput );

            } else if ( command.equals( "write" ) ) {
                // write doesn't have an operation, but the filename goes there
                lastCmdOutput = CommandWrite.run( operation, args );
                System.out.println( lastCmdOutput );
            
            
            } else if ( command.equals( "showtweet" ) ) {
                lastCmdOutput = CommandShowTweet.run( operation, args );
                System.out.println( lastCmdOutput );
                
            } else if ( command.equals( "showuser" ) ) {
                lastCmdOutput = CommandShowUser.run( operation, args );
                System.out.println( lastCmdOutput );
                

            } else if ( command.equals( "gettweet" ) ) {
                lastCmdOutput = CommandGetTweet.run( operation, args );
                System.out.println( lastCmdOutput );
                
            } else if ( command.equals( "getuser" ) ) {
                lastCmdOutput = CommandGetUser.run( operation, args );
                System.out.println( lastCmdOutput );
                
            } else if ( command.equals( "getfollowers" ) ) {
                lastCmdOutput = CommandGetFollowers.run( operation, args );
                System.out.println( lastCmdOutput );
                    
            } else if ( command.equals( "getfollowing" ) ) {
                lastCmdOutput = CommandGetFollowing.run( operation, args );
                System.out.println( lastCmdOutput );
                
            } else if ( !command.equals("" ) ) {
                System.out.println( "Invalid command: " + command );
            }

        }

        db.quietClose(); //I don't care if it fails to close!

    }//main()

}