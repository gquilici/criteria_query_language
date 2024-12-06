package fr.gquilici.cql.operator;

import org.springframework.data.jpa.domain.Specification;

import fr.gquilici.cql.Filter;
import fr.gquilici.cql.Operator;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public abstract class ExistsOperator<N> implements Operator<N> {

	private final PathResolver pathResolver;

	public ExistsOperator(PathResolver pathResolver) {
		this.pathResolver = pathResolver;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Specification<T> build(Filter<N> filter) {
		Filter<N> subqueryFilter = parseSubqueryFilter(filter);
		String joinProperty = parseJoinProperty(filter);
		return (root, query, builder) -> {
			// SELECT clause
			Subquery<Integer> subquery = query.subquery(Integer.class);
			subquery.select(builder.literal(1));

			// FROM clause
			Class<T> targetType = (Class<T>) root.getJavaType();
			Root<T> subqueryRoot = subquery.from(targetType);
			Path<?> rootPath = pathResolver.resolve(root, joinProperty);
			Path<?> subqueryPath = pathResolver.resolve(subqueryRoot, joinProperty);

			// WHERE clause
			Predicate joinPredicate = builder.equal(rootPath, subqueryPath);
			Predicate subqueryPredicate = subqueryFilter.<T>build().toPredicate(subqueryRoot, query, builder);
			subquery.where(joinPredicate, subqueryPredicate);

			return builder.exists(subquery);
		};
	}

	protected abstract Filter<N> parseSubqueryFilter(Filter<N> filter);

	protected abstract String parseJoinProperty(Filter<N> filter);

}
