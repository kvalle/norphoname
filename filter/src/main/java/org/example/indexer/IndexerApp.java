package org.example.indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class IndexerApp {

	private static String url = "http://localhost:8983/solr/";
	private static int number = 100;
	private static HttpSolrServer solrCore = new HttpSolrServer(url);
	private static int batchSize = 10;

	public static void main(String[] args) throws Exception {
		print("Indekserer mot " + url);
		long start = System.currentTimeMillis();
		indekser(number);
		long stop = System.currentTimeMillis();
		print("Ferdig etter " + ((stop - start) / 1000) + "s");
	}

	private static void print(Object txt) {
		System.out.println(new java.util.Date() + ": " + txt);
	}

	private static void indekser(int antall) throws ClassNotFoundException, SolrServerException, IOException {
		int kunderIndeksert = 0;

		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		SolrInputDocument doc = null;

		for (int i = 1; i <= antall; i++) {
			kunderIndeksert++;
			doc = new SolrInputDocument();
			doc.addField("id", "" + i);
			doc.addField("name", "");
			doc.addField("birth_date", "");
			doc.addField("phone_number", "");
			docs.add(doc);
		
			if (i % batchSize == 0)
				commit(docs);
		}

		if (!docs.isEmpty()) {
			commit(docs);
		}

		print("Totalt: " + kunderIndeksert + "\n");
	}

	private static void commit(Collection<SolrInputDocument> docs) throws SolrServerException, IOException {
		solrCore.add(docs);
		solrCore.commit();
		docs.clear();
	}

}