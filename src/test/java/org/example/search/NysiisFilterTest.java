package org.example.search;

import java.io.StringReader;

import org.apache.lucene.analysis.BaseTokenStreamTestCase;
import org.apache.lucene.analysis.MockTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.junit.Test;

public class NysiisFilterTest extends BaseTokenStreamTestCase {
	
	@Test
	public void testAtFonetiskFilterFungerer() throws Exception {
	    StringReader reader = new StringReader("Gunner Hansen");
	    MockTokenizer tokenizer = new MockTokenizer(reader, MockTokenizer.WHITESPACE, false);
		TokenStream stream = new NysiisFilter(tokenizer);
	    assertTokenStreamContents(stream, new String[] { "GANAR", "HANSAN" });
	}
}
