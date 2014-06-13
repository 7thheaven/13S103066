import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ftpudpclient extends JFrame
  implements ActionListener
{
  private JButton enter;
  private JButton back;
  private JTextField urlTxt;
  private JTextArea etxt;
  private JList docList;
  DefaultListModel listModel;
  private JPanel mypanel;
  private String dir;
  private DatagramSocket client;
  private String serverIP = "";
  private DatagramPacket initPacket;
  private int port;
  private InetAddress addr;

  public static void main(String[] args)
  {
    new ftpudpclient().setVisible(true);
  }

  public ftpudpclient()
  {
    initData();
    initFrame();
  }

  public void initFrame()
  {
    this.mypanel = new JPanel();
    this.enter = new JButton();
    this.back = new JButton();
    this.urlTxt = new JTextField();
    this.etxt = new JTextArea();
    this.listModel = new DefaultListModel();
    this.docList = new JList(this.listModel);
    this.urlTxt.setBounds(10, 10, 400, 15);
    this.urlTxt.setPreferredSize(new Dimension(400, 30));

    this.enter.setBounds(420, 10, 50, 15);
    this.enter.setText("conn");
    this.enter.addActionListener(this);

    this.back.setBounds(480, 10, 50, 15);
    this.back.setText("back");
    this.back.addActionListener(this);

    this.etxt.setBounds(10, 35, 520, 400);
    this.etxt.setPreferredSize(new Dimension(520, 400));

    this.docList.setBounds(10, 35, 520, 400);
    this.docList.setPreferredSize(new Dimension(520, 400));
    this.docList.addMouseListener(new MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        if ((ftpudpclient.this.docList.getSelectedIndex() != -1) &&
          (e.getClickCount() == 2))
          ftpudpclient.this.listDoubleClickAction();
      }
    });
    this.mypanel.add(this.urlTxt);
    this.mypanel.add(this.enter);
    this.mypanel.add(this.back);

    this.docList.setVisible(false);
    this.etxt.setVisible(false);

    this.mypanel.add(this.docList);
    this.mypanel.add(this.etxt);
    add(this.mypanel);
    setDefaultCloseOperation(3);
    setSize(560, 500);
  }

  public void initData()
  {
    this.port = 5050;
    this.dir = "/";
    try {
      this.client = new DatagramSocket();
    }
    catch (SocketException e) {
      e.printStackTrace();
    }
  }

  public void UDPSend(String sendStr)
  {
    try
    {
      byte[] sendBuf = sendStr.getBytes();
      DatagramPacket sendPacket =
        new DatagramPacket(sendBuf, sendBuf.length, this.addr, this.port);
      this.client.send(sendPacket);
      System.out.println("?");
    }
    catch (SocketException e) {
      e.printStackTrace();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String UDPRead()
  {
    String reciStr = null;
    try {
      byte[] recvBuf = new byte[1024];
      DatagramPacket recvPacket =
        new DatagramPacket(recvBuf, recvBuf.length);
      this.client.receive(recvPacket);
      reciStr = new String(recvPacket.getData(), 0, recvPacket.getLength());
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
    return reciStr;
  }

  public void actionPerformed(ActionEvent e)
  {
    if (e.getSource() == this.enter)
      enterAction();
    else if (e.getSource() == this.back)
      backAction();
  }

  public void enterAction()
  {
    try {
      String rec = null;

      this.serverIP = this.urlTxt.getText();
      this.addr = InetAddress.getByName(this.serverIP);

      UDPSend("hello");
      rec = UDPRead();
      this.port = Integer.parseInt(rec);

      UDPSend("USER annoymouce");
      rec = UDPRead();
      System.out.println(rec);

      UDPSend("PASS xxx@xxx.com");
      rec = UDPRead();
      System.out.println(rec);

      UDPSend("TYPE I");
      rec = UDPRead();
      System.out.println(rec);

      UDPSend("PASV");
      rec = UDPRead();
      System.out.println(rec);

      UDPSend("CWD /");
      rec = UDPRead();
      System.out.println(rec);

      commLIST();
    }
    catch (UnknownHostException e)
    {
      e.printStackTrace();
    }
  }

  public void backAction()
  {
    if (this.dir.equals("/")) {
      return;
    }

    for (int i = this.dir.length() - 2; i >= 0; i--)
    {
      if (this.dir.charAt(i) == '/')
        break;
    }
    if (i + 1 > 0)
      this.dir = this.dir.substring(0, i + 1);
    else {
      this.dir = "/";
    }

    UDPSend("CWD " + this.dir);
    String str = UDPRead();
    commLIST();
    this.urlTxt.setText(this.addr.toString().substring(0) + this.dir);
  }

  public void listDoubleClickAction()
  {
    String name = (String)this.listModel.get(this.docList.getSelectedIndex());

    if (name.endsWith("/"))
    {
      UDPSend("CWD " + this.dir + name);
      String str = UDPRead();
      this.dir += name;
      commLIST();
    }
    else
    {
      UDPSend("RETR " + name);
      this.dir += name;
      String str = UDPRead();
      str = UDPRead();
      this.docList.setVisible(false);
      this.etxt.setText(str);
      this.etxt.setVisible(true);
      str = UDPRead();
    }
    this.urlTxt.setText(this.addr.toString().substring(0) + this.dir);
  }

  public void commLIST()
  {
    UDPSend("LIST");
    String rec = UDPRead();
    System.out.println(rec);
    rec = UDPRead();
    System.out.println(rec);
    String[] items = rec.split("\r\n");
    this.listModel.clear();
    for (int i = 0; i < items.length; i++)
    {
      if (items[i].startsWith("d"))
      {
        this.listModel.addElement(items[i].substring(2) + "/");
      }
      else
      {
        this.listModel.addElement(items[i].substring(2));
      }
    }
    this.etxt.setVisible(false);
    this.docList.setVisible(true);

    rec = UDPRead();
    System.out.println(rec);
  }
}
