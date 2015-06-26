/**
 * 
 */
package cn.edu.bistu.ngpod.core;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import cn.edu.bistu.ngpod.utils.ConfigReader;
import cn.edu.bistu.ngpod.utils.DateFormatter;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

/**
 * 将抓取的内容持久化到Lucene索引中的PipeLine实现
 * 
 * @author chenruoyu
 *
 */
public class LucenePipeLine implements Pipeline {
	private static final Logger log = Logger.getLogger(LucenePipeLine.class);
	private IndexWriter writer = null;
	private FieldType desc = null;	
	private static long counter = 0;

	// /Users/chenruoyu/Documents/workspace/NGPODCollector/index
	private static final String indexDir = ConfigReader.getConfig("INDEX_DIR");

	public LucenePipeLine() throws IOException {
		StandardAnalyzer analyzer = new StandardAnalyzer();
		File dir = new File(indexDir);
		Directory index = FSDirectory.open(dir.toPath());
		IndexWriterConfig config = new IndexWriterConfig(analyzer);
		writer = new IndexWriter(index, config);
		if(desc==null){
			desc = new FieldType();
			desc.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS_AND_OFFSETS);
			desc.setStoreTermVectors(true);
			desc.setStored(true);
			desc.setTokenized(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * us.codecraft.webmagic.pipeline.Pipeline#process(us.codecraft.webmagic
	 * .ResultItems, us.codecraft.webmagic.Task)
	 */
	@Override
	public void process(ResultItems results, Task task) {
		if(results.isSkip()){
			//首页不写入索引
			log.info("跳过页面:"+results.getRequest().getUrl());
			return;
		}
		//写入索引前，先检查当前页面是否已经索引过了
		String pageId = results.get("pageId").toString();
		Document doc = new Document();
		doc.add(new TextField("title", results.get("title").toString(),
				Store.YES));
		doc.add(new TextField("credit", results.get("credit").toString(),
				Store.YES));
		//处理时间
		String pubTime = results.get("pubTime").toString();		
		if(pubTime==null||"".equals(pubTime)){
			log.error("发布日期字段为空");
		}
		//将原始的日期表示保存起来，但不建立索引
		doc.add(new StoredField("pubTimeRaw",pubTime));
		int date = 0;
		try {
			date=DateFormatter.format2Num(pubTime);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		//将解析为整数后的日期以整数类型字段建立索引
		doc.add(new IntField("pubTime", date, Store.YES));
		doc.add(new StringField("pageId", pageId, Store.YES));
		doc.add(new StoredField("photo", results.get("photo").toString()));
		if(results.get("wallPaper")!=null){
			//存在壁纸，将壁纸文件名保存起来
			doc.add(new StoredField("wallPaper", results.get("wallPaper").toString()));
			//使用一个StringField作为是否存在壁纸的标识符
			doc.add(new StringField("hasWallPaper","TURE",Store.NO));
		}
		
		@SuppressWarnings("unchecked")
		List<String> descriptions = (List<String>) results.get("descriptions");
		if(descriptions.get(2).contains("This Month in Photo of the Day")){
			doc.add(new Field("description", descriptions.get(3), desc));
			log.debug("Description:"+descriptions.get(3));
		}else{
			doc.add(new Field("description", descriptions.get(2), desc));
			log.debug("Description:"+descriptions.get(2));
		}

		try {
			//writer.addDocument(doc);
			writer.updateDocument(new Term("pageId",pageId), doc);
			counter++;
			log.debug("["+counter+"]将页面ID为"+pageId+"的页面写入索引");
			writer.commit();
		} catch (IOException e) {
			log.error("["+counter+"]将页面ID为"+results.get("pageId").toString()+"的页面写入索引时失败");
			e.printStackTrace();
		}
	}
	
	public void close(){
		if(writer!=null){
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
