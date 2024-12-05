package fr.gquilici.cql.operator;

import fr.gquilici.cql.OperandsParser;

public class EndsWithOperator<N> extends LikeOperator<N> {

	public EndsWithOperator(PathResolver pathResolver, OperandsParser<N> operandsParser,
			StringExpressionFormatter<N> expressionFormatter) {
		super(pathResolver, operandsParser, expressionFormatter, "%", "");
	}

}
