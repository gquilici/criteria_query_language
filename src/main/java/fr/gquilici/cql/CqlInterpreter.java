package fr.gquilici.cql;

import org.springframework.data.jpa.domain.Specification;

public class CqlInterpreter<N, T> {

	private final FilterParser<N> filterParser;
	private final JoinsBuilder<T> joinsMaker;

	public CqlInterpreter(FilterParser<N> filterParser) {
		this(filterParser, (root) -> {});
	}

	public CqlInterpreter(FilterParser<N> filterParser, JoinsBuilder<T> joinsMaker) {
		this.filterParser = filterParser;
		this.joinsMaker = joinsMaker;
	}

	public Specification<T> build(N criteria) {
		return (root, query, builder) -> {
			joinsMaker.build(root);
			Filter<N> filter = filterParser.parse(criteria);
			return filter.<T>build().toPredicate(root, query, builder);
		};
	}

}
