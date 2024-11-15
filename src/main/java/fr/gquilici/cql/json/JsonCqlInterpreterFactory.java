package fr.gquilici.cql.json;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import fr.gquilici.cql.CqlInterpreter;
import fr.gquilici.cql.JoinsBuilder;
import fr.gquilici.cql.Operator;
import fr.gquilici.cql.PathResolver;
import fr.gquilici.cql.operator.AndOperator;
import fr.gquilici.cql.operator.BetweenOperator;
import fr.gquilici.cql.operator.ContainsOperator;
import fr.gquilici.cql.operator.EndsWithOperator;
import fr.gquilici.cql.operator.EqualsOperator;
import fr.gquilici.cql.operator.InOperator;
import fr.gquilici.cql.operator.LikeOperator;
import fr.gquilici.cql.operator.NegateOperator;
import fr.gquilici.cql.operator.NotOperator;
import fr.gquilici.cql.operator.OrOperator;
import fr.gquilici.cql.operator.StartsWithOperator;

public class JsonCqlInterpreterFactory {

	private final JsonFilterParser filterParser = new JsonFilterParser();
	private final PathResolver pathResolver = new PathResolver();
	private final JsonOperandsParser operandsParser = new JsonOperandsParser();

	public JsonCqlInterpreterFactory() {
		Map<String, Operator<JsonNode>> operators = new HashMap<>();
		operators.put("$and", new AndOperator<>(filterParser));
		operators.put("$or", new OrOperator<>(filterParser));
		operators.put("$not", new NotOperator<>(filterParser));

		EqualsOperator<JsonNode> equalsOperator = new EqualsOperator<>(pathResolver, operandsParser);
		operators.put("$eq", equalsOperator);
		operators.put("$neq", new NegateOperator<>(equalsOperator));

		BetweenOperator<JsonNode> betweenOperator = new BetweenOperator<>(pathResolver, operandsParser);
		operators.put("$bw", betweenOperator);
		operators.put("$nbw", new NegateOperator<>(betweenOperator));

		InOperator<JsonNode> inOperator = new InOperator<>(pathResolver, operandsParser);
		operators.put("$in", inOperator);
		operators.put("$nin", new NegateOperator<>(inOperator));

		LikeOperator<JsonNode> likeOperator = new LikeOperator<>(pathResolver, operandsParser);
		operators.put("$lk", likeOperator);
		operators.put("$nlk", new NegateOperator<>(likeOperator));

		StartsWithOperator<JsonNode> startsWithOperator = new StartsWithOperator<>(pathResolver, operandsParser);
		operators.put("$sw", startsWithOperator);
		operators.put("$nsw", new NegateOperator<>(startsWithOperator));

		ContainsOperator<JsonNode> containsOperator = new ContainsOperator<>(pathResolver, operandsParser);
		operators.put("$ct", containsOperator);
		operators.put("$nct", new NegateOperator<>(containsOperator));

		EndsWithOperator<JsonNode> endsWithOperator = new EndsWithOperator<>(pathResolver, operandsParser);
		operators.put("$ew", endsWithOperator);
		operators.put("$new", new NegateOperator<>(endsWithOperator));

		filterParser.setOperators(operators);
	}

	public <T> CqlInterpreter<JsonNode, T> build() {
		return new CqlInterpreter<>(filterParser, (root) -> {});
	}

	public <T> CqlInterpreter<JsonNode, T> build(JoinsBuilder<T> joinsBuilder) {
		return new CqlInterpreter<>(filterParser, joinsBuilder);
	}

}
