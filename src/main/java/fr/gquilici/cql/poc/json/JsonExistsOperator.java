package fr.gquilici.cql.poc.json;

import com.fasterxml.jackson.databind.JsonNode;

import fr.gquilici.cql.Filter;
import fr.gquilici.cql.FilterParser;
import fr.gquilici.cql.operator.ExistsOperator;
import fr.gquilici.cql.operator.PathResolver;

public class JsonExistsOperator extends ExistsOperator<JsonNode> {

	private static final String SUBQUERY_FILTER_FIELD = "filter";

	private final FilterParser<JsonNode> filterParser;

	public JsonExistsOperator(FilterParser<JsonNode> filterParser, PathResolver pathResolver) {
		super(pathResolver);
		this.filterParser = filterParser;
	}

	@Override
	protected Filter<JsonNode> parseSubqueryFilter(Filter<JsonNode> filter) {
		JsonNode subqueryFilter = filter.options().required(SUBQUERY_FILTER_FIELD);
		return filterParser.parse(subqueryFilter);
	}

}
