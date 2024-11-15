package fr.gquilici.cql.operator;

public class GreaterOrEqualsOperator<N> extends NegateOperator<N> {

	public GreaterOrEqualsOperator(LessThanOperator<N> lessThanOperator) {
		super(lessThanOperator);
	}

}
