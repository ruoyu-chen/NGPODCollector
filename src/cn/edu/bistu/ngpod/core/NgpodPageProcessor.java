package cn.edu.bistu.ngpod.core;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import cn.edu.bistu.ngpod.utils.ConfigReader;
import cn.edu.bistu.ngpod.utils.HttpHelper;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

public class NgpodPageProcessor implements PageProcessor {

	public static final String START_PAGE = ConfigReader.getConfig("START_PAGE");

	public static final String photoDir = ConfigReader.getConfig("PHOTO_DIR");

	public static final String wallPaperDir = ConfigReader.getConfig("WALLPAPER_DIR");

	private static final Logger log = Logger.getLogger(NgpodPageProcessor.class);

	// 抓取网站的相关配置，包括编码、抓取间隔、重试次数等
	private Site site = Site.me().setCycleRetryTimes(20).setSleepTime(2000)
			.setTimeOut(20000);

	public Site getSite() {
		return site;
	}

	private HttpHelper http = null;

	public NgpodPageProcessor() {
		http = new HttpHelper();
	}

	// *[@id="content_top"]/div[2]/a/img
	public void process(Page page) {
		// 定制爬虫逻辑的核心方法，在这里编写抽取逻辑
		if (page.getRequest().getUrl().equalsIgnoreCase(START_PAGE)) {
			for (String url : page.getHtml()
					.xpath("//div[@class='nav']/p/a/@href").all()) {
				page.addTargetRequest(url);
			}
			page.addTargetRequest(page.getHtml()
					.xpath("//link[@rel=\"canonical\"]/@href").toString());
			page.setSkip(true);
			return;
		}

		// 部分二：定义如何抽取页面信息，并保存下来
		// 图片标题
		String title = page.getHtml().xpath("//div[@id='caption']/h2/text()")
				.toString();
		page.putField("title", title);
		// 作者信息
		String credit = page.getHtml()
				.xpath("//div[@id='caption']/p[@class='credit']/allText()")
				.toString();
		page.putField("credit", credit);
		// 发布日期
		String pubTime = page
				.getHtml()
				.xpath("//div[@id='caption']/p[@class='publication_time']/text()")
				.toString();
		page.putField("pubTime", pubTime);
		// 页面ID
		String pageId = page
				.getUrl()
				.regex("http://photography.nationalgeographic.com/photography/photo-of-the-day/(.+)/")
				.toString();
		page.putField("pageId", pageId);
		log.info("抓取页面：" + pageId);
		// 图片URL
		String photoURL = page
				.getHtml()
				.xpath("//div[@id=\"content_top\"]/div[@class=\"primary_photo\"]//img/@src")
				.toString();
		String fileName = photoURL.substring(photoURL.lastIndexOf('/') + 1);
		downloadFile(photoDir, fileName, "http:" + photoURL, 4);
		page.putField("photo", fileName);

		// //*[@id="content_mainA"]/div[1]/div/div[1]/div[2]
		// 部分页面存在壁纸下载，XPath路径为：//div[@id="content_mainA"]//div[@class="download_link"]/a/@href
		String wallpaperURL = page
				.getHtml()
				.xpath("//div[@id=\"content_mainA\"]//div[@class=\"download_link\"]/a/@href")
				.toString();
		if (wallpaperURL != null) {
			String wallpaperFile = wallpaperURL.substring(wallpaperURL
					.lastIndexOf('/') + 1);
			log.debug("存在壁纸链接：" + wallpaperURL);
			downloadFile(wallPaperDir, wallpaperFile, wallpaperURL, 4);
			page.putField("wallPaper", wallpaperFile);
		}
		List<String> descriptions = page.getHtml()
				.xpath("//div[@id=\"caption\"]/p/allText()").all();
		page.putField("descriptions", descriptions);
		// 部分三：从页面发现后续的url地址来抓取
		List<String> urls = page.getHtml()
				.xpath("//div[@class='nav']/p/a/@href").all();
		for (String url : urls) {
			log.info("添加待抓取页面地址：" + url);
			page.addTargetRequest(url);
		}
	}

	private boolean downloadFile(String dir, String fileName, String url,
			int maxRetry) {
		if (exists(dir, fileName)) {
			// 文件已经存在，不要重复下载
			log.debug("文件存在");
			return true;
		}
		int retry = 0;
		while (retry < maxRetry) {
			if (http.getFile(url, dir, fileName)) {
				return true;
			} else {
				retry++;
				log.error("图片" + url + "下载失败" + ",重试第" + retry + "次！");
			}
		}
		log.error("图片" + url + "下载失败");
		return false;
	}

	private boolean exists(String dir, String file) {
		File f = new File(dir, file);
		return f.exists();
	}

	public static void main(String[] args) throws IOException {
		LucenePipeLine lucene = new LucenePipeLine();
		Spider spider = Spider
				.create(new NgpodPageProcessor())
				// 设置起始URL
				.addUrl(START_PAGE).addPipeline(lucene);
		spider.run();
	}
}
