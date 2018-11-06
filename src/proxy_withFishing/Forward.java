package proxy_withFishing;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Forward implements Runnable {

  private InputStream clientInput;
  private OutputStream proxyOutput;

  public Forward(InputStream clientInput, OutputStream proxyOutput) {
    this.clientInput = clientInput;
    this.proxyOutput = proxyOutput;
  }

  @Override
  public void run() {
    // TODO Auto-generated method stub

    try {
      while (true)
        if (clientInput.available() > 0)
          proxyOutput.write(clientInput.read());
    } catch (IOException e) {
      // TODO Auto-generated catch block
//      e.printStackTrace();
      System.out.println("输出流意外关闭");
    }
  }

}
