package controllers;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Query;

import play.db.jpa.JPA;


public class Paginator<T extends Serializable> {
	private static final int PAGE_SIZE = 10;
	private final String tableName;
	private String condition;
	
	public Paginator(String tableName, String condition) {
		this.tableName = tableName;
		if (condition == null) {
			this.condition = "";
		} else {
			this.condition = " WHERE " + condition;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<T> get(final int page) {
		int offset = (page - 1) * PAGE_SIZE;
		Query query = JPA.em().createQuery("SELECT t FROM " + tableName + " t" + condition).setMaxResults(PAGE_SIZE).setFirstResult(offset);
		return query.getResultList();
	}

	public int getPageCount() {
		double count = JPA.em().createQuery("SELECT count(*) FROM " + tableName + condition, Long.class).getSingleResult();
		return (int) Math.ceil(count / PAGE_SIZE);
	}
}
