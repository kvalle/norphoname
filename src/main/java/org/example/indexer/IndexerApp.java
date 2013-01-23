package org.example.indexer;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class IndexerApp {

	private static String url = "http://localhost:8983/solr/";
	private static HttpSolrServer solrCore;

	private static int batchsize = 5000;
	private static Integer antall = 10000;

	public static void main(String[] args) throws Exception {
		Map<String, String> options = parseOptions(args);
		if (options.containsKey("-batch"))
			batchsize = Integer.parseInt(options.get("-batch"));
		if (options.containsKey("-antall"))
			antall = Integer.parseInt(options.get("-antall"));
		if (options.containsKey("-port"))
			url = "http://localhost:" + options.get("-port") + "/solr/";

		System.out.println("\n======================================");

		Connection connection = getConnection();

		print("Indekserer mot " + url);
		print("Antall: " + antall + ". Batchsize: " + batchsize + "\n");
		long start = System.currentTimeMillis();
		print("INDEKSERER PERSONER");
		indekserFraDB("person", connection);
		connection.close();
		long stop = System.currentTimeMillis();
		print("Ferdig etter " + ((stop - start) / 1000) + "s");
	}

	private static Connection getConnection() throws SQLException, ClassNotFoundException, IOException {
		String dburl, dbuser, dbpassword;
		String configLocation = System.getProperty("config");
		if (configLocation != null) {
			Properties properties = new Properties();
			properties.load(new FileInputStream(configLocation));
			dburl = properties.getProperty("db.url");
			dbuser = properties.getProperty("db.user");
			dbpassword = properties.getProperty("db.password");
			print("db.url=" + dburl);
		} else {
			dburl = "jdbc:oracle:thin:@139.116.11.4:1521:TIKUNDE";
			dbuser = "kunde";
			dbpassword = "kunde";
			print("default db.url=" + dburl);
		}
		Class.forName("oracle.jdbc.OracleDriver");
		return DriverManager.getConnection(dburl, dbuser, dbpassword);
	}

	private static void print(Object txt) {
		System.out.println(new java.util.Date() + ": " + txt);
	}

	private static Map<String, String> parseOptions(String[] args) {
		Map<String, String> options = null;
		try {
			options = new HashMap<String, String>();
			for (String arg : args) {
				String[] option = arg.split("\\=");
				options.put(option[0], option[1]);
			}
		} catch (Exception e) {
			print("Bruk: indexer.jar [OPTIONS]");
			print("");
			print("-batch=<batchsize>");
			print("-antall=<antall>");
			print("-port=<solr port>");
			print("-tid=<indekser fra denne datotid> yyyy:MM:dd-HH:mm:ss");
			System.exit(1);
		}
		return options;
	}

	private static void indekserFraDB(String tabell, Connection conn) throws ClassNotFoundException, SQLException,
			SolrServerException, IOException {
		solrCore = new HttpSolrServer(url);
		Statement statement = conn.createStatement();
		statement.setFetchSize(batchsize);
		String sql = lagSql(tabell);
		
		ResultSet rs = statement.executeQuery(sql);

		int kunderIndeksert = 0;
		long start = System.currentTimeMillis();

		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		String sisteKundeId = "";
		SolrInputDocument doc = null;

		while (rs.next()) {
			String kundeId = rs.getString("kunde_id");

			if (kundeId.equals(sisteKundeId)) {
				//settAdresseFelter(rs, doc);
			} else {
				leggTilForrigeDokument(docs, doc);
				kunderIndeksert++;
				doc = lagNyttDokument(tabell, rs);
				sisteKundeId = kundeId;
			}

			if (kunderIndeksert % batchsize == 0 && !docs.isEmpty())
				commitDocs(docs, kunderIndeksert, start);
		}

		if (doc != null) {
			leggTilForrigeDokument(docs, doc);
			commitDocs(docs, kunderIndeksert, start);
		}

		statement.close();
		print("Totalt: " + kunderIndeksert + "\n");
	}

	private static SolrInputDocument lagNyttDokument(String tabell, ResultSet rs) throws SQLException {
		SolrInputDocument doc;
		doc = new SolrInputDocument();
		doc.addField("id", rs.getString("kunde_id"));
		settPersonFelter(rs, doc);
		return doc;
	}

	private static String lagSql(String tabell) {
		//@formatter:off
		String sql = "";
		sql += " SELECT p.*,a.*, poststed.postnummer, poststed.navn poststed, fylke.navn k_fylke, p_fylke.navn p_fylke, land.navn land\n";
		sql += " FROM kun_"+tabell+" p\n";
		sql += " LEFT OUTER JOIN kun_adresse a ON p.kunde_id = a.kunde_id\n";
		sql += " LEFT OUTER JOIN poststed ON a.poststed_id = poststed.id\n";
		sql += " LEFT OUTER JOIN kommune ON a.kommune_id = kommune.id\n";
		sql += " LEFT OUTER JOIN fylke ON kommune.fylke_id = fylke.id\n";
		sql += " LEFT OUTER JOIN kommune p_kommune ON poststed.kommune_id = p_kommune.id\n";
		sql += " LEFT OUTER JOIN fylke p_fylke ON p_kommune.fylke_id = p_fylke.id\n";
		sql += " LEFT OUTER JOIN land ON a.land_id = land.id\n";
		sql += " WHERE p.gyldigtil > SYSDATE\n";
		sql += " AND (a.gyldigtil > SYSDATE OR a.gyldigtil IS NULL)\n";
		
		if (antall != null) {
			sql	+= " AND rownum <= " + antall + "\n";
		}
		
		sql += " ORDER BY p.kunde_id";
		//@formatter:on
		return sql;
	}

	private static void leggTilForrigeDokument(Collection<SolrInputDocument> docs, SolrInputDocument doc) {
		if (doc != null)
			docs.add(doc);
	}

	private static void commitDocs(Collection<SolrInputDocument> docs, int antallSaaLangt, long start) throws SolrServerException, IOException {
		print(antallSaaLangt + " (" + (System.currentTimeMillis() - start) + "ms)");
		solrCore.add(docs);
		solrCore.commit();
		docs.clear();
	}

	private static void settPersonFelter(ResultSet rs, SolrInputDocument doc) throws SQLException {
		doc.addField("first_name", rs.getString("fornavn"));
		doc.addField("middle_name", rs.getString("mellomnavn"));
		doc.addField("last_name", rs.getString("etternavn"));
		java.sql.Date sqldato = rs.getDate("fodselsdato");
		if (sqldato != null) {
			doc.addField("birth_date", sqldato.toString());
		}
	}

}