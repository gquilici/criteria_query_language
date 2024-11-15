package fr.gquilici.cql.operator;

import fr.gquilici.cql.OperandsParser;
import fr.gquilici.cql.PathResolver;

public class ContainsOperator<N> extends LikeOperator<N> {

	public ContainsOperator(PathResolver pathResolver, OperandsParser<N> operandsParser) {
		super(pathResolver, operandsParser, "%", "%");
	}

}
