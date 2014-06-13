import java.io.*;
import java.net.*;
import java.util.*;
//http://localhost:6789/bnf.html  �ǲ����õ�HTML��ַ
class WebServer extends Thread{
	public Socket connectionSocket;//�뱾���������ӵĿͻ��˵��׽��֣��˴��ͻ��˼�Ϊ�����
	public String requestMessageLine;//������
	public String fileName; //����ҳ���ļ���
	public WebServer(Socket a){  //���캯��
	connectionSocket=a;
	}
	public void run(){  //�̵߳�������
	try{
	BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));//������ȡ�ͻ����������
	DataOutputStream outToClient=new DataOutputStream(connectionSocket.getOutputStream());//������ͻ����������
	requestMessageLine=inFromClient.readLine();//��ȡ������
	StringTokenizer tokenizedLine=new StringTokenizer(requestMessageLine);//�����������ĵĶ���
	if(tokenizedLine.nextToken().equals("GET")){ //������ʽΪGET
	fileName=tokenizedLine.nextToken();//��ʼ����Ҫ������ļ�
	if(fileName.startsWith("/")==true)
	fileName=fileName.substring(1);
	File file=new File(fileName);
	int numOfBytes=(int) file.length();
	FileInputStream inFile=new FileInputStream(
			fileName);
			byte[] fileInBytes=new byte[numOfBytes];
			inFile.read(fileInBytes);
			outToClient.writeBytes(
			"HTTP/1.0 200 Document Follows\r\n");//��Ӧ����
			if(fileName.endsWith(".jpg"))//�����ļ����͵Ĵ���
				outToClient.writeBytes(
				"Content-Type:image /jpeg\r\n");
				if(fileName.endsWith(".gif"))
				outToClient.writeBytes(
				"Content-Type:image /gif\r\n");
				outToClient.writeBytes(
				"Content-Length:"+numOfBytes+"\r\n");
				outToClient.writeBytes("\r\n");//��ͷ����
				outToClient.write(fileInBytes,0,numOfBytes);//���ļ�����
				}
				else System.out.println("Bad Request Message");  //������GET��ʽ�򱨴�
				}
				catch(Exception ex){
				System.out.print("ERROR:"+ex.toString());
				}
				try {
					connectionSocket.close(); //�ر��׽��ֵ�����
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	}
	public static void main(String argv[]) throws Exception
	{
	ServerSocket listenSocket=new ServerSocket(6789);  //����������TCP�׽���
	while(true){
	Socket oldconnectionSocket=listenSocket.accept();  //����뱾���������ӵĿͻ��˵��׽��֣��˴��ͻ��˼�Ϊ�����
	Thread a=new WebServer(oldconnectionSocket);  //��ÿһ�������½�һ���߳�������
	a.start();  //�����½����߳�
	}
	}
	}