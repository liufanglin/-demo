package com.example.filedemo;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.briup.bean.Result;
import com.google.gson.Gson;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
/*Accept: text/plain, 
Accept-Language: zh-cn 
Host: 192.168.24.56
Content-Type:multipart/form-data;boundary=-----------------------------7db372eb000e2
User-Agent: WinHttpClient 
Content-Length: 3693
Connection: Keep-Alive

-------------------------------7db372eb000e2

Content-Disposition: form-data; name="file"; filename="kn.jpg"

Content-Type: image/jpeg

(此处省略jpeg文件二进制数据...）

-------------------------------7db372eb000e2--
*/



public class MainActivity extends Activity {
	private Map<String, String> params;  
	private String urlPath=null;  
	Gson gson = new Gson();
	private TextView tv_show;
	//private String filePath=Environment.getExternalStorageDirectory().getPath()+"/0/7.png";
	private String filePath="file:///android_asset/www.png";
	private InputStream is;
	private File file=new File(filePath);
	protected static final int FAILTURE = 0;	
	protected static final int SUCCESS=1;
    public void initData(){    
        urlPath="http://192.168.216.27:8080/SpringMVC8/user/1/photo";  
        params=new HashMap<>(); 
        params.put("imgFile", "www.png");  
     // params.put("id", "1");          
        }
    private Handler handler =new Handler(){
		public void handleMessage(Message msg) {
			if (msg.what==FAILTURE) {
				Toast.makeText(MainActivity.this, "请求失败", 0).show();
			}
			if (msg.what==SUCCESS) {
				String str=(String) msg.obj;
				tv_show.setText(str);
				Toast.makeText(MainActivity.this, "请求成功", 0).show();
			}
			
		}
	};
    
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv_show=(TextView) findViewById(R.id.textView1);
		initData();
		System.out.println(filePath);
		
	}
	public void myStart(View view){
		new Thread(){
			public void run() {
				try {
					is=getAssets().open("www.png");
				} catch (IOException e) {
					e.printStackTrace();
				}
			SendBomb(file,urlPath, params);
				//sendPostRequest(urlPath,params);
			
			
			}
		}.start();
		
		
	}
	
	
	public void SendBomb(File file,String path,Map<String, String> params) {  
		 String BOUNDARY = UUID.randomUUID().toString(); 
	     String PREFIX = "--" ;
	     String LINE_END = "\r\n"; 
        StringBuilder sb = new StringBuilder();  
        if(params!=null &params.size()!=0){  
            for (Map.Entry<String, String> entry : params.entrySet()) {  
  
                   /*try {
					sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8"));
                    sb.append("&");   
                   } catch (UnsupportedEncodingException e) {
                	   
   					e.printStackTrace();
   				}  */
            	 String value = entry.getValue();  
                 sb.append("--" + BOUNDARY + "\r\n");  
                 sb.append("Content-Disposition: form-data; name=\"" + entry.getKey()+"\"\r\n");  
                 sb.append("\r\n");  
                 sb.append(URLEncoder.encode(value) + "\r\n");
            	
                // Content-Disposition: form-data; name="img"; filename="t.txt" （\r\n）
  
            }  
         //   sb.deleteCharAt(sb.length()-1);  
  
        byte[] entity = sb.toString().getBytes();  
        try {
         URL url=new URL(path);
         System.out.println("-==00");
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
         conn.setDoOutput(true);  
         conn.setUseCaches(false);  
         conn.setConnectTimeout(2000); //连接超时为10秒  
         conn.setRequestMethod("POST");  
         conn.setRequestProperty("connection", "keep-alive");    
         conn.setRequestProperty("Content-Type", "multipart/form-data"+ ";boundary=" + BOUNDARY);   
         DataOutputStream dos=null;
         if(file!=null) {  
        	 if((android.os.Environment.getExternalStorageState().equals(  
        			    android.os.Environment.MEDIA_MOUNTED))){
             OutputStream outputSteam=conn.getOutputStream();    
             dos= new DataOutputStream(outputSteam);   
             dos.write(entity); 
             
             StringBuffer buffer = new StringBuffer();    
             buffer.append(PREFIX);    
             buffer.append(BOUNDARY); 
             buffer.append(LINE_END);    
             
             buffer.append("Content-Disposition: form-data; name=\"imgFile\"; filename=\""+file.getName()+"\""+LINE_END);   
             buffer.append("Content-Type: application/octet-stream; charset="+"UTF-8"+LINE_END);    
             buffer.append(LINE_END);   
             //写请求头
             dos.write(buffer.toString().getBytes());    
            // InputStream is = new FileInputStream(file);   
             byte[] bytes = new byte[1024];    
             int len = 0;  
            // 写二进制数据文件
           
             while((len=is.read(bytes))!=-1)    
             {    
                dos.write(bytes, 0, len);    
             }    
             is.close();    
             ///r/n
             dos.write(LINE_END.getBytes());    
           //  -------------------------------7db372eb000e2--/r/n
             byte[] end_data = (PREFIX+BOUNDARY+PREFIX+LINE_END).getBytes();    
             dos.write(end_data);    
             dos.flush();  
             dos.close(); 
        	 }
        	 else {
				Toast.makeText(this, "sd卡不可用", 0).show();
			}
         }
         System.out.println("返回码"+conn.getResponseCode());
         if (conn.getResponseCode() == 200) {  
        	InputStream is = conn.getInputStream();
        	StringBuilder builder=new StringBuilder();
    		BufferedReader reader=new BufferedReader(new InputStreamReader(is));
    		String line=null;
    		while (((line=reader.readLine()))!=null) {
    			builder.append(line);
            }  
    		reader.close();
    		
    		Result result = gson.fromJson(builder.toString(), Result.class);
    		System.out.println(result+"-------------------------");
    		
			System.out.println(builder.toString()+"==================++");
			Message msg=Message.obtain();
			msg.obj=builder.toString();
			msg.what=SUCCESS;
			handler.sendMessage(msg);
         }
        } catch (Exception e) {
        	Message msg=Message.obtain();
			msg.what=FAILTURE;
			handler.sendMessage(msg);
			e.printStackTrace();
		}  
      
     }  
	}
	

	
	
	public void sendPostRequest(String path,Map<String, String> params) {  
	     
        StringBuilder sb = new StringBuilder();  
        if(params!=null &params.size()!=0){  
            for (Map.Entry<String, String> entry : params.entrySet()) {  
  
                   try {
					sb.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8"));
                    sb.append("&");   
                   } catch (UnsupportedEncodingException e) {
   					e.printStackTrace();
   				}  
  
            }  
            //我们要删除最后一个&
            sb.deleteCharAt(sb.length()-1);  
        }  
  
        //entity为请求体部分内容  
        byte[] entity = sb.toString().getBytes();  
       
        try {
        	 System.out.println("======");
         URL url = new URL(path);
         
         System.out.println("----------------------------");
         HttpURLConnection conn = (HttpURLConnection) url.openConnection();  
    
         conn.setConnectTimeout(2000);  
         conn.setRequestMethod("POST");  
         conn.setDoOutput(true);  
         conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");  
         conn.setRequestProperty("Content-Length", entity.length+"");  
         OutputStream out = conn.getOutputStream();  
         out.write(entity);  
         out.flush();    
         out.close();  
         System.out.println("===================");
         System.out.println(conn.getResponseCode());
         if (conn.getResponseCode() == 200) {
        	 
        	 Toast.makeText(this, "post请求成功", 0).show();
            }  
         if(conn!=null)  
                conn.disconnect();  
        } catch (Exception e) {
			e.printStackTrace();
		}  
      
     }  
        

	
}

