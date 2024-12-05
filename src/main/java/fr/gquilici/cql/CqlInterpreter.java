package fr.gquilici.cql;

import org.springframework.data.jpa.domain.Specification;

public class CqlInterpreter<N, T> {

	private final FilterParser<N> filterParser;

	public CqlInterpreter(FilterParser<N> filterParser) {
		this.filterParser = filterParser;
	}

	public Specification<T> build(N criteria) {
		return (root, query, builder) -> {
			Filter<N> filter = filterParser.parse(criteria);
			return filter.<T>build().toPredicate(root, query, builder);
		};
	}

}
