import java.util.logging.Filter;
import java.util.logging.LogRecord;

public class MalletLogFilter implements Filter {
  public boolean isLoggable( LogRecord record ) {
      String message = record.getMessage();
      //if ( message.startsWith( "CRF about to train with" ) ) return false;
      if ( message.startsWith( "CRF finished one iteration of maximizer" ) ) return false;
      if ( message.startsWith( "getValue() (" ) ) return false;
      return true;
  }
}
