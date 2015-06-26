/**
 * 
 */
package cn.edu.bistu.ngpod.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
/**
 * 对HttpClient的简单封装，
 * 提供HTML页面下载，
 * 文件下载等功能
 * @author chenruoyu
 *
 */
public class HttpHelper {
	
	/**
	 * HttpClient对象，公用
	 */
	private CloseableHttpClient client = null;
	
	/**
	 * 默认超时时间为15秒
	 */
	private int timeOut = 15000;

    private RequestConfig config = null;

	
	public HttpHelper(int timeOut){
		this.timeOut = timeOut;
		config = RequestConfig.custom()
	            .setConnectionRequestTimeout(timeOut)
	            .setSocketTimeout(timeOut)
	            .setConnectTimeout(timeOut).build();
	}
	
	public HttpHelper(){
		config = RequestConfig.custom()
	            .setConnectionRequestTimeout(timeOut)
	            .setSocketTimeout(timeOut)
	            .setConnectTimeout(timeOut).build();
	}
	
	/**
	 * 下载指定URL对应的文件，以指定的文件名保存到指定的目录
	 * @param url
	 * @param dir
	 * @param fileName
	 * @return
	 */
	public boolean getFile(String url, String dir, String fileName){
		client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(config);
		CloseableHttpResponse response = null;
        File outputFile = new File(dir, fileName);
        FileOutputStream output = null;
        HttpEntity entity = null;
		try {
			response = client.execute(httpGet);
		    entity = response.getEntity();
		    byte[] buffer = new byte[2048];
		    int read = 0;
		    output = new FileOutputStream(outputFile);
		    while ((read = entity.getContent().read(buffer)) != -1) {
		        output.write(buffer, 0, read);
		    }
		    output.flush();
		    return true;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				EntityUtils.consume(entity);
				response.close();
				output.close();
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public void setTimeOut(int timeOut) {
		if(this.timeOut!=timeOut){
			this.timeOut = timeOut;
			config = RequestConfig.custom()
		            .setConnectionRequestTimeout(timeOut)
		            .setSocketTimeout(timeOut)
		            .setConnectTimeout(timeOut).build();	
		}
	}
}
