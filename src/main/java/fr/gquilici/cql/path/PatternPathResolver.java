package fr.gquilici.cql.path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;

public abstract class PatternPathResolver extends DefaultPathResolver {

	private final HashMap<Class<?>, List<String>> patterns = new HashMap<>();

	public PatternPathResolver register(Class<?> rootType, String... patterns) {
		this.patterns.putIfAbsent(rootType, new ArrayList<>());
		this.patterns.get(rootType).addAll(Arrays.asList(patterns));
		return this;
	}

	public List<String> getPatterns(Root<?> root) {
		return Optional.ofNullable(patterns.get(root.getJavaType()))
				.map(ArrayList::new)
				.orElseGet(ArrayList::new);
	}

	public abstract boolean isAuthorized(Root<?> root, String property);

	@Override
	public <R, T> Path<R> resolve(Root<T> root, String property) {
		if (!isAuthorized(root, property)) {
			throw new IllegalArgumentException("Property <" + property + "> is not authorized for target type <"
					+ root.getJavaType().getName() + ">");
		}
		return super.resolve(root, property);
	}

}
