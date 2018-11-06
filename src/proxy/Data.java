package proxy;

import java.util.Date;

public class Data {
  private byte[] data;
  private Date date;
  
  public Data(Date date,byte[] data) {
    this.data=data;
    this.date=date;
  }
  
  public Date getDate() {
    return date;
  }
  
  public byte[] getData() {
    return data;
  }
  
}
