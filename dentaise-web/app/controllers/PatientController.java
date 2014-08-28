package controllers;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import static controllers.Application.*;
import models.Patient;
import play.Logger;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.patient;
import views.html.patients;
import controllers.pagination.CriteriaApplier;
import controllers.pagination.CriteriaPaginator;

@Security.Authenticated(Secured.class)
public class PatientController extends Controller {
	
	@Transactional
	public static Result create() {
		Patient patient = new Patient();
		JPA.em().persist(patient);
		if (requestFromMobilePhone()) {
			return ok(Json.toJson(patient));
		} else {
			return get(patient.getId());
		}
	}
	
	@Transactional
	public static Result get(long id) {
		Patient patientEntity = JPA.em().find(Patient.class, id);
		
		if (requestFromMobilePhone()) {
			return ok(Json.toJson(patientEntity));
		} else {
			Form<Patient> form = form(Patient.class);
			form = form.fill(patientEntity);
			return ok(patient.render(form));
		}
	}
	
	@Transactional
	public static Result list(int page, final String query) {
		Logger.debug("Accept: " + request().getHeader("Accept"));
		CriteriaPaginator<Patient> paginator = new CriteriaPaginator<Patient>(Patient.class, new CriteriaApplier() {
			@Override
			public <S, T> void applyOrder(CriteriaBuilder builder,
					CriteriaQuery<S> criteriaQuery, Root<T> root) {
				criteriaQuery.orderBy(builder.asc(root.get("surname")));
			}
			
			@Override
			public <S, T> void applyCondition(CriteriaBuilder builder,
					CriteriaQuery<S> criteriaQuery, Root<T> root) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (query != null) {
					System.out.println("query: " + query);
					String parts[] = query.split(" ");
					if (parts.length > 0) {
						for (int i = 0; i < parts.length; i++) {
							String part = parts[i];
							predicates.add(builder.or(
								builder.like(
									builder.lower(root.get("forename").as(String.class)),
									part.toLowerCase() + "%"
								),
								builder.like(
									builder.lower(root.get("surname").as(String.class)),
									part.toLowerCase() + "%"
								),
								builder.like(
									builder.lower(root.get("pesel").as(String.class)),
									part.toLowerCase() + "%"
								)
							));
						}
					}
				}
				
				if (predicates.size() > 0) {
					criteriaQuery.where(predicates.toArray(new Predicate[0]));
				}
			}
		});
		List<Patient> patientList = paginator.get(page);
		
		if (requestFromMobilePhone()) {
			return ok(Json.toJson(patientList));
		} else {
			return ok(patients.render(page, patientList, paginator.getPageCount(), query));
		}
	}
	
	@Transactional
	public static Result save() {
		Logger.debug("hello!");
		Form<Patient> patientForm = Form.form(Patient.class);
		Patient patient = patientForm.bindFromRequest().get();
		Logger.debug("saving " + patient);
		JPA.em().merge(patient); 
		return list(1, null);
	}
	
	@Transactional
	public static Result remove(long id) {
		Patient patient = JPA.em().find(Patient.class, id);
		JPA.em().remove(patient);
		return list(1, null);
	}
	
}
