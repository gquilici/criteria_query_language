package fr.gquilici.cql.json;

import com.fasterxml.jackson.databind.JsonNode;

import fr.gquilici.cql.CqlInterpreterFactory;
import fr.gquilici.cql.FilterParser;
import fr.gquilici.cql.operator.AndOperator;
import fr.gquilici.cql.operator.BetweenOperator;
import fr.gquilici.cql.operator.ContainsOperator;
import fr.gquilici.cql.operator.DefaultPathResolver;
import fr.gquilici.cql.operator.EndsWithOperator;
import fr.gquilici.cql.operator.EqualsOperator;
import fr.gquilici.cql.operator.ExistsOperator;
import fr.gquilici.cql.operator.InOperator;
import fr.gquilici.cql.operator.LikeOperator;
import fr.gquilici.cql.operator.NegateOperator;
import fr.gquilici.cql.operator.NotOperator;
import fr.gquilici.cql.operator.OrOperator;
import fr.gquilici.cql.operator.PathResolver;
import fr.gquilici.cql.operator.StartsWithOperator;

public class JsonCqlInterpreterFactory implements CqlInterpreterFactory<JsonNode> {

	private final JsonFilterParser filterParser;

	public JsonCqlInterpreterFactory() {
		filterParser = new JsonFilterParser();
		PathResolver pathResolver = new DefaultPathResolver();
		JsonOperandsParser operandsParser = new JsonOperandsParser();
		JsonStringExpressionFormatter expressionFormatter = new JsonStringExpressionFormatter();

		filterParser.registerOperator("$and", new AndOperator<>(filterParser));
		filterParser.registerOperator("$or", new OrOperator<>(filterParser));
		filterParser.registerOperator("$not", new NotOperator<>(filterParser));

		EqualsOperator<JsonNode> equalsOperator = new EqualsOperator<>(pathResolver, operandsParser,
				expressionFormatter);
		filterParser.registerOperator("$eq", equalsOperator);
		filterParser.registerOperator("$neq", new NegateOperator<>(equalsOperator));

		BetweenOperator<JsonNode> betweenOperator = new BetweenOperator<>(pathResolver, operandsParser,
				expressionFormatter);
		filterParser.registerOperator("$bw", betweenOperator);
		filterParser.registerOperator("$nbw", new NegateOperator<>(betweenOperator));

		InOperator<JsonNode> inOperator = new InOperator<>(pathResolver, operandsParser, expressionFormatter);
		filterParser.registerOperator("$in", inOperator);
		filterParser.registerOperator("$nin", new NegateOperator<>(inOperator));

		LikeOperator<JsonNode> likeOperator = new LikeOperator<>(pathResolver, operandsParser, expressionFormatter);
		filterParser.registerOperator("$lk", likeOperator);
		filterParser.registerOperator("$nlk", new NegateOperator<>(likeOperator));

		StartsWithOperator<JsonNode> startsWithOperator = new StartsWithOperator<>(pathResolver, operandsParser,
				expressionFormatter);
		filterParser.registerOperator("$sw", startsWithOperator);
		filterParser.registerOperator("$nsw", new NegateOperator<>(startsWithOperator));

		ContainsOperator<JsonNode> containsOperator = new ContainsOperator<>(pathResolver, operandsParser,
				expressionFormatter);
		filterParser.registerOperator("$ct", containsOperator);
		filterParser.registerOperator("$nct", new NegateOperator<>(containsOperator));

		EndsWithOperator<JsonNode> endsWithOperator = new EndsWithOperator<>(pathResolver, operandsParser,
				expressionFormatter);
		filterParser.registerOperator("$ew", endsWithOperator);
		filterParser.registerOperator("$new", new NegateOperator<>(endsWithOperator));

		ExistsOperator<JsonNode> existsOperator = new JsonExistsOperator(filterParser, pathResolver);
		filterParser.registerOperator("$ex", existsOperator);
		filterParser.registerOperator("$nex", new NegateOperator<>(existsOperator));
	}

	@Override
	public FilterParser<JsonNode> getFilterParser() {
		return filterParser;
	}

}
