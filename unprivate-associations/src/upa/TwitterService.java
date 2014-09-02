package upa;

import java.util.LinkedList;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import twitter4j.ResponseList;
import twitter4j.PagableResponseList;
import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.HashtagEntity;
import twitter4j.UserMentionEntity;

import java.sql.SQLException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * API to access the Twitter service.  All pull functions will also store the
 *  returned data in the database. How convenient! :D
 *
 * @author Jonathan Walsh (jdw74@drexel.edu)
 */
public class TwitterService {

    /*Twitter service access information*/
    private static final String DEFAULT_CONSUMER_KEY = "1sJYolvvM9pozh5GO1LZw";
    private static final String DEFAULT_CONSUMER_SECRET = "J97SyRyh7pfPCCF1QmpobTKe2YltPWdb8DuJLPnH2lA";
    private static final String DEFAULT_ACCESS_TOKEN = "1219579171-tszCiJGXvU2IS90YGaYDBxlSO9CRDLa6WVUwM9q";
    private static final String DEFAULT_ACCESS_TOKEN_SECRET = "GrPdeMCxSyEycnaendwQeuu6NhpmsjTsjJ75ArZ2qvY";

    private String consumerKey;
    private String consumerSecret;
    private String accessToken;
    private String accessTokenSecret;

    /** The flag which means not to limit the number of replies requested */
    public static final int NO_LIMIT = -1;

    /** Stub user to apply when we don't know who the user is*/
    public static final int EMPTY_PERSON_ID = 0;

    /* Twitter Service Error Codes*/
    /** Error returned by the twitter service for rate limiting*/
    public static final int TWITTER_ERROR_RATE = 88;
    /** Error returned by the twitter service when a query is made for an 
     *    unknown item.  An example on where this would show up unexpectedly is
     *    when a user replies to another, and that user is deleted. */
    public static final int TWITTER_ERROR_UNKNOWN_ITEM = 34;
    /** Error returned by the twitter service when the tweet is marked private*/
    public static final int TWITTER_ERROR_PROTECTED_TWEET = 179;

    /** Default configuration file path */
    public static final String DEFAULT_CONFIG_PATH = "config/twitter_service.config";

    /*Enable or disable debugging*/
    private boolean debug = false;

    /* Private member data*/
    private TwitterDatabase db;
    private Twitter twitter;

    /**
     * Create a new TwitterService object.
     */
    public TwitterService() throws SQLException {
        this( new TwitterDatabase() );
    }

    /**
     * Create a new Twitter service object with a given database reference.
     *
     * @param db - The database to use
     */
    public TwitterService( TwitterDatabase db ) {
        this.db = db;

        consumerKey = DEFAULT_CONSUMER_KEY;
        consumerSecret = DEFAULT_CONSUMER_SECRET;
        accessToken = DEFAULT_ACCESS_TOKEN;
        accessTokenSecret = DEFAULT_ACCESS_TOKEN_SECRET;

        setupService();
    }

    /**
     * Create a twitter service object from a configuration file with a specific
     * database object.
     *
     * @param db - The database object to use
     * @param configFile - The configuration file to use
     * @param throws IOException - Errors in config file reading
     */    
    public TwitterService( TwitterDatabase db, String configFile ) throws IOException {
        this.db = db;

        consumerKey = DEFAULT_CONSUMER_KEY;
        consumerSecret = DEFAULT_CONSUMER_SECRET;
        accessToken = DEFAULT_ACCESS_TOKEN;
        accessTokenSecret = DEFAULT_ACCESS_TOKEN_SECRET;
        
        String line;
        BufferedReader in = new BufferedReader( new FileReader( configFile ) );

        while ( ( line = in.readLine() ) != null ) {
            
            if ( line.indexOf( "=" ) == -1 ) {
                System.out.println( "WARNING: Lines must be format key=value" );
                System.out.println( "WARNING: Invalid line: " + line );
                continue;
            }

            String key = line.substring( 0, line.indexOf( "=" ) ).trim();
            String value = line.substring( line.indexOf( "=" ) + 1, 
                                           line.length() ).trim();
            if ( key.equals( "consumer_key" ) ) {
                consumerKey = value;
            } else if ( key.equals( "consumer_secret" ) ) {
                consumerSecret = value;
            } else if ( key.equals( "access_token" ) ) {
                accessToken = value;
            } else if ( key.equals( "access_token_secret" ) ) {
                accessTokenSecret = value;
            } else if ( key.equals( "debug" ) ) {
                debug = value.equals("true");
            } else {
                System.out.println( "WARNING: In TwitterDatabase() Unknown configuration key = " + key );
            }
        }
        in.close();

        setupService();
    }

    /**
     * Private helper function to set up the service
     */    
    private void setupService() {
        /*Set up for the Twitter queries*/
        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(consumerKey, consumerSecret);
        AccessToken access = new AccessToken(accessToken, accessTokenSecret);
        twitter.setOAuthAccessToken(access);
    }

    /**
     * Get a user object from the Twitter service by handle
     *
     * @param handle - The handle to query a user for
     * @return The user for the handle; null if it does not exist
     * @throws SQLException - When the database connection fails
     */
    public UPUser getUserByHandle( String handle ) throws SQLException {
        System.out.println( "TRACE: getUserByHandle( " + handle + " ) " );

        twitter4j.User tu = null;
        while ( tu == null ) {
            try {
                tu = twitter.showUser( handle );
            } catch ( TwitterException te ) {
                int errorCode = handleTE( te );
                if ( errorCode == TWITTER_ERROR_RATE ) continue;

                if ( errorCode == TWITTER_ERROR_UNKNOWN_ITEM ) {
                    System.out.println( "ERROR (34): Unknown user handle: " + handle );
                } else {
                    System.out.println( "ERROR ("+errorCode+"): Failed to get user. " + te.getMessage() );
                }
                return null;
            }
        }

        // Make our local type and stick it in the DB
        UPUser upu = new UPUser( tu );
        db.insertUser( upu );
        return upu;
    }

    /**
     * Get a user object from the Twitter service by id
     *
     * @param id - The id to query a user for
     * @return The user for the id; null if it does not exist
     * @throws SQLException - When the database connection fails
     */
    public UPUser getUserById( long id ) throws SQLException {
        System.out.println( "TRACE: getUserById( " + id + " )" );

        twitter4j.User tu = null;
        while ( tu == null ) {
            try {
                tu = twitter.showUser( id );
            } catch ( TwitterException te ) {
                int errorCode = handleTE( te );
                if ( errorCode == TWITTER_ERROR_RATE ) continue;
                
                if ( errorCode == TWITTER_ERROR_UNKNOWN_ITEM ) {
                    System.out.println( "ERROR ("+TWITTER_ERROR_UNKNOWN_ITEM+"): Unknown user id: " + id );
                } else {
                    System.out.println( "ERROR ("+errorCode+"): Failed to get user. " + te.getMessage() );
                }
                return null;
            }
        }

        // Make our local type and stick it in the DB
        UPUser upu = new UPUser( tu );
        db.insertUser( upu );
        return upu;
    }

    /**
     * Get a tweet for a given tweet id, without knowing the user already
     *
     * @param id - The id to query a tweet for
     * @return The tweet for the id; null if it does not exist
     * @throws SQLException - When the database connection fails
     */
    public UPTweet getTweetById( long id ) throws SQLException {
        return getTweetById( id, null );
    }

    /**
     * Get a tweet for a given tweet id
     *
     * @param id - The id to query a tweet for
     * @param user - The previously known user that this tweet matches
     * @return The tweet for the id; null if it does not exist
     * @throws SQLException - When the database connection fails
     */
    public UPTweet getTweetById( long id, UPUser user ) throws SQLException {
        System.out.println( "TRACE: getTweetById( " + id + " )" );

        twitter4j.Status tStatus = null;
        while ( tStatus == null ) {
            try {
                tStatus = twitter.showStatus( id );
            } catch ( TwitterException te ) {
                int errorCode = handleTE( te );
                
                // If something is returned, it is another problem
                if ( errorCode != TWITTER_ERROR_RATE ) {

                    if ( errorCode == 179 ) {
                        db.insertTweet( id, EMPTY_PERSON_ID, "PROTECTED" );
                    } else if ( errorCode == TWITTER_ERROR_UNKNOWN_ITEM ) {
                        db.insertTweet( id, EMPTY_PERSON_ID, "NOTFOUND" );
                    } else {
                        db.insertTweet( id, EMPTY_PERSON_ID, "OTHER" );
                    }
                    return db.queryTweetById( id );
                }
            }
        }

        /* If we don't know the user, get it from Twitter */
        if ( user == null ) {
            twitter4j.User tUser = null;
            tUser = tStatus.getUser();
            
            // Make our local type and stick it in the DB
            user = new UPUser( tUser );
            if ( !db.hasUser( user.getPersonId() ) ) db.insertUser( user );
        }

        UPTweet upt = new UPTweet( tStatus, user.getPersonId() );

        /* Before we insert the tweet, we need to add any referenced data*/
        getTweetReferences( upt );

        /* Actually insert the tweet now*/
        db.insertTweet( upt );

        /* Handle any mentioned users or hash tags*/
        handleTweetEntities( tStatus, upt );

        return upt;
    }

    /**
     * Get any tweets referenced by the given tweet from the service.  This
     *  includes reply_to_status and reply_to_user.
     *
     * @param tweet - The tweet to collect referenced data for
     */
    public void getTweetReferences( UPTweet tweet ) throws SQLException {
        System.out.println( "TRACE: getTweetReferences( tweet(" + tweet.getTweetId() + ") )" );

        /* Get the user that this is a reply to, if not already in the DB */
        long personId = tweet.getReplyToUserId();
        if ( personId != UPTweet.NO_REPLY_TO_USER 
             && personId != UPTweet.UNKNOWN_REPLY_TO_USER
             && !db.hasUser( personId ) ) {

            getUserById( personId );
        }

        /* Get the tweet that this is a reply to, if not already in the DB */
        long tweetId = tweet.getReplyToStatusId();
        if ( tweetId != UPTweet.NO_REPLY_TO_TWEET && !db.hasTweet( tweetId ) ) {
            getTweetById( tweetId );
        }

    }

    /**
     * Helper function which inserts hash tags (#foo) and mentioned users
     *  (@person).
     * 
     * @param tStatus - The twitter4j status object to get entities from
     * @param upt - The UPTweet object corresponding to tStatus
     */
    private void handleTweetEntities( twitter4j.Status tStatus, 
                                      UPTweet upt ) throws SQLException {
        System.out.println( "TRACE: handleTweetEntities( tweet(" + upt.getTweetId() + ") )" );

        /* Handle any mentioned users (@person) */
        UserMentionEntity[] muList = tStatus.getUserMentionEntities();
        for ( UserMentionEntity mu : muList ) {
            System.out.println( "TRACE:\tMentioned User: " + mu.getText() );

            /*Get the user from the DB, otherwise get from twitter service*/
            UPUser mentionedUser = db.queryUserByPersonId( mu.getId() );
            if ( mentionedUser == null ) {
                System.out.println("TRACE:\tGetting mentioned user from Twitter" );
                mentionedUser = getUserById( mu.getId() );
            }
            
            /* If we have the user, add the reference */
            if ( mentionedUser != null ) {
                db.insertUserMention( upt, mentionedUser );
            } else {
                System.out.println( "TRACE:\tFailed to find user to add mention reference!" );
            }
        }
            
        /* Handle any mentioned hash tags (#foo) */
        HashtagEntity[] htList = tStatus.getHashtagEntities();
        for ( HashtagEntity ht : htList ) {
            String htText = ht.getText();
            System.out.println( "TRACE:\tAdding hashtag map ("+upt.getTweetId()+","+htText+")" );
            db.insertHashTag( upt, htText );
        }

        /* Detect if the tweet is english or not */
        int result = DetectLangWrapper.isEnglish( upt.getTweet() );
        if ( result != 1 ) {
            db.setTweetNotEnglish( upt );
        }

    }

    /**
     * Return a twitter timeline, given a handle; also insert into the DB.
     *
     * @param handle - The user to get the timeline for
     * @return The user timeline tweet list, or null if there is an error
     * @throws SQLException - When the database connection fails
     */
    public LinkedList<UPTweet> getUserTimeline( String handle ) throws SQLException {
        return getUserTimeline( handle, NO_LIMIT );
    }

    /**
     * Return a twitter timeline, given a handle; also insert into the DB with a
     * limit
     *
     * @param handle - The user to get the timeline for
     * @param limit - Limit the number of tweets returned
     * @return The user timeline tweet list, or null if there is an error
     * @throws SQLException - When the database connection fails
     */
    public LinkedList<UPTweet> getUserTimeline( String handle, int limit ) throws SQLException {
        System.out.println( "TRACE: getUserTimeline( "+handle+", "+limit+" )" );

        LinkedList<UPTweet> ret = new LinkedList<UPTweet>();

        UPUser u = db.queryUserByHandle( handle );

        if ( u == null ) {
            u = getUserByHandle( handle );
        }

        Paging paging;
        if ( limit > 0 ) {
            paging = new Paging( 1, limit );
        } else {
            paging = new Paging( 1, 1000 );
        }
        ResponseList<twitter4j.Status> timeline = null;
        while ( timeline == null ) {
            try {
                timeline = twitter.getUserTimeline( handle, paging );
            } catch ( TwitterException te ) {
                int errorCode = handleTE( te );
                if ( errorCode == TWITTER_ERROR_RATE ) continue;

                if ( errorCode == TWITTER_ERROR_UNKNOWN_ITEM ) {
                    System.out.println( "ERROR (34): Unknown user handle: " + handle );
                } else if ( errorCode == -1 ) {
                    System.out.println( "ERROR: Failed to get user tweets (Is timeline private?)" );
                } else {
                    System.out.println( "ERROR ("+errorCode+"): Failed to get user tweets. " + te.getMessage() );
                }
                return null;
            }
        }

        long personId = u.getPersonId();
        for ( twitter4j.Status s : timeline ) {
            UPTweet t = new UPTweet( s, personId );

            /* Before we insert the tweet, we need to add any referenced data */
            getTweetReferences( t );

            /* Insert the tweet inside*/
            db.insertTweet( t );

            /* Handle any hash tags or mentioned users */
            handleTweetEntities( s, t );

            /* Add the tweet to the return list */
            ret.add( t );
        }

        return ret;
    }

    /**
     * Get followers of a given user.
     *
     * @param user - The user to get the list of followers for
     * @return The user timeline tweet list
     */    
    public LinkedList<UPUser> getFollowers( UPUser user ) {
        return getFollowers( user, NO_LIMIT );
    }

    /**
     * Get followers of a given user, up to a limit.
     *
     * @param user - The user to get followers for
     * @param limit - A limit to the number of followers to get
     * @return The user timeline tweet list
     */    
    public LinkedList<UPUser> getFollowers( UPUser user, int limit ) {
        System.out.println( "TRACE: getFollowers( " + user.getHandle() + ", " + limit + " ) " );

        LinkedList<UPUser> ret = new LinkedList<UPUser>();

        LinkedList<twitter4j.User> followers = new LinkedList<twitter4j.User>();
        PagableResponseList<twitter4j.User> responseUserList;

        long personId = user.getPersonId();
        long cursor = -1;
        while ( cursor != 0 ) {
            //System.out.println( "TRACE: in getFollowers, cursor = " + cursor );

            try { 
                responseUserList = twitter.getFollowersList( personId, cursor );
            } catch ( TwitterException te ) {

                int errorCode = handleTE( te );
                if ( errorCode == TWITTER_ERROR_RATE ) continue;

                if ( errorCode == TWITTER_ERROR_UNKNOWN_ITEM ) {
                    System.out.println( "ERROR (34): Unknown user id: " + personId );
                } else {
                    System.out.println( "ERROR ("+errorCode+"): Failed to get user followers. " + te.getMessage() );
                }
                return null;
            }
            
            cursor = responseUserList.getNextCursor();
            followers.addAll( responseUserList );
            if ( limit != NO_LIMIT && followers.size() >= limit ) {
                break;
            }
        }

        /*Add each user to the database, and set up the return list*/
        for ( twitter4j.User tUser : followers )  {
            UPUser newUser = new UPUser( tUser );
            try {
                db.insertUser( newUser );
                db.insertFollower( user, newUser );
                ret.add( newUser );
            } catch ( SQLException sqle ) {
                System.out.println( "Failed to add new user: " + 
                                    newUser.getHandle() + " " + 
                                    sqle.getMessage() );
            }
        }                

        return ret;
    }

    /**
     * Get followed users of a given user, up to a limit.
     *
     * @param user - The user to get followers for
     * @param limit - A limit to the number of followers to get
     * @return The user timeline tweet list
     */    
    public LinkedList<UPUser> getFollowing( UPUser user, int limit ) {
        System.out.println( "TRACE: getFollowing( " + user.getHandle() + ", " + limit + " ) " );

        LinkedList<UPUser> ret = new LinkedList<UPUser>();

        LinkedList<twitter4j.User> following = new LinkedList<twitter4j.User>();
        PagableResponseList<twitter4j.User> responseUserList;

        long personId = user.getPersonId();
        long cursor = -1;
        while ( cursor != 0 ) {
            try {
                responseUserList = twitter.getFriendsList( personId, cursor );

            } catch ( TwitterException te ) {
                int errorCode = handleTE( te );
                if ( errorCode == TWITTER_ERROR_RATE ) continue;

                if ( errorCode == TWITTER_ERROR_UNKNOWN_ITEM ) {
                    System.out.println( "ERROR (34): Unknown user id: " + personId );
                } else {
                    System.out.println( "ERROR ("+errorCode+"): Failed to get user followers. " + te.getMessage() );
                }
                return null;
            }
            
            cursor = responseUserList.getNextCursor();
            following.addAll( responseUserList );
            if ( limit != NO_LIMIT && following.size() >= limit ) {
                break;
            }
        }

        /*Add each user to the database, and set up the return list*/
        for ( twitter4j.User tUser : following )  {
            UPUser newUser = new UPUser( tUser );
            try {
                db.insertUser( newUser );
                db.insertFollower( newUser, user );
                ret.add( newUser );
            } catch ( SQLException sqle ) {
                System.out.println( "Failed to add new user: " + 
                                    newUser.getHandle() + " " + 
                                    sqle.getMessage() );
            }
        }                

        return ret;
    }


    /* **** Helper Functions **** */
    /**
     * Handles the timeout twitter exception, or returns an error code
     *
     * @param te - The exception to handle
     * @return The error code from the exception
     */
    private int handleTE( TwitterException te ) {

        if ( te.getErrorCode() == TWITTER_ERROR_RATE ) { //we have a timeout
            try {
                int retry = te.getRateLimitStatus().getSecondsUntilReset();

                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                Date date = new Date();
                
                System.out.println( "Timeout reached! Sleeping at "+dateFormat.format(date)+" for "+retry+" seconds! zzzz" );
                Thread.sleep(retry*1000);
            } catch ( InterruptedException ie ) {}
        }
        
        if ( te.isCausedByNetworkIssue() ) {
            System.out.println( "WARNING: twitter4j is reporting that this is a network issue" );
        }

        return te.getErrorCode();
    }

}
