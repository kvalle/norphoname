package org.example.search;

import java.io.IOException;

import org.apache.commons.codec.language.Nysiis;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public final class NysiisFilter extends TokenFilter {
	private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
	private final Nysiis encoder;

	public NysiisFilter(TokenStream input) {
		super(input);
		encoder = new Nysiis(false);
	}

	@Override
	public final boolean incrementToken() throws IOException {
		if (input.incrementToken()) {
			String navn = new String(termAtt.buffer()).substring(0, termAtt.length());
			String fonetiskRepresentasjon = encoder.encode(navn);
			
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
