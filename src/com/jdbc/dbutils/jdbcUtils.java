package com.jdbc.dbutils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jdbc.dbutils.domain.UserInfo;
import com.mysql.jdbc.ResultSetMetaData;


public class jdbcUtils {

	//�������ݿ���û�
	private final String USERNAME="sea";
	//�������ݿ������
	private final String PASSWORD = "just";
	//�������ݿ��������Ϣ
	private final String DRIVER = "com.mysql.jdbc.Driver";
	//����������ݿ�ĵ�ַ
	private final String URL = "jdbc:mysql://localhost:3306/scuboard";
	//�������ݿ�����
	private Connection connection;
	//����sql����ִ��
	private PreparedStatement pstmt;
	//���巵�صĽ����
	private ResultSet resultSet;
	
	public jdbcUtils() {
		// TODO Auto-generated constructor stub
		try {
			Class.forName(DRIVER);
			//DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			//System.setProperty("jdbc.drivers", DRIVER);
			System.out.println("ע�������ɹ�");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("ע������ʧ��");
		}
	}

	//���������ݿ������
	/**
	 * 
	 * @return
	 */
	public Connection getConnection(){
		try {
//			connection=DriverManager.getConnection(URL, USERNAME,PASSWORD);
			connection=DriverManager.getConnection(URL+"?user="+USERNAME+"&password="+PASSWORD+"&useUnicode=true&characterEncoding=utf-8");
			System.out.println("connect sucess");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("connect fail!");
		}
		return connection;
	}
	
	//��ɶ����ݿ�ı�����ɾ�����޸Ĳ���
	public boolean updateByPrepareStatement(String sql, List<Object> params) 
			throws SQLException{
		boolean flag=false;
		int result = -1;//��ʾӵ��ִ�����ɾ�����޸�ʱ��Ӱ������ݿ������
		pstmt = connection.prepareStatement(sql);
		int index = 1;
		if(params !=null&&!params.isEmpty()){
			for(int i=0;i<params.size();i++){
				pstmt.setObject(index++, params.get(i));
			}
		}
		result = pstmt.executeUpdate();
		flag = result>0?true:false;
		return flag;
	}
	
	//���ص�����¼
	/**
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public Map<String, Object> findSimpleResult(String sql, List<Object> params)
			throws SQLException{
		Map<String, Object> map= new HashMap<String, Object>();
		int index = 1;
		pstmt = connection.prepareStatement(sql);
		if(params!=null&&!params.isEmpty()){
			for(int i =0;i<params.size();i++){
				pstmt.setObject(index++, params.get(i));
			}
		}
		resultSet = pstmt.executeQuery();//���ز�ѯ���
		java.sql.ResultSetMetaData metaData = resultSet.getMetaData();
		int col_len = metaData.getColumnCount();//����������
		while(resultSet.next()){
			for(int i =0;i<col_len;i++){
				String cols_name = metaData.getColumnName(i+1);
				Object cols_value = resultSet.getObject(cols_name);
				if(cols_value==null){
					cols_value = "";
				}
				map.put(cols_name, cols_value);
			}
		}
		return map;
	}
	
	//��ѯ���ض�����¼
	/**
	 * 
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String, Object>> finndMoreResultSet(String sql, List<Object> params)
			throws SQLException{
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		int index = 1;
		pstmt = connection.prepareStatement(sql);
		if(params!=null&&!params.isEmpty()){
			for(int i =0;i<params.size();i++){
				pstmt.setObject(index++, params.get(i));
			}
		}
		resultSet = pstmt.executeQuery();//���ز�ѯ���
		java.sql.ResultSetMetaData metaData = resultSet.getMetaData();
		int cols_len = metaData.getColumnCount();//�����г���
		while(resultSet.next()){
			Map<String, Object> map = new HashMap<String, Object>();
			for(int i=0;i<cols_len;i++){
				String cols_name = metaData.getColumnName(i+1);
				Object cols_value = resultSet.getObject(cols_name);
				if(cols_value==null){
					cols_value="";
				}
				map.put(cols_name, cols_value);
			}
			list.add(map);
		}
		return list;
	}

	
	//jdbc�ķ�װ����ʹ�÷����������װ
	public <T> T findSimpleRefResult(String sql,List<Object> params,Class<T> cls)
			throws Exception{
		T resultObject = null;
		int index = 1;
		pstmt = connection.prepareStatement(sql);
		if(params!=null&&!params.isEmpty()){
			for(int i =0;i<params.size();i++){
				pstmt.setObject(index++, params.get(i));
			}
		}
		resultSet = pstmt.executeQuery();//���ز�ѯ���
		ResultSetMetaData metaData = (ResultSetMetaData) resultSet.getMetaData();
		int cols_len = metaData.getColumnCount();//�����г���
		while(resultSet.next()){
			//ͨ��������ƴ���ʵ��
			resultObject = cls.newInstance();
			for(int i=0; i<cols_len;i++){
				String cols_name = metaData.getColumnName(i+1);
				Object cols_value = resultSet.getObject(cols_name);
				if(cols_value==null){
					cols_value="";
				}
				Field filed = cls.getDeclaredField(cols_name);
				filed.setAccessible(true);//��javabean��˽�з���Ȩ��
				filed.set(resultObject,cols_value);
			}
		}
		return resultObject;
	}
	
	/**
	 * ͨ��������Ʒ������ݿ�
	 * @param sql
	 * @param params
	 * @param cls
	 * @return
	 * @throws Exception
	 */
	public <T> List<T> findMoreRefResult(String sql,List<Object> params,Class<T> cls)
			throws Exception{
		List<T> list = new ArrayList<T>();
		int index = 1;
		pstmt = connection.prepareStatement(sql);
		if(params!=null&&!params.isEmpty()){
			for(int i =0;i<params.size();i++){
				pstmt.setObject(index++, params.get(i));
			}
		}
		resultSet = pstmt.executeQuery();//���ز�ѯ���
		java.sql.ResultSetMetaData metaData = resultSet.getMetaData();
		int cols_len = metaData.getColumnCount();//�����г���
		while(resultSet.next()){
			//ͨ��������ƴ���ʵ��
			T resultObject = cls.newInstance();
			for(int i=0; i<cols_len;i++){
				String cols_name = metaData.getColumnName(i+1);
				Object cols_value = resultSet.getObject(cols_name);
				if(cols_value==null){
					cols_value="";
				}
				Field filed = cls.getDeclaredField(cols_name);
				filed.setAccessible(true);//��javabean��˽�з���Ȩ��
				filed.set(resultObject,cols_value);
			}
			list.add(resultObject);
		}
		return list;
	}
	
	/**
	 * �ر�����
	 * �ͷ���Դ
	 */
	public void releaseConn() {
		if(resultSet!=null){
			try {
				resultSet.close();
			} catch (SQLException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		if(pstmt!=null){
			try {
				pstmt.close();
			} catch (SQLException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		if(connection!=null){
			try {
				connection.close();
			} catch (SQLException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * 
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	
	public static void main(String[] args) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		/*jdbcUtils jdbcUtils = new jdbcUtils();
		jdbcUtils.getConnection();*/
		/*String sql ="insert into userinfo(username,pswd) values(?,?)";
		List<Object> params = new ArrayList<>();
		params.add("gy");
		params.add("secret");
		try {
			boolean flag =jdbcUtils.updateByPrepareStatement(sql, params);
			System.out.println(flag);
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}*/
		/*String sql ="insert into finding(title,content) values(?,?)";
		String info ="��Ҳ����ǣǿ��˵���ң����ҵ����ҲҪ����";
		String title="�ؼ�";
		List<Object> params = new ArrayList<>();*/
//		info=new String(info.getBytes(),"utf-8");
//		title=new String(title.getBytes(),"utf-8");
		/*params.add(title);
		params.add(info);
		try {
			boolean flag =jdbcUtils.updateByPrepareStatement(sql, params);
			System.out.println(flag);
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}*/
		/*String sql = "select username from userinfo where id = ? ";
		List<Object> params=new ArrayList<Object>();
		params.add(1);//��һ����¼
		try {
			Map<String,Object> map = jdbcUtils.findSimpleResult(sql, params);
			System.out.println(map);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}*/
		
		/*sql = "select * from finding where id = ? ";
		params=new ArrayList<Object>();
		params.add(5);//��һ����¼
		try {
			Map<String,Object> map = jdbcUtils.findSimpleResult(sql, params);
			System.out.println(map);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}*/
		
		//������¼
		/*String sql = "select * from userinfo";
		try {
			List<Map<String,Object>> map = jdbcUtils.finndMoreResultSet(sql, null);
			System.out.println(map);
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			jdbcUtils.releaseConn();//�ͷ�����
		}*/
		//String sql = "select * from userinfo";
		//List<Object> params=new ArrayList<Object>();
		//params.add(1);//��һ����¼
		/*try {
			List<UserInfo> userInfo=jdbcUtils.findMoreRefResult(sql,null, UserInfo.class);
			System.out.println(userInfo);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			jdbcUtils.releaseConn();//�ͷ�����
		}*/
		
		jdbcUtils jdbcu = new jdbcUtils();
		jdbcu.getConnection();
		String msg="MOON|sea";
		String []info1=msg.split("\\|");  //�ָ���Ϣ
		System.out.print(info1);
		String sql="select * from admin where admin_name=? and admin_pass=?";
		List<Object> params=new ArrayList<Object>();
		params.add(info1[0]);
		params.add(info1[1]);
		try{
			//��ѯ����Ա��Ϣ
			Map<String,Object> map=jdbcu.findSimpleResult(sql, params);
			System.out.println(map);
			if(!map.isEmpty()){
				//�жϲ�ѯ����Ƿ�Ϊ��
				System.out.println("not empty");
			}
		}catch(SQLException e){
			System.out.println("login error");
			e.printStackTrace();
		}finally{
			jdbcu.releaseConn();//�ͷ�����
		}
		
	}

}
