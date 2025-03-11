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
        if(prefix == 0)
        {
            try {
                InetAddress a46 = InetAddress.getByName(ip);
                InetAddress a461 = InetAddress.getByName(addr1);
                return a46.getClass() == a461.getClass(); // explicit 0 bits matches always same type
            } catch(UnknownHostException|ClassCastException e) { 
                return false;
            }
        }
    }
    boolean ip6 = false;
    Inet4Address a =null;
    Inet4Address a1 =null;
    Inet6Address a6 = null;
    Inet6Address a61 = null;
    
    try {
        InetAddress a46 = InetAddress.getByName(ip);
        InetAddress a461 = InetAddress.getByName(addr1);
        ip6 = a46 instanceof Inet6Address;
        if (ip6) {
            a6 = (Inet6Address) a46;
            a61 = (Inet6Address) a461;
            return netMatch6(a6, a61, prefix);
        } else {
        	a = (Inet4Address) a46;
        	a1 = (Inet4Address) a461;
            return netMatch4(a, a1, prefix);
        }
    } catch (UnknownHostException|ClassCastException e){ // soms ook Inet6Address, nog geen support voor nodig
      return false;
    }

}

private static int[] masks = { 0xFF, 0x80, 0xC0, 0xE0, 0xF0, 0xF8, 0xFC, 0xFE };
  
  
/**
 * Match a classless subnet.
 * prefix 0 means all
 * @param a 1st address
 * @param a1 2nd address
 * @param prefix the prefix to match
 * @return result
 */
private static boolean netMatch6(InetAddress a, InetAddress a1, int prefix) {
	byte[] b = a.getAddress();
	byte[] b1 = a1.getAddress();
	int bytes = prefix == 0 ? b.length-1 : (prefix-1) / 8;
	int mask = masks[prefix%8];
	int i;
	for (i = 0; i < bytes; i++) {
		if (b[i] != b1[i]) return false;
	}	
	return (b[i] & mask) == (b1[i] & mask);
}


static boolean netMatch4(Inet4Address a, Inet4Address a1, int prefix) {
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
