package edu.kndev.dataparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import edu.kndev.mongo.entity.Patent;
import edu.kndev.mongo.entity.PriorityCountry;

public class DataByCountry {
	
	/**
	 * 最早优先权国家数据：
	 * 从一个存储所有最早优先权国家的集合中获取每个国家的最早优先权数量,并用对象存储
	 */
	public List<PriorityCountry> countryData(List<Patent> list) {
		List<PriorityCountry> pri = new ArrayList<>();
		Map<String,Integer> map =new HashMap<>();
		List<String> records = getPriCountry(list);
		
		for(String record:records) {
//			if(record.equals("")) {
//				continue;
//			}
			if(!map.containsKey(record)) {
				map.put(record, 1);
			}else {
				map.put(record, map.get(record)+1);
			}
		}
		for(Map.Entry<String, Integer> entry:map.entrySet()) {
			PriorityCountry cou = new PriorityCountry();
			String country = entry.getKey();
			int number = entry.getValue();
			
			//System.out.println(country+"\t"+number);
			cou.setCountry(entry.getKey());
			cou.setNumber(entry.setValue(entry.getValue()));
			pri.add(cou);
		}
		
		Collections.sort(pri,new Comparator<PriorityCountry>() {

			@Override
			public int compare(PriorityCountry o1, PriorityCountry o2) {
				// TODO Auto-generated method stub
				return o2.getNumber()-o1.getNumber();
			}
		});
		return pri;
	}
	/**
	 * 获取每个记录对应的最早优先权国家
	 */
	private List<String> getPriCountry(List<Patent> list) {
		List<String> priCountries = new LinkedList<>();
		for(Patent patent:list) {
			String pi = patent.getPi();
			Map<String, String> map = new HashMap<>();
			map.put("Jan", "01");
			map.put("Feb", "02");
			map.put("Mar", "03");
			map.put("Apr", "04");
			map.put("May", "05");
			map.put("Jun", "06");
			map.put("Jul", "07");
			map.put("Aug", "08");
			map.put("Sep", "09");
			map.put("Oct", "10");
			map.put("Nov", "11");
			map.put("Dec", "12");

			int mintime = 99999999;
			String priCountry = "";
			String[] priInfo = pi.split("\n|;");
			//防止pi有空数据
			if(pi.length()<=0) {
				continue;
			}
			//从一条pi信息中抓取最早优先权国家
			for (String singleInfo : priInfo) {
				StringBuffer sb = new StringBuffer();
				String[] block = singleInfo.trim().split("\\s+");
				if (block.length == 4) {
					String country_temp = block[0].substring(0, 2);
					String country = country_temp;
					String day = block[1];
					String month = map.get(block[2]);
					String year = block[3];
					sb.append(year);
					sb.append(month);
					sb.append(day);
					String date = sb.toString();
					if (isNumber(date)) {
						int temp = Integer.parseInt(date);
						if (temp < mintime) {
							mintime = temp;
							priCountry = country;
						}
					}
				}else {//防止pi里面的某一条有空数据
					continue;
				}
			}
			priCountries.add(priCountry);
		}
		
		return priCountries;
	}
	/**
	 * 判断一个字符串是否为数值型字符串
	 * 
	 * @param year
	 * @return
	 */
	public boolean isNumber(String year) {
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
