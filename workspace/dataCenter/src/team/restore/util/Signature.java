package team.restore.util;

import org.apache.hadoop.io.MD5Hash;

public class Signature {
	public static byte[] calculate(WebPage web) {
	    byte[] data = web.getContent().array();
	    return MD5Hash.digest(data).getDigest();
	  }
	public static int signatureComparator(byte[] data1, byte[] data2) {
		if (data1 == null && data2 == null) return 0;
		if (data1 == null) return -1;
		if (data2 == null) return 1;
		if (data2.length > data1.length) return -1;
	    if (data2.length < data1.length) return 1;
	    int res = 0;
	    for (int i = 0; i < data1.length; i++) {
	      res = (data1[i] - data2[i]);
	      if (res != 0) return res;
	    }
	    return 0;
	}

}
