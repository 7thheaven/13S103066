import java.io.*;
import java.net.*;

public class TCPFileServer {

    public static void main(String argv[]) throws Exception {
        String ClientSentence;  //从客户端得到的文件或文件夹名
        ServerSocket welcomeSocket = new ServerSocket(6789); //建立TCP套接字
        while (true) {
            Socket connectionSocket = welcomeSocket.accept();  //获得与本服务器连接的客户端的套接字
            BufferedReader infromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));//用来接收客户端数据的流
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());//用来向客户端输出数据的流
            ClientSentence = infromClient.readLine();  //从客户端接收文件或文件夹名
            File f = new File(ClientSentence);  //定义该文件或文件夹对象
            if(f.exists() && f.isDirectory()){  //如果该对象存在且为文件夹
                if(f.listFiles().length==0){   //如果文件夹为空
                	outToClient.writeBytes("No file in this dir.\n");  //告诉客户端文件夹为空 
                }else{ //文件夹不为空
                    File lookFile[]=f.listFiles();  //读取文件夹下所有文件及文件夹   
                    int i =f.listFiles().length;   
                    for(int j=0;j<i;j++){     //遍历
                        if(lookFile[j].isDirectory()){  //若为文件夹则告诉客户端为“Dir 文件夹名”   
                        	outToClient.writeBytes("Dir "+lookFile[j].getName()+" "); 
                        }   
                        else   //若为文件则告诉客户端“文件名”，可以依此区分文件和文件夹
                        	outToClient.writeBytes(lookFile[j].getName()+" ");  
                    }   
                }
                outToClient.writeBytes("\n");
            }
            else  //若不为文件夹
            try {
                RandomAccessFile in = new RandomAccessFile(ClientSentence,"r");  //定义读取文件内容的随机访问对象
                String s;
                String total = "";
                while ((s = in.readLine()) != null) {  //读取文件内容
                    total = total + s;
                }
                outToClient.writeBytes(total + '\n');  //传给客户端
                in.close();
            } 
            catch (Exception e) //文件不存在则告诉客户端
            {
                outToClient.writeBytes("File not exist.\n");
            }
        }
    }
}
