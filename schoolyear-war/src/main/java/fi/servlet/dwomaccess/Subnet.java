package fi.servlet.dwomaccess;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;


/**
 * Utility class, check ip subnet ranges.
 * @author wim
 *
 */
public class Subnet {

  public static boolean netMatchRange(String addrrange, String addr1) {
    if(addrrange == null || addrrange.isEmpty())
      return true;
    StringTokenizer st = new StringTokenizer(addrrange, ",");
    while (st.hasMoreTokens()) {
      String token = st.nextToken().trim();
      if (netMatch(token, addr1))
          return true;
    }
    return false;
  }
  
  
  public static boolean netMatch(String addr, String addr1)
  { //addr is subnet address and addr1 is ip address. Function will return true, if addr1 is within addr(subnet)

    String[] parts = addr.split("/");
    String ip = parts[0];
    int prefix;

    if (parts.length < 2) {
        prefix = 0;
    } else {
        prefix = Integer.parseInt(parts[1]);
        if(prefix == 0) return true; // explicit 0 bits matches always
    }

    Inet4Address a =null;
    Inet4Address a1 =null;
    try {
        a = (Inet4Address) InetAddress.getByName(ip); // IP4 only
        
        InetAddress ip4or6 = InetAddress.getByName(addr1);
        if (ip4or6 instanceof Inet4Address)
        	a1 = (Inet4Address) ip4or6;
        else
        	return false; // Inet6Address?? 
    } catch (UnknownHostException|ClassCastException e){
      return false;
    }

    byte[] b = a.getAddress();
    int ipInt = ((b[0] & 0xFF) << 24) |
                     ((b[1] & 0xFF) << 16) |
                     ((b[2] & 0xFF) << 8)  |
                     ((b[3] & 0xFF) << 0);

    byte[] b1 = a1.getAddress();
    int ipInt1 = ((b1[0] & 0xFF) << 24) |
                     ((b1[1] & 0xFF) << 16) |
                     ((b1[2] & 0xFF) << 8)  |
                     ((b1[3] & 0xFF) << 0);

    int mask = ~((1 << (32 - prefix)) - 1);

    return ((ipInt & mask) == (ipInt1 & mask));
}
  
  
}
