package proxy_withFishing;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

public class Proxy {
  static HashSet<String> userblocked = new HashSet<>();
  static HashSet<String> urlblocked = new HashSet<>();
  static HashMap<String, Data> buffer = new HashMap<>();

  public static void main(String[] args) throws IOException {
    ServerSocket proxy = null;
    Scanner in = new Scanner(System.in);
    System.out.println(
        "Type in the user that should be blocked, use ';' to separate:(in format like X:X:X:X:X:X:X:X)");

    String temp1 = in.nextLine();
    int i;
    String[] temp2 = temp1.split(";");
    int length = temp2.length;
    for (i = 0; i < length; i++)
      if(!temp2[i].equals(""))
        userblocked.add(temp2[i]);

    System.out.println("Type in the url that should be blocked, use ';' to separate:");
    String temp3 = in.nextLine();
    String temp4[] = temp3.split(";");
    length = temp4.length;
    for (i = 0; i < length; i++)
      if(!temp4[i].equals(""))
        urlblocked.add(temp4[i]);

    in.close();
    System.out.println("Settings done. Now starting Proxy.");

    // TODO Auto-generated method stub
    try {
      proxy = new ServerSocket(8080);
      while (true) {
        Socket temp = proxy.accept();
        if (temp != null) {
          SocketHandler handler = new SocketHandler(temp);
          Thread thread = new Thread(handler);
          thread.start();
        }
      }
    } catch (Exception e) {
      System.out.println("初始化代理出现错误");
      e.printStackTrace();
    }
  }
}
