
package jikken;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataB {
	static Connection conn;
	static Statement stmt;
	static ResultSet rs;
	static int ID;
	public static void DeleteDB(){
		try {
			Class.forName("org.sqlite.JDBC");
			/conn =DriverManager.getConnection("jdbc:mysql://localhost/YOUR_DBNAME?"+
                     "user=USER_ID&password=PASSWORD");
			stmt = conn.createStatement();
			stmt.execute("delete from Miner");
			stmt.execute("delete from Block");
			String hostAddress = InetAddress.getLocalHost().getHostAddress();
			stmt.execute( "insert into Miner values (0,0,'"+hostAddress+"')" );

		} catch (SQLException| ClassNotFoundException | UnknownHostException e) {

		}

	}
	public static void CreateDB()  {
		try {
			//テーブル作成
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:Mining.db");
			stmt = conn.createStatement();
			stmt.executeUpdate("create table Miner( ID integer, Belong integer, IP string )" );
			stmt.executeUpdate("create table Block( Height integer, Belong integer, Nonce string )" );
			String hostAddress = InetAddress.getLocalHost().getHostAddress();
			stmt.execute( "insert into Miner values (0,0,'"+hostAddress+"')" );
			//値を入力する
			//stmt.execute( "insert into Miner values (1,0,'0')" );

			//結果を表示する
			/*rs = stmt.executeQuery("select * from test1");
			while(rs.next()) {
				System.out.println(rs.getString("name"));
				System.out.println(rs.getInt("age"));
			}
			 */
		} catch (SQLException | UnknownHostException | ClassNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}



public static int Adminer(String IP,int belong) {
		try {
			ID=-2;
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:Mining.db");
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select max(ID) as maxID from Miner");
			while(rs.next()) {
			ID=rs.getInt("maxID")+1;
			}
			stmt.execute( "insert into Miner values ("+ID+","+belong+",'"+IP+"')" );

		} catch (SQLException  e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			return -200;

		}finally{
			if(conn != null) {
				try {
					//接続を閉じる
					conn.close();
				} catch (SQLException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			}
		}
		return ID;
	}

public static int chain(int Height,int belong,String nonce) throws SQLException {
	conn = DriverManager.getConnection("jdbc:sqlite:Mining.db");
	stmt = conn.createStatement();
	//自軍が存在すれば自軍の最大、自軍がなければ全体のさいだい
	stmt.execute( "insert into Block values ("+Height+","+belong+",'"+nonce+"')" );
	boolean opposite;
	int HosHeight=0;
	int GoodHeight=0;
	if(belong==1){
		GoodHeight=Height;
		rs = stmt.executeQuery("select Height from Block where belong=2");
		opposite=rs.next();
		//悪者ブロックの有無を確認
		if(opposite){
		rs = stmt.executeQuery("select max(Height) as maxID from Block where belong=2");
		while(rs.next()) {
			HosHeight=rs.getInt("maxID");
			}
		if(GoodHeight-HosHeight>4){
			stmt.execute("delete from Block where belong=2");
			conn.close();
			return 0;
		}
		conn.close();
		return 1;
		}
		conn.close();
		return 2;

	}else{
	HosHeight=Height;
	rs = stmt.executeQuery("select Height from Block where belong=1");
	opposite=rs.next();
	if(opposite){
	rs = stmt.executeQuery("select max(Height) as maxID from Block where belong=1");
	while(rs.next()) {
		GoodHeight=rs.getInt("maxID");
		}
	if(HosHeight-GoodHeight>4){
		stmt.execute("delete from Block where belong=1");

		//悪者が勝つ
		conn.close();
		return 3;
	}
	//悪者だけに通知
	conn.close();
	return 4;
	}
	}
	conn.close();
	return 5;
}
public static String[] IPs (){
	int Howmany=0;
	String[] a;
	try {
		Class.forName("org.sqlite.JDBC");
		conn = DriverManager.getConnection("jdbc:sqlite:Mining.db");
		stmt = conn.createStatement();
		rs = stmt.executeQuery("select max(ID) as maxID from Miner");
		while(rs.next()) {
		Howmany=rs.getInt("maxID");
		}
		a=new String[Howmany+1];
		for (int i=0;i<Howmany+1;i++){
		a[i]="";
		}
		rs = stmt.executeQuery("select * from Miner");
		while(rs.next()) {
			a[rs.getInt("ID")]=rs.getString("IP");
			}
			a[0]="";
		return a;
	} catch (SQLException | ClassNotFoundException  e) {
		e.printStackTrace();
	}finally{
		if(conn != null) {
			try {
				//接続を閉じる
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	return null;
}
public static  ResultSet GoodIPs (){
	try {
		Class.forName("org.sqlite.JDBC");
	conn = DriverManager.getConnection("jdbc:sqlite:Mining.db");
	stmt = conn.createStatement();
	rs = stmt.executeQuery("select * from Miner where belong=1");
	return rs;
	} catch (ClassNotFoundException | SQLException e) {
	}
	return null;
}
public static  ResultSet HostileIPs (){
	try {
		Class.forName("org.sqlite.JDBC");
	conn = DriverManager.getConnection("jdbc:sqlite:Mining.db");
	stmt = conn.createStatement();
	rs = stmt.executeQuery("select * from Miner where belong=2");
	return rs;
	} catch (ClassNotFoundException | SQLException e) {
	}
	return null;
}
public static void Delminer(int ID) {
	try {
	Class.forName("org.sqlite.JDBC");
	conn = DriverManager.getConnection("jdbc:sqlite:Mining.db");
	stmt = conn.createStatement();
	stmt.execute("delete from Miner where ID="+ID);

	} catch (SQLException | ClassNotFoundException e) {
		e.printStackTrace();
	}finally{
		if(conn != null) {
			try {
				//接続を閉じる
				conn.close();
			} catch (SQLException e) {
				//
				e.printStackTrace();
			}
		}
	}
}

public static boolean[][] vision(){
	boolean[][] validated = new boolean[2][20];
	for(int i =0;i<2;i++){
		for(int j =0;j<2;j++){
			validated[i][j]=false;
		}
	}
	try {
		conn = DriverManager.getConnection("jdbc:sqlite:Mining.db");
		stmt = conn.createStatement();
		rs = stmt.executeQuery("select * from Block ");
		while(rs.next()){
			int B=rs.getInt("Belong");
			int H=rs.getInt("Height");
			validated[B-1][H-1]=true;
		}
		conn.close();
		return validated;
	} catch (SQLException e) {
		e.printStackTrace();
	}
	
	
	return null;
}
}

