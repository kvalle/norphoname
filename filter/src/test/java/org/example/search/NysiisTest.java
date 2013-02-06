package org.example.search;

import static org.junit.Assert.assertEquals;

import org.apache.commons.codec.language.Nysiis;
import org.junit.Before;
import org.junit.Test;

public class NysiisTest {

	private Nysiis encoder;

	@Before
	public void setup() {
		encoder = new Nysiis(false);
	}

	@Test
	public void testEnkeltCase() throws Exception {
		assertEquals("JAN", encoder.encode("John"));
	}
	
	@Test
	public void testEnkelOgDobbelEnkodingErLik() throws Exception {
		String enkel = encoder.encode("Børre");
		String dobbel = encoder.encode(enkel);
		assertEquals(dobbel, enkel);
	}
	
}
