package edu.kndev.dataparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.kndev.mongo.entity.Applicant;
import edu.kndev.mongo.entity.ImpoApplicant;
import edu.kndev.mongo.entity.Patent;
import edu.kndev.mongo.entity.PatentNumByYear;

public class DataByApplicant {
	public static void main(String[] args) {
		
	}

	/**
	 * 主要申请人专利技术数据
	 * 
	 * 主要申请人年度趋势数据
	 */
	public List<ImpoApplicant> trendByApplicant(List<Patent> list) {
		List<ImpoApplicant> ims = new ArrayList<>();
		List<Applicant> im = getAppTop10(list);
		// System.out.println(im);
		// System.out.println(im.size());
		// for(Applicant s:im) {
		// System.out.println(s);
		// }
		for (Applicant impo : im) {
			// 创建主要申请人相关数据的对象
			ImpoApplicant impoapplicant = new ImpoApplicant();
			// 添加主要的ipc号
			String mainipc = getMain(impo.getIpcNums());
			impoapplicant.setIpcNum(mainipc);
			String name = impo.getName();
			impoapplicant.setApplicantName(name);

			Map<Integer, Integer> map = impo.getPatentCountsByYear();
			List<PatentNumByYear> pnby = new ArrayList<>();
			for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
				PatentNumByYear pny = new PatentNumByYear();
				int year = entry.getKey();
				int pnCounts = entry.getValue();
				pny.setYear(year);
				pny.setPatentCounts(pnCounts);
				pnby.add(pny);
			}
			impoapplicant.setPnbyYear(pnby);
			ims.add(impoapplicant);

		}
		return ims;
	}

	/**
	 * 找出存储ipc号的列表中次数出现最多的ipc分类号
	 * 
	 * @param ipc
	 */
	private String getMain(List<String> ipc) {
		// TODO Auto-generated method stub
		String maxIpc = "";
		Map<String, Integer> map = new HashMap<>();
		for (String s : ipc) {
			if (map.containsKey(s)) {
				map.put(s, map.get(s) + 1);
			} else {
				map.put(s, 1);
			}
		}
		int max = 0;
		for (Map.Entry<String, Integer> entry : map.entrySet()) {
			if (entry.getValue() > max) {
				max = entry.getValue();
				maxIpc = entry.getKey();
			}
		}
		return maxIpc;
	}

	/**
	 * 获取主要申请人：按照专利申请量来判断，取前10
	 */
	private List<Applicant> getAppTop10(List<Patent> list) {
		List<Applicant> top10 = new ArrayList<>();
		List<Applicant> impoapplicants = getImpoApplicant(list);
		Collections.sort(impoapplicants, new Comparator<Applicant>() {
			// 按照申请量降序排列
			@Override
			public int compare(Applicant o1, Applicant o2) {
				// TODO Auto-generated method stub
				return o2.getIpcNums().size() - o1.getIpcNums().size();
			}
		});
		//防止申请人不超过10个的情况
		int size = impoapplicants.size();
		
		int cyclecount;
		if(size>=10) {
			cyclecount = 10;
		}else {
			cyclecount=size;
		}
		for (int i = 0; i < cyclecount; i++) {
			top10.add(impoapplicants.get(i));
		}
		
		
		return top10;
	}

	/**
	 * 获取申请人的相关信息：申请人、申请人每一年对应的专利情况、申请人专利的ipc分类号、申请人对应的中国专利(CN开头)
	 */
	private List<Applicant> getImpoApplicant(List<Patent> list) {
		List<Applicant> impoapplicants = new ArrayList<>();
		//为了降低时间复杂度，改为用map查询是否存在相同申请人的情况以及申请人所在的位置
		Map<String, Integer> map_compare = new HashMap<>();
		//索引的位置
		int index=0;
		for (Patent patent : list) {
			String pd = patent.getPd();
			String ad = patent.getAd();
			String ae = patent.getAe();
			List<String> ip = patent.getIp();
			// pd可能是多条
			String[] patentDetails = pd.split("\n|;");
			// ae可能是多条
			String[] applicants_m = ae.split("\n|;");
			// ad可能是多条
			String[] nums = ad.split("\n|;");
			
			//对申请人去重,因为一条记录里面可能有两个相同的申请人
			List<String> applicants = new ArrayList<>();	
			List<String> applicants_lowercase = new ArrayList<>();
			for(String s:applicants_m) {
				String lowercase = s.trim().replaceAll(" ", "").toLowerCase();
				if(applicants_lowercase.contains(lowercase)) {
					continue;
				}
				applicants_lowercase.add(lowercase);
				applicants.add(s.trim());
			}
			
			for (String applicant : applicants) {
				// 判断ImpoApplicant对象列表中是否含有申请人为指定applicant的对象，
				// 如果没有就创建新对象并添加信息，如果有的话就找到该对象再添加信息
				if (!isValid(applicant, map_compare)) {
					map_compare.put(applicant.replaceAll(" ", "").toLowerCase(), index);
					index++;
					Applicant im = new Applicant(applicant);
					im.setPatentNumByYear(new TreeMap<Integer, List<String>>());
					im.setIpcNums(new ArrayList<>());
					im.setPatentNumsCN(new LinkedList<>());
					//返回在指定申请人下，存储的所有不重复专利号的列表
					List<String> applist = addInfo(nums, im);
					//添加ipc分类号信息
					addPd(im, ip, patentDetails, applist);
					impoapplicants.add(im);				
				} else {
					int loc = findIndex(applicant, map_compare);
					//int loc = findIndex(applicant_trim, impoapplicants);
					Applicant imp = impoapplicants.get(loc);
					//返回在指定申请人下，存储的所有不重复专利号的列表
					List<String> applist = addInfo(nums, imp);
					//添加ipc分类号信息
					addPd(imp, ip, patentDetails, applist);
				}
			}
		}
		for (Applicant im : impoapplicants) {
			//System.out.println(im);
			Map<Integer, List<String>> map = im.getPatentNumByYear();
			Map<Integer, Integer> count = new TreeMap<>();
			for (Map.Entry<Integer, List<String>> data : map.entrySet()) {
				count.put(data.getKey(), data.getValue().size());
			}
			im.setPatentCountsByYear(count);
			//System.out.println(im);
		}
		
		return impoapplicants;
	}

	/**
	 * 判断list之中是否含有指定的申请人
	 */
//	private boolean isValid(String applicant, List<Applicant> impoapplicants) {
//		for (Applicant amp : impoapplicants) {
//			if (amp.getName().replaceAll(" ", "").toLowerCase().equals(applicant.replaceAll(" ", "").toLowerCase())) {
//				return true;
//			}
//		}
//		return false;
//	}
	private boolean isValid(String applicant, Map<String,Integer> map_compare) {
		//不同的记录之间还可能有相同的申请人，所以还需要比较
		return map_compare.containsKey(applicant.replaceAll(" ", "").toLowerCase());

	}

	/**
	 * 判断指定的已经存在的申请人在列表中的位置
	 */
//	private int findIndex(String applicant, List<Applicant> impoapplicants) {
//		int index = 0;
//		for (int i = 0; i < impoapplicants.size(); i++) {
//			if (impoapplicants.get(i).getName().replaceAll(" ", "").toLowerCase().equals(applicant.replaceAll(" ", "").toLowerCase())) {
//				index = i;
//			}
//		}
//		return index;
//	}
	private int findIndex(String applicant, Map<String,Integer> map_compare) {
		return map_compare.get(applicant.replaceAll(" ", "").toLowerCase());
	}

	/**
	 * 添加ad字段里面的内容:按年份的专利号、CN开头的专利号
	 * 返回一个list，存储Applicant对象的专利申请号
	 * 
	 */
	private List<String> addInfo(String[] nums, Applicant im) {
		List<String> applist = new ArrayList<>();
		for (String singleString : nums) {
			// 申请时间和对应的专利号信息
			List<String> ap = getAP(singleString);
			if (ap.size() == 2) {
				int year = Integer.parseInt(ap.get(0));
				String pn = ap.get(1);
				// String prefixPN = pn.substring(0, pn.indexOf("-"));
				// 添加以“CN”开头的专利号到ImpoApplicant对象当中
				if (pn.startsWith("CN")) {
					im.getPatentNumsCN().add(pn);
				}
				if (!im.getPatentNumByYear().containsKey(year)) {
					im.getPatentNumByYear().put(year, new ArrayList<>());
					// 防止两个相同的专利被算做两次
					if (!im.getPatentNumByYear().get(year).contains(pn)) {
						im.getPatentNumByYear().get(year).add(pn);
						applist.add(pn);
					}
				} else {
					if (!im.getPatentNumByYear().get(year).contains(pn)) {
						im.getPatentNumByYear().get(year).add(pn);
						applist.add(pn);
					}
				}

			}
		}
		// 去除重复元素
		im.setPatentNumsCN(removeDuplicate(im.getPatentNumsCN()));
		return applist;
	}
	/**
	 * 按照专利号添加ipc分类号
	 */
	private void addPd( Applicant im,List<String> ip, String[] patentDetails,List<String> applist) {
		//保证程序速度，将专利号和对应的ipc分类号存储在map
		Map<String,String> patent_ipc = new HashMap<>();
		String ipc = null ;
		for (String singlePd : patentDetails) {
			String trimStr = singlePd.trim();
			String prefixPN = "";
			if (trimStr.contains("-")) {
				prefixPN = trimStr.substring(0, trimStr.indexOf("-"));
			}

			// 防止两个相同的专利被算做两次
			if (patent_ipc.containsKey(prefixPN)) {
				continue;
			}
			Matcher m = Pattern.compile("[A-Z]\\d{2}[A-Z]-\\d{3}/\\d+").matcher(singlePd);
			if (m.find()) {
				ipc = m.group();
			} else {
				if (!ip.isEmpty()) {
					ipc = ip.get(0);
				}
			}
			if (prefixPN != "") {
				patent_ipc.put(prefixPN, ipc);
			}
		}
		for(String patentnum:applist) {
			if(patent_ipc.containsKey(patentnum)) {
				im.getIpcNums().add(patent_ipc.get(patentnum));
			}else {
				im.getIpcNums().add(ip.get(0));
			}
		}
	}

//	/**
//	 * 添加pd字段里面的内容，ipc分类号
//	 */
//	private void addPd(List<String> ip, String[] patentDetails, Applicant im) {
//		String ipc = null;
//		List<String> pn = new ArrayList<>();
//		for (String singlePd : patentDetails) {
//			String trimStr = singlePd.trim();
//			String prefixPN = "";
//			if (trimStr.contains("-")) {
//				prefixPN = trimStr.substring(0, trimStr.indexOf("-"));
//			}
//
//			// 防止两个相同的专利被算做两次
//			if (pn.contains(prefixPN)) {
//				continue;
//			}
//			Matcher m = Pattern.compile("[A-Z]\\d{2}[A-Z]-\\d{3}/\\d+").matcher(singlePd);
//			if (m.find()) {
//				ipc = m.group();
//			} else {
//				if (!ip.isEmpty()) {
//					ipc = ip.get(0);
//				}
//			}
//			if(im.getName().equals("UNIV KOBE NAT CORP (UKOB-C)")) {
//				System.out.println(ipc);
//				System.out.println("aaaaaa");
//			}
//			if (prefixPN != "") {
//				pn.add(prefixPN);
//			}
//
//			im.getIpcNums().add(ipc);
//		}
//	}

	/**
	 * 从一个字符串中获取申请年份及专利号,这里的专利号是不带后缀字母的，防止同一个专利被算做申请两次
	 * 
	 * @param ad(单独的一条申请信息,即数据库中ad字段分割后的字符串)
	 * @return 申请年份和专利号
	 */
	private List<String> getAP(String ad) {
		List<String> list = new ArrayList<>();
		String[] items = ad.trim().split("\\s+");

		if (items.length == 5) {
			// System.out.println(items.length);
			// 取最后一个字符串，就是专利申请年
			String syear = items[items.length - 1].trim();
			if (isNumber(syear)) {
				list.add(syear);
			}
			// 取第一个字符串，就是专利号
			String patentNum = items[0].trim();
			// 存储的专利号不带后缀字母，为了防止两个相同的专利被算做两次
			String prefixPN = patentNum.substring(0, patentNum.indexOf("-"));
			list.add(prefixPN);
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

	/**
	 * 去除list中的重复元素
	 */
	private List<String> removeDuplicate(List<String> list) {
		HashSet<String> set = new HashSet<>(list);
		List<String> newlist = new ArrayList<>(set);
		return newlist;
	}

}
