package org.example.search;

/**
 * This contents of this class is based on the NorphoneComparator from Duke:
 * http://code.google.com/p/duke/source/browse/src/main/java/no/priv/garshol/duke/comparators/NorphoneComparator.java
 *
 * LICENCE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
public class Norphone {

	//@formatter:off
	/**
	 * Produces the Norphone key for the given string.
	 * 
	 * x = the sound of both sj and kj
     * ^ == start of string
     * $ == end of string
     * [abc] == a set of characters (as in regexp)
     * 
     * IMPLEMENTED
     * vowels stripped, except initial vowel
     * double consonants collapse into one
     * ^aa -> å
     * ch -> k  
     * ck -> k
     * [oiuaeæødy]d ->
     * dt$ -> t
     * gh -> k
     * gj -> j
     * ^gi -> j
     * hg -> k
     * hj -> j
     * hl -> l
     * hr -> r
     * kj -> x
     * ki -> x
     * ld -> l
     * nd -> n
     * ph -> f
     * th -> t
     * w -> v
     * x -> ks
     * z -> s
     * 
     * NOT IMPLEMENTED (YET?)
     * ^c -> k
     * sj -> x
     * skj -> x
     * ^ei -> æ
     * d -> t
     * g -> k
     * kei -> x
     * skei -> x
     * ^ky -> x
     * ^sky -> x
     * h$ -> 
     * 
     * NOT SURE ABOUT THESE
     * ^ch[aeiouy] -> x  (charlotte)
     * en$ ->
	 */
	//@formatter:on
	public static String encode(String str) {
		if (str.length() < 1)
			return ""; // no norphone key for the empty stringp

		str = str.toUpperCase();
		char[] key = new char[str.length() * 2]; // could be all X-es
		int pos = 0;
		Matcher m = new Matcher(str);

		while (m.hasNext()) {
			char ch = m.next();

			// discard duplicate characters
			if (m.isNext(ch) && ch != 'A')
				ch = ' ';

			// discard vowels
			else if (isVowel(ch) && !m.atStart())
				ch = ' ';

			else {
				switch (ch) {
				case 'A': // we only come here on the first character
					if (m.isNext('A'))
						ch = '\u00C5'; // Å
					break;

				case 'C':
					if (m.isNext('H') || m.isNext('K')) {
						ch = 'K';
						m.skip();
					} else
						ch = 'K';
					break;

				case 'D':
					if (m.isNext('T') && m.nextIsLast()) {
						ch = 'T';
						m.skip();
					} else if (m.previousOneOf("IOUAEY\u00D8\u00C6\u00C5") && m.isLast())
						ch = ' ';
					break;

				case 'G':
					if (m.isNext('H'))
						m.skip(); // 'H' is silent
					else if (m.isNext('J') || (m.isNext('I') && m.atStart())) {
						ch = 'J';
						m.skip();
					}
					break;

				case 'H':
					if (m.isNext('J')) {
						ch = 'J';
						m.skip();
					} else if (m.isNext('L')) {
						ch = 'L';
						m.skip();
					} else if (m.isNext('G')) {
						ch = 'G';
						m.skip();
					} else if (m.isNext('R')) {
						ch = 'R';
						m.skip();
					}

					break;

				case 'K':
					if (m.isNext('J') || m.isNext('I')) {
						ch = 'X';
						m.skip();
					}
					break;

				case 'L':
					if (m.isNext('D') && m.nextIsLast()) {
						ch = 'L';
						m.skip();
					}
					break;

				case 'N':
					if (m.isNext('D'))
						m.skip();
					break;

				case 'P':
					if (m.isNext('H')) {
						ch = 'F';
						m.skip(); // eat the 'H'
					}
					break;

				case 'T':
					if (m.isNext('H'))
						m.skip(); // 'H' is silent
					break;

				case 'W':
					ch = 'V';
					break;

				case 'X':
					key[pos++] = 'K';
					ch = 'S';
					break;

				case 'Z':
					ch = 'S';
					break;
				}
			}

			if (ch != ' ')
				key[pos++] = ch;
		}

		return new String(key, 0, pos);
	}

	private static boolean isVowel(char ch) {
		return (ch == 'A' || ch == 'E' || ch == 'I' || ch == 'O' || ch == 'U' || ch == 'Y' || ch == '\u00C5' || ch == '\u00C6' || ch == '\u00D8');
	}

	/**
	 * Wrapping this around the input string to simplify the code.
	 */
	private static class Matcher {
		private String str;
		private int ix;

		public Matcher(String str) {
			this.str = str;
			this.ix = -1;
		}

		public boolean isNext(char ch) {
			return ix + 1 < str.length() && str.charAt(ix + 1) == ch;
		}

		public boolean atStart() {
			return ix == 0;
		}

		public boolean hasNext() {
			return ix + 1 < str.length();
		}

		public boolean nextIsLast() {
			return ix + 2 == str.length();
		}

		public boolean isLast() {
			return ix + 1 == str.length();
		}

		public char next() {
			return str.charAt(++ix);
		}

		public void skip() {
			ix++;
		}

		public boolean previousOneOf(String chars) {
			if (ix == 0)
				return false;
			return chars.indexOf(str.charAt(ix - 1)) != -1;
		}
	}
}
