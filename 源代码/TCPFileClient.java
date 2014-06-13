import java.io.*;
import java.net.*;
public class TCPFileClient {

    public static void main(String argv[]) throws Exception {
        String sentence;  //�û�������ַ���
        String modifiedSentence;  //�ӷ������õ����ַ���
        Socket clientSocket = new Socket("127.0.0.1", 6789);//����TCP�׽���
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());//������������������
        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); //������ȡ�û��������
        BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); //������÷������������
        System.out.println("Please Enter File Name:");  //��ʾ�û������ļ����ļ���
        sentence = inFromUser.readLine();  //��ȡ�û�����
        outToServer.writeBytes(sentence+"\n");  //���䴫��������
        modifiedSentence = inFromServer.readLine(); //�ӷ������õ��ļ����ļ�������
        System.out.println("FROM SERVER:" + modifiedSentence); //��ʾ���û�
        File f = new File(sentence);
        RandomAccessFile out = new RandomAccessFile(f,"rw");
        out.writeBytes(modifiedSentence);
        out.close();
        clientSocket.close();  //�ر��׽���
    }
}
