package edu.kndev.dataparser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.kndev.mongo.entity.IpcByYear;
import edu.kndev.mongo.entity.LifeCycle;
import edu.kndev.mongo.entity.Patent;
import edu.kndev.mongo.entity.YearTrend;

public class DataByYear {

	/**
	 * 技术生命周期的数据: 每两年对应的申请人数量和申请量
	 * 
	 *
	 */
	public List<LifeCycle> lifeCycleData(List<Patent> list) {
		List<LifeCycle> lcs = new ArrayList<>();

		List<YearTrend> lcd = getDataByYear(list);
		Collections.sort(lcd, new Comparator<YearTrend>() {
			// 按照升序排列
			@Override
			public int compare(YearTrend o1, YearTrend o2) {
				// TODO Auto-generated method stub
				return o1.getYear() - o2.getYear();
			}

		});
		if(lcd.size()!=0) {
			//填补中间有的年份为0的数据
			int inityear = lcd.get(0).getYear();
			int endyear = lcd.get(lcd.size() - 1).getYear();
			
			Map<Integer, Integer> amap = new LinkedHashMap<>();
			Map<Integer, Integer> bmap = new LinkedHashMap<>();
			// Map<Integer,String> cmap = new LinkedHashMap<>();

			for (YearTrend yt : lcd) {
				amap.put(yt.getYear(), yt.getApplicationCounts());
				bmap.put(yt.getYear(), yt.getApplicantCounts());
				// cmap.put(yt.getYear(), getMainIpc(yt.getIpcNums()));

			}
			while (inityear < endyear) {
				if (!amap.containsKey(inityear)) {
					amap.put(inityear, 0);
				}
				if (!bmap.containsKey(inityear)) {
					bmap.put(inityear, 0);
				}
				inityear++;
			}
			List<Integer> alist = new ArrayList<>(amap.keySet());
			Collections.sort(alist);
			//amap的size可能为偶数，也可能为奇数
			if(amap.size()%2==0) {
				for (int i = 0; i < amap.size(); i += 2) {
					LifeCycle lifec = new LifeCycle();
					int byear = alist.get(i);
					int eyear = byear+1;
					
					String year = byear + "-" + eyear;
					lifec.setYear(year);
					if(!amap.containsKey(eyear)) {
						amap.put(eyear, 0);
						bmap.put(eyear, 0);
					}
					int patentC = amap.get(byear) + amap.get(eyear);
					lifec.setApplicationCounts(patentC);
					int peopleC = bmap.get(byear) + bmap.get(eyear);
					lifec.setPeopleCounts(peopleC);
					lcs.add(lifec);
				}
			}else {
				for (int i = 0; i < amap.size()-1; i += 2) {
					LifeCycle lifec = new LifeCycle();
					int byear = alist.get(i);
					int eyear = byear+1;
					
					String year = byear + "-" + eyear;
					lifec.setYear(year);
					if(!amap.containsKey(eyear)) {
						amap.put(eyear, 0);
						bmap.put(eyear, 0);
					}
					int patentC = amap.get(byear) + amap.get(eyear);
					lifec.setApplicationCounts(patentC);
					int peopleC = bmap.get(byear) + bmap.get(eyear);
					lifec.setPeopleCounts(peopleC);
					lcs.add(lifec);
				}
				lcs.add(new LifeCycle(null, ""+endyear, bmap.get(endyear), amap.get(endyear)));
			}
		}
		
		return lcs;

	}

	/**
	 * 专利技术时间趋势数据：每一年对应的ipc分类号(只计算了主ipc分类号)
	 */
	public List<IpcByYear> ipcYearData(List<Patent> list) {
		List<IpcByYear> ibys = new ArrayList<>();
		List<YearTrend> lcd = getDataByYear(list);
		Collections.sort(lcd, new Comparator<YearTrend>() {
			// 按照升序排列
			@Override
			public int compare(YearTrend o1, YearTrend o2) {
				// TODO Auto-generated method stub
				return o1.getYear() - o2.getYear();
			}

		});
		for (YearTrend yt : lcd) {
			IpcByYear iby = new IpcByYear();
			int year = yt.getYear();
			iby.setYear(year);
			String ipc = getMainIpc(yt.getIpcNums());
			iby.setIpcNum(ipc);
			ibys.add(iby);
		}
		return ibys;
	}

	/**
	 * 找出存储ipc号的列表中次数出现最多的ipc分类号
	 * 
	 * @param ipc
	 */
	private String getMainIpc(List<String> ipc) {
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
	 * 从所有记录中: 获取每一年份对应的申请人，例如1993: [CHIBA PREFECTURE (CHIB-Non-standard), JAPAN
	 * TAFU GURASU 获取每一年份对应的专利号，例如2004: [KR758209-B1, US2005196774-A1]
	 * 获取每一年份申请的ipc分类号，例如1995:[G03F-001/62,G03F-001/63]
	 * 
	 * @param
	 */
	private List<YearTrend> getDataByYear(List<Patent> list) {
		// TODO Auto-generated method stub
		List<YearTrend> lcs = new ArrayList<>();
		for (Patent patent : list) {
			String ad = patent.getAd();
			String ae = patent.getAe();
			String pd = patent.getPd();
			List<String> ip = patent.getIp();
			// ad可能是多条
			String[] nums = ad.split("\n|;");
			Map<String, String> patIpc = patentIpc_map(ip, pd);
			for (String singleString : nums) {
				List<String> ap = getAP(singleString);
				if (ap.size() == 2) {
					int year = Integer.parseInt(ap.get(0));
					String patentNum = ap.get(1);
					if (!isHave(year, lcs)) {
						YearTrend lc = new YearTrend();
						lc.setYear(year);
						lc.setApplicants(new LinkedList<>());
						lc.setApplications(new LinkedList<>());
						lc.setIpcNums(new LinkedList<>());
						addAe(ae, lc);
						// 添加专利号之前判断是否有重复的专利号
						if (!lc.getApplications().contains(patentNum)) {
							lc.getApplications().add(patentNum);
						}
						addIpc(ip, patIpc, patentNum, lc);
						lcs.add(lc);
					} else {
						int i = find(year, lcs);
						addAe(ae, lcs.get(i));
						if (!lcs.get(i).getApplications().contains(patentNum)) {
							lcs.get(i).getApplications().add(patentNum);
						}
						addIpc(ip, patIpc, patentNum, lcs.get(i));
					}
				}
			}
		}
		for (YearTrend yeart : lcs) {
			int applicationCounts = yeart.getApplications().size();
			int applicantCounts = yeart.getApplicants().size();
			yeart.setApplicantCounts(applicantCounts);
			yeart.setApplicationCounts(applicationCounts);
		}
		return lcs;
	}

	/**
	 * 找到列表中年份为year的对象的位置
	 */
	private int find(int year, List<YearTrend> lcs) {
		int index = 0;
		for (int i = 0; i < lcs.size(); i++) {
			if (lcs.get(i).getYear() == year) {
				index = i;
			}
		}
		return index;
	}

	/**
	 * 判断列表中是否含有年份为year的对象
	 */
	private boolean isHave(int year, List<YearTrend> lcs) {
		for (YearTrend life : lcs) {
			if (life.getYear() == year) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 添加ae字段申请人的信息
	 */
	private void addAe(String ae, YearTrend lc) {
		// ae可能是多条
		String[] applicants = ae.split("\n|;");
		for (String app : applicants) {
			String applicant = app.trim();
			if (!lc.getApplicants().contains(applicant)) {
				lc.getApplicants().add(applicant);
			}
		}
	}

	/**
	 * 添加pd里面的ipc分类号信息 当获取一个专利号的时候去判断存储“专利号-ipc分类号”的map里面是否含有该专利号，
	 * 如果有的话将其对应的ipc分类号添加到对象当中。
	 */
	private void addIpc(List<String> ip, Map<String, String> patIpc, String patentNum, YearTrend lc) {
		if (patIpc.containsKey(patentNum)) {
			String ipcnum = patIpc.get(patentNum);
			lc.getIpcNums().add(ipcnum);
		} else {
			lc.getIpcNums().add(ip.get(0));
		}
	}

	/**
	 * 将pd字段里面的每一条信息对应的“专利号-ipc分类号”提取出来，存为一个map
	 * 
	 * @return
	 */
	private Map<String, String> patentIpc_map(List<String> ip, String pd) {

		String ipc = null;
		String pn = null;
		Map<String, String> patIpc = new HashMap<>();
		String[] pds = pd.split("\n|;");
		for (String singlePd : pds) {
			Matcher m = Pattern.compile("[A-Z]\\d{2}[A-Z]-\\d{3}/\\d+").matcher(singlePd);
			if (m.find()) {
				ipc = m.group();
			} else {
				if (!ip.isEmpty()) {
					ipc = ip.get(0);
				}
			}
			// 存储的专利号不带有后缀字母
			Matcher m1 = Pattern.compile("[A-Z]{2}\\d+").matcher(singlePd);
			if (m1.find()) {
				pn = m1.group();
			}
			patIpc.put(pn, ipc);
		}

		return patIpc;
	}

	/**
	 * 获取每一年份对应的专利号，例如2004: [KR758209-B1, US2005196774-A1]
	 * 
	 * @param ad（数据库中的ad字段）
	 * @return
	 */
	private Map<Integer, List<String>> getApplicantionByYear(String ad, Map<Integer, List<String>> applicationByYear) {

		// ad信息可能是多条，用\n或者;连接
		String[] nums = ad.split("\n|;");
		for (String singleString : nums) {
			List<String> ap = getAP(singleString);
			// list里面存储的是申请年和专利号，必须同时存在，才认为是有效数据
			if (ap.size() == 2) {
				int year = Integer.parseInt(ap.get(0));
				String singlePatentNum = ap.get(1);
				// 将每一年对应的专利申请存储在map
				if (!applicationByYear.containsKey(year)) {
					applicationByYear.put(year, new ArrayList<String>());
					applicationByYear.get(year).add(singlePatentNum);
				} else {
					applicationByYear.get(year).add(singlePatentNum);
				}
			}
		}
		return applicationByYear;
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
		if (items.length == 5) {
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
