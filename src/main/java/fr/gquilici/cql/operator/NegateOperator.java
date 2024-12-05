package fr.gquilici.cql.operator;

import org.springframework.data.jpa.domain.Specification;

import fr.gquilici.cql.Filter;
import fr.gquilici.cql.Operator;

public class NegateOperator<N> implements Operator<N> {

	private final Operator<N> delegate;

	public NegateOperator(Operator<N> delegate) {
		this.delegate = delegate;
	}

	@Override
	public <T> Specification<T> build(Filter<N> filter) {
		Specification<T> criteria = delegate.<T>build(filter);
		return (root, query, builder) -> builder.not(criteria.toPredicate(root, query, builder));
	}

}
