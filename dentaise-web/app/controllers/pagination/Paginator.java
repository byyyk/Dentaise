package controllers.pagination;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import play.db.jpa.JPA;


public class Paginator<T extends Serializable> {
	private static final int PAGE_SIZE = 10;
	private final String tableName;
	private String conditions = "";
	private String orderCombined = "";
	
	public Paginator(String tableName, String conditions, String orderBy, String order) {
		this.tableName = tableName;
		if (conditions != null && !conditions.isEmpty()) {
			this.conditions += " WHERE " + conditions;
		}
		if (orderBy != null && !orderBy.isEmpty()) {
			this.orderCombined = " ORDER BY " + orderBy;
		}
		if (order != null && !order.isEmpty()) {
			this.orderCombined += " " + order;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<T> get(final int page) {
		int offset = (page - 1) * PAGE_SIZE;
		Query query = listQuery().setMaxResults(PAGE_SIZE).setFirstResult(offset);
		return query.getResultList();
	}

	private Query listQuery() {
		return JPA.em().createQuery("SELECT t FROM " + tableName + " t" + conditions + orderCombined);
	}

	public int getPageCount() {
		TypedQuery<Long> query = countQuery();
		double count = query.getSingleResult();
		if (count == 0) { 
			return 1;
		} else {
			return (int) Math.ceil(count / PAGE_SIZE);
		}
	}

	private TypedQuery<Long> countQuery() {
		return JPA.em().createQuery("SELECT count(*) FROM " + tableName + conditions, Long.class);
	}
}
