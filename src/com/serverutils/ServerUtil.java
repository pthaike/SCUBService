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

	String message=null; //�������Ϣ
	String result=null; //��ѯ���
	jdbcUtils jdbcu; //���ݿ�����
	Connection connection;
	
	public ServerUtil(String msg) {
		// TODO Auto-generated constructor stub
		message=msg;
		/*jdbcUtils jdbcu=new jdbcUtils();
		jdbcu.getConnection(); //�������ݿ�
*/	}
	
	public String MessageSolve(DataInputStream in) throws ParseException, IOException, SQLException{		
		jdbcu=new jdbcUtils();
		connection =jdbcu.getConnection(); //�������ݿ�		
		if(message.startsWith("<#LOGIN_INFO#>")){  
			//����Ա��¼��Ϣ,�û������û�����
			String msg=message.substring(14);	//��ȡ��Ϣ
			if(Login(msg)){
				result="<#MANAGE_TRUE#>"; //����Ա�û�����
			}
			else{
				result="<#MANAGE_FALSE#>";
			}
		}
		else if(message.startsWith("<#STORE_JWC#>")){
			//�洢������Ϣ
			String msg=message.substring(13);
			//1��ʾ������Ϣ,msgΪ�洢�ľ�����Ϣinfo_source_id,info_title,info_content,info_time
			result=StoreInfo(msg,1);			
		}
		else if(message.startsWith("<#GET_SINGLEINFO#>")){
			//��ȡ����������Ϣ
			String msg=message.substring(18);
			result=GetSingleInfo(msg); //msgΪ��ϢID
		}
		else if(message.startsWith("<#GET_JWCT#>")){
			//��ȡ���񴦵�title
			String msg=message.substring(12);
			result=GetTitle(msg,1); //msgΪ�ӵ�m����ʼ��n����1Ϊ����
		}
		else if(message.startsWith("<#STORE_QG#>")){
			//�洢�����Ϣ
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
			
			//1.ʧ����������� ��2.�Ƿ�����Ƭ��3.��Ƭ��������ʧʲô4.��Ҫ����
			String []m=msg.split("\\|");
			if(Integer.parseInt(m[1])==1){
				int size = in.readInt();			//��ȡͼƬ��С
				byte [] buf = new byte[size];		//�����ֽ�����
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
			//�ӵ�n����ʼ��m��
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
	
	//����Ա��½��Ϣ��֤
	private boolean Login(String msg){
		String []info1=msg.split("\\|");  //�ָ���Ϣ
		System.out.print(info1);
		String sql="select * from admin where admin_name=? and admin_pass=?";
		List<Object> params=new ArrayList<Object>();
		params.add(info1[0]);
		params.add(info1[1]);
		try{
			//��ѯ����Ա��Ϣ
			Map<String,Object> map=jdbcu.findSimpleResult(sql, params);
			if(!map.isEmpty()){
				//�жϲ�ѯ����Ƿ�Ϊ��
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

	//��information��Ϣ�Ĳ�����񴦣����
	private String StoreInfo(String msg,int flag) throws ParseException{
		String []m=msg.split("\\|"); //info_source_id,info_title,info_content,info_time
		int sid=Integer.parseInt(m[0]);
		String sql="insert into infomation(info_source,info_source_id,info_title,info_content,info_time)"+
		"values(?,?,?,?,?)";
		List<Object> params=new ArrayList<>();
		params.add(flag);  //��Դ1.����,2.���
		params.add(sid);  //������ID��������ԱID
		params.add(m[1]);  //title
		params.add(m[2]);  //content
		params.add(m[3]);  //�ʱ��
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
	
	//��ȡ��������Ϣ
	private String GetSingleInfo(String msg){
		String res=null;
		int Id=Integer.parseInt(msg);
		String sql="select * from infomation where info_id=?";
		List<Object> params=new ArrayList<Object>();
		params.add(Id);
		try{
			Map<String,Object> map=jdbcu.findSimpleResult(sql, params);
			if(map.isEmpty()){
				res="<#INFO_NONE#>"; //��Ϣ������
			}else{
				res="<#INFO_SUCCES#>";//��Ϣ���ҳɹ�
				//��ȡ��Ϣ
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
	
	//��ȡ����,����ÿ����Ϣ�ĵ�ID�ͱ���
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
					//ȡ��ÿ����Ϣ
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
	
	//��ȡinfomation��Ϣ����
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
	
	//��ʧ��������Ϣ�ı���
	private String StorelfInfo(String[] m,byte[] buf) throws IOException, SQLException{
		String res=null;
		//1.ʧ����������� ��2.�Ƿ�����Ƭ��3.��Ƭ��������ʧʲô4.��Ҫ����
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
				ps.setBinaryStream(5, in,(int)(in.available()));		//ת��Ϊint��Ϊ�汾����
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
	
	//��ȡʧ��������Ϣ����
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
					//ȡ��ÿ����Ϣ
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
	
	//ɾ����Ϣ
	private String DeleteInfo(String msg){
		String res=null;
		int Id=Integer.parseInt(msg); //��Ϣid
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
	
	//ɾ��ʧ��������Ϣ
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
	
	//���뽲����Ϣ
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
	
	//ɾ��������Ϣ
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
	
	//��ȡ������Ϣ����
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
					//ȡ��ÿ����Ϣ
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
	
	//��ȡ����������Ϣ
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
