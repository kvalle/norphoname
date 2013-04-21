package org.example.indexer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class IndexerApp {

	private static String url = "http://localhost:8983/solr/";
	private static HttpSolrServer solrCore = new HttpSolrServer(url);

	public static void main(String[] args) throws Exception {
		print("Indexing " + url);
		long start = System.currentTimeMillis();
		indexer();
		long stop = System.currentTimeMillis();
		print("Done after " + ((stop - start) / 1000) + "s");
	}

	private static void print(Object txt) {
		System.out.println(new java.util.Date() + ": " + txt);
	}

	private static void indexer() throws ClassNotFoundException, SolrServerException, IOException {
		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();

		int personId = 0;
		docs.add(createPerson(personId++, "123456789", "2000-02-13", "hans-ole hansen"));
		docs.add(createPerson(personId++, "123456789", "2000-02-13", "per arne hanssen"));
		docs.add(createPerson(personId++, "123456789", "2000-02-13", "jon larsen"));
		docs.add(createPerson(personId++, "123456789", "2000-02-13", "john pedro"));
		docs.add(createPerson(personId++, "123456789", "2000-02-13", "lars jonathan perdersen"));
		docs.add(createPerson(personId++, "123456789", "2000-02-13", "birte johansen"));
		docs.add(createPerson(personId++, "123456789", "2000-02-13", "berit johansson"));
		docs.add(createPerson(personId++, "123456789", "2000-02-13", "ole kristian"));
		docs.add(createPerson(personId++, "123456789", "2000-02-13", "kristen giftemål"));
		docs.add(createPerson(personId++, "123456789", "2000-02-13", "vilde giftemaal"));
		docs.add(createPerson(personId++, "123456789", "2000-02-13", "vidar ole-arne"));

		if (!docs.isEmpty()) {
			commit(docs);
		}

		print("Total: " + personId + "\n");
	}

	private static SolrInputDocument createPerson(int id, String pnumber, String bdate, String name) {
		SolrInputDocument doc = new SolrInputDocument();
		doc.addField("id", name + id);
		doc.addField("name", name);
		doc.addField("birth_date", bdate);
		doc.addField("phone_number", pnumber);
		return doc;
	}

	private static void commit(Collection<SolrInputDocument> docs) throws SolrServerException, IOException {
		solrCore.add(docs);
		solrCore.commit();
		docs.clear();
	}

}