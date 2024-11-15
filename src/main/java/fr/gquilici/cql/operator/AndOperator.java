package fr.gquilici.cql.operator;

import java.util.stream.Stream;

import org.springframework.data.jpa.domain.Specification;

import fr.gquilici.cql.Filter;
import fr.gquilici.cql.FilterParser;
import fr.gquilici.cql.Operator;

public class AndOperator<N> implements Operator<N> {

	private FilterParser<N> filterParser;

	public AndOperator(FilterParser<N> filterParser) {
		this.filterParser = filterParser;
	}

	@Override
	public <T> Specification<T> build(Filter<N> filter) {
		Stream<Specification<T>> criteria = filter.operands().stream()
				.map(filterParser::parse)
				.map(Filter::<T>build);
		return (root, query, builder) -> {
			return criteria
					.map(s -> s.toPredicate(root, query, builder))
					.reduce(builder.conjunction(), builder::and);
		};
	}

}
