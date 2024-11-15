package fr.gquilici.cql.operator;

import fr.gquilici.cql.OperandsParser;
import fr.gquilici.cql.PathResolver;

public class StartsWithOperator<N> extends LikeOperator<N> {

	public StartsWithOperator(PathResolver pathResolver, OperandsParser<N> operandsParser) {
		super(pathResolver, operandsParser, "", "%");
	}

}
