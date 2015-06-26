/**
 * 
 */
package cn.edu.bistu.ngpod.index;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import cn.edu.bistu.ngpod.utils.ConfigReader;
import cn.edu.bistu.ngpod.utils.DateFormatter;

//import org.apache.lucene.util.BytesRef;

/**
 * 基于Lucene的检索
 * 
 * @author chenruoyu
 */
public class LuceneSearch {

	private static final Logger log = Logger.getLogger(LuceneSearch.class);
	
	private int hitsPerPage = 10;

	private DirectoryReader dirr = null;
	
	private IndexSearcher searcher = null;

	private QueryParser parser = null;	
	
	private static final String INDEX_DIR=ConfigReader.getConfig("INDEX_DIR");
	
	private static final int START=20090101;
	
	private static LuceneSearch instance = null;
	
	private LuceneSearch() throws IOException {
		this(10);
	}
	
	private LuceneSearch(int hitsPerPage) throws IOException {
		this.hitsPerPage = hitsPerPage;
		if (searcher == null) {
			Analyzer analyzer = new StandardAnalyzer();
			File file = new File(INDEX_DIR);
			Directory dir = FSDirectory.open(file.toPath());
			dirr = DirectoryReader.open(dir);
			searcher = new IndexSearcher(dirr);
			parser = new QueryParser("description", analyzer);
		}
	}

	public static LuceneSearch getInstance(){
		if(instance == null){
			try {
				instance = new LuceneSearch();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
		return instance;
	}
	

	public Document getRandomPod(){
		try{
			DirectoryReader nr = DirectoryReader.openIfChanged(dirr);
			if(nr == null){
				//nr为null，说明自从上次打开目录以来，索引没有发生变化，什么都不必做
				log.info("索引没有发生变化");
			}else{
				log.info("索引发生了变化");
				dirr.close();
				searcher = new IndexSearcher(nr);
				dirr = nr;
			}
			int docs = dirr.getDocCount("title");
			int doc = 0;
			if(docs>0){
				Random ran = new Random(System.currentTimeMillis());
				doc = ran.nextInt(docs);
				return dirr.document(doc);
			}else{
				return null;
			}
		}catch(IOException e){
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Document> doSearch(String start, String end, String query,int page){
		int startDate = 0;
		if (start == null || "".equals(start)) {
			log.info("起始日期字段为空");
			startDate=START;
		}else{
			try {
				startDate = Integer.parseInt(start);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				log.error("起始日期格式不正确:"+start);
				startDate = START;
			}			
		}
		int endDate = 0;
		if (end == null || "".equals(end)) {
			log.info("结束日期字段为空");
			endDate = DateFormatter.format2Int(null);
		} else {
			try {
				endDate = Integer.parseInt(end);
			} catch (NumberFormatException e) {
				e.printStackTrace();
				log.error("结束日期格式不正确");
				endDate = DateFormatter.format2Int(null);
			}
		}
		BooleanQuery bq = new BooleanQuery();
		Query q = NumericRangeQuery.newIntRange("pubTime", startDate,
				endDate, true, true);
		bq.add(q, Occur.MUST);
		if(query==null||"".equals(query)){
			
		}else{
			Query nq;
			try {
				nq = parser.parse(query);
				bq.add(nq, Occur.MUST);
			} catch (ParseException e) {
				log.error("提供给QueryParser的索引串存在错误:"+query);
				e.printStackTrace();
			}
		}
		ArrayList<Document> results = new ArrayList<>(hitsPerPage);
		int startRec = hitsPerPage * (page - 1);
		int endRec = hitsPerPage * page;
		TopDocs hits;
		try {
			DirectoryReader nr = DirectoryReader.openIfChanged(dirr);
			if(nr == null){
				//nr为null，说明自从上次打开目录以来，索引没有发生变化，什么都不必做
				log.info("索引没有发生变化");
			}else{
				log.info("索引发生了变化");
				dirr.close();
				searcher = new IndexSearcher(nr);
				dirr = nr;
			}
			hits = searcher.search(bq, endRec);
			ScoreDoc[] docs = hits.scoreDocs;
			for (int i = startRec; i <endRec && i< docs.length; i++) {
				results.add(searcher.doc(docs[i].doc));
			}
		} catch (IOException e) {
			log.error("搜索过程出现异常");
			e.printStackTrace();
		}
		return results;
	}

	public int getHitsPerPage() {
		return hitsPerPage;
	}
}
