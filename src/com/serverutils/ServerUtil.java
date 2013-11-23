package com.serverutils;


import java.io.DataInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;




import com.jdbc.dbutils.jdbcUtils;
import com.mysql.jdbc.PreparedStatement;

public class ServerUtil {

	String message=null; //传入的信息
	String result=null; //查询结果
	jdbcUtils jdbcu; //数据库连接
	Connection connection;
	
	public ServerUtil(String msg) {
		// TODO Auto-generated constructor stub
		message=msg;
		/*jdbcUtils jdbcu=new jdbcUtils();
		jdbcu.getConnection(); //连接数据库
*/	}
	
	public String MessageSolve(DataInputStream in) throws ParseException, IOException, SQLException{		
		jdbcu=new jdbcUtils();
		connection =jdbcu.getConnection(); //连接数据库		
		if(message.startsWith("<#LOGIN_INFO#>")){  
			//管理员登录信息,用户名，用户密码
			String msg=message.substring(14);	//提取信息
			if(Login(msg)){
				result="<#MANAGE_TRUE#>"; //管理员用户存在
			}
			else{
				result="<#MANAGE_FALSE#>";
			}
		}
		else if(message.startsWith("<#STORE_JWC#>")){
			//存储教务处信息
			String msg=message.substring(13);
			//1表示教务处信息,msg为存储的具体信息info_source_id,info_title,info_content,info_time
			result=StoreInfo(msg,1);			
		}
		else if(message.startsWith("<#GET_SINGLEINFO#>")){
			//获取单条内容信息
			String msg=message.substring(18);
			result=GetSingleInfo(msg); //msg为信息ID
		}
		else if(message.startsWith("<#GET_JWCT#>")){
			//获取教务处的title
			String msg=message.substring(12);
			result=GetTitle(msg,1); //msg为从第m条开始的n条，1为教务处
		}
		else if(message.startsWith("<#STORE_QG#>")){
			//存储青广信息
			String msg=message.substring(12);
			result=StoreInfo(msg,2);			
		}
		else if(message.startsWith("<#GET_QGT#>")){
			String msg=message.substring(11);
			result=GetTitle(msg,2);
		}
		else if(message.startsWith("<#STORE_JZ#>")){
			String msg=message.substring(12);
			result=Storejzinfo(msg);
		}
		else if(message.startsWith("<#GET_JZT#>")){
			String msg=message.substring(11);
			result=GetjzTitle(msg);
		}
		else if(message.startsWith("<#GET_JZ#>")){
			String msg=message.substring(10);
			result=GetSinglejz(msg);
		}
		else if(message.startsWith("<#STORE_LOSTFOUND#>")){
			String msg=message.substring(19);
			
			//1.失物或招领类型 ，2.是否有照片，3.照片描述即丢失什么4.主要内容
			String []m=msg.split("\\|");
			if(Integer.parseInt(m[1])==1){
				int size = in.readInt();			//读取图片大小
				byte [] buf = new byte[size];		//创建字节数组
				for(int i=0;i<size;i++){
					buf[i] = in.readByte();
				}
				result=StorelfInfo(m,buf);
			}else{
				result=StorelfInfo(m,null);
			}
		}
		else if(message.startsWith("<#LF_T#>")){
			String msg=message.substring(8);
			//从第n条开始的m条
			result=GetlfT(msg);
		}
		else if(message.startsWith("<#DEL_INFO#>")){
			String msg=message.substring(12);
			result=DeleteInfo(msg);
		}
		else if(message.startsWith("<#DEL_JZ#>")){
			String msg=message.substring(10);
			result=Deletejzinfo(msg);
		}
		else if(message.startsWith("<#DEL_LF#>")){
			String msg=message.substring(10);
			result=Deletelf(msg);
		}
		else{
			result="<#ERROR#>";
		}
		return result;
	}
	
	//管理员登陆信息验证
	private boolean Login(String msg){
		String []info1=msg.split("\\|");  //分割信息
		System.out.print(info1);
		String sql="select * from admin where admin_name=? and admin_pass=?";
		List<Object> params=new ArrayList<Object>();
		params.add(info1[0]);
		params.add(info1[1]);
		try{
			//查询管理员信息
			Map<String,Object> map=jdbcu.findSimpleResult(sql, params);
			if(!map.isEmpty()){
				//判断查询结果是否为空
				return true;
			}
		}catch(SQLException e){
			System.out.println("login error");
			e.printStackTrace();
		}finally{
			jdbcu.releaseConn();
		}
		return false;
	}

	//对information信息的插入教务处，青广
	private String StoreInfo(String msg,int flag) throws ParseException{
		String []m=msg.split("\\|"); //info_source_id,info_title,info_content,info_time
		int sid=Integer.parseInt(m[0]);
		String sql="insert into infomation(info_source,info_source_id,info_title,info_content,info_time)"+
		"values(?,?,?,?,?)";
		List<Object> params=new ArrayList<>();
		params.add(flag);  //来源1.教务处,2.青广
		params.add(sid);  //发布者ID，即管理员ID
		params.add(m[1]);  //title
		params.add(m[2]);  //content
		params.add(m[3]);  //活动时间
		try{
			boolean b=jdbcu.updateByPrepareStatement(sql, params);
			if(b){
				return "<#STORE_SUCCESE#>";
			}
		}catch(SQLException e){
			e.printStackTrace();
		}finally{
			jdbcu.releaseConn();
		}
		return "<#STORE_FAIL#>";
	}
	
	//获取单条的信息
	private String GetSingleInfo(String msg){
		String res=null;
		int Id=Integer.parseInt(msg);
		String sql="select * from infomation where info_id=?";
		List<Object> params=new ArrayList<Object>();
		params.add(Id);
		try{
			Map<String,Object> map=jdbcu.findSimpleResult(sql, params);
			if(map.isEmpty()){
				res="<#INFO_NONE#>"; //信息不存在
			}else{
				res="<#INFO_SUCCES#>";//信息查找成功
				//提取信息
				res=res+map.get("info_id")+"|"+map.get("info_source")+"|"+map.get("info_source_id")+"|"+
						map.get("info_title")+"|"+map.get("info_content")+"|"+map.get("info_date")+"|"+map.get("info_time");
			}
		}catch (SQLException e){
			e.printStackTrace();
		}finally{
			jdbcu.releaseConn();
		}
		return res;
	}
	
	//获取标题,返回每条信息的的ID和标题
	private String GetTitle(String msg,int flag){
		String res=null;
		String []m=msg.split("\\|");
		int start=Integer.parseInt(m[0]);
		int end=Integer.parseInt(m[1]);
		String sql="select info_id,info_title,info_date from infomation where info_source=? order by info_id desc limit ?, ?";
		String sql0="select count(*) from infomation where info_source=? order by info_id desc limit ?, ?";
		List<Object> params=new ArrayList<Object>();
		params.add(flag);
		params.add(start);
		params.add(end);
		try{
			List<Map<String,Object>> list=jdbcu.finndMoreResultSet(sql, params);
			Map<String,Object> l=jdbcu.findSimpleResult(sql0, params);
			Iterator it=list.iterator();
			if(it.hasNext()){
				res="<#INFO_SUCCES#>";
				res=res+l.get("count(*)");
				while(it.hasNext()){
					//取出每条信息
					Map<String,Object> map=(Map<String, Object>) it.next(); 
					res=res+"|"+map.get("info_id")+"|"+map.get("info_title")+"|"+map.get("info_date");
				}
			}else{
				res="<#INFO_NONE#>";
			}
		}catch (SQLException e){
			e.printStackTrace();
		}finally{
			jdbcu.releaseConn();
		}
		return res;
	}
	
	//获取infomation信息条数
	private String GetCount(String msg){
		String res=null;
		String sql="select count(*) from infomation where info_source=?";
		List<Object> params=new ArrayList<>();
		params.add(msg);
		try{
			Map<String,Object> map=jdbcu.findSimpleResult(sql, params);
			if(!map.isEmpty()){
				res="<#INFO_SUCCES#>"+(String) map.get("count(*)");
			}else{
				res="<#INFO_NONE#>";
			}
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			jdbcu.releaseConn();
		}
		return res;
	}
	
	//对失物招领信息的保存
	private String StorelfInfo(String[] m,byte[] buf) throws IOException, SQLException{
		String res=null;
		//1.失物或招领类型 ，2.是否有照片，3.照片描述即丢失什么4.主要内容
		java.sql.PreparedStatement ps = null;
		int p=Integer.parseInt(m[1]);
		System.out.println(p);
		String sql=null;
		if(p==1){
			sql="insert into lostandfound(lf_type,lf_p,lf_describe,lf_content,lf_photo) values(?,?,?,?,?)";
		}
		else{
			sql="insert into lostandfound(lf_type,lf_p,lf_describe,lf_content) values(?,?,?,?)";
		}
		try{
			System.out.println(sql);
			ps=connection.prepareStatement(sql);
			ps.setInt(1, Integer.parseInt(m[0]));		
			ps.setInt(2, p);
			ps.setString(3, m[2]);
			ps.setString(4, m[3]);
			if(p==1){
				java.io.InputStream in = new java.io.ByteArrayInputStream(buf);
				ps.setBinaryStream(5, in,(int)(in.available()));		//转换为int因为版本过低
			}
			int r=ps.executeUpdate();
			if(r>0){
				res="<#STORE_SUCCESE#>";
			}else{
				res="<#STORE_FAIL#>";
			}
		}catch(SQLException e){
			res="<#STORE_FAIL#>";
			e.printStackTrace();
		}finally{
			ps.close();
		}
		
		return res;
	}
	
	//获取失物招领信息标题
	private String GetlfT(String msg){
		String res=null;
		String []m=msg.split(msg);
		int start=Integer.parseInt(m[0]);
		int end=Integer.parseInt(m[1]);
		String sql="select lf_id,lf_type,lf_describe,lf_date from lostandfound order by lf_id desc limit ?,?";
		String sql0="select count(*) from lostandfound order by lf_id desc limit ?,?";
		List<Object> params=new ArrayList<Object>();
		params.add(start);
		params.add(end);
		try{
			List<Map<String,Object>> list=jdbcu.finndMoreResultSet(sql, params);
			Map<String,Object> l=jdbcu.findSimpleResult(sql0, params);
			Iterator it=list.iterator();
			if(it.hasNext()){
				res="<#INFO_SUCCES#>";
				res=res+l.get("count(*)");
				while(it.hasNext()){
					//取出每条信息
					Map<String,Object> map=(Map<String, Object>) it.next(); 
					res=res+"|"+map.get("info_id")+"|"+map.get("info_title")+"|"+map.get("lf_date");
				}
			}else{
				res="<#INFO_NONE#>";
			}
		}catch (SQLException e){
			e.printStackTrace();
		}finally{
			jdbcu.releaseConn();
		}
		return res;
	}
	
	//删除信息
	private String DeleteInfo(String msg){
		String res=null;
		int Id=Integer.parseInt(msg); //信息id
		String sql="delete from infomation where info_id=?";
		List<Object> params=new ArrayList<>();
		params.add(Id);
		try{
			boolean b=jdbcu.updateByPrepareStatement(sql, params);
			if(b){
				res="<#DELINFO_S#>";
			}
			else{
				res="<#DELINFO_F#>";
			}
		}catch (SQLException e){
			e.printStackTrace();
		}finally{
			jdbcu.releaseConn();
		}
		return res;
	}
	
	//删除失物招领信息
	private String Deletelf(String msg){
		String res=null;
		int Id=Integer.parseInt(msg);
		String sql="delete from lostandfound where lf_id=?";
		List<Object> params=new ArrayList<>();
		params.add(Id);
		try{
			boolean b=jdbcu.updateByPrepareStatement(sql, params);
			if(b){
				res="<#DELINFO_S#>";
			}else{
				res="<#DELINFO_F#>";
			}
		}catch (SQLException e){
			e.printStackTrace();
		}finally{
			jdbcu.releaseConn();
		}
		return res;
	}
	
	//插入讲座信息
	private String Storejzinfo(String msg){
		String res=null;
		String []m=msg.split("\\|"); //jz_sid,jz_title,jz_hname,jz_content,jz_time
		int id=Integer.parseInt(m[0]);
		String sql="insert into jzinfo(jz_sid,jz_title,jz_hname,jz_content,jz_time)"+
		"values(?,?,?,?,?)";
		List<Object> params=new ArrayList<>();
		params.add(id);
		params.add(m[1]);
		params.add(m[2]);
		params.add(m[3]);
		params.add(m[4]);
		try{
			boolean b=jdbcu.updateByPrepareStatement(sql, params);
			if(b){
				res="<#STORE_SUCCESE#>";
			}else{
				res="<#STORE_FAIL#>";
			}
		}catch (SQLException e){
			e.printStackTrace();
		}finally{
			jdbcu.releaseConn();
		}
		return res;
	}
	
	//删除讲座信息
	private String Deletejzinfo(String msg){
		String res=null;
		int Id=Integer.parseInt(msg);
		String sql="delete from jzinfo where jz_id=?";
		List<Object> params=new ArrayList<>();
		params.add(Id);
		try{
			boolean b=jdbcu.updateByPrepareStatement(sql, params);
			if(b){
				res="<#DELINFO_S#>";
			}else{
				res="<#DELINFO_F#>";
			}
		}catch (SQLException e){
			e.printStackTrace();
		}finally{
			jdbcu.releaseConn();
		}
		return res;
	}
	
	//获取讲座信息标题
	private String GetjzTitle(String msg){
		String res=null;
		String []m=msg.split("\\|");
		int start=Integer.parseInt(m[0]);
		int end=Integer.parseInt(m[1]);
		System.out.println(""+start+end);
		String sql="select jz_id,jz_title,jz_hname,jz_time from jzinfo order by jz_id desc limit ?,?";
		String sql0="select count(*) from jzinfo order by jz_id desc limit ?,?";
		List<Object> params=new ArrayList<Object>();
		params.add(start);
		params.add(end);
		try{
			List<Map<String,Object>> list=jdbcu.finndMoreResultSet(sql, params);
			Map<String,Object> l=jdbcu.findSimpleResult(sql0, params);
			System.out.println(list);
			Iterator it=list.iterator();
			if(it.hasNext()){
				res="<#INFO_SUCCES#>";
				res=res+l.get("count(*)");
				while(it.hasNext()){
					//取出每条信息
					Map<String,Object> map=(Map<String, Object>) it.next(); 
					res=res+"|"+map.get("jz_id")+"|"+map.get("jz_title")+"|"+map.get("jz_hname")+"|"+map.get("jz_time");
				}
			}else{
				res="<#INFO_NONE#>";
			}
		}catch (SQLException e){
			e.printStackTrace();
		}finally{
			jdbcu.releaseConn();
		}
		return res;
	}
	
	//获取单条讲座信息
	private String GetSinglejz(String msg){
		String res=null;
		int id=Integer.parseInt(msg);
		String sql="select * from jzinfo where jz_id=?";
		List<Object> params=new ArrayList<>();
		params.add(id);
		try{
			Map<String,Object> map=jdbcu.findSimpleResult(sql, params);
			if(map.isEmpty()){
				res="<#INFO_NONE#>";
			}else{
				res="<#INFO_SUCCES#>"+map.get("jz_id")+"|"+map.get("jz_sid")+"|"+map.get("jz_title")+"|"+
			map.get("jz_hname")+"|"+map.get("jz_content")+"|"+map.get("jz_time")+"|"+map.get("jz_date");
			}
		}catch (SQLException e){
			e.printStackTrace();
		}finally{
			jdbcu.releaseConn();
		}
		return res;
	}
}
