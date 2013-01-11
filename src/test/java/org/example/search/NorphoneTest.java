package org.example.search;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NorphoneTest {

	@Test
	public void testNorphoneEncoding() throws Exception {
		assertEquals(Norphone.encode("Pål"), Norphone.encode("Paal"));
		assertEquals(Norphone.encode("Pål"), Norphone.encode("Paul"));
		System.out.println(Norphone.encode("Ola"));
		System.out.println(Norphone.encode("Nordmann"));
	}
	
}
