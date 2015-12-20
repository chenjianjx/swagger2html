package org.swagger2html;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;
import io.swagger.models.Response;
import io.swagger.models.Swagger;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.properties.Property;
import io.swagger.models.properties.RefProperty;
import io.swagger.parser.SwaggerParser;

import java.io.IOException;
import java.io.Writer;
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
		Swagger originalSwagger = swaggerParser.read(swaggerUrl);
		SwaggerWrapper sw = new SwaggerWrapper(originalSwagger);
		model.put("sw", sw);
		model.put("displayList", new DisplayList());
		model.put("paramType", new ParamType());
		model.put("isResponsePrimitiveType", new IsResponsePrimitiveType());
		model.put("isResponseDefType", new IsResponseDefType());

		model.put("responseTypeStr", new ReponseTypeString());

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
	
	private Object propertyTypeString(Property property) {
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
	private final class IsResponseDefType implements TemplateMethodModelEx {
		@Override
		public Object exec(List arguments) throws TemplateModelException {
			TemplateModel arg = (TemplateModel) arguments.get(0);
			Response response = (Response) DeepUnwrap.unwrap(arg);
			if (response == null) {
				return false;
			}

			Property schema = response.getSchema();
			return isPropertyDefType(schema);

		}

	}

	@SuppressWarnings("rawtypes")
	private final class IsResponsePrimitiveType implements
			TemplateMethodModelEx {
		@Override
		public Object exec(List arguments) throws TemplateModelException {
			TemplateModel arg = (TemplateModel) arguments.get(0);
			Response response = (Response) DeepUnwrap.unwrap(arg);
			if (response == null) {
				return false;
			}

			Property schema = response.getSchema();
			return isPropertyPrimitiveType(schema);
		}

	}

	private Object isPropertyDefType(Property property) {
		if (property == null) {
			return false;
		}

		if (!(property instanceof RefProperty)) {
			return false;
		}

		RefProperty rf = (RefProperty) property;
		return rf.getSimpleRef() != null;
	}

	private Object isPropertyPrimitiveType(Property schema) {
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
		return (String) getProperty(param, prop);
	}

	private Object getProperty(Object obj, String prop) {
		try {
			return PropertyUtils.getProperty(obj, prop);
		} catch (Exception e) {
			return null;
		}
	}

}
