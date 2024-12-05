package fr.gquilici.cql.operator;

import org.springframework.data.jpa.domain.Specification;

import fr.gquilici.cql.Filter;
import fr.gquilici.cql.FilterParser;
import fr.gquilici.cql.Operator;

public class NotOperator<N> implements Operator<N> {

	private final FilterParser<N> filterParser;

	public NotOperator(FilterParser<N> filterParser) {
		this.filterParser = filterParser;
	}

	@Override
	public <T> Specification<T> build(Filter<N> filter) {
		Specification<T> criteria = filterParser.parse(filter.operands().get(0)).build();
		return (root, query, builder) -> builder.not(criteria.toPredicate(root, query, builder));
	}

}
