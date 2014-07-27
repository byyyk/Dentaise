package controllers;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Query;

import play.db.jpa.JPA;


public class Paginator<T extends Serializable> {
	private static final int PAGE_SIZE = 10;
	private final String tableName;
	private String conditions = "";
	private String orderCombined = "";
	
	public Paginator(String tableName, String custom, String orderBy, String order) {
		this.tableName = tableName;
		if (custom != null && !custom.isEmpty()) {
			this.conditions += " WHERE " + custom;
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
		Query query = JPA.em().createQuery("SELECT t FROM " + tableName + " t" + conditions + orderCombined).setMaxResults(PAGE_SIZE).setFirstResult(offset);
		return query.getResultList();
	}

	public int getPageCount() {
		double count = JPA.em().createQuery("SELECT count(*) FROM " + tableName + conditions, Long.class).getSingleResult();
		if (count == 0) { 
			return 1;
		} else {
			return (int) Math.ceil(count / PAGE_SIZE);
		}
	}
}
