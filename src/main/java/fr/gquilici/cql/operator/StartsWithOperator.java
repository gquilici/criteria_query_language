package fr.gquilici.cql.operator;

import fr.gquilici.cql.OperandsParser;

public class StartsWithOperator<N> extends LikeOperator<N> {

	public StartsWithOperator(PathResolver pathResolver, OperandsParser<N> operandsParser,
			StringExpressionFormatter<N> expressionFormatter) {
		super(pathResolver, operandsParser, expressionFormatter, "", "%");
	}

}
