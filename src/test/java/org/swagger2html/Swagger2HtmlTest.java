package org.swagger2html;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * @author chenjianjx@gmail.com
 *
 */
public class Swagger2HtmlTest {

	File dir = null;
	File petstoreJsonFile = null;

	@Before
	public void init() throws IOException {
		dir = new File(System.getProperty("user.home") + "/temp/s2h-test");
		dir.mkdirs();
		petstoreJsonFile = new File(dir, "petstore.json.generated");

		FileUtils.writeStringToFile(petstoreJsonFile,
				IOUtils.toString(Swagger2HtmlTest.class
						.getResource("/petstore.json")), "utf8");

	}

	@Test
	public void toHtmlTest() throws IOException {

		Swagger2Html s2h = new Swagger2Html();
		File outputFile = new File(dir, "output.html");
		Writer out = null;
		try {
			out = new FileWriter(outputFile);
			s2h.toHtml(petstoreJsonFile.getAbsolutePath(), out);
		} finally {
			IOUtils.closeQuietly(out);
		}
		System.out
				.println("Done. Please check " + outputFile.getAbsolutePath());

	}
}
