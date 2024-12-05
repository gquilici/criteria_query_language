package fr.gquilici.cql.operator;

import fr.gquilici.cql.OperandsParser;

public class ContainsOperator<N> extends LikeOperator<N> {

	public ContainsOperator(PathResolver pathResolver, OperandsParser<N> operandsParser,
			StringExpressionFormatter<N> expressionFormatter) {
		super(pathResolver, operandsParser, expressionFormatter, "%", "%");
	}

}
