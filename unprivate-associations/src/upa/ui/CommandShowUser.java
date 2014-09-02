package upa.ui;

import java.io.*;
import upa.*;
import java.util.LinkedList;
import java.sql.SQLException;

/**
 * Implements the showuser command which is a set of operations to display user 
 * information.
 *
 * @author Jonathan Walsh (jdw74@drexel.edu)
 */
public class CommandShowUser {

    public static String help( String operation ) {
        String ret = "";
        if ( operation != null ) {
            ret = "Invalid operation or arguments for: " + operation + "\n";
        }
        ret += "  showuser id <userid>\t\tPrints user information for a given user id.\n" +
            "  showuser user <handle>\t\tPrints user information for a given handle.\n" +
            "  showuser regex <regex> [limit]\tPrints all users which have a username\n\t\t\t\t\tmatching the given psql regex.\n";

        return ret;
    }

    /**
     * Run the actual command.
     */
    public static String run( String operation, String[] args ) {
        String ret = "";

        if ( operation == null ) {
            return help(null);
        } 

        // for showtweet, all operations have at least one argument
        if ( args == null || args.length < 1 ) return help(operation);

        if ( operation.equals( "id" ) ) {
            UPUser u;
            try {
                u = ui.db.queryUserByPersonId( Long.parseLong( args[0] ) );
            } catch ( SQLException sqle ) {
                ret = "Unable to find user by id: " + sqle.getMessage();
                return ret;
            }
            if ( u == null ) {
                ret = "Unable to find user: Not found for ID";
            } else {
                ret = u.toString();
            }

        } else if ( operation.equals( "user" ) ) {
            UPUser u;
            try {
                u = ui.db.queryUserByHandle( args[0] );
            } catch ( SQLException sqle ) {
                ret = "Unable to find user by handle: " + sqle.getMessage();
                return ret;
            }
            if ( u == null ) {
                ret = "Unable to find user: Not found for handle";
            } else {
                ret = u.toString();
            }

        } else if ( operation.equals( "regex" ) ) {
            ret = "TODO: NOTIMPL";
        } else { 
            return help(operation);
        }
    
        return ret;
    }


}