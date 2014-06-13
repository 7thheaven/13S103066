import java.io.*;
import java.net.*;
public class TCPFileClient {

    public static void main(String argv[]) throws Exception {
        String sentence;  //用户输入的字符串
        String modifiedSentence;  //从服务器得到的字符串
        Socket clientSocket = new Socket("127.0.0.1", 6789);//建立TCP套接字
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());//用来向服务器输出的流
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); //用来读取用户输入的流
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //用来获得服务器输入的流
        System.out.println("Please Enter File Name:");  //提示用户输入文件或文件夹
        sentence = inFromUser.readLine();  //读取用户输入
        outToServer.writeBytes(sentence+"\n");  //将其传给服务器
        modifiedSentence = inFromServer.readLine(); //从服务器得到文件或文件夹内容
        System.out.println("FROM SERVER:" + modifiedSentence); //显示给用户
        File f = new File(sentence);
        RandomAccessFile out = new RandomAccessFile(f,"rw");
        out.writeBytes(modifiedSentence);
        out.close();
        clientSocket.close();  //关闭套接字
    }
}
