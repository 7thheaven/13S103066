import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

public class ftpserver
{
  public ArrayList<ftpserver.FtpHandler> users = new ArrayList();
  public static int counter = 0;
  public static String initDir = "ftp/";

  public static void main(String[] args)
  {
    new ftpserver();
  }

  public ftpserver() {
    try {
      DatagramSocket server = new DatagramSocket(5050);
      byte[] recvBuf = new byte[1024];
      DatagramPacket recvPacket =
        new DatagramPacket(recvBuf, recvBuf.length);
      while (true) {
        server.receive(recvPacket);
        ftpserver.FtpHandler h = new ftpserver.FtpHandler(recvPacket);
        h.start();
      }
    }
    catch (IOException e1) {
      e1.printStackTrace(); }  }
  class FtpHandler extends Thread { String ServerIP = "192.168.1.107";
    DatagramSocket server;
    int port;
    InetAddress clientAddr = null;
    int id;
    String cmd = "";
    String param = "";
    String user;
    String remoteHost = " ";
    int remotePort = 0;
    String dir = "/";
    String rootdir = "E:/codes/FTPTest/ftp/";
    int state = 0;
    String reply;
    int type = 0;
    String requestfile = "";
    boolean isrest = false;
    int sendPort;
    InetAddress addr;

    public FtpHandler(DatagramPacket reciPacket) { this.dir = "/";
      this.sendPort = reciPacket.getPort();
      this.addr = reciPacket.getAddress();
      this.port = getPort();
      System.out.println(this.port);
      try {
        this.server = new DatagramSocket(this.port);
      }
      catch (SocketException e) {
        e.printStackTrace();
      }

      udpSend(this.port);
    }

    public void run()
    {
      String str = "";
      try
      {
        this.state = 0;
        boolean finished = false;
        while (!finished) {
          str = UdpRead();
          if (str == null) {
            finished = true;
          } else {
            int parseResult = parseInput(str);
            System.out.println("指令:" + this.cmd + " 参数:" + this.param);
            System.out.print("->");
            switch (this.state)
            {
            case 0:
              finished = commandUSER();
              break;
            case 1:
              finished = commandPASS();
              break;
            case 2:
              switch (parseResult)
              {
              case -1:
                errCMD();
                break;
              case 2:
                finished = commandPASV();
                break;
              case 3:
                finished = commandSYST();
                break;
              case 4:
                finished = commandCDUP();
                break;
              case 6:
                finished = commandCWD();
                break;
              case 7:
                finished = commandQUIT();
                break;
              case 9:
                finished = commandPORT();
                break;
              case 11:
                finished = commandTYPE();
                break;
              case 14:
                finished = commandRETR();
                break;
              case 15:
                finished = commandSTOR();
                break;
              case 22:
                finished = commandABOR();
                break;
              case 23:
                finished = commandDELE();
                break;
              case 25:
                finished = commandMKD();
                break;
              case 27:
                finished = commandLIST();
                break;
              case 26:
              case 33:
                finished = commandPWD();
                break;
              case 32:
                finished = commandNOOP();
              case 0:
              case 1:
              case 5:
              case 8:
              case 10:
              case 12:
              case 13:
              case 16:
              case 17:
              case 18:
              case 19:
              case 20:
              case 21:
              case 24:
              case 28:
              case 29:
              case 30:
              case 31: }  }  } System.out.println(this.reply);
          udpSend(this.reply);
        }
      }
      catch (Exception e) {
        System.out.println("connection reset!");
      }
    }

    void udpSend(String sendStr)
    {
      try
      {
        byte[] sendBuf = sendStr.getBytes();
        DatagramPacket sendPacket =
          new DatagramPacket(sendBuf, sendBuf.length, this.addr, this.sendPort);

        this.server.send(sendPacket);
      }
      catch (SocketException e) {
        e.printStackTrace();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }

    String UdpRead()
    {
      String reciStr = null;
      try {
        byte[] recvBuf = new byte[1024];
        DatagramPacket recvPacket =
          new DatagramPacket(recvBuf, recvBuf.length);
        this.server.receive(recvPacket);
        reciStr = new String(recvPacket.getData(), 0, recvPacket.getLength());
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
      return reciStr;
    }

    public int getPort() {
      DatagramSocket s = null;

      int MINPORT = 10000;
      int MAXPORT = 65000;

      while (MINPORT < MAXPORT)
      {
        try
        {
          s = new DatagramSocket(MINPORT);
          s.close();
          return MINPORT;
        }
        catch (IOException localIOException)
        {
          MINPORT++;
        }

      }

      return -1;
    }

    int parseInput(String s)
    {
      int p = 0;
      int i = -1;
      p = s.indexOf(" ");
      if (p == -1)
        this.cmd = s;
      else {
        this.cmd = s.substring(0, p);
      }
      if ((p >= s.length()) || (p == -1))
        this.param = "";
      else
        this.param = s.substring(p + 1, s.length());
      this.cmd = this.cmd.toUpperCase();

      if (this.cmd.equals("PASV"))
        i = 2;
      if (this.cmd.equals("SYST"))
        i = 3;
      if (this.cmd.equals("CDUP"))
        i = 4;
      if (this.cmd.equals("CWD"))
        i = 6;
      if (this.cmd.equals("QUIT"))
        i = 7;
      if (this.cmd.equals("PORT"))
        i = 9;
      if (this.cmd.equals("TYPE"))
        i = 11;
      if (this.cmd.equals("RETR"))
        i = 14;
      if (this.cmd.equals("STOR"))
        i = 15;
      if (this.cmd.equals("ABOR"))
        i = 22;
      if (this.cmd.equals("DELE"))
        i = 23;
      if (this.cmd.equals("MKD"))
        i = 25;
      if (this.cmd.equals("PWD"))
        i = 26;
      if (this.cmd.equals("LIST"))
        i = 27;
      if (this.cmd.equals("NOOP"))
        i = 32;
      if (this.cmd.equals("XPWD"))
        i = 33;
      return i;
    }

    int validatePath(String s)
    {
      File f = new File(s);
      if ((f.exists()) && (!f.isDirectory())) {
        String s1 = s.toLowerCase();
        String s2 = this.rootdir.toLowerCase();
        if (s1.startsWith(s2)) {
          return 1;
        }
        return 0;
      }
      f = new File(addTail(this.dir) + s);
      if ((f.exists()) && (!f.isDirectory())) {
        String s1 = (addTail(this.dir) + s).toLowerCase();
        String s2 = this.rootdir.toLowerCase();
        if (s1.startsWith(s2)) {
          return 2;
        }
        return 0;
      }
      return 0;
    }

    private boolean commandPASV() {
      this.reply = ("227 Entering Passive Mode (" + this.ServerIP + "," + this.port / 256 + "," + this.port % 256 + ").");
      return false;
    }

    private boolean commandSYST()
    {
      this.reply = "215 UNIX Type: L8";
      return false;
    }

    boolean commandUSER()
    {
      if (this.cmd.equals("USER")) {
        this.reply = "331 用户名正确,需要口令";
        this.user = this.param;
        this.state = 1;
        return false;
      }
      this.reply = "501 参数语法错误,用户名不匹配";
      return true;
    }

    boolean commandPASS()
    {
      if (this.cmd.equals("PASS")) {
        this.reply = "230 用户登录了";
        this.state = 2;
        System.out.println("新消息: 用户: " + this.param + " 来自于: " +
          this.remoteHost + "登录了");
        System.out.print("->");
        return false;
      }
      this.reply = "501 参数语法错误,密码不匹配";
      return true;
    }

    void errCMD()
    {
      this.reply = "500 语法错误";
    }

    boolean commandCDUP()
    {
      File f = new File(this.dir);
      if ((f.getParent() != null) && (!this.dir.equals(this.rootdir)))
      {
        this.dir = f.getParent();
        this.reply = "200 命令正确";
      } else {
        this.reply = "550 当前目录无父路径";
      }

      return false;
    }

    boolean commandCWD()
    {
      if (this.param.equals("/"))
        this.param = this.rootdir;
      else if (this.param.startsWith("/"))
        this.param = (this.rootdir + this.param.substring(1, this.param.length()));
      File f = new File(this.param);
      String s = "";
      String s1 = "";
      if (this.dir.endsWith("/"))
        s = this.dir;
      else
        s = this.dir + "/";
      File f1 = new File(s + this.param);

      if ((f.isDirectory()) && (f.exists())) {
        if ((this.param.equals("..")) || (this.param.equals("..\\"))) {
          if (this.dir.compareToIgnoreCase(this.rootdir) == 0) {
            this.reply = "550 此路径不存在";
          }
          else {
            s1 = new File(this.dir).getParent();
            if (s1 != null) {
              this.dir = s1;
              this.reply = ("250 请求的文件处理结束, 当前目录变为: " + this.dir);
            } else {
              this.reply = "550 此路径不存在";
            }
          }
        } else if ((!this.param.equals(".")) && (!this.param.equals(".\\")))
        {
          this.dir = this.param;
          this.reply = ("250 请求的文件处理结束, 工作路径变为 " + this.dir);
        }
      } else if ((f1.isDirectory()) && (f1.exists())) {
        this.dir = (s + this.param);
        this.reply = ("250 请求的文件处理结束, 工作路径变为 " + this.dir);
      } else {
        this.reply = "501 参数语法错误";
      }
      return false;
    }

    boolean commandQUIT() {
      this.reply = "221 服务关闭连接";
      return true;
    }

    boolean commandPORT()
    {
      int p1 = 0;
      int p2 = 0;
      int[] a = new int[6];
      int i = 0;
      try {
        while ((p2 = this.param.indexOf(",", p1)) != -1)
        {
          a[i] = Integer.parseInt(this.param.substring(p1, p2));
          p2++;
          p1 = p2;
          i++;
        }
        a[i] = Integer.parseInt(this.param.substring(p1, this.param.length()));
      } catch (NumberFormatException e) {
        this.reply = "501 参数语法错误";
        return false;
      }

      this.remoteHost = (a[0] + "." + a[1] + "." + a[2] + "." + a[3]);
      this.remotePort = (a[4] * 256 + a[5]);
      this.reply = "200 命令正确";
      return false;
    }

    boolean commandLIST()
    {
      udpSend("150 文件状态正常,ls以 ASCII 方式操作");

      File f = new File(this.dir);
      String[] dirStructure = f.list();

      String out = "";
      for (int i = 0; i < dirStructure.length; i++)
      {
        String fileType;
        String fileType;
        if (dirStructure[i].indexOf(".") != -1)
          fileType = "- ";
        else {
          fileType = "d ";
        }
        out = out + fileType + dirStructure[i] + "\r\n";
      }
      udpSend(out);
      this.reply = "226 传输数据连接结束";

      return false;
    }

    boolean commandTYPE()
    {
      if (this.param.equals("A")) {
        this.type = 0;
        this.reply = "200 命令正确 ,转 ASCII 模式";
      } else if (this.param.equals("I")) {
        this.type = 1;
        this.reply = "200 命令正确 转 BINARY 模式";
      } else {
        this.reply = "504 命令不能执行这种参数";
      }
      return false;
    }

    boolean commandRETR()
    {
      String fillname = this.dir;
      if (this.param.startsWith("/"))
        fillname = fillname + this.param.substring(1);
      else
        fillname = fillname + this.param;
      System.out.println(this.dir);
      System.out.println(addTail(this.dir.substring(1)));
      System.out.println(fillname);
      this.requestfile = fillname;
      File f = new File(this.requestfile);
      if (!f.exists()) {
        f = new File(fillname);
        if (!f.exists()) {
          this.reply = "550 文件不存在";
          return false;
        }
      }
      if (!f.isDirectory())
      {
        if (this.type == 1) {
          try
          {
            udpSend("150 文件状态正常,以二进治方式打开文件:  " +
              this.requestfile);
            BufferedInputStream fin = new BufferedInputStream(
              new FileInputStream(this.requestfile));
            byte[] buf = new byte[1024];
            int l = 0;
            String sendStr = "";
            while ((l = fin.read(buf, 0, 1024)) != -1)
            {
              sendStr = sendStr + new String(buf, 0, l);
            }
            udpSend(sendStr);
            fin.close();
            this.reply = "226 传输数据连接结束";
          }
          catch (Exception e) {
            e.printStackTrace();
            this.reply = "451 请求失败: 传输出故障";
            return false;
          }
        }

        if (this.type == 0) {
          try
          {
            udpSend("150 Opening ASCII mode data connection for " +
              this.requestfile);
            BufferedReader fin = new BufferedReader(
              new FileReader(this.requestfile));

            String sendStr = "";
            String s;
            while ((s = fin.readLine()) != null)
            {
              String s;
              sendStr = sendStr + s;
            }
            fin.close();
            udpSend(sendStr);
            this.reply = "226 传输数据连接结束";
          } catch (Exception e) {
            e.printStackTrace();
            this.reply = "451 请求失败: 传输出故障";
            return false;
          }
        }
      }
      return false;
    }

    boolean commandSTOR()
    {
      if (this.param.equals("")) {
        this.reply = "501 参数语法错误";
        return false;
      }
      this.requestfile = (addTail(this.dir) + this.param);
      if (this.type == 1) {
        try
        {
          udpSend("150 Opening Binary mode data connection for " +
            this.requestfile);

          BufferedOutputStream fout = new BufferedOutputStream(
            new FileOutputStream(this.requestfile));
          byte[] buf = new byte[1024];
          int l = 0;
          String tmp = null;
          while ((tmp = UdpRead()) != null) {
            fout.write(tmp.getBytes(), 0, tmp.getBytes().length);
          }
          fout.close();
          this.reply = "226 传输数据连接结束";
        } catch (Exception e) {
          e.printStackTrace();
          this.reply = "451 请求失败: 传输出故障";
          return false;
        }
      }
      if (this.type == 0) {
        try
        {
          udpSend("150 Opening ASCII mode data connection for " +
            this.requestfile);
          PrintWriter fout = new PrintWriter(
            new FileOutputStream(this.requestfile));
          String line;
          while ((line = UdpRead()) != null)
          {
            String line;
            fout.println(line);
          }
          fout.close();
          this.reply = "226 传输数据连接结束";
        } catch (Exception e) {
          e.printStackTrace();
          this.reply = "451 请求失败: 传输出故障";
          return false;
        }
      }
      return false;
    }

    boolean commandPWD() {
      this.reply = ("257 " + this.dir + " 是当前目录.");
      return false;
    }

    boolean commandNOOP() {
      this.reply = "200 命令正确.";
      return false;
    }

    boolean commandABOR()
    {
      this.reply = "421 服务不可用, 关闭数据传送连接";
      return false;
    }

    boolean commandDELE()
    {
      int i = validatePath(this.param);
      if (i == 0) {
        this.reply = "550 请求的动作未执行,文件不存在,或目录不对,或其他";
        return false;
      }
      if (i == 1) {
        File f = new File(this.param);
        f.delete();
      }
      if (i == 2) {
        File f = new File(addTail(this.dir) + this.param);
        f.delete();
      }

      this.reply = "250 请求的文件处理结束,成功删除服务器上文件";
      return false;
    }

    boolean commandMKD()
    {
      String s1 = this.param.toLowerCase();
      String s2 = this.rootdir.toLowerCase();
      if (s1.startsWith(s2)) {
        File f = new File(this.param);
        if (f.exists()) {
          this.reply = "550 请求的动作未执行,目录已存在";
          return false;
        }
        f.mkdirs();
        this.reply = "250 请求的文件处理结束, 目录建立";
      }
      else {
        File f = new File(addTail(this.dir) + this.param);
        if (f.exists()) {
          this.reply = "550 请求的动作未执行,目录已存在";
          return false;
        }
        f.mkdirs();
        this.reply = "250 请求的文件处理结束, 目录建立";
      }

      return false;
    }

    String addTail(String s) {
      if (!s.endsWith("/"))
        s = s + "/";
      return s;
    }
  }

  class FtpState
  {
    static final int FS_WAIT_LOGIN = 0;
    static final int FS_WAIT_PASS = 1;
    static final int FS_LOGIN = 2;
    static final int FTYPE_ASCII = 0;
    static final int FTYPE_IMAGE = 1;
    static final int FMODE_STREAM = 0;
    static final int FMODE_COMPRESSED = 1;
    static final int FSTRU_FILE = 0;
    static final int FSTRU_PAGE = 1;

    FtpState()
    {
    }
  }

  class UserInfo
  {
    String user;
    String password;
    String workDir;

    public UserInfo(String a, String b, String c)
    {
      this.user = a;
      this.password = b;
      this.workDir = c;
    }
  }
}
