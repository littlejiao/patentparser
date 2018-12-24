package edu.kndev.dataparser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.kndev.mongo.entity.LawStatus;
import edu.kndev.mongo.entity.Patent;

public class DataByLawStatus {
	/**
	 * 法律状态数据：
	 * 获取专利的法律状态（有错误的，看看之后怎么改）,存储为LawStatus对象
	 */
	public LawStatus lawStatusData(List<Patent> list) {
		LawStatus law = new LawStatus();
		law.setAuthorizationPN(new ArrayList<>());
		law.setPublicPN(new ArrayList<>());
		
		for(Patent patent:list) {
			String ad = patent.getAd();
			String[] segments = ad.split(";|\n");
			for(String single:segments) {
				List<String> ap = getAP(single);
				if (ap.size() == 2) {
					//int appyear = Integer.parseInt(ap.get(0));
					String singlePatentNum = ap.get(1);
					if(isPubApp(singlePatentNum)) {
						law.getPublicPN().add(singlePatentNum);
					}else {
						law.getAuthorizationPN().add(singlePatentNum);
					}
				}
			}
		}
		law.setPublicCounts(law.getPublicPN().size());
		law.setAuthorizationCounts(law.getAuthorizationPN().size());
		return law;
		
	}
	
	/**
	 * 判断专利是否为申请公开专利
	 * 
	 */
	private boolean isPubApp(String pn) {
		Matcher m = Pattern.compile("(-)([A-Z]\\d?)").matcher(pn);
		String suffix = "";
		if(m.find()) {
			suffix = m.group(2);
		}
		if(suffix.contains("A")||suffix.contains("P")||suffix.contains("U")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 从一个字符串中获取申请年份及专利号,
	 * 
	 * @param ad(单独的一条申请信息,即数据库中ad字段分割后的字符串)
	 * @return 申请年份和专利号
	 */
	private List<String> getAP(String ad) {
		List<String> list = new ArrayList<>();
		String[] items = ad.trim().split("\\s+");
		if (items.length != 0) {
			// 取最后一个字符串，就是专利申请年
			String syear = items[items.length - 1].trim();
			if (isNumber(syear)) {
				list.add(syear);
			}
			// 取第一个字符串，就是专利号
			String patentNum = items[0].trim();
			list.add(patentNum);
		}
		return list;
	}
	/**
	 * 为了防止取到的最后一个值不是年份，做一次判断
	 * 
	 * @param year
	 * @return
	 */
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
