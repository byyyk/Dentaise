package controllers;

import static controllers.Application.requestFromMobilePhone;
import static play.data.Form.form;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import models.Area;
import models.Diagnosis;
import models.Doctor;
import models.Patient;
import models.Treatment;
import models.Visit;
import models.Work;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.visit;
import views.html.visits;
import controllers.pagination.CriteriaApplier;
import controllers.pagination.CriteriaPaginator;

@Security.Authenticated(Secured.class)
public class VisitController extends Controller {
	
	@Transactional
	public static Result create(long patientId) {
		System.out.println("patientId: " + patientId);
		Visit visit = new Visit();
		Patient patient = JPA.em().find(Patient.class, patientId);
		System.out.println(patient);
		visit.setPatient(patient);
		JPA.em().persist(visit);
		return get(visit.getId());
	}
	
	@Transactional
	public static Result get(long id) {
		Visit visitEntity = JPA.em().find(Visit.class, id);
		
		if (requestFromMobilePhone()) {
			return ok(Json.toJson(visitEntity));
		} else {
			Form<Visit> form = form(Visit.class);
			form = form.fill(visitEntity);
			return ok(visit.render(form, Util.findAll(Area.class), Util.findAll(Diagnosis.class), Util.findAll(Treatment.class)));
		}
	}
	
	@Transactional
	public static Result list(int page, final boolean onlyMine, final String fromDate, final String toDate, final Long patientId) {
		Patient patient = null;
		if (patientId != -1) {
			patient = JPA.em().find(Patient.class, patientId);
		}
		final Patient finalPatient = patient;
		CriteriaApplier conditionsApplier = new CriteriaApplier() {
			@Override
			public <S, T> void applyCondition(CriteriaBuilder cb,
					CriteriaQuery<S> criteriaQuery, Root<T> root) {
				List<Predicate> predicates = new ArrayList<Predicate>();
				if (onlyMine) {
					predicates.add(cb.equal(root.get("doctor"), Application.getLoggedInDoctor()));
				}
				if (toDate != null && !toDate.isEmpty()) {
					try {
						Calendar calendar = Calendar.getInstance();
						Date date = Application.dateFormatter.parse(toDate);
						calendar.setTime(date);
						calendar.add(Calendar.DATE, 1);
						date = calendar.getTime();
						predicates.add(cb.lessThan(root.<Date>get("date"), date));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				if (fromDate != null && !fromDate.isEmpty()) {
					try {
						predicates.add(cb.greaterThanOrEqualTo(root.<Date>get("date"), Application.dateFormatter.parse(fromDate)));
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				if (finalPatient != null) {
					predicates.add(cb.equal(root.get("patient"), finalPatient));
				}
				if (predicates.size() > 0) {
					criteriaQuery.where(predicates.toArray(new Predicate[0]));
				}
				
			}
			@Override
			public <S, T> void applyOrder(CriteriaBuilder builder,
					CriteriaQuery<S> criteriaQuery, Root<T> root) {
				criteriaQuery.orderBy(builder.asc(root.get("date")));
			}
		};
		CriteriaPaginator<Visit> paginator = new CriteriaPaginator<Visit>(Visit.class, conditionsApplier);
		List<Visit> visitsList = paginator.get(page);
		if (requestFromMobilePhone()) {
			/* this may come in handy if it will be necessary to un-jsonignore doctor
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			Json.setObjectMapper(mapper);
			 */
			return ok(Json.toJson(visitsList));
		} else {
			return ok(visits.render(page, visitsList, paginator.getPageCount(), onlyMine, fromDate, toDate, finalPatient));
		}
	}
	
	@Transactional
	public static Result save() {
		Form<Visit> visitForm = Form.form(Visit.class);
		Visit visit = visitForm.bindFromRequest().get();
		Visit oldVisit = JPA.em().find(Visit.class, visit.getId());
		visit.setPatient(oldVisit.getPatient());
		Doctor doctor = Application.getLoggedInDoctor();
		visit.setDoctor(doctor);
		updateWorkList(oldVisit, visit);
		JPA.em().merge(visit);
		return defaultList();
	}

	private static void updateWorkList(Visit oldVisit, Visit visit) {
		if (oldVisit.getWorkList() != null) {
		ArrayList<Work> oldWorkList = new ArrayList<Work>(oldVisit.getWorkList());
			for (Work work : oldWorkList) {
				work.setVisit(null);
			}
		}
		if (visit.getWorkList() != null) {
			for (Work work : visit.getWorkList()) {
				work.setVisit(visit);
			}
		}
	}
	
	@Transactional
	public static Result remove(long id) {
		Visit visit = JPA.em().find(Visit.class, id);
		JPA.em().remove(visit);
		return defaultList();
	}
	
	public static Result defaultList() {
		return list(1, false, null, null, -1L);
	}

}
