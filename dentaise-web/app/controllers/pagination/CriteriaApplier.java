package controllers.pagination;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

public interface CriteriaApplier {
	public <S,T> void applyCondition(CriteriaBuilder builder, CriteriaQuery<S> criteriaQuery, Root<T> root);
	public <S,T> void applyOrder(CriteriaBuilder builder, CriteriaQuery<S> criteriaQuery, Root<T> root);
}
