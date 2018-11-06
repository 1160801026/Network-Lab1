package proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SocketHandler implements Runnable {
  private Socket socket; // ���ӿͻ���
  private OutputStream clientOutput = null;
  private InputStream clientInput = null;
  private Socket proxySocket = null;
  private InputStream proxyInput = null;
  private OutputStream proxyOutput = null;

  public SocketHandler(Socket socket) {
    this.socket = socket;
  }

  @Override
  public void run() {
    // System.out.println(socket.getInetAddress().getHostAddress());
    if (Proxy.userblocked.contains(socket.getInetAddress().getHostAddress())) {
      System.out.println("The user has been blocked");
      return;
    }
    try { // ��������host��port
      byte[] clientinput = new byte[4096]; // �û������������
      clientInput = socket.getInputStream();
      clientOutput = socket.getOutputStream();
      clientInput.read(clientinput);
      String headtext = new String(clientinput);
      if (headtext != "") {
        Head head = new Head(headtext);

        if (Proxy.buffer.containsKey(headtext)) {
          System.out.println("���ڻ��棬�鿴�����Ƿ�Ϊ����");
          proxyOutput.write((("If-Modified-Since: " + (Proxy.buffer.get(headtext)).getDate())+headtext).getBytes());
          byte[] temp = new byte[4096];
          proxyInput.read(temp);
          if (new String(temp).contains("Not Modified")) {
            clientOutput.write(Proxy.buffer.get(headtext).getData());
            System.out.println("ʹ�û���");
          }
          else {
            clientOutput.write(temp);
            DateFormat df = new SimpleDateFormat("E, dd MMM YYYY hh:mm:ss z");
            Date date = df.parse(new Date().toString());
            Proxy.buffer.replace(headtext, new Data(date, temp)); // ���»���
          }
        } else { // ������û��
          // String type = head.split(" ")[0];
          // String[] temp = head.split("\n");
          // for (String s : temp) {
          // if (s.contains("Host:")) {
          // dest = s;
          // break;
          // }
          // // if (s.contains("Date:")) {
          // // date = new Date(Date.parse(s));
          // // }
          // }
          // if (dest != null) {
          // String info = dest.split(" ")[1];
          // String host = info.split(":|\r")[0];
          // int port = 80;
          // if (info.contains(":")) {
          // port = Integer.valueOf(info.split(":|\r")[1]);
          // }

          // String host = head.getHost();
          // int port = head.getPort();
          // proxySocket = new Socket(host, port);
          // if (proxySocket != null) {
          // proxyOutput = proxySocket.getOutputStream();
          // proxyInput = proxySocket.getInputStream();
          String host = head.getHost();
          if(host!=null) {
          boolean isblocked=false;
          for(String s:Proxy.urlblocked) {
            if(host.contains(s)) {
              isblocked=true;
            }
          }
          if (isblocked) {
            System.out.println("The host "+host+" has been blocked");
            return;
          }
          int port=head.getPort();
          
          if (head.getType().equalsIgnoreCase("CONNECT")) {// https�Ƚ������
             clientOutput.write("HTTP/1.1 200 Connection Established\r\n\r\n".getBytes());
             clientOutput.flush();

             proxySocket = new Socket(host, 443);
             if (proxySocket != null) {
               proxyOutput = proxySocket.getOutputStream();
               proxyInput = proxySocket.getInputStream();

//               System.out.println("׼����ʼ����" + host + ":" + port);
//               proxyOutput.write(head.getHead().getBytes());
//               System.out.println("�����ѷ���");

//               while (true) {
//                 int flag = 0;
//                 if (proxyInput.available() > 0) {
//                   clientOutput.write(proxyInput.read());
//                   flag = 1;
//                 }
//                 if (flag == 1 && proxyInput.available() == 0) {
//                   break;
//                 }
//               }
            // System.out.println("׼����ʼ����" + host + ":" + port);
            // proxyOutput.write(head.getHead().getBytes());
            // System.out.println("�����ѷ���");
            //
            // // HTTPS���ӵ����ֹ���
             Forward forward = new Forward(clientInput, proxyOutput);
             Thread thread = new Thread(forward);
             thread.start();
             
             while (true) {
               clientOutput.write(proxyInput.read());
             }
           }
            //
            // while (true) {
            // int flag = 0;
            // if (proxyInput.available() > 0) {
            // // System.out.println(proxyInput.available());
            // clientOutput.write(proxyInput.read());
            // flag = 1;
            // }
            // if (flag == 1 && proxyInput.available() == 0) {
            // break;
            // }
            // }

            // ������ӵľͲ�������
          } else {
//            String host = head.getHost();
//            if (Proxy.urlblocked.contains(host)) {
//              System.out.println("The host has been blocked");
//              return;
//            }
//            int port = head.getPort();
            proxySocket = new Socket(host, port);
            if (proxySocket != null) {
              proxyOutput = proxySocket.getOutputStream();
              proxyInput = proxySocket.getInputStream();

              System.out.println("׼����ʼ����" + host + ":" + port);
              proxyOutput.write(head.getHead().getBytes());
              System.out.println("�����ѷ���");

              while (true) {
                int flag = 0;
                if (proxyInput.available() > 0) {
                  clientOutput.write(proxyInput.read());
                  flag = 1;
                }
                if (flag == 1 && proxyInput.available() == 0) {
                  break;
                }
              }
             
            }
          }
        }
      }
    }} catch (IOException e) {
      System.out.println("������ֹ���������");
      return;
      // e.printStackTrace();
      // this.run();
    } catch (ParseException e) {
      // TODO Auto-generated catch block
      System.out.println("Http�����������");
      e.printStackTrace();
      return;
    }finally {
      if (proxyInput != null) {
        try {
            proxyOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    if (proxyOutput != null) {
        try {
            proxyOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    if (proxySocket != null) {
        try {
            proxySocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    if (clientInput != null) {
        try {
            clientInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    if (clientOutput != null) {
        try {
            clientOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    if (socket != null) {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

  }
  }
 }

