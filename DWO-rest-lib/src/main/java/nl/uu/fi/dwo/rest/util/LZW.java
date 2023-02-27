package nl.uu.fi.dwo.rest.util;

import java.util.HashMap;
import java.util.Map;

/**
 * LZW compression.
 * @author wim
 *
 */

public class LZW {
	public static String SIGNATURE = "L";
	static final char MAX = 128;
	public static String compress(String ascii) {
/*
 *    var dict = {};
    var data = (s + "").split("");
    var out = [];
    var currChar;
    var phrase = data[0];
    var code = 256;
    for (var i=1; i<data.length; i++) {
        currChar=data[i];
        if (dict[phrase + currChar] != null) {
            phrase += currChar;
        }
        else {
            out.push(phrase.length > 1 ? dict[phrase] : phrase.charCodeAt(0));
            dict[phrase + currChar] = code;
            code++;
            phrase=currChar;
        }
    }
    out.push(phrase.length > 1 ? dict[phrase] : phrase.charCodeAt(0));
    for (var i=0; i<out.length; i++) {
        out[i] = String.fromCharCode(out[i]);
    }
    
    var retrunedresult = out.join("");
 */
		if(ascii.isEmpty()) return ascii;
		Map<String,Character> dict = new HashMap<>();
		StringBuilder out = new StringBuilder();
		char[] data = ascii.toCharArray();
		String phrase = String.valueOf(ok(data[0]));
		char code = MAX;
		int length = data.length;
		for(int i = 1; i < length; i++) {
			if(code == 0) {
				out.append( phrase.length() > 1 ? dict.get(phrase).charValue() : phrase.charAt(0));
				dict.clear();
				code = MAX;
				phrase = String.valueOf(ok(data[i]));
				continue;
			}
			char curChar = ok(data[i]);
			if( dict.containsKey(phrase+curChar)) {
				phrase += curChar;
			} else {
				out.append( phrase.length() > 1 ? dict.get(phrase).charValue() : phrase.charAt(0));
				dict.put(phrase + curChar, code);
				code++;
				phrase = String.valueOf(curChar);
			}
		}
		out.append(phrase.length()>1 ? dict.get(phrase).charValue(): phrase.charAt(0));
		return out.toString();
	}
	
	
	private static char ok(char c) {
		if(c >= MAX) throw new IllegalArgumentException( "non-ASCII " + (int)c);
		return c;
	}


	public static String uncompress(String binary) {
/*
 function lzw_decode(s) {
var dict = {};
var data = (s + "").split("");
var currChar = data[0];
var oldPhrase = currChar;
var out = [currChar];
var code = 256;
var phrase;
for (var i=1; i<data.length; i++) {
var currCode = data[i].charCodeAt(0);
if (currCode < 256) {
phrase = data[i];
}
else {
phrase = dict[currCode] ? dict[currCode] : (oldPhrase + currChar);
}
out.push(phrase);
currChar = phrase.charAt(0);
dict[code] = oldPhrase + currChar;
code++;
oldPhrase = phrase;
}
return out.join("");
}
		
 */
		if(binary.isEmpty()) return binary;
		Map<Character,String> dict = new HashMap<>();
		String phrase;
		char code = MAX;
		char[] data = binary.toCharArray();
		char curChar = data[0];
		String oldPhrase = String.valueOf(curChar);
		StringBuilder out = new StringBuilder(oldPhrase);
		int length = data.length;
		for (int i = 1; i < length; i++) {
			if(code == 0) { //RESET engine if code wraps.
				dict.clear();
				code = MAX;
				curChar = data[i];
				oldPhrase = String.valueOf(curChar);
				out.append(oldPhrase);
				continue;
			}
			char curCode = data[i];
			if(curCode < MAX) {
				phrase = String.valueOf(curCode);
			} else {
				phrase = dict.getOrDefault(curCode, oldPhrase + curChar);
			}
			out.append(phrase);
			curChar = phrase.charAt(0);
			dict.put(code, oldPhrase+curChar);
			code ++ ;
			oldPhrase = phrase;
		}
		return out.toString();
	}
}
