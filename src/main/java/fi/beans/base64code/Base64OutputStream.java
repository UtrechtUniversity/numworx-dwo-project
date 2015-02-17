package fi.beans.base64code;

import java.io.*;

public class Base64OutputStream extends FilterOutputStream
{	public Base64OutputStream(OutputStream out)
   	{  super(out);
   	}

@Override
	public void write(byte b[], int off, int len) throws IOException 
	{	if ((off | len | (b.length - (len + off)) | (off + len)) < 0)
	    throw new IndexOutOfBoundsException();

		for (int i = 0 ; i < len ; i++) 
		{	write(b[off + i]);
		}
    }
    
@Override
   	public void write(int c) throws IOException
   	{  inbuf[_i] = c;
      _i++;
      if (_i == 3)
      {  super.write(toBase64[(inbuf[0] & 0xFC) >> 2]);
         super.write(toBase64[((inbuf[0] & 0x03) << 4) |
            ((inbuf[1] & 0xF0) >> 4)]);
         super.write(toBase64[((inbuf[1] & 0x0F) << 2) |
            ((inbuf[2] & 0xC0) >> 6)]);
         super.write(toBase64[inbuf[2] & 0x3F]);
         col += 4;
         _i = 0;
         if (col >= 76)
         {  //super.write('\n');
            col = 0;
         }
      }
   }

   private void flush0() throws IOException
   {  if (_i == 1)
      {  super.write(toBase64[(inbuf[0] & 0xFC) >> 2]);
         super.write(toBase64[(inbuf[0] & 0x03) << 4]);
         super.write('=');
         super.write('=');
      }
      else if (_i == 2)
      {  super.write(toBase64[(inbuf[0] & 0xFC) >> 2]);
         super.write(toBase64[((inbuf[0] & 0x03) << 4) |
            ((inbuf[1] & 0xF0) >> 4)]);
         super.write(toBase64[(inbuf[1] & 0x0F) << 2]);
         super.write('=');
      }
      _i = 0;
   }
   
   public void finish() throws IOException
   {
	   flush0();
	   flush();
   }
   

   private static final char[] toBase64 =
   {  'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
      'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
      'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
      'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f',
      'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
      'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
      'w', 'x', 'y', 'z', '0', '1', '2', '3',
      '4', '5', '6', '7', '8', '9', '+', '/'
   };

   private int col = 0;
   private int _i = 0;
   private final int[] inbuf = new int[3];
/* (non-Javadoc)
 * @see java.io.FilterOutputStream#close()
 */
@Override
public void close() throws IOException {
	flush0();
	super.close();
}
}
