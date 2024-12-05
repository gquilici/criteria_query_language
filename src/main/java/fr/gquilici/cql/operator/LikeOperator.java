package fr.gquilici.cql.operator;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import fr.gquilici.cql.Filter;
import fr.gquilici.cql.OperandsParser;
import fr.gquilici.cql.Operator;
import fr.gquilici.cql.PathResolver;
import fr.gquilici.cql.StringExpressionFormatter;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;

public class LikeOperator<N> implements Operator<N> {

	private final PathResolver pathResolver;
	private final OperandsParser<N> operandsParser;
	private StringExpressionFormatter stringExpressionFormatter;
	private final String prefix;
	private final String suffix;

	public LikeOperator(PathResolver pathResolver, OperandsParser<N> operandsParser) {
		this(pathResolver, operandsParser, "", "");
	}

	public LikeOperator(PathResolver pathResolver, OperandsParser<N> operandsParser, String prefix, String suffix) {
		this.pathResolver = pathResolver;
		this.operandsParser = operandsParser;
		this.stringExpressionFormatter = new StringExpressionFormatter();
		this.prefix = prefix;
		this.suffix = suffix;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Specification<T> build(Filter<N> filter) {
		return (root, query, builder) -> {
			Path<?> path = pathResolver.resolve(filter.property(), root);
			Class<?> type = path.getJavaType();
			if (!String.class.equals(type)) {
				throw new UnsupportedOperationException("L'opérateur <" + getClass().getSimpleName()
						+ "> ne prend pas en charge le type d'opérande <" + type.getSimpleName() + ">");
			}

			List<String> operands = operandsParser.parseAsString(filter.operands());
			Expression<String> literal = builder.literal(prefix + operands.get(0) + suffix);

			var formatPath = stringExpressionFormatter.format(builder, (Path<String>) path, filter.options());
			var pattern = stringExpressionFormatter.format(builder, literal, filter.options());
			return builder.like(formatPath, pattern);
		};
	}

}
