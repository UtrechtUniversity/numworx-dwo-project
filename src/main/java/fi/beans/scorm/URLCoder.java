package fi.beans.scorm;

/**
* With this class you can encode and decode a String containing an URL.<BR>
* <B>Class overview</B><BR>
* encode - changes all non Character.isJavaIdentifierPart characters into % followed by two hex
* numbers representing that character<BR>
* decode - changes all % followed by two hex into the character represented by the hex numbers<BR>
* @author Bastiaan Grutters
* @author Alexander Elias
*/
public abstract class URLCoder
{
  /**
  * @param s String containing the text to encode.
  * @return String containing the encoded text.
  */
  public static String encode( String s )
  {
    String ret = "",
           tmp;

    for( int p = 0; p < s.length(); p ++ )
    {
      if( !Character.isJavaIdentifierPart( s.charAt( p ) ) )
      {
        tmp = Integer.toHexString( ( int ) s.charAt( p ) );
        if( tmp.length() == 1 ) tmp = "%0" + tmp;
        else tmp = "%" + tmp;
        ret += tmp;
      }
      else
      {
        ret += s.charAt( p );
      }
    }

    return ret;
  }

  /**
  * @param s String containing the encoded text.
  * @return String containing the decoded text.
  */
  public static String decode( String s )
  {
    String ret = "";
    int hex = 0;
    try
    {
      for( int p = 0; p < s.length(); p ++ )
      {
        if( s.charAt( p ) == '%' )
        {
          ret += ( char ) Integer.parseInt( s.substring( p + 1, p + 3 ), 16 );
          hex = 2;
        }
        else
        {
          if( hex > 0 ) hex --;
          else ret += s.charAt( p );
        }
      }
    } catch ( Exception e )
    {
      return s;
    }

    return ret;
  }

}