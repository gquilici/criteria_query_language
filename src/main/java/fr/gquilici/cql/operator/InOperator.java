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

public class InOperator<N> implements Operator<N> {

	private PathResolver pathResolver;
	private OperandsParser<N> operandsParser;
	private StringExpressionFormatter stringExpressionFormatter;

	public InOperator(PathResolver pathResolver, OperandsParser<N> operandsParser) {
		this.pathResolver = pathResolver;
		this.operandsParser = operandsParser;
		this.stringExpressionFormatter = new StringExpressionFormatter();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Specification<T> build(Filter<N> filter) {
		return (root, query, builder) -> {
			Path<?> path = pathResolver.resolve(filter.property(), root);
			Class<?> type = path.getJavaType();
			// TODO checkAcceptedType(type);

			if (type.equals(String.class)) {
				List<String> operands = operandsParser.parseAsString(filter.operands());
				var formatPath = stringExpressionFormatter.format(builder, (Path<String>) path, filter.options());
				var formatOperands = stringExpressionFormatter.format(builder, operands, filter.options());
				return formatPath.in(formatOperands.toArray(new Expression[0]));
			}

			List<?> operands = operandsParser.parseAs(filter.operands(), type);
			return path.in(operands);
		};
	}

}
