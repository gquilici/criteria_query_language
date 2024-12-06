package fr.gquilici.cql.operator;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import fr.gquilici.cql.Filter;
import fr.gquilici.cql.OperandsParser;
import fr.gquilici.cql.Operator;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;

public class LikeOperator<N> implements Operator<N> {

	private final PathResolver pathResolver;
	private final OperandsParser<N> operandsParser;
	private final StringExpressionFormatter<N> expressionFormatter;
	private final String prefix;
	private final String suffix;

	public LikeOperator(PathResolver pathResolver, OperandsParser<N> operandsParser,
			StringExpressionFormatter<N> expressionFormatter) {
		this(pathResolver, operandsParser, expressionFormatter, "", "");
	}

	public LikeOperator(PathResolver pathResolver, OperandsParser<N> operandsParser,
			StringExpressionFormatter<N> expressionFormatter, String prefix, String suffix) {
		this.pathResolver = pathResolver;
		this.operandsParser = operandsParser;
		this.expressionFormatter = expressionFormatter;
		this.prefix = prefix;
		this.suffix = suffix;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Specification<T> build(Filter<N> filter) {
		return (root, query, builder) -> {
			Path<?> path = pathResolver.resolve(root, filter.property());
			Class<?> type = path.getJavaType();
			if (!String.class.equals(type)) {
				throw new UnsupportedOperationException("Operator <" + getClass().getName()
						+ "> does not support operands of type <" + type.getName() + ">");
			}

			List<String> operands = operandsParser.parseAsString(filter.operands());
			Expression<String> literal = builder.literal(prefix + operands.get(0) + suffix);

			var formatPath = expressionFormatter.format(builder, (Path<String>) path, filter.options());
			var pattern = expressionFormatter.format(builder, literal, filter.options());
			return builder.like(formatPath, pattern);
		};
	}

}
