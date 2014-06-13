import java.io.*;
import java.net.*;
import java.util.*;
//http://localhost:6789/bnf.html  是测试用的HTML地址
class WebServer extends Thread{
	public Socket connectionSocket;//与本服务器连接的客户端的套接字，此处客户端即为浏览器
	public String requestMessageLine;//请求报文
	public String fileName; //请求页面文件名
	public WebServer(Socket a){  //构造函数
	connectionSocket=a;
	}
	public void run(){  //线程的主函数
	try{
	BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));//用来读取客户端输入的流
	DataOutputStream outToClient=new DataOutputStream(connectionSocket.getOutputStream());//用来向客户端输出的流
	requestMessageLine=inFromClient.readLine();//读取请求报文
	StringTokenizer tokenizedLine=new StringTokenizer(requestMessageLine);//用来分析报文的对象
	if(tokenizedLine.nextToken().equals("GET")){ //若请求方式为GET
	fileName=tokenizedLine.nextToken();//开始传递要请求的文件
	if(fileName.startsWith("/")==true)
	fileName=fileName.substring(1);
	File file=new File(fileName);
	int numOfBytes=(int) file.length();
	FileInputStream inFile=new FileInputStream(
			fileName);
			byte[] fileInBytes=new byte[numOfBytes];
			inFile.read(fileInBytes);
			outToClient.writeBytes(
			"HTTP/1.0 200 Document Follows\r\n");//响应报文
			if(fileName.endsWith(".jpg"))//各种文件类型的处理
				outToClient.writeBytes(
				"Content-Type:image /jpeg\r\n");
				if(fileName.endsWith(".gif"))
				outToClient.writeBytes(
				"Content-Type:image /gif\r\n");
				outToClient.writeBytes(
				"Content-Length:"+numOfBytes+"\r\n");
				outToClient.writeBytes("\r\n");//报头结束
				outToClient.write(fileInBytes,0,numOfBytes);//传文件内容
				}
				else System.out.println("Bad Request Message");  //若不是GET方式则报错
				}
				catch(Exception ex){
				System.out.print("ERROR:"+ex.toString());
				}
				try {
					connectionSocket.close(); //关闭套接字的连接
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	public static void main(String argv[]) throws Exception
	{
	ServerSocket listenSocket=new ServerSocket(6789);  //建立服务器TCP套接字
	while(true){
	Socket oldconnectionSocket=listenSocket.accept();  //获得与本服务器连接的客户端的套接字，此处客户端即为浏览器
	Thread a=new WebServer(oldconnectionSocket);  //对每一个连接新建一个线程来处理
	a.start();  //运行新建的线程
	}
	}
	}