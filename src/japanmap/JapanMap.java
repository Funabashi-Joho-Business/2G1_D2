package japanmap;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.arnx.jsonic.JSON;

class JsonData{
	public String cmd;
	public int id;
	public String name;
	public String area;
	public String pc;
	public int pop;
	public int size;
}
class MapData{
	public int id;
	public String name;
	public String area;
	public String pc;
	public int pop;
	public int size;	
}
/**
 * Servlet implementation class JapanMap
 */
@WebServlet("/JapanMap")
public class JapanMap extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Oracle mOracle;
    private final static String DB_ID = "";
    private final static String DB_PASS = "";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public JapanMap() {
        super();
        // TODO Auto-generated constructor stub
    }
	public void init() throws ServletException {
		// TODO 自動生成されたメソッド・スタブ
		super.init();


		try{
			mOracle = new Oracle();
			mOracle.connect("ux4", DB_ID, DB_PASS);

			//テーブルが無ければ作成
			if(!mOracle.isTable("db_map"))
			{
				mOracle.execute("create table db_map(id number,name varchar2(200),area varchar2(200),pc varchar2(200),pop number,msize number)");
				mOracle.execute("create sequence db_map_seq");
			}
		} catch (Exception e) {
			System.err.println("認証に失敗しました");
		}
	}

	@Override
	public void destroy() {
		//DB切断
		mOracle.close();
		// TODO 自動生成されたメソッド・スタブ
		super.destroy();
	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		action(request,response);
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		action(request, response);
	}
	private void action(HttpServletRequest request, HttpServletResponse response) throws IOException {
		// TODO 自動生成されたメソッド・スタブ
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter out = response.getWriter();

        //データの受け取り処理
        JsonData jsonData = null;
        try {
			jsonData = JSON.decode(request.getInputStream(),JsonData.class);
		} catch (Exception e) {	}
        
        
        if(jsonData!=null)
        {
        	if("write".equals(jsonData.cmd))
	        {
				//mOracle.execute("create table db_map(id number,name varchar2(200),area varchar2(200),pc varchar2(200),pop number,size number)");
        		if(jsonData.id == 0){
            		//書き込み処理
    	        	String sql = String.format(
    	        			"insert into db_map values(db_map_seq.nextval,'%s','%s','%s',%d,%d)",
    	        			jsonData.name,jsonData.area,jsonData.pc,jsonData.pop,jsonData.size);
    	        	mOracle.execute(sql);        			
        		}else{
        			//更新
    	        	String sql = String.format(
    	        			"update db_map set name='%s',area='%s',pc='%s',pop=%d,msize=%d where id=%d",
    	        			jsonData.name,jsonData.area,jsonData.pc,jsonData.pop,jsonData.size,jsonData.id);
    	        	mOracle.execute(sql);         			
        		}
	            //出力
	            out.println("{\"ret\":true};");	
	        }		
        	else if("list".equals(jsonData.cmd)){
				ArrayList<MapData> list = new ArrayList<MapData>();
				ResultSet res = mOracle.query("select id,name from db_map order by area");
				try {
					while(res.next())
					{
						MapData sendData = new MapData();
						sendData.id = res.getInt(1);
						sendData.name = res.getString(2);
						list.add(sendData);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
				//JSON形式に変換
	            String json = JSON.encode(list);
	            //出力
	            out.println(json);	        	
        	}
        	else if("read".equals(jsonData.cmd)){
				ArrayList<MapData> list = new ArrayList<MapData>();
				String sql = String.format("select * from db_map where id='%d'",jsonData.id);
				ResultSet res = mOracle.query(sql);
				MapData mapData = new MapData();
				try {
					if(res.next()){
						mapData.id = res.getInt(1);
						mapData.name = res.getString(2);
						mapData.area = res.getString(3);
						mapData.pc = res.getString(4);
						mapData.pop = res.getInt(5);
						mapData.size = res.getInt(6);
					}
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				//JSON形式に変換
	            String json = JSON.encode(mapData);
	            //出力
	            out.println(json);	
	            
				try {
					while(res.next())
					{
						MapData sendData = new MapData();
						sendData.id = res.getInt(1);
						sendData.name = res.getString(2);
						list.add(sendData);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
	        	
        	}       		
        }
	}
}
