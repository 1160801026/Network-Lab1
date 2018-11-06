package proxy_withBuffer;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Head {
  private String head;
  private String host;
  private int port;
  private String type;
  private Date date;

  public Head(String head) throws ParseException {
    this.head = head;
    String dest = null;
    type = head.split(" ")[0];
    String[] temp = head.split("\n");
    for (String s : temp) {
      if (s.contains("Host:")) {
        dest = s;
        break;
      }
      // Sun, 21 Oct 2018 20:12:54 GMT
      DateFormat df = new SimpleDateFormat("E, dd MMM YYYY hh:mm:ss z");
      if (s.contains("Date:")) {
        date = df.parse(s);
      }
    }
    if (dest != null) {
      String info = dest.split(" ")[1];
      host = info.split(":|\r")[0];
      port = 80;
      if (info.contains(":")) {
        port = Integer.valueOf(info.split(":|\r")[1]);
      }
    }
  }

  public String getHead() {
    return head;
  }

  public String getHost() {
    return host;
  }

  public String getType() {
    return type;
  }

  public int getPort() {
    return port;
  }
  
  public Date getDate() {
    return date;
  }
}
