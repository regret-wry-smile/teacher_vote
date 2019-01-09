package com.zkxltech.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ejet.core.util.constant.Constant;
import com.ejet.core.util.io.IOUtils;
import com.zkxltech.config.ConfigConstant;
import com.zkxltech.domain.Result;

/**
 * 服务器oracleJDBC链接
 * @author zkxl
 *
 */
public class DBHelper3{
	private static final Logger log = LoggerFactory.getLogger(DBHelper3.class);
	
	private static String url= ConfigConstant.serverDbConfig.getUrl(); 
	private static String userName =  ConfigConstant.serverDbConfig.getUser_name();
	private static String password =  ConfigConstant.serverDbConfig.getPassword();
	private static String driver =  ConfigConstant.serverDbConfig.getDriver();
	private static void onCreate(String sql) {
		// TODO Auto-generated method stub
		Connection conn = null;
		Statement stmt = null;
		try {
			Class.forName(driver);
			
			conn = getConnection();
		    stmt = conn.createStatement();
		    stmt.executeUpdate(sql);
		    stmt.close();
		    conn.close();
		    } catch ( Exception e ) {
		    	log.error(IOUtils.getError(e));
		    }
	}
	
	/**
	 * 查询
	 * @param dbNameStr 数据库名
	 * @param sql 查询的SQL语句
	 * @param key 查询结果对应的key
	 * @return
	 */
	public static Result onQuery(String sql,String[] key,List<Object> param){
	    Result result = new Result();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Connection conn = null;
		PreparedStatement pStmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
	//	    System.out.println("Opened database successfully");
			conn.setAutoCommit(false);

		    pStmt = conn.prepareStatement(sql);
		    if(param == null){
		    	 rs = pStmt.executeQuery();
		    }else {
		    	 for(int i=0; i< param.size(); i++){
		    		 pStmt.setObject(i+1, param.get(i));
				    }
				 rs = pStmt.executeQuery();
			}
		   
		    while ( rs.next() ) {
		    	Map<String, Object> map = new HashMap<String, Object>();
		    	for(int i=0;i<key.length;i++){
		    		Object object = rs.getObject(key[i]);
		    		map.put(key[i], object);
		    	}
		    	list.add(map);
		    }
		    rs.close();
		    pStmt.close();
		    conn.close();
		    result.setRet(Constant.SUCCESS);
		    result.setItem(list);
		  } catch ( Exception e ) {
			  log.error(IOUtils.getError(e));
			  result.setRet(Constant.ERROR);
			  result.setMessage("Sql exec failure！");
			  result.setDetail(e.getMessage());
		  }finally {
				try {
					 if (pStmt != null && !pStmt.isClosed()) {
						 pStmt.close();
						}
				    	if (conn != null && !conn.isClosed()) {
							conn.close();
						}
				} catch (Exception e2) {
					log.error(IOUtils.getError(e2));
				}
				
			}
		return result;
	}
	
	
	/**
	 * 增加 删除 修改 
	 * @param dbNameStr 数据库名
	 * @param sql sql语句
	 * @param param 插入的参数
	 */
	public static void onUpdate(String sql,List<Object> param){
		Connection conn = null;
		PreparedStatement pStmt = null;
		try {
			conn = getConnection();
//		    System.out.println("Opened database successfully");
			conn.setAutoCommit(false);
			
			pStmt = conn.prepareStatement(sql);
			if (param!=null) {
				for(int i=0;i<param.size();i++){
					pStmt.setObject(i+1,param.get(i));
				}
			}
			pStmt.executeUpdate();
			
			pStmt.close();
		    conn.commit();
		    conn.close();
		} catch (Exception e) {
			log.error(IOUtils.getError(e));
		}finally {
			try {
				 if (pStmt != null && !pStmt.isClosed()) {
					 pStmt.close();
					}
			    	if (conn != null && !conn.isClosed()) {
						conn.close();
					}
			} catch (Exception e2) {
				log.error(IOUtils.getError(e2));
			}
			
		}
	}
	
	public static Connection getConnection() throws ClassNotFoundException, SQLException{
		Class.forName(driver);
		return DriverManager.getConnection(url,userName,password);
	}
	
	public static void main(String[] args) {
		String[] key = {"id"};
		DBHelper3.onQuery("select * from student", key, null);
	}
}
