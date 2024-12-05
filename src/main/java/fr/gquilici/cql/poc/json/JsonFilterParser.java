package fr.gquilici.cql.poc.json;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import fr.gquilici.cql.Filter;
import fr.gquilici.cql.FilterParser;
import fr.gquilici.cql.Operator;

public class JsonFilterParser implements FilterParser<JsonNode> {

	private static final String PROPERTY_PATH_FIELD = "property";
	private static final String OPERATOR_FIELD = "operator";
	private static final String OPERATOR_CODE_FIELD = "code";
	private static final String OPERANDS_FIELD = "operands";

	private final Map<String, Operator<JsonNode>> operators = new HashMap<>();

	public void registerOperator(String code, Operator<JsonNode> operator) {
		this.operators.put(code, operator);
	}

	@Override
	public Filter<JsonNode> parse(JsonNode criteria) {
		String property = parsePropertyPath(criteria);
		Operator<JsonNode> operator = parseOperator(criteria);
		JsonNode options = criteria.required(OPERATOR_FIELD);
		List<JsonNode> operands = parseOperands(criteria);
		return new Filter<>(property, operator, options, operands);
	}

	private String parsePropertyPath(JsonNode criteria) {
		return criteria.path(PROPERTY_PATH_FIELD).textValue();
	}

	private Operator<JsonNode> parseOperator(JsonNode criteria) {
		JsonNode operatorNode = criteria.required(OPERATOR_FIELD);
		String operatorCode = operatorNode.isObject()
				? operatorNode.required(OPERATOR_CODE_FIELD).textValue()
				: operatorNode.textValue();
		if (operators.containsKey(operatorCode)) {
			return operators.get(operatorCode);
		}
		throw new IllegalArgumentException("L'opérateur <" + operatorCode + "> est inconnu dans ce dialecte !");
	}

	private List<JsonNode> parseOperands(JsonNode criteria) {
		Iterator<JsonNode> iterator = criteria.path(OPERANDS_FIELD).iterator();
		List<JsonNode> operands = new ArrayList<>();
		iterator.forEachRemaining(operands::add);
		return operands;
	}

}
