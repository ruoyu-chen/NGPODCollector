package cn.edu.bistu.search.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import us.codecraft.webmagic.Spider;
import cn.edu.bistu.ngpod.core.LucenePipeLine;
import cn.edu.bistu.ngpod.core.NgpodPageProcessor;
import cn.edu.bistu.ngpod.utils.ConfigReader;

/**
 * 爬虫Servlet，当服务启动时自动加载（通过在web.xml中配置load-on-startup参数为0来实现），
 * 负责启动和停止WebMagic爬虫
 */
@WebServlet("/CrawlerServlet")
public class CrawlerServlet extends HttpServlet {
	private static Spider spider = null;
	private static LucenePipeLine lucene = null;
	private static final Logger log = Logger.getLogger(CrawlerServlet.class);
	@Override
	public void init() throws ServletException {
		super.init();
		try {
			log.info("NGPOD爬虫启动");
			lucene = new LucenePipeLine();
			spider = Spider.create(new NgpodPageProcessor()).addUrl(ConfigReader.getConfig("START_PAGE")).addPipeline(lucene);
			spider.runAsync();
		} catch (IOException e) {
			log.error("WebMagic爬虫初始化失败");
			e.printStackTrace();
		}
	}	

	@Override
	public void destroy() {
		super.destroy();
		log.info("NGPOD爬虫停止");
		if(spider!=null){
			spider.stop();
		}
		if(lucene!=null){
			lucene.close();
		}
	}



	private static final long serialVersionUID = -3457094261246450029L;

	/**
     * @see HttpServlet#HttpServlet()
     */
    public CrawlerServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
