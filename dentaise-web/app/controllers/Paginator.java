package controllers;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Query;

import play.db.jpa.JPA;


public class Paginator<T extends Serializable> {
	private static final int PAGE_SIZE = 10;
	private final String tableName;
	
	public Paginator(String tableName) {
		this.tableName = tableName;
	}
	
	@SuppressWarnings("unchecked")
	public List<T> get(final int page) {
		int offset = (page - 1) * PAGE_SIZE;
		Query query = JPA.em().createQuery("SELECT t FROM " + tableName + " t").setMaxResults(PAGE_SIZE).setFirstResult(offset);
		return query.getResultList();
	}

	public int getPageCount() {
		double count = JPA.em().createQuery("SELECT count(*) FROM " + tableName, Long.class).getSingleResult();
		return (int) Math.ceil(count / PAGE_SIZE);
	}
}
