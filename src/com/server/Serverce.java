package com.server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.serverutils.ServerUtil;

public class Serverce implements Runnable{

	public int PORT=8888;
	public Serverce() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			ServerSocket serverSocket=new ServerSocket(PORT);
			while(true){
				System.out.print("listent--");
				Socket socket=serverSocket.accept();
				System.out.println("accept");
				try {
					DataOutputStream out= new DataOutputStream(socket.getOutputStream());
					DataInputStream in= new DataInputStream(socket.getInputStream());
					String str=in.readUTF();
					System.out.println("receive---->"+str);
					ServerUtil serverutl=new ServerUtil(str);
					String reply=serverutl.MessageSolve();
					System.out.println("reply--->"+reply);
					out.writeUTF(reply);
					out.close();
					in.close();
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("�������쳣��"+e.getMessage());
				}finally{
					socket.close();
					System.out.println("close");
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	
	

	public static void main(String[] args) throws ParseException, IOException, SQLException {
		// TODO Auto-generated method stub
		//���������߳�
		Thread scuServerThread=new Thread(new Serverce());
		scuServerThread.start();
		
		//DataInputStream in=null;
		
		//String msg="<#LOGIN_INFO#>moon|sea";
		//String msg="<#LOGIN_INFO#>Moon|sea";
		//String msg="<#STORE_QG#>1|�����9������|��������|2013-09-24 15:26:12";
		//String msg="<#STORE_QG#>1|change|go to do it|2013-09-24 15:26:12";
		//String msg="<#GET_QGT#>0|3";
		//String msg="<#GET_QG#>1";
		//String msg="<#DEL_QG#>2";
		
		//String msg="<#STORE_JZ#>1|�����һ�ÿ�|��͢��|��ѧ�������ͬ|2013-09-24 15:26:12";
		//String msg="<#STORE_JZ#>1|ѧ��ı�|��͢��|˼ά�ռ�|���ϸı䣬���߱�˼��|2013-09-24 20:51:12";
		//String msg="<#STORE_JZ#>1|���һƴ|��͢��|ֻҪ����ڣ����ź�|2013-09-24 20:53:12";
		//String msg="<#STORE_JZ#>1|���һƴ|��͢��|ֻҪ����ڣ����ź�|2013-09-24 20:53:12";
		//String msg="<#GET_JZ#>2";
		//String msg="<#GET_JZT#>1|3";
		//String msg="<#DEL_JZ#>4";
		
		//String msg="<#STORE_LF#>0|1143041137|һ��ͨ|��ϵ��ʽ";
		//String msg="<#STORE_LF#>0|1143041137|һ��ͨ|��ϵ��ʽ";
		//String msg="<#GET_LF#>1";
		//String msg="<#GET_LFT#>0|1";
		//String msg="<#DEL_LF#>2";
		
		//String msg = "<#STORE_COM#>5|1143041137|�ҽ��������ڽ��Ҽ�һ��Կ�ף�Ӧ������ģ�������ϵ��û�˽ӵ绰��������Ϣ��������ϵ���绰��10086";
		//String msg = "<#STORE_COM#>5|1143041137|лл����ͬѧ��";
		//String msg = "<#GET_COM#>5";
		
		//ServerUtil su=new ServerUtil(msg);
		//String s=su.MessageSolve();
		
		//s=s.substring(15);
		//String[]m=s.split("\\|");
		//System.out.println("\n"+s);
		
		//java.util.Date now = new java.util.Date();
		//SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		//String nowdate = dateFormat.format(now);
		//System.out.println(now.getYear()+"-"+now.getMonth()+"-"+now.getDate());
	}
}
