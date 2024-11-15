package fr.gquilici.cql.operator;

import fr.gquilici.cql.OperandsParser;
import fr.gquilici.cql.PathResolver;

public class EndsWithOperator<N> extends LikeOperator<N> {

	public EndsWithOperator(PathResolver pathResolver, OperandsParser<N> operandsParser) {
		super(pathResolver, operandsParser, "%", "");
	}

}
