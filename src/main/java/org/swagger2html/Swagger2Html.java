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
		model.put("displayList", new DisplayListMethodModel());
		model.put("paramType", new ParamTypeMethodModel());
		model.put("responseType", new ResponseTypeMethodModel());		
		

		try {
			template.process(model, out);
		} catch (TemplateException e) {
			throw new IllegalStateException(e);
		}

	}

	@SuppressWarnings("rawtypes")
	private final class DisplayListMethodModel implements TemplateMethodModelEx {
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
	private final class ParamTypeMethodModel implements TemplateMethodModelEx {
		@Override
		public Object exec(List arguments) throws TemplateModelException {
			String prop = "type";
			return getParamProp(arguments, prop);
		}
	}

	@SuppressWarnings("rawtypes")
	private final class ResponseTypeMethodModel implements
			TemplateMethodModelEx {
		@Override
		public Object exec(List arguments) throws TemplateModelException {
			TemplateModel arg = (TemplateModel) arguments.get(0);
			Response response = (Response) DeepUnwrap.unwrap(arg);
			if (response == null) {
				return null;
			}

			Property schema = response.getSchema();
			if (schema == null) {
				return null;
			}

			if (schema instanceof RefProperty) {
				RefProperty rf = (RefProperty) schema;
				return rf.getSimpleRef();
			}
			return schema.getType();
		}
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
