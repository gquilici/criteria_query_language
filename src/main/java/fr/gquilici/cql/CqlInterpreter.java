package fr.gquilici.cql;

import org.springframework.data.jpa.domain.Specification;

public class CqlInterpreter<N, T> {

	private final FilterParser<N> filterParser;
	private final JoinsBuilder<T> joinsBuilder;

	public CqlInterpreter(FilterParser<N> filterParser, JoinsBuilder<T> joinsBuilder) {
		this.filterParser = filterParser;
		this.joinsBuilder = joinsBuilder;
	}

	public Specification<T> build(N criteria) {
		return (root, query, builder) -> {
			joinsBuilder.build(root);
			Filter<N> filter = filterParser.parse(criteria);
			return filter.<T>build().toPredicate(root, query, builder);
		};
	}

}
