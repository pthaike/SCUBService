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
					System.out.println("服务器异常："+e.getMessage());
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
		//创建监听线程
		Thread scuServerThread=new Thread(new Serverce());
		scuServerThread.start();
		
		//DataInputStream in=null;
		
		//String msg="<#LOGIN_INFO#>moon|sea";
		//String msg="<#LOGIN_INFO#>Moon|sea";
		//String msg="<#STORE_QG#>1|计算机9班招新|快来快来|2013-09-24 15:26:12";
		//String msg="<#STORE_QG#>1|change|go to do it|2013-09-24 15:26:12";
		//String msg="<#GET_QGT#>0|3";
		//String msg="<#GET_QG#>1";
		//String msg="<#DEL_QG#>2";
		
		//String msg="<#STORE_JZ#>1|川大第一堂课|庞廷海|大学因你而不同|2013-09-24 15:26:12";
		//String msg="<#STORE_JZ#>1|学会改变|庞廷海|思维空间|不断改变，边走边思考|2013-09-24 20:51:12";
		//String msg="<#STORE_JZ#>1|最后一拼|庞廷海|只要不后悔，不遗憾|2013-09-24 20:53:12";
		//String msg="<#STORE_JZ#>1|最后一拼|庞廷海|只要不后悔，不遗憾|2013-09-24 20:53:12";
		//String msg="<#GET_JZ#>2";
		//String msg="<#GET_JZT#>1|3";
		//String msg="<#DEL_JZ#>4";
		
		//String msg="<#STORE_LF#>0|1143041137|一卡通|联系方式";
		//String msg="<#STORE_LF#>0|1143041137|一卡通|联系方式";
		//String msg="<#GET_LF#>1";
		//String msg="<#GET_LFT#>0|1";
		//String msg="<#DEL_LF#>2";
		
		//String msg = "<#STORE_COM#>5|1143041137|我今天中午在教室捡到一串钥匙，应该是你的，今天联系你没人接电话，看到消息请与我联系，电话：10086";
		//String msg = "<#STORE_COM#>5|1143041137|谢谢上面同学啦";
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
