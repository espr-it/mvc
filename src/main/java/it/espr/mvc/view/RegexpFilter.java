package it.espr.mvc.view;

import java.util.regex.Pattern;

public class RegexpFilter {

	char[] regexpCharacters = new char[] { '.', ',', '+', '*', '(', '[' };

	String filterRegexpFromPattern(Pattern pattern) {
		String path = pattern.toString();
		for (int i = 0; i < path.length(); i++) {
			if (!isRegexp(path.charAt(i))) {
				continue;
			}
			path = path.substring(0, i - 1);
			break;
		}
		return this.removeTrail(path);
	}

	boolean isRegexp(char c) {
		for (char regexpCharacter : regexpCharacters) {
			if (regexpCharacter == c) {
				return true;
			}
		}
		return false;
	}

	String removeTrail(String text) {
		if (text.contains(".")) {
			text = text.substring(0, text.indexOf(".") + 1);
		}
		if (text.endsWith("/")) {
			text = text.substring(0, text.length() - 1);
		}
		return text;
	}
}
