package fr.gquilici.cql;

import java.util.List;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;

public class StringExpressionFormatter {

	private static final String TRANSLATE_ACCENTS   = "ÁÀÂÄÃÇÉÈÊËÍÌÎÏÑÓÒÔÖÕÚÙÛÜÝŸáàâäãçéèêëíìîïñóòôöõúùûüýÿ";
	private static final String TRANSLATE_NO_ACCENT = "AAAAACEEEEIIIINOOOOOUUUUYYaaaaaceeeeiiiinooooouuuuyy";

	public List<Expression<String>> format(CriteriaBuilder builder, List<?> operands,
			OperatorOptions options) {
		return operands.stream()
				.map(Object::toString)
				.map(builder::literal)
				.map(expression -> format(builder, expression, options))
				.toList();
	}

	public Expression<String> format(CriteriaBuilder builder, Expression<String> expression,
			OperatorOptions options) {
		if (options.ignoresCase()) {
			expression = builder.lower(expression);
		}
		if (options.ignoresAccents()) {
			expression = builder.function("translate", String.class,
					expression,
					builder.literal(TRANSLATE_ACCENTS),
					builder.literal(TRANSLATE_NO_ACCENT));
		}
		return expression;
	}

}
