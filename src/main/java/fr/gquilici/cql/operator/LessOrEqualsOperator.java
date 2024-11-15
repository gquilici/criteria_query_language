package fr.gquilici.cql.operator;

public class LessOrEqualsOperator<N> extends NegateOperator<N> {

	public LessOrEqualsOperator(GreaterThanOperator<N> greaterThanOperator) {
		super(greaterThanOperator);
	}

}
