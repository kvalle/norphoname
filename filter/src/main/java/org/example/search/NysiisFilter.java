package org.example.search;

import java.io.IOException;

import org.apache.commons.codec.language.Nysiis;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public final class NysiisFilter extends TokenFilter {
	private final CharTermAttribute termAttribute = addAttribute(CharTermAttribute.class);
	private final Nysiis encoder;

	public NysiisFilter(TokenStream input) {
		super(input);
		encoder = new Nysiis(false);
	}

	@Override
	public final boolean incrementToken() throws IOException {
		if (input.incrementToken()) {
			String name = new String(termAttribute.buffer()).substring(0, termAttribute.length());
			String phoneticRepresentation = encoder.encode(name);
			
			setBuffer(phoneticRepresentation);
			termAttribute.setLength(phoneticRepresentation.length());
			return true;
		}
		return false;
	}

	private void setBuffer(String representation) {
		int length = representation.length();
		if (length > termAttribute.length()) {
			termAttribute.resizeBuffer(length);
		}
		char[] buffer = termAttribute.buffer();
		for (int i = 0; i < length; i++) {
			buffer[i] = representation.charAt(i);
		}
	}

}
