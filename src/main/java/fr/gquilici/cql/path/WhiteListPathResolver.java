package fr.gquilici.cql.path;

import java.util.List;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import jakarta.persistence.criteria.Root;

public class WhiteListPathResolver extends PatternPathResolver {

	private final PathMatcher pathMatcher = new AntPathMatcher(".");

	@Override
	public boolean isAuthorized(Root<?> root, String property) {
		List<String> patterns = getPatterns(root);
		for (String pattern : patterns) {
			if (pathMatcher.match(pattern, property)) {
				return true;
			}
		}
		return false;
	}

}
