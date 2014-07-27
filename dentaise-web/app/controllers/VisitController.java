package controllers;

import static play.data.Form.form;

import java.util.HashMap;
import java.util.Map;

import models.Doctor;
import models.Patient;
import models.Visit;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.visit;
import views.html.visits;

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
		Form<Visit> form = form(Visit.class);
		form = form.fill(visitEntity);
		return ok(visit.render(form));
	}
	
	//TODO: return all if page=0 (for mobile client which won't have pagination)
	@Transactional
	public static Result list(int page) {
		Paginator<Visit> paginator = new Paginator<Visit>(Visit.class.getName(), null, "date", "ASC");
		return ok(visits.render(page, paginator.get(page), paginator.getPageCount()));
	}
	
	@Transactional
	public static Result save() {
		Form<Visit> visitForm = Form.form(Visit.class);
		Visit visit = visitForm.bindFromRequest().get();
		Visit oldVisit = JPA.em().find(Visit.class, visit.getId());
		visit.setPatient(oldVisit.getPatient());
		Doctor doctor = Application.getLoggedInDoctor();
		visit.setDoctor(doctor);
		JPA.em().merge(visit); 
		return list(1);
	}
	
	@Transactional
	public static Result remove(long id) {
		Visit visit = JPA.em().find(Visit.class, id);
		JPA.em().remove(visit);
		return list(1);
	}

}
