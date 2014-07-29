package controllers.pagination;

import java.io.Serializable;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import play.db.jpa.JPA;


public class CriteriaPaginator<T extends Serializable> {
	private static final int PAGE_SIZE = 10;
	private final Class<T> table;
	private static final CriteriaBuilder builder = JPA.em().getCriteriaBuilder();
	private ConditionsApplier conditionsApplier;
	
	
	public CriteriaPaginator(Class<T> table, ConditionsApplier conditionsApplier) {
		this.table = table;
		this.conditionsApplier = conditionsApplier;
	}
	
	public List<T> get(final int page) {
		CriteriaQuery<T> criteriaQuery = builder.createQuery(table);
		Root<T> root = criteriaQuery.from(table);
		criteriaQuery.select(root);
		
		conditionsApplier.apply(builder, criteriaQuery, root);
		
		int offset = (page - 1) * PAGE_SIZE;
		TypedQuery<T> query = JPA.em().createQuery(criteriaQuery).setMaxResults(PAGE_SIZE).setFirstResult(offset);
		return query.getResultList();
	}

	public int getPageCount() {
		CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
		Root<T> root = criteriaQuery.from(table);
		criteriaQuery.select(builder.count(root));
		
		conditionsApplier.apply(builder, criteriaQuery, root);
		
		long count = JPA.em().createQuery(criteriaQuery).getSingleResult();
		System.out.println("calculated page count: " + count);
		if (count == 0) {
			return 1;
		} else {
			return (int) Math.ceil(count / PAGE_SIZE);
		}
	}

}
