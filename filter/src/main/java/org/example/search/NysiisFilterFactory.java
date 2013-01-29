package org.example.search;

import java.util.Map;

import org.apache.solr.analysis.BaseTokenFilterFactory;
import org.apache.lucene.analysis.TokenStream;

public class NysiisFilterFactory extends BaseTokenFilterFactory {
	Map<String, String> args;

	public Map<String, String> getArgs() {
		return args;
	}

	public void init(Map<String, String> args) {
		this.args = args;
	}

	public NysiisFilter create(TokenStream input) {
		return new NysiisFilter(input);
	}
	
}
