package org.example.indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class IndexerApp {

	private static String url = "http://localhost:8983/solr/";
	private static HttpSolrServer solrCore;

	public static void main(String[] args) throws Exception {
		print("Indekserer mot " + url);
		long start = System.currentTimeMillis();
		indekser(hentPersoner());
		long stop = System.currentTimeMillis();
		print("Ferdig etter " + ((stop - start) / 1000) + "s");
	}
	
	private static List<Map<String, String>> hentPersoner() {
		return null;
	}

	private static void print(Object txt) {
		System.out.println(new java.util.Date() + ": " + txt);
	}

	private static void indekser(List<Map<String,String>> personer) throws ClassNotFoundException, SolrServerException, IOException {
		solrCore = new HttpSolrServer(url);

		int kunderIndeksert = 0;

		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		SolrInputDocument doc = null;

		for (Map<String,String> person : personer) {
			kunderIndeksert++;
			doc = new SolrInputDocument();
			doc.addField("id", person.get("kunde_id"));
			doc.addField("name", person.get("name"));
			doc.addField("birth_date", person.get("fodselsdato"));
			docs.add(doc);
			solrCore.add(docs);
			solrCore.commit();
			docs.clear();
		}

		print("Totalt: " + kunderIndeksert + "\n");
	}

}