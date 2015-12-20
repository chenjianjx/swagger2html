package org.swagger2html;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

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
					.println("The command is:  s2h.sh swagger-json-url /path/to/your/doc.html");
			return;
		}

		String urlStr = args[0];
		String outputFileStr = args[1];

		FileWriter output = new FileWriter(outputFileStr);

		try {
			s2h.toHtml(urlStr, output);
		} finally {
			IOUtils.closeQuietly(output);
		}

	}

}
