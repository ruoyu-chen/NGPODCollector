/**
 * 
 */
package cn.edu.bistu.ngpod.utils;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * @author hadoop
 *
 */
public class ConfigReader {

	private static HashMap<String, String> store = null;

	public static String getConfig(String key) {
		if (store == null) {
			reload();
		}
		return store.get(key);
	}

	/**
	 * 重新加载配置文件中的配置项
	 */
	private static synchronized void reload() {
		store = new HashMap<String, String>();
		try {
			Configuration config = new PropertiesConfiguration(
					"conf.properties");
			@SuppressWarnings("unchecked")
			Iterator<String> keys = config.getKeys();
			while (keys.hasNext()) {
				String key = keys.next();
				store.put(key, config.getString(key));
			}
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		// /home/hadoop/apache-tomcat-8.0.23/webapps/NGPODCollector/WEB-INF/classes/
		String basePath = ConfigReader.class.getResource("/").getPath();
		int index = basePath.indexOf("WEB-INF/classes/");
		if (index != -1) {
			// 路径中包含WEB-INF/classes/子串，说明当前类是位于Web容器中的，用容器的路径替换配置中的部分默认路径
			basePath = basePath.substring(0,index);
			store.put("PHOTO_DIR", basePath+"photos");
			store.put("WALLPAPER_DIR", basePath+"wallpaper");
			store.put("INDEX_DIR", basePath+"WEB-INF/index");
		}
	}
	
	public static void main(String[] args){
		String base = "/home/hadoop/apache-tomcat-8.0.23/webapps/NGPODCollector/WEB-INF/classes/";
		int index = base.indexOf("WEB-INF/classes/");
		if (index != -1) {
			// 路径中包含WEB-INF/classes/子串，说明当前类是位于Web容器中的，用容器的路径替换配置中的部分默认路径
			base = base.substring(0,index);
		}
		System.out.println(base);
	}
}
