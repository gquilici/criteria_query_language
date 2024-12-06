package fr.gquilici.cql.operator;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import fr.gquilici.cql.Filter;
import fr.gquilici.cql.OperandsParser;
import fr.gquilici.cql.Operator;
import jakarta.persistence.criteria.Path;

public class EqualsOperator<N> implements Operator<N> {

	private final PathResolver pathResolver;
	private final OperandsParser<N> operandsParser;
	private final StringExpressionFormatter<N> expressionFormatter;

	public EqualsOperator(PathResolver pathResolver, OperandsParser<N> operandsParser,
			StringExpressionFormatter<N> expressionFormatter) {
		this.pathResolver = pathResolver;
		this.operandsParser = operandsParser;
		this.expressionFormatter = expressionFormatter;
	}

	@Override
	public <T> Specification<T> build(Filter<N> filter) {
		return (root, query, builder) -> {
			Path<?> path = pathResolver.resolve(root, filter.property());
			Class<?> type = path.getJavaType();

			List<?> operands = operandsParser.parseAs(filter.operands(), type);
			if (operands.get(0) == null) {
				return builder.isNull(path);
			}

			if (type.equals(String.class)) {
				@SuppressWarnings("unchecked")
				var formatPath = expressionFormatter.format(builder, (Path<String>) path, filter.options());
				var formatOperands = expressionFormatter.format(builder, operands, filter.options());
				return builder.equal(formatPath, formatOperands.get(0));
			}
			return builder.equal(path, operands.get(0));
		};
	}

}
