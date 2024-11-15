package fr.gquilici.cql.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import fr.gquilici.cql.Filter;
import fr.gquilici.cql.FilterParser;
import fr.gquilici.cql.Operator;
import fr.gquilici.cql.OperatorOptions;

public class JsonFilterParser implements FilterParser<JsonNode> {

	private static final String PROPERTY_PATH_FIELD = "property";
	private static final String OPERATOR_CODE_FIELD = "operator";
	private static final String IGNORE_CASE_OPTION_FIELD = "ignoreCase";
	private static final String IGNORE_ACCENTS_OPTION_FIELD = "ignoreAccents";
	private static final String OPERANDS_FIELD = "operands";

	private Map<String, Operator<JsonNode>> operators = new HashMap<>();

	public void setOperators(Map<String, Operator<JsonNode>> operators) {
		this.operators = operators;
	}

	@Override
	public Filter<JsonNode> parse(JsonNode criteria) {
		String property = parsePropertyPath(criteria);
		Operator<JsonNode> operator = parseOperator(criteria);
		OperatorOptions options = parseOperatorOptions(criteria);
		List<JsonNode> operands = parseOperands(criteria);
		return new Filter<>(property, operator, options, operands);
	}

	private String parsePropertyPath(JsonNode criteria) {
		return criteria.path(PROPERTY_PATH_FIELD).textValue();
	}

	private Operator<JsonNode> parseOperator(JsonNode criteria) {
		String operatorCode = criteria.required(OPERATOR_CODE_FIELD).textValue();
		if (operators.containsKey(operatorCode)) {
			return operators.get(operatorCode);
		}
		throw new IllegalArgumentException("L'opérateur <" + operatorCode + "> est inconnu dans ce dialecte !");
	}

	private OperatorOptions parseOperatorOptions(JsonNode criteria) {
		boolean ignoresCase = criteria.path(IGNORE_CASE_OPTION_FIELD).booleanValue();
		boolean ignoresAccents = criteria.path(IGNORE_ACCENTS_OPTION_FIELD).booleanValue();
		return new OperatorOptions(ignoresCase, ignoresAccents);
	}

	private List<JsonNode> parseOperands(JsonNode criteria) {
		Iterator<JsonNode> iterator = criteria.path(OPERANDS_FIELD).iterator();
		List<JsonNode> operands = new ArrayList<>();
		iterator.forEachRemaining(operands::add);
		return operands;
	}

}
