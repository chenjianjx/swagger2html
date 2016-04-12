package org.swagger2html.util;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author chenjianjx@gmail.com
 *
 */
public class S2hUtils {

	/**
	 * 
	 * @param location
	 *            a url or a file path
	 * @return
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	public static String readLocationToString(String location)
			throws IOException {
		if (StringUtils.isBlank(location)) {
			return null;
		}

		try {
			return IOUtils.toString(new URL(location), "UTF8");
		} catch (MalformedURLException e) {
			return FileUtils.readFileToString(new File(location), "UTF8");
		}
	}
}
