package org.swagger2html;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;
import io.swagger.models.Model;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.parser.SwaggerParser;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.swagger2html.util.FreemarkerTemplateFactory;

/**
 * 
 * @author chenjianjx@gmail.com
 *
 */
public class Swagger2Html {

	private FreemarkerTemplateFactory freemarkerFactory = FreemarkerTemplateFactory
			.getInstance();

	private SwaggerParser swaggerParser = new SwaggerParser();

	public void toHtml(String swaggerUrl, Writer out) throws IOException {
		Template template = freemarkerFactory
				.getClasspathTemplate("/single-html.ftl");
		Map<String, Object> model = new HashMap<String, Object>();
		Swagger swagger = swaggerParser.read(swaggerUrl);
		SwaggerWrapper sw = new SwaggerWrapper(swagger);
		model.put("sw", sw);
		model.put("displayList", new DisplayList());
		model.put("paramType", new ParamType());
		model.put("isResponsePrimitiveType", new IsResponseValidPrimitiveType());
		model.put("isResponseDefType", new IsResponseValidDefType(swagger));
		model.put("responseTypeStr", new ReponseTypeString());
		model.put("refPropertyToModelRows", new RefPropertyToModelRows(swagger));

		try {
			template.process(model, out);
		} catch (TemplateException e) {
			throw new IllegalStateException(e);
		}

	}

	@SuppressWarnings("rawtypes")
	private final class DisplayList implements TemplateMethodModelEx {
		@Override
		public Object exec(List arguments) throws TemplateModelException {
			TemplateModel arg = (TemplateModel) arguments.get(0);
			List list = (List) DeepUnwrap.unwrap(arg);
			if (list == null) {
				return null;
			}
			return StringUtils.join(list, ",");
		}
	}

	@SuppressWarnings("rawtypes")
	private final class ParamType implements TemplateMethodModelEx {
		@Override
		public Object exec(List arguments) throws TemplateModelException {
			String prop = "type";
			return getParamProp(arguments, prop);
		}
	}

	@SuppressWarnings("rawtypes")
	private final class ReponseTypeString implements TemplateMethodModelEx {
		@Override
		public Object exec(List arguments) throws TemplateModelException {
			TemplateModel arg = (TemplateModel) arguments.get(0);
			Response response = (Response) DeepUnwrap.unwrap(arg);
			if (response == null) {
				return null;
			}
			Property schema = response.getSchema();
			return propertyTypeString(schema);
		}
	}

	@SuppressWarnings("rawtypes")
	private final class RefPropertyToModelRows implements TemplateMethodModelEx {

		private Swagger swagger;

		public RefPropertyToModelRows(Swagger swagger) {
			this.swagger = swagger;
		}

		@Override
		public Object exec(List arguments) throws TemplateModelException {
			TemplateModel arg = (TemplateModel) arguments.get(0);
			RefProperty refProp = (RefProperty) DeepUnwrap.unwrap(arg);
			List<ModelRow> rows = new ArrayList<Swagger2Html.ModelRow>();
			if (refProp == null) {
				return rows;
			}
			toRows(refProp, "$", rows);
			return rows;
		}

		private void toRows(Property property, String ognlPath,
				List<ModelRow> rows) {
			ModelRow row = new ModelRow();
			String type = propertyTypeString(property);

			row.setOgnlPath(ognlPath);
			row.setProperty(property);
			row.setTypeStr(type);
			rows.add(row);

			if (isPropertyValidPrimitiveType(property)) {
				return; // no more
			}
			if (isPropertyValidDefType(swagger, property) && type != null) {
				Model model = swagger.getDefinitions().get(type);
				Map<String, Property> childProperties = model.getProperties();
				for (Map.Entry<String, Property> entry : childProperties
						.entrySet()) {
					String childPath = entry.getKey();
					Property childProperty = entry.getValue();
					toRows(childProperty, ognlPath + "." + childPath, rows);
				}
			}

		}
	}

	private String propertyTypeString(Property property) {
		if (property == null) {
			return null;
		}

		if (property instanceof RefProperty) {
			RefProperty rf = (RefProperty) property;
			return rf.getSimpleRef();
		}
		return property.getType();
	}

	@SuppressWarnings("rawtypes")
	private final class IsResponseValidDefType implements TemplateMethodModelEx {
		private Swagger swagger;

		public IsResponseValidDefType(Swagger swagger) {
			this.swagger = swagger;
		}

		@Override
		public Object exec(List arguments) throws TemplateModelException {
			TemplateModel arg = (TemplateModel) arguments.get(0);
			Response response = (Response) DeepUnwrap.unwrap(arg);
			if (response == null) {
				return false;
			}

			Property schema = response.getSchema();
			return isPropertyValidDefType(swagger, schema);

		}

	}

	@SuppressWarnings("rawtypes")
	private final class IsResponseValidPrimitiveType implements
			TemplateMethodModelEx {
		@Override
		public Object exec(List arguments) throws TemplateModelException {
			TemplateModel arg = (TemplateModel) arguments.get(0);
			Response response = (Response) DeepUnwrap.unwrap(arg);
			if (response == null) {
				return false;
			}

			Property schema = response.getSchema();
			return isPropertyValidPrimitiveType(schema);
		}

	}

	private boolean isPropertyValidDefType(Swagger swagger, Property property) {
		if (property == null) {
			return false;
		}

		if (!(property instanceof RefProperty)) {
			return false;
		}

		RefProperty rf = (RefProperty) property;
		String simpleRef = rf.getSimpleRef();
		if (simpleRef == null) {
			return false;
		}
		if (swagger.getDefinitions() == null) {
			return false;
		}
		boolean containsRef = swagger.getDefinitions().containsKey(simpleRef);
		return containsRef;
	}

	private boolean isPropertyValidPrimitiveType(Property schema) {
		if (schema == null) {
			return false;
		}
		if (schema instanceof RefProperty) {
			return false;
		}
		return schema.getType() != null;
	}

	// TODO: response headers

	private Object getParamProp(@SuppressWarnings("rawtypes") List arguments,
			String prop) throws TemplateModelException {
		TemplateModel arg = (TemplateModel) arguments.get(0);
		Parameter param = (Parameter) DeepUnwrap.unwrap(arg);
		if (param == null) {
			return null;
		}
		return getProperty(param, prop);
	}

	private Object getProperty(Object obj, String prop) {
		try {
			return PropertyUtils.getProperty(obj, prop);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * one row, one property of a model
	 * 
	 * @author chenjianjx@gmail.com
	 *
	 */
	public static class ModelRow {
		/**
		 * something like "$.father.son,  the root will be "$". not null
		 */
		private String ognlPath;

		/**
		 * may be null
		 */
		private String typeStr;

		/**
		 * will not be null
		 */
		private Property property;

		public String getOgnlPath() {
			return ognlPath;
		}

		public void setOgnlPath(String ognlPath) {
			this.ognlPath = ognlPath;
		}

		public String getTypeStr() {
			return typeStr;
		}

		public void setTypeStr(String type) {
			this.typeStr = type;
		}

		public Property getProperty() {
			return property;
		}

		public void setProperty(Property property) {
			this.property = property;
		}

	}

}
