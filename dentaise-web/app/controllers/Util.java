package controllers;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import models.Area;
import models.Diagnosis;
import models.Doctor;
import models.Patient;
import models.Treatment;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Util extends Controller {

	public static <T> List<T> findAll(Class<T> type) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(type);
        Root<T> rootEntry = cq.from(type);
        CriteriaQuery<T> select = cq.select(rootEntry);
        TypedQuery<T> allQuery = JPA.em().createQuery(select);
        return allQuery.getResultList();
	}
	
}
