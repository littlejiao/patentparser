package edu.kndev.dataparser;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.xdtech.las.crawler.ds.diidw.PatentFetcher;
import com.xdtech.las.crawler.util.CommonUtil;

import edu.kndev.mongo.entity.CorePatent;

public class DataByCorePatent {
	private CloseableHttpClient client = null;

	public static void main(String[] args) {
		DataByCorePatent dbcp = new DataByCorePatent();
		String query = "ts=(\"molecul* breed*\") AND IP=(A01H* or C12N*)";
		for(CorePatent cp : dbcp.getCite(query)) {
			System.out.println(cp.getPatentNum()+"\t"+cp.getCitedCount()+"\t"+cp.getApplicant());
			
		}
	}

	/**
	 * 重点核心专利数据：获取被引次数最高的前十个专利
	 * 
	 * @param query
	 */
	public List<CorePatent> getCite(String query) {
		PatentFetcher pfetcher = new PatentFetcher();
		Map<String, String> result = pfetcher.getCited(query);
		String url = result.get("resultUrl");
		// System.out.println(url);
		String s = url.replace("&doc=1", "");
		String urlByCitedCounts = s.replace("&update_back2search_link_param=yes",
				"&parentProduct=DIIDW&page=1&action=sort&sortBy=LC.D;PY.D;AU.A;SO.A&showFirstPage=1&isCRHidden=false");
		List<CorePatent> cores = crawlerResult(urlByCitedCounts);
		return cores;
		// System.out.println(l);
	}

	// /**
	// * 获取所有记录中第一个专利号对应的被引次数
	// * @param query
	// * @return
	// */
	// private List<CorePatent> getCite(String query) {
	// PatentFetcher pfetcher = new PatentFetcher();
	// Map<String, String> result = pfetcher.getCited(query);
	// // String referer = result.get("historyUrl");
	// String pag = result.get("pages");
	// int page = 0;
	// if (pag != null && pag.trim().length() > 0) {
	// page = Integer.parseInt(pag);
	// }
	// String resultUrl = result.get("resultUrl");
	// // String sessionId = result.get("sessionId");
	// // String page5 = resultUrl+"&page=5";
	// // System.out.println("1111111111111111");
	// // System.out.println(page5);
	// // List<String[]> crawler = crawlerResult(page5);
	// // System.out.println("111111");
	// // for(String[] sarr:crawler) {
	// // System.out.println(Arrays.toString(sarr));
	// // }
	// List<CorePatent> cores = new LinkedList<>();
	// for (int i = 1; i <= page; i++) {
	// String pageUrl = resultUrl + "&page=" + i;
	// try {
	// Thread.sleep(1000);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// List<CorePatent> crawler = crawlerResult(pageUrl);
	// int cycle = 3;
	// while (cycle > 0) {
	// if (crawler.size() == 0) {
	// crawler = crawlerResult(pageUrl);
	// } else {
	// break;
	// }
	// cycle--;
	// }
	// cores.addAll(crawler);
	// }
	// return cores;
	// }

	/**
	 * 解析一个页面的核心专利相关信息，存储为一个list
	 * 
	 * @param url
	 * @return
	 */
	private List<CorePatent> crawlerResult(String url) {
		if (client == null) {
			client = HttpClientBuilder.create().build();
		}
		// List<String[]> list = new LinkedList<>();
		List<CorePatent> list = new LinkedList<>();
		HttpGet get = new HttpGet(url);
		get.setConfig(CommonUtil.getRequestConfig(100000, 60000));
		// CommonUtil.addHeader(get, referer);
		// get.addHeader("Cookie","JSESSIONID="+sessionId);
		// get.addHeader("Host","apps.webofknowledge.com");
		// get.addHeader("Upgrade-Insecure-Requests","1");
		// get.addHeader("Connection","keep-alive");
		CloseableHttpResponse response = null;
		try {
			response = client.execute(get);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			HttpEntity entity = response.getEntity();
			// String s = EntityUtils.toString(entity);
			// System.out.println(s);
			Document doc = Jsoup.parse(EntityUtils.toString(entity));
			// System.out.println(doc);
			Elements elements = doc.getElementsByAttributeValue("class", "search-results-content");
			for (Element ele : elements) {
				CorePatent cp = new CorePatent();
				// String[] data = new String[4];
				String citeCount = ele.child(0).child(0).text();
				// data[1] = citeCount;
				if (isNumber(citeCount)) {
					int count = Integer.parseInt(citeCount);
					cp.setCitedCount(count);
				}
				String title = ele.child(1).child(0).child(1).child(0).text();
				// data[2] = title;
				cp.setTitle(title);
				int index = 2;
				if (ele.child(index).tagName().equals("div")) {
					index = 2;
				} else {
					index = 3;
				}
				String[] people = ele.child(index).text().split(":|;");
				if (people.length >= 2) {
					String person = people[1];
					// data[3] = person;
					cp.setApplicant(person);
				}
				String single = ele.text();
				Matcher matcher = Pattern.compile("[A-Z]{2}\\d+-[A-Z]\\d?").matcher(single);
				if (matcher.find()) {
					String firstPN = matcher.group();
					// data[0] = firstPN;
					cp.setPatentNum(firstPN);
				}
				list.add(cp);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				response.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return list;
	}

	private boolean isNumber(String year) {
		boolean valid = true;
		try {
			Integer.parseInt(year);
		} catch (Exception e) {
			// TODO: handle exception
			valid = false;
		}
		return valid;
	}
}
