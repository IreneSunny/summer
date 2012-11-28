package team.restore.tool;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

public class TypeConversion {
	public static String decode(ByteBuffer buffer) {
        //System.out.println("buffer=" + buffer);
        Charset charset = null;
        CharsetDecoder decoder = null;
        CharBuffer charBuffer = null;
        try {
            charset = Charset.forName("utf-8");
            decoder = charset.newDecoder();
            charBuffer = decoder.decode(buffer);
            //System.out.println("charBuffer=" + charBuffer);
            //System.out.println(charBuffer.toString());
            return charBuffer.toString();
        } catch (Exception ex) {
            return "";
        }
	}
	public static long bytes2long(byte[] b) {

		   int mask = 0xff;
		   int temp = 0;
		   int res = 0;
		   for (int i = 0; i < 8; i++) {
		    res <<= 8;
		    temp = b[i] & mask;
		    res |= temp;
		   }
		   return res;
		}

}
