package org.swagger2html;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * @author chenjianjx@gmail.com
 */
public class Swagger2HtmlTest {

    File dir = null;


    @Before
    public void init() throws IOException {
        dir = new File(System.getProperty("user.home") + "/temp/s2h-test");
        dir.mkdirs();

    }

    @Test
    public void toHtmlTest_petstore() throws IOException {
        toHtml("petstore.json");
    }

    @Test
    public void toHtmlTest_circularReference() throws IOException {
        toHtml("circular-reference.json");
    }

    private void toHtml(String jsonFilename) throws IOException {
        File jsonFile = new File(dir, jsonFilename);
        FileUtils.writeStringToFile(jsonFile,
                IOUtils.toString(Swagger2HtmlTest.class.getResource("/" + jsonFilename)), "utf8");


        Swagger2Html s2h = new Swagger2Html();
        File outputFile = new File(dir, jsonFile.getName() + ".html");
        Writer out = null;
        try {
            out = new FileWriter(outputFile);
            s2h.toHtml(jsonFile.getAbsolutePath(), out);
        } finally {
            IOUtils.closeQuietly(out);
        }
        System.out
                .println("Done. Please check " + outputFile.getAbsolutePath());
    }

}
