package fr.gquilici.cql.json;

import com.fasterxml.jackson.databind.JsonNode;

import fr.gquilici.cql.operator.StringExpressionFormatter;
import fr.gquilici.cql.operator.StringFormatterOptions;

public class JsonStringExpressionFormatter extends StringExpressionFormatter<JsonNode> {

	private static final String IGNORE_CASE_OPTION_FIELD = "ignoreCase";
	private static final String IGNORE_ACCENTS_OPTION_FIELD = "ignoreAccents";

	@Override
	protected StringFormatterOptions parse(JsonNode options) {
		boolean ignoresCase = options.path(IGNORE_CASE_OPTION_FIELD).booleanValue();
		boolean ignoresAccents = options.path(IGNORE_ACCENTS_OPTION_FIELD).booleanValue();
		return new StringFormatterOptions(ignoresCase, ignoresAccents);
	}

}
