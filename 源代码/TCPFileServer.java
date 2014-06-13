import java.io.*;
import java.net.*;

public class TCPFileServer {

    public static void main(String argv[]) throws Exception {
        String ClientSentence;  //�ӿͻ��˵õ����ļ����ļ�����
        ServerSocket welcomeSocket = new ServerSocket(6789); //����TCP�׽���
        while (true) {
            Socket connectionSocket = welcomeSocket.accept();  //����뱾���������ӵĿͻ��˵��׽���
            BufferedReader infromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));//�������տͻ������ݵ���
            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());//������ͻ���������ݵ���
            ClientSentence = infromClient.readLine();  //�ӿͻ��˽����ļ����ļ�����
            File f = new File(ClientSentence);  //������ļ����ļ��ж���
            if(f.exists() && f.isDirectory()){  //����ö��������Ϊ�ļ���
                if(f.listFiles().length==0){   //����ļ���Ϊ��
                	outToClient.writeBytes("No file in this dir.\n");  //���߿ͻ����ļ���Ϊ�� 
                }else{ //�ļ��в�Ϊ��
                    File lookFile[]=f.listFiles();  //��ȡ�ļ����������ļ����ļ���   
                    int i =f.listFiles().length;   
                    for(int j=0;j<i;j++){     //����
                        if(lookFile[j].isDirectory()){  //��Ϊ�ļ�������߿ͻ���Ϊ��Dir �ļ�������   
                        	outToClient.writeBytes("Dir "+lookFile[j].getName()+" "); 
                        }   
                        else   //��Ϊ�ļ�����߿ͻ��ˡ��ļ��������������������ļ����ļ���
                        	outToClient.writeBytes(lookFile[j].getName()+" ");  
                    }   
                }
                outToClient.writeBytes("\n");
            }
            else  //����Ϊ�ļ���
            try {
                RandomAccessFile in = new RandomAccessFile(ClientSentence,"r");  //�����ȡ�ļ����ݵ�������ʶ���
                String s;
                String total = "";
                while ((s = in.readLine()) != null) {  //��ȡ�ļ�����
                    total = total + s;
                }
                outToClient.writeBytes(total + '\n');  //�����ͻ���
                in.close();
            } 
            catch (Exception e) //�ļ�����������߿ͻ���
            {
                outToClient.writeBytes("File not exist.\n");
            }
        }
    }
}
