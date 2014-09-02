package upa;

import twitter4j.User;

/**
 * Object repsenting a user within the UPA code.
 *
 * @author Jonathan Walsh (jdw74@drexel.edu)
 */
public class UPUser {
    private long personId;
    private String handle;
    private String personName;
    private String location;
    private boolean verified;
    private String url;
    private String createdDate;

    /**
     * Create a user object
     */
    public UPUser( long personId, String handle, String personName,
                   String location, boolean verified, String url, 
                   String createdDate ) {

        this.personId = personId;
        this.handle = handle;
        this.personName = personName;
        this.location = location;
        this.verified = verified;
        this.url = url;
        this.createdDate = createdDate;
    }

    /*
     * Create a user object from a twitter API user
     */
    public UPUser( twitter4j.User u ) {
        this.personId = u.getId();
        this.handle = u.getScreenName();
        this.personName = u.getName();
        this.location = u.getLocation();
        this.verified = u.isVerified();
        this.url = u.getURL();
        this.createdDate = u.getCreatedAt().toString();
    }

    /*
     * Various getters and setters
     */
    public long getPersonId() { return personId; }
    public String getHandle() { return handle; }
    public String getPersonName() { return personName; }
    public String getLocation() { return location; }
    public boolean getVerified() { return verified; }
    public String getURL() { return url; }
    public String getCreatedDate() { return createdDate; }

    public String toString() {
        return personId + "\t" + handle + "\t" + verified;
    }
}
