package org.swagger2html;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.swagger2html.util.S2hUtils;

/**
 * The entry method
 * 
 * @author chenjianjx@gmail.com
 *
 */
public class S2hMain {

	private static Swagger2Html s2h = new Swagger2Html();

	public static void main(String[] args) throws IOException {

		if (args == null || args.length < 2) {
			System.err
					.println("The command is:  s2h swagger-json-url /path/to/your/doc.html");
			return;
		}

		String urlStr = args[0];
		String outputFileStr = args[1];

		FileWriter output = new FileWriter(outputFileStr);

		Map<String, String> params = getExtraParams(args);

		try {
			String cssLocation = params.get("css");
			String cssToInclude = null;
			if (!StringUtils.isBlank(cssLocation)) {
				cssToInclude = S2hUtils.readLocationToString(cssLocation);
			}

			s2h.toHtml(urlStr, cssToInclude, output);
		} finally {
			IOUtils.closeQuietly(output);
		}

	}

	private static Map<String, String> getExtraParams(String[] args) {
		Map<String, String> params = new HashMap<String, String>();
		String lastParamKey = null;
		for (int i = 2; i < args.length; i++) {
			String token = args[i];
			if (token.startsWith("-")) {
				if (token.length() > "-".length()) {
					lastParamKey = token.substring("-".length());
				}
				continue;
			}

			// token is a param value
			if (lastParamKey != null) {
				params.put(lastParamKey, token);
				continue;
			}
		}
		return params;
	}

}
