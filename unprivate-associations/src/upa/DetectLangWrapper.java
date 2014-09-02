package upa;

import java.sql.SQLException;
import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

/**
 * Wrapper code for the language detection code.
 * 
 * @author Jonathan Walsh (jdw74@drexel.edu)
 * 
 */
public class DetectLangWrapper {

    public static final String PROFILE_DIRECTORY = "etc/lang_profiles";
    public static final String LANG_ENGLISH = "en";

    private static boolean initialized = false;

    public static int isEnglish( String text ) {

        int ret = -1;
        try {
            if ( !initialized ) {
                System.out.println( "INFO: Initializing Language Detector Factory!" );
                DetectorFactory.loadProfile(PROFILE_DIRECTORY);
                initialized = true;
            }
            
            Detector detector = DetectorFactory.create();
            detector.append( text );
            String lang = detector.detect();
            if ( lang.equals( LANG_ENGLISH ) ) ret = 1;
            else ret = 0;

            //  System.out.println( "\nI detected my language as: "+ lang );
            // System.out.println( "For tweet " + text + "\n\n" );
        } catch ( LangDetectException lde ) {
            System.out.println( "ERROR: Failed in language detection! " +
                                lde.getMessage() );
        }
        return ret;
    }
 
    

       
}