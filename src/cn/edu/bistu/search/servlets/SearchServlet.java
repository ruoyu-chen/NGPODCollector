package cn.edu.bistu.search.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.document.Document;

import cn.edu.bistu.ngpod.index.LuceneSearch;

/**
 * Servlet implementation class SearchServlet
 */
@WebServlet("/search")
public class SearchServlet extends HttpServlet {
	private static final long serialVersionUID = 3941043306661276816L;

	private static final String NGPOD_URL_PREFIX = "http://photography.nationalgeographic.com/photography/photo-of-the-day/";

	private LuceneSearch searcher = null;
	
	/**
	 * @throws IOException
	 * @see HttpServlet#HttpServlet()
	 */
	public SearchServlet() throws IOException {
		super();
		searcher = LuceneSearch.getInstance();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		if(searcher == null){
			searcher = LuceneSearch.getInstance();
			if(searcher == null){
				return;
			}
		}
		String query = request.getParameter("query");
		String start = request.getParameter("start");
		String end = request.getParameter("end");
		int page = Integer.parseInt(request.getParameter("page"));
		List<Document> list = null;
		list = searcher.doSearch(start, end, query, page);
		PrintWriter writer = response.getWriter();
		writer.append("<!DOCTYPE html>" + "<html>" + "<head>"
				+ "<meta charset=\"UTF-8\">" + "<title>国家地理每日一图检索系统</title>"
				+ "</head>" + "<body>"
				+ "<form action=\"search\" method=\"post\">"
				+ "检索关键字：<input type=\"text\" name=query />"
				+ "起始时间：<input type=\"text\" name=\"start\" />"
				+ "结束时间：<input type=\"text\" name=\"end\" />"
				+ "<input type=\"hidden\" name=\"page\" value=\"1\">"
				+ "<input type=\"submit\" value=\"submit\" title=\"提交\" />"
				+ "</form>");
		if (list == null) {
			writer.append("<p>搜索出现异常！</p>");
			// request.setAttribute("MESSAGE", "搜索出现异常！");
		} else {
			writer.append("<p>搜索得到" + list.size() + "条结果！</p>");
			writer.append("<p>结果分页：");
			for (int i = 1; i < page; i++) {
				writer.append("<a href=\"search?query=" + query + "&start="
						+ start + "&end=" + end + "&page=" + i + "\">" + i
						+ "</a>");
			}
			writer.append(String.valueOf(page));
			if (list.size() == searcher.getHitsPerPage()) {
				writer.append("<a href=\"search?query=" + query + "&start="
						+ start + "&end=" + end + "&page=" + (page + 1)
						+ "\">下一页</a>");
			}
			writer.append("</p>");
			for (Document doc : list) {
				writer.append("<p>");
				String pubtime = doc.get("pubTimeRaw");
				writer.append("<a href=\"" + NGPOD_URL_PREFIX
						+ doc.get("pageId") + "\">" + doc.get("title") + "</a>");
				writer.append(" " + pubtime);
				String wall = doc.get("wallPaper");
				if (wall != null) {
					writer.append(" <a href=\"wallpaper/" + wall
							+ "\">下载壁纸</a>");
				}
				writer.append("</p>");

				writer.append("<p>");
				writer.append("<img alt=\""+doc.get("title")+"\" width=\"200\" src=\"photos/"
						+ doc.get("photo") + "\">");
				writer.append("</p>");

				writer.append("<p>");
				writer.append("描述：" + doc.get("description"));
				writer.append("</p>");
			}
		}
		writer.append("</body></html>");
		writer.close();
	}

}
