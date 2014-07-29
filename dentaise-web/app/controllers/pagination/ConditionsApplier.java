package controllers.pagination;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public interface ConditionsApplier {
	public <S,T> void apply(CriteriaBuilder builder, CriteriaQuery<S> criteriaQuery, Root<T> root);
}
