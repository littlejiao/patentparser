package edu.kndev.web.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import edu.kndev.mongo.entity.Patent;
import edu.kndev.mongo.repo.PatentRepo;
@Service
public class PatentService {
	@Autowired
	PatentRepo patentrepo;

	// 多线程读取数据
	public List<Patent> getlist(String topicId) throws Exception {
		// TODO Auto-generated method stub
		Vector<Patent> vec = new Vector<>();
		long count = patentrepo.findByTopicId(topicId).size();
		int perSize = 500;
		int total;
		if (count % perSize == 0) {
			total = ((int) count) / perSize;
		} else
			total = ((int) count) / perSize + 1;
		System.out.println("total: "+total + " page");

		int nthread = 9;

		AtomicInteger page = new AtomicInteger(0);
		final int maxPage = total;
		CompletableFuture[] futures = new CompletableFuture[nthread];
		ExecutorService executor = Executors.newFixedThreadPool(nthread);
		for (int i = 0; i < nthread; i++) {
			futures[i] = CompletableFuture.runAsync(() -> {
				int p = page.getAndIncrement();
				while (p < maxPage) {

					//System.out.println("read page " + p + "...");
					Page<Patent> patents = patentrepo.findAllPatentBytopicId(topicId, new PageRequest(p, perSize));

					for (Patent pa : patents) {
						vec.add(pa);
					}

					patents = null;
					System.out.println("read page " + p + " complete...");
					System.gc();
					p = page.getAndIncrement();

				}
			}, executor);
		}
		CompletableFuture<Void> f = CompletableFuture.allOf(futures);
		f.get();
		executor.shutdown();
		System.out.println("-------total" + "finished-------");
		System.out.println(vec.size());
		List<Patent> list = new ArrayList<>(vec);
		return vec;

	}

}
