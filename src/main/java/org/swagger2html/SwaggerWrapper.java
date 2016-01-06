package org.swagger2html;

import io.swagger.models.HttpMethod;
import io.swagger.models.Info;
import io.swagger.models.Operation;
import io.swagger.models.Path;
import io.swagger.models.Scheme;
import io.swagger.models.Swagger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.pegdown.PegDownProcessor;

/**
 * {@link Swagger} class is not friendly to freemarker. Let's wrap it to produce
 * a friendly one.
 * 
 * @author chenjianjx@gmail.com
 *
 */
public class SwaggerWrapper {

	private TreeMap<OperationIdentity, Operation> allOperationsMap = new TreeMap<OperationIdentity, Operation>();
	private TreeMap<String, List<OperationIdentity>> tagAndIds = new TreeMap<String, List<OperationIdentity>>();

	private PegDownProcessor pgp = new PegDownProcessor();

	private Swagger swagger;

	public SwaggerWrapper(Swagger swagger) {
		if (swagger == null) {
			throw new RuntimeException();
		}
		this.swagger = swagger;

		init();
	}

	private void init() {
		Map<String, Path> paths = swagger.getPaths();
		if (paths == null) {
			return;
		}

		for (Map.Entry<String, Path> entry : paths.entrySet()) {
			String pathStr = entry.getKey();
			Path pathObj = entry.getValue();
			if (pathObj == null) {
				continue;
			}

			initPath(pathStr, pathObj);

		}
	}

	private void initPath(String pathStr, Path pathObj) {
		Map<HttpMethod, Operation> operationMap = pathObj.getOperationMap();
		if (operationMap == null) {
			return;
		}

		for (Map.Entry<HttpMethod, Operation> entry : operationMap.entrySet()) {
			String method = entry.getKey().name().toLowerCase();
			Operation operation = entry.getValue();
			List<String> tags = operation.getTags();
			for (String tagStr : tags) {
				addOperation(operation, tagStr, pathStr, method);
			}
		}
		return;
	}

	private void addOperation(Operation operation, String tagStr,
			String pathStr, String method) {
		OperationIdentity id = new OperationIdentity();

		id.setTag(tagStr);
		id.setPath(pathStr);
		id.setMethod(method);

		allOperationsMap.put(id, operation);

		List<OperationIdentity> idList = tagAndIds.get(tagStr);
		if (idList == null) {
			idList = new ArrayList<OperationIdentity>();
			tagAndIds.put(tagStr, idList);
		}
		idList.add(id);
	}

	public String getTitle() {
		Info info = swagger.getInfo();
		if (info == null) {
			return null;
		}
		return info.getTitle();
	}

	public String getDescription() {
		Info info = swagger.getInfo();
		if (info == null) {
			return null;
		}
		String markdown = info.getDescription();
		try {
			return pgp.markdownToHtml(markdown);
		} catch (Exception e) {
			return markdown;
		}
	}

	public Swagger getSwagger() {
		return swagger;
	}

	public List<String> getBaseUrls() {
		List<String> urls = new ArrayList<String>();
		List<Scheme> schemes = swagger.getSchemes();
		if (schemes == null) {
			return urls;
		}
		for (Scheme scheme : schemes) {
			String withoutScheme = swagger.getHost() + "/"
					+ swagger.getBasePath();
			withoutScheme = StringUtils.replace(withoutScheme, "//", "/");
			String url = (scheme + "://" + withoutScheme).toLowerCase();
			urls.add(url);
		}
		return urls;
	}

	/**
	 * will not return null
	 * 
	 * @param tagName
	 * @return
	 */
	public List<OperationIdentity> getOperationIdsOfTag(String tagName) {
		List<OperationIdentity> idList = tagAndIds.get(tagName);
		if (idList == null) {
			idList = new ArrayList<OperationIdentity>();
			tagAndIds.put(tagName, idList);
		}
		return idList;
	}

	public Operation getOperation(OperationIdentity id) {
		return allOperationsMap.get(id);
	}

	public static class OperationIdentity implements
			Comparable<OperationIdentity> {
		private String tag;
		/**
		 * in lower case
		 */
		private String method;
		private String path;

		public String getTag() {
			return tag;
		}

		public void setTag(String tag) {
			this.tag = tag;
		}

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((method == null) ? 0 : method.hashCode());
			result = prime * result + ((path == null) ? 0 : path.hashCode());
			result = prime * result + ((tag == null) ? 0 : tag.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			OperationIdentity other = (OperationIdentity) obj;
			if (method == null) {
				if (other.method != null)
					return false;
			} else if (!method.equals(other.method))
				return false;
			if (path == null) {
				if (other.path != null)
					return false;
			} else if (!path.equals(other.path))
				return false;
			if (tag == null) {
				if (other.tag != null)
					return false;
			} else if (!tag.equals(other.tag))
				return false;
			return true;
		}

		public String serialize() {
			return tag + "::::" + path + "::::" + method;
		}

		public String toString() {
			return serialize();
		}

		@Override
		public int compareTo(OperationIdentity obj) {
			if (obj == null) {
				return 1;
			}
			return this.serialize().compareTo(obj.serialize());
		}

	}
}
