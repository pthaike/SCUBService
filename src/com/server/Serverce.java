package com.server;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.text.ParseException;

import com.jdbc.dbutils.jdbcUtils;
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
					String reply=serverutl.MessageSolve(in);
					System.out.println("reply--->"+reply);
					out.writeUTF(reply);
					out.close();
					in.close();
				} catch (Exception e) {
					// TODO: handle exception
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
		//String msg="<#LOGIN_INFO#>MOON|sea";
		//String msg="<#STORE_QG#>1|�����9������|��������|2013-09-24 15:26:12";
		//String msg="<#STORE_JWC#>1|�����һ�ÿ�|��ѧ�������ͬ|2013-09-24 15:26:12";
		//String msg="<#STORE_JZ#>1|ѧ��ı�|��͢��|���ϸı䣬���߱�˼��|2013-09-24 20:51:12";
		//String msg="<#STORE_JZ#>1|���һƴ|��͢��|ֻҪ����ڣ����ź�|2013-09-24 20:53:12";
		//String msg="<#GET_JWCT#>0|3";
		//String msg="<#STORE_LOSTFOUND#>1|0|һ��ͨ|��ϵfangshi";
		//String msg="<#DEL_LF#>2";
		//String msg="<#GET_SINGLEINFO#>3";
		//String msg="<#GET_JZ#>2";
		//String msg="<#DEL_INFO#>4";
		//ServerUtil su=new ServerUtil(msg);
		//String s=su.MessageSolve(in);
		//s=s.substring(15);
		//String[]m=s.split("\\|");
		//System.out.println(s);
	}
}
