package upa;

/**
 * Object representing a tweet within the UPA code.
 *
 * @author Jonathan Walsh (jdw74@drexel.edu)
 */
public class UPTweet {

    /** Flag for a tweet which doesn't have a reply to user*/
    public static final long NO_REPLY_TO_USER = 0;

    /** Flag for a tweet which has an unknown user set */
    public static final long UNKNOWN_REPLY_TO_USER = -1;

    /** Flag for a tweet which doesnt have a reply to tweet */
    public static final long NO_REPLY_TO_TWEET = 0;

    private long tweetId;
    private long personId;
    private String tweet;
    private long replyToUser;
    private long replyToStatus;
    private String location;
    private int retweetCount;
    private int favoriteCount;
    private String createdDate;

    /**
     * Create a tweet object from the many values which make up a tweet
     */
    public UPTweet( long tweetId, long personId, String tweet, long replyToUser,
                    long replyToStatus, String location, int retweetCount,
                    int favoriteCount, String createdDate ) {

        this.tweetId = tweetId;
        this.personId = personId;
        this.tweet = tweet;
        this.replyToUser = replyToUser;
        this.replyToStatus = replyToStatus;
        this.location = location;
        this.retweetCount = retweetCount;
        this.favoriteCount = favoriteCount;
        this.createdDate = createdDate;

    }

  /**
     * Create a tweet object from a twitter API tweet
     */
    public UPTweet( twitter4j.Status status, long userId ) {
        this( status.getId(), 
              userId,
              status.getText(),
              status.getInReplyToUserId(),
              status.getInReplyToStatusId(),
              "NULL",
              status.getRetweetCount(),
              status.getFavoriteCount(),
              status.getCreatedAt().toString() );
        
        if ( status.getPlace() != null )
            this.location = status.getPlace().getFullName();
    }

    /**
     * Create a tweet object from a twitter API tweet
     */
    public UPTweet( twitter4j.Status status, twitter4j.User user ) {
        this( status, user.getId() );
    }

    public long getTweetId() { return tweetId; }
    public long getPersonId() { return personId; }
    public String getTweet() { return tweet; }
    public long getReplyToUserId() { return replyToUser; }
    public long getReplyToStatusId() { return replyToStatus; }
    public String getLocation() { return location; }
    public int getRetweetCount() { return retweetCount; }
    public int getFavoriteCount() { return favoriteCount; }
    public String getCreatedDate() { return createdDate; }

    public String toString() {

        return tweetId + "\t" + tweet + "\t" + replyToUser + "\t" + retweetCount + "\t" + favoriteCount;
    }
}

