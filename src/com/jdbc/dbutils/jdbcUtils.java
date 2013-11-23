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

	//定义数据库的用户
	private final String USERNAME="sea";
	//定义数据库的密码
	private final String PASSWORD = "just";
	//定义数据库的驱动信息
	private final String DRIVER = "com.mysql.jdbc.Driver";
	//定义访问数据库的地址
	private final String URL = "jdbc:mysql://localhost:3306/scuboard";
	//定义数据库连接
	private Connection connection;
	//定义sql语句的执行
	private PreparedStatement pstmt;
	//定义返回的结果集
	private ResultSet resultSet;
	
	public jdbcUtils() {
		// TODO Auto-generated constructor stub
		try {
			Class.forName(DRIVER);
			//DriverManager.registerDriver(new com.mysql.jdbc.Driver());
			//System.setProperty("jdbc.drivers", DRIVER);
			System.out.println("注册驱动成功");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.out.println("注册驱动失败");
		}
	}

	//定义获得数据库的连接
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
	
	//完成对数据库的表的添加删除和修改操作
	public boolean updateByPrepareStatement(String sql, List<Object> params) 
			throws SQLException{
		boolean flag=false;
		int result = -1;//表示拥护执行添加删除和修改时所影响的数据库的行数
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
	
	//返回单条记录
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
		resultSet = pstmt.executeQuery();//返回查询结果
		java.sql.ResultSetMetaData metaData = resultSet.getMetaData();
		int col_len = metaData.getColumnCount();//返回列名称
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
	
	//查询返回多条记录
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
		resultSet = pstmt.executeQuery();//返回查询结果
		java.sql.ResultSetMetaData metaData = resultSet.getMetaData();
		int cols_len = metaData.getColumnCount();//返回列长度
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

	
	//jdbc的封装可以使用反射机制来封装
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
		resultSet = pstmt.executeQuery();//返回查询结果
		ResultSetMetaData metaData = (ResultSetMetaData) resultSet.getMetaData();
		int cols_len = metaData.getColumnCount();//返回列长度
		while(resultSet.next()){
			//通过反射机制创建实例
			resultObject = cls.newInstance();
			for(int i=0; i<cols_len;i++){
				String cols_name = metaData.getColumnName(i+1);
				Object cols_value = resultSet.getObject(cols_name);
				if(cols_value==null){
					cols_value="";
				}
				Field filed = cls.getDeclaredField(cols_name);
				filed.setAccessible(true);//打开javabean的私有访问权限
				filed.set(resultObject,cols_value);
			}
		}
		return resultObject;
	}
	
	/**
	 * 通过反射机制访问数据库
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
		resultSet = pstmt.executeQuery();//返回查询结果
		java.sql.ResultSetMetaData metaData = resultSet.getMetaData();
		int cols_len = metaData.getColumnCount();//返回列长度
		while(resultSet.next()){
			//通过反射机制创建实例
			T resultObject = cls.newInstance();
			for(int i=0; i<cols_len;i++){
				String cols_name = metaData.getColumnName(i+1);
				Object cols_value = resultSet.getObject(cols_name);
				if(cols_value==null){
					cols_value="";
				}
				Field filed = cls.getDeclaredField(cols_name);
				filed.setAccessible(true);//打开javabean的私有访问权限
				filed.set(resultObject,cols_value);
			}
			list.add(resultObject);
		}
		return list;
	}
	
	/**
	 * 关闭连接
	 * 释放资源
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
		String info ="你也不必牵强再说爱我，反我的灵魂也要掉落";
		String title="回家";
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
		params.add(1);//第一条记录
		try {
			Map<String,Object> map = jdbcUtils.findSimpleResult(sql, params);
			System.out.println(map);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}*/
		
		/*sql = "select * from finding where id = ? ";
		params=new ArrayList<Object>();
		params.add(5);//第一条记录
		try {
			Map<String,Object> map = jdbcUtils.findSimpleResult(sql, params);
			System.out.println(map);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}*/
		
		//多条记录
		/*String sql = "select * from userinfo";
		try {
			List<Map<String,Object>> map = jdbcUtils.finndMoreResultSet(sql, null);
			System.out.println(map);
		} catch (Exception e) {
			// TODO: handle exception
		}finally{
			jdbcUtils.releaseConn();//释放连接
		}*/
		//String sql = "select * from userinfo";
		//List<Object> params=new ArrayList<Object>();
		//params.add(1);//第一条记录
		/*try {
			List<UserInfo> userInfo=jdbcUtils.findMoreRefResult(sql,null, UserInfo.class);
			System.out.println(userInfo);
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally{
			jdbcUtils.releaseConn();//释放连接
		}*/
		
		jdbcUtils jdbcu = new jdbcUtils();
		jdbcu.getConnection();
		String msg="MOON|sea";
		String []info1=msg.split("\\|");  //分割信息
		System.out.print(info1);
		String sql="select * from admin where admin_name=? and admin_pass=?";
		List<Object> params=new ArrayList<Object>();
		params.add(info1[0]);
		params.add(info1[1]);
		try{
			//查询管理员信息
			Map<String,Object> map=jdbcu.findSimpleResult(sql, params);
			System.out.println(map);
			if(!map.isEmpty()){
				//判断查询结果是否为空
				System.out.println("not empty");
			}
		}catch(SQLException e){
			System.out.println("login error");
			e.printStackTrace();
		}finally{
			jdbcu.releaseConn();//释放连接
		}
		
	}

}
