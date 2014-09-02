package upa.ui;

import upa.*;

import java.io.*;
import java.sql.SQLException;

/**
 * Implements the getuser command which is a set of operations to pull user 
 * information from the Twitter service.
 *
 * @author Jonathan Walsh (jdw74@drexel.edu)
 */
public class CommandGetUser {

    public static String help( String operation ) {
        String ret = "";
        if ( operation != null ) {
            ret = "Invalid operation or arguments for: " + operation + "\n";
        }
        ret += "  getuser id <userid>\tGets and stores user information for a given user id.\n  getuser user <user>\tGets and stores user information for a given username.";

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

        // for getuser, all operations have exactly one argument
        if ( args == null || args.length != 1 ) return help(operation);

        if ( operation.equals( "id" ) ) {

            long personId = Long.parseLong( args[0] );
            
            // First see if the user is in the database already
            UPUser u;
            try {
                u = ui.db.queryUserByPersonId( personId );
            } catch ( SQLException sqle ) {
                ret = "Some sort of DB error querying by id: " + sqle.getMessage();
                return ret;
            }
            if ( u != null ) {
                return "User already exists in database: " + personId;
            }

            // Get the user from the Twitter service
            try {
                u = ui.twitterService.getUserById( personId );
            } catch ( Exception e ) {
                ret = "Unable to find user for id: " + personId + "\n" + e.getMessage();
            }
            if ( u == null ) {
                ret = "Unable to find user for id: " + personId;
            } else {
                ret = u.toString();
            }
            
        } else if ( operation.equals( "user" ) ) {

            String handle = args[0];
            
            // First see if the user is in the database already
            UPUser u;
            try {
                u = ui.db.queryUserByHandle( handle );
            } catch ( SQLException sqle ) {
                ret = "Some sort of DB error querying by handle: " + sqle.getMessage();
                return ret;
            }
            if ( u != null ) {
                return "User already exists in database: " + handle;
            }

            // Get the user from the Twitter service
            try {
                u = ui.twitterService.getUserByHandle( handle );
            } catch ( Exception e ) {
                ret = "Unable to find user for handle: " + handle + "\n" + e.getMessage();
            }
            if ( u == null ) {
                ret = "Unable to find user for handle: " + handle;
            } else {
                ret = u.toString();
            }


        } else { 
            return help(operation);
        }
    
        return ret;
    }


}