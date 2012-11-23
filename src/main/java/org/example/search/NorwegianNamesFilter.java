package org.example.search;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public final class NorwegianNamesFilter extends TokenFilter {
	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);

	public NorwegianNamesFilter(TokenStream input) {
		super(input);
	}

	@Override
	public final boolean incrementToken() throws IOException {
		if (input.incrementToken()) {
			String navn = new String(termAtt.buffer()).substring(0, termAtt.length());
			String fonetiskRepresentasjon = Norphone.encode(navn);

			settBuffer(fonetiskRepresentasjon);
			termAtt.setLength(fonetiskRepresentasjon.length());
			return true;
		}
		return false;
	}

	private void settBuffer(String rep) {
		int lengde = rep.length();
		if (lengde > termAtt.length()) {
			termAtt.resizeBuffer(lengde);
		}
		char[] buffer = termAtt.buffer();
		for (int i = 0; i < lengde; i++) {
			buffer[i] = rep.charAt(i);
		}
	}

}
