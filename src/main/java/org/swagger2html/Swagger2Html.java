package org.swagger2html;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;
import io.swagger.models.Model;
import io.swagger.models.RefModel;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.RefParameter;
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
		model.put("paramTypeStr", new ParamTypeStringTmme());
		model.put("paramToModelRows", new ParameterToModelRowsTmme(swagger));
		model.put("propertyTypeStr", new PropertyTypeStringTmme());
		model.put("propertyToModelRows", new PropertyToModelRowsTmme(swagger));
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
	private final class ParamTypeStringTmme implements TemplateMethodModelEx {
		@Override
		public Object exec(List arguments) throws TemplateModelException {
			TemplateModel arg = (TemplateModel) arguments.get(0);
			Parameter param = (Parameter) DeepUnwrap.unwrap(arg);
			return getParamTypeString(param);
		}

	}

	private String getParamTypeString(Parameter param) {
		if (param == null) {
			return null;
		}
		if (param instanceof BodyParameter) {
			BodyParameter bp = (BodyParameter) param;
			Model schema = bp.getSchema();

			if (schema == null) {
				return null;
			}
			if (!(schema instanceof RefModel)) {
				return null;
			}
			RefModel rm = (RefModel) schema;
			return rm == null ? null : rm.getSimpleRef();
		}
		if (param instanceof RefParameter) {
			RefParameter rp = (RefParameter) param;
			return rp.getSimpleRef();
		}
		return (String) getBeanProperty(param, "type");
	}

	@SuppressWarnings("rawtypes")
	private final class PropertyTypeStringTmme implements TemplateMethodModelEx {
		@Override
		public Object exec(List arguments) throws TemplateModelException {
			TemplateModel arg = (TemplateModel) arguments.get(0);
			Property schema = (Property) DeepUnwrap.unwrap(arg);
			return propertyTypeString(schema);
		}
	}

	@SuppressWarnings("rawtypes")
	private final class ParameterToModelRowsTmme implements
			TemplateMethodModelEx {

		private Swagger swagger;

		public ParameterToModelRowsTmme(Swagger swagger) {
			this.swagger = swagger;
		}

		@Override
		public Object exec(List arguments) throws TemplateModelException {
			List<ModelRow> rows = new ArrayList<Swagger2Html.ModelRow>();

			TemplateModel arg = (TemplateModel) arguments.get(0);
			Parameter param = (Parameter) DeepUnwrap.unwrap(arg);
			if (param == null) {
				return rows;
			}
			String type = getParamTypeString(param);
			if (type == null) {
				return rows;
			}

			if (swagger.getDefinitions() == null) {
				return rows;
			}

			if (param instanceof BodyParameter || param instanceof RefParameter) {
				Model model = swagger.getDefinitions().get(type);
				if (model == null) {
					return rows;
				}
				modelPropertiesToRows(model.getProperties(), swagger, null,
						rows);
				return rows;
			}
			return rows;
		}

	}

	@SuppressWarnings("rawtypes")
	private final class PropertyToModelRowsTmme implements
			TemplateMethodModelEx {
		private Swagger swagger;

		public PropertyToModelRowsTmme(Swagger swagger) {
			this.swagger = swagger;
		}

		@Override
		public Object exec(List arguments) throws TemplateModelException {
			TemplateModel arg = (TemplateModel) arguments.get(0);
			Property prop = (Property) DeepUnwrap.unwrap(arg);
			List<ModelRow> rows = new ArrayList<Swagger2Html.ModelRow>();
			if (prop == null) {
				return rows;
			}

			if (isPropertyDefType(swagger, prop)) {
				String type = propertyTypeString(prop);
				Model model = swagger.getDefinitions().get(type);
				Map<String, Property> childProperties = model.getProperties();
				modelPropertiesToRows(childProperties, swagger, null, rows);
				return rows;
			} else {
				return rows;
			}
		}

	}

	private void modelPropertiesToRows(Map<String, Property> properties,
			Swagger swagger, String parentPath, List<ModelRow> rows) {

		if (properties == null) {
			return;
		}

		for (Map.Entry<String, Property> entry : properties.entrySet()) {
			String ognlPath = parentPath == null ? entry.getKey() : (parentPath
					+ "." + entry.getKey());
			Property property = entry.getValue();
			String type = propertyTypeString(property);

			ModelRow row = new ModelRow();
			row.setOgnlPath(ognlPath);
			row.setProperty(property);
			row.setTypeStr(type);

			rows.add(row);

			if (isPropertyPrimitiveType(property)) {
				continue; // no more
			}
			if (isPropertyDefType(swagger, property) && type != null) {
				Model model = swagger.getDefinitions().get(type);
				Map<String, Property> childProperties = model.getProperties();
				modelPropertiesToRows(childProperties, swagger, ognlPath, rows);
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

	private boolean isPropertyDefType(Swagger swagger, Property property) {
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
		return true;
	}

	private boolean isPropertyPrimitiveType(Property schema) {
		if (schema == null) {
			return false;
		}
		if (schema instanceof RefProperty) {
			return false;
		}
		return schema.getType() != null;
	}

	// TODO: response headers

	private Object getBeanProperty(Object obj, String prop) {
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
