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
	
	public String MessageSolve() throws ParseException, IOException, SQLException{		
		jdbcu=new jdbcUtils();
		connection =jdbcu.getConnection(); //�������ݿ�		
		if(message.startsWith("<#LOGIN_INFO#>")){  
			//����Ա��¼��Ϣ,�û������û�����
			String msg=message.substring(14);	//��ȡ��Ϣ
			result=Login(msg);
			//System.out.println(result);
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
			result=GetjzInfo(msg);
		}
		else if(message.startsWith("<#STORE_QG#>")){
			String msg=message.substring(12);
			result=StoreQgInfo(msg);
		}
		else if(message.startsWith("<#GET_QGT#>")){
			String msg=message.substring(11);
			result=GetqgTitle(msg);
		}
		else if(message.startsWith("<#GET_QG#>")){
			String msg=message.substring(10);
			result=GetqgInfo(msg);
		}
		else if(message.startsWith("<#STORE_LF#>")){
			String msg=message.substring(12);
			result=StorelfInfo(msg);
		}
		else if(message.startsWith("<#GET_LF#>")){
			String msg=message.substring(10);
			result=GetlfInfo(msg);
		}
		else if(message.startsWith("<#GET_LFT#>")){
			String msg=message.substring(11);
			result=GetlfTitle(msg);
		}
		else if(message.startsWith("<#DEL_QG#>")){
			String msg=message.substring(10);
			result=DeleteqgInfo(msg);
		}
		else if(message.startsWith("<#DEL_JZ#>")){
			String msg=message.substring(10);
			result=Deletejzinfo(msg);
		}
		else if(message.startsWith("<#DEL_LF#>")){
			String msg=message.substring(10);
			result=Deletelf(msg);
		}
		else if (message.startsWith("<#STORE_COM#>")){
			String msg = message.substring(13);
			result = StoreCom(msg);
		}
		else if (message.startsWith("<#GET_COM#>")){
			String msg = message.substring(11);
			result = GetCom(msg);
		}
		else{
			result="<#ERROR#>";
		}
		return result;
	}
	
	//����Ա��½��Ϣ��֤
	private String Login(String msg){
		String res=null;
		String []info1=msg.split("\\|");  //�ָ���Ϣ
		System.out.print(info1);
		String sql="select * from admin where admin_name=? and admin_pswd=?";
		List<Object> params=new ArrayList<Object>();
		params.add(info1[0]);
		params.add(info1[1]);
		try{
			//��ѯ����Ա��Ϣ
			Map<String,Object> map=jdbcu.findSimpleResult(sql, params);
			if(!map.isEmpty()){
				//�жϲ�ѯ����Ƿ�Ϊ��
				String id=""+map.get("admin_id");
				System.out.println(id+"-----------");
				res="<#MANAGE_TRUE#>"+id;
			}else{
				res="<#MANAGE_FALSE#>";
			}
		}catch(SQLException e){
			System.out.println("login error");
			res="<#MANAGE_FALSE#>";
			e.printStackTrace();
		}finally{
			jdbcu.releaseConn();
		}
		return res;
	}

	//��information��Ϣ�Ĳ������
	private String StoreQgInfo(String msg) throws ParseException{
		String res=null;
		String []m=msg.split("\\|"); 
		int sid=Integer.parseInt(m[0]);
		String sql="insert into qginfo(qg_sid,qg_title,qg_context,qg_date)"+
		"values(?,?,?,?)";
		List<Object> params=new ArrayList<>();
		params.add(sid);  //������ID��������ԱID
		params.add(m[1]);  //title
		params.add(m[2]);  //content
		params.add(m[3]);  //�ʱ��
		try{
			boolean b=jdbcu.updateByPrepareStatement(sql, params);
			if(b){
				res="<#STORE_SUCCESE#>";
			}else{
				res="<#STORE_FAIL#>";
			}
		}catch(SQLException e){
			res="<#STORE_FAIL#>";
			e.printStackTrace();
		}finally{
			jdbcu.releaseConn();
		}
		return res;
	}
	
	//��ȡ��������Ϣ
	private String GetqgInfo(String msg){
		String res=null;
		int Id=Integer.parseInt(msg);
		String sql="select * from qginfo where qg_id=?";
		List<Object> params=new ArrayList<Object>();
		params.add(Id);
		try{
			Map<String,Object> map=jdbcu.findSimpleResult(sql, params);
			if(map.isEmpty()){
				res="<#INFO_NONE#>"; //��Ϣ������
			}else{
				res="<#INFO_SUCCES#>";//��Ϣ���ҳɹ�
				//��ȡ��Ϣ
				res=res+map.get("qg_id")+"|"+map.get("qg_sid")+"|"+
						map.get("qg_title")+"|"+map.get("qg_context")+"|"+map.get("qg_time");
			}
		}catch (SQLException e){
			e.printStackTrace();
		}finally{
			jdbcu.releaseConn();
		}
		return res;
	}
	
	//��ȡ����,����ÿ����Ϣ�ĵ�ID�ͱ���
	private String GetqgTitle(String msg){
		//
		String res=null;
		String []m=msg.split("\\|");
		int start=Integer.parseInt(m[0]);
		int end=Integer.parseInt(m[1]);
		String sql="select qg_id,qg_title,qg_date from qginfo order by qg_id desc limit ?,?";
		String sql0="select count(*) from qginfo order by qg_id desc limit ?,?";
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
					res=res+"|"+map.get("qg_id")+"|"+map.get("qg_title")+"|"+map.get("qg_date");
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
	private String DeleteqgInfo(String msg){
		String res=null;
		int Id=Integer.parseInt(msg); //��Ϣid
		String sql="delete from qginfo where qg_id=?";
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
			res="<#DELINFO_F#>";
			e.printStackTrace();
		}finally{
			jdbcu.releaseConn();
		}
		return res;
	}
	
	//��ȡ��Ϣ����
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
//	private String StorelfInfo(String[] m,byte[] buf) throws IOException, SQLException{
//		String res=null;
//		//1.ʧ����������� ��2.�Ƿ�����Ƭ��3.��Ƭ��������ʧʲô4.��Ҫ����
//		java.sql.PreparedStatement ps = null;
//		int p=Integer.parseInt(m[1]);
//		System.out.println(p);
//		String sql=null;
//		if(p==1){
//			sql="insert into lostandfound(lf_type,lf_p,lf_describe,lf_content,lf_photo) values(?,?,?,?,?)";
//		}
//		else{
//			sql="insert into lostandfound(lf_type,lf_p,lf_describe,lf_content) values(?,?,?,?)";
//		}
//		try{
//			System.out.println(sql);
//			ps=connection.prepareStatement(sql);
//			ps.setInt(1, Integer.parseInt(m[0]));		
//			ps.setInt(2, p);
//			ps.setString(3, m[2]);
//			ps.setString(4, m[3]);
//			if(p==1){
//				java.io.InputStream in = new java.io.ByteArrayInputStream(buf);
//				ps.setBinaryStream(5, in,(int)(in.available()));		//ת��Ϊint��Ϊ�汾����
//			}
//			int r=ps.executeUpdate();
//			if(r>0){
//				res="<#STORE_SUCCESE#>";
//			}else{
//				res="<#STORE_FAIL#>";
//			}
//		}catch(SQLException e){
//			res="<#STORE_FAIL#>";
//			e.printStackTrace();
//		}finally{
//			ps.close();
//		}
//		
//		return res;
//	}
	
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
	
	//���뽲����Ϣ
	private String Storejzinfo(String msg){
		String res=null;
		String []m=msg.split("\\|"); //jz_sid,jz_title,jz_hname,jz_content,jz_time
		int id=Integer.parseInt(m[0]);
		String sql="insert into jzinfo(jz_sid,jz_title,jz_hname,jz_addr,jz_content,jz_time)"+
		"values(?,?,?,?,?,?)";
		List<Object> params=new ArrayList<>();
		params.add(id);
		params.add(m[1]);
		params.add(m[2]);
		params.add(m[3]);
		params.add(m[4]);
		params.add(m[5]);
		try{
			boolean b=jdbcu.updateByPrepareStatement(sql, params);
			if(b){
				res="<#STORE_SUCCESE#>";
			}else{
				res="<#STORE_FAIL#>";
			}
		}catch (SQLException e){
			res="<#STORE_FAIL#>";
			e.printStackTrace();
		}finally{
			jdbcu.releaseConn();
		}
		return res;
	}
	
	private String GetjzInfo(String msg){
		
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
			map.get("jz_hname")+"|"+map.get("jz_addr")+"|"+map.get("jz_content")+
			"|"+map.get("jz_time")+"|"+map.get("jz_date");
			}
		}catch (SQLException e){
			res="<#INFO_NONE#>";
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
	
	private String StorelfInfo(String msg){
		String res=null;
		String []m=msg.split("\\|"); 
		int type=Integer.parseInt(m[0]);
		String sql="insert into lfinfo(lf_type,lf_snum,lf_des,lf_cont)"+
		"values(?,?,?,?)";
		List<Object> params=new ArrayList<>();
		params.add(type);
		params.add(m[1]);
		params.add(m[2]);
		params.add(m[3]);
		try{
			boolean b=jdbcu.updateByPrepareStatement(sql, params);
			if(b){
				res="<#STORE_SUCCESE#>";
			}else{
				res="<#STORE_FAIL#>";
			}
		}catch (SQLException e){
			res="<#STORE_FAIL#>";
			e.printStackTrace();
		}finally{
			jdbcu.releaseConn();
		}
		return res;
	}
	
	//��ȡʧ��������Ϣ����
	private String GetlfTitle(String msg){
		String res=null;
		String []m=msg.split("\\|");
		System.out.println(msg);
		int start=Integer.parseInt(m[0]);
		int end=Integer.parseInt(m[1]);
		String sql="select lf_id,lf_type,lf_des,lf_time from lfinfo order by lf_id desc limit ?,?";
		String sql0="select count(*) from lfinfo order by lf_id desc limit ?,?";
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
					res=res+"|"+map.get("lf_id")+"|"+map.get("lf_type")+"|"+map.get("lf_des")+"|"+map.get("lf_time");
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
	
	//��ȡ������ʧ��������Ϣ
	private String GetlfInfo(String msg){
		String res=null;
		int id = Integer.parseInt(msg);
		String sql="select lf_id,lf_type,lf_des,lf_cont,lf_time from lfinfo where lf_id=?";
		List<Object> params=new ArrayList<>();
		params.add(id);
		try{
			Map<String,Object> map=jdbcu.findSimpleResult(sql,params);
			if(map.isEmpty()){
				res="<#INFO_NONE#>";
			}else{
				res="<#INFO_SUCCES#>"+map.get("lf_id")+"|"+map.get("lf_type")+"|"+
			map.get("lf_des")+"|"+map.get("lf_cont")+"|"+map.get("lf_time");
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		return res;
	}
	
	
	//ɾ��ʧ��������Ϣ
	private String Deletelf(String msg){
		String res=null;
		int Id=Integer.parseInt(msg);
		String sql="delete from lfinfo where lf_id=?";
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
	
	private String StoreCom(String msg){
		String res=null;
		String []m=msg.split("\\|"); 
		String sql="insert into comment(c_lfid,c_num,c_com)"+
		"values(?,?,?)";
		List<Object> params=new ArrayList<>();
		params.add(m[0]);
		params.add(m[1]);
		params.add(m[2]);
		try{
			boolean b=jdbcu.updateByPrepareStatement(sql, params);
			if(b){
				res="<#STORE_SUCCESE#>";
			}else{
				res="<#STORE_FAIL#>";
			}
		}catch (SQLException e){
			res="<#STORE_FAIL#>";
			e.printStackTrace();
		}finally{
			jdbcu.releaseConn();
		}
		return res;
	}
	
	private String GetCom(String msg){
		String res=null;
		int lfid=Integer.parseInt(msg);
		
		String sql="select c_id,c_com,c_date from comment where c_lfid=?";
		
		List<Object> params=new ArrayList<Object>();
		params.add(lfid);
		try{
			List<Map<String,Object>> list=jdbcu.finndMoreResultSet(sql, params);
			System.out.println(list);
			Iterator it=list.iterator();
			if(it.hasNext()){
				res="<#INFO_SUCCES#>";
				while(it.hasNext()){
					//ȡ��ÿ����Ϣ
					Map<String,Object> map=(Map<String, Object>) it.next(); 
					res=res+"|"+map.get("c_id")+"|"+map.get("c_com")+"|"+map.get("c_date");
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
	
}
