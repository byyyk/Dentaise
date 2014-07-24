package controllers;

import static play.data.Form.form;

import java.util.ArrayList;
import java.util.List;

import models.Doctor;
import models.Patient;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.doctor;
import views.html.doctors;
import views.html.index;
import views.html.patient;
import views.html.patients;

@Security.Authenticated(Secured.class)
public class PatientController extends Controller {
	private static final Paginator<Patient> paginator = new Paginator<Patient>(
			Patient.class.getName());
	
	@Transactional
	public static Result create() {
		return ok();
	}
	
	@Transactional
	public static Result get(long id) {
		Patient patientEntity = JPA.em().find(Patient.class, id);
		Form<Patient> form = form(Patient.class);
		form = form.fill(patientEntity);
		return ok(patient.render(form));
	}
	
	//TODO: show all on page=0
	@Transactional
	public static Result list(int page) {
		return ok(patients.render(page, paginator.get(page), paginator.getPageCount()));
	}
	
	@Transactional
	public static Result save() {
		Form<Patient> patientForm = Form.form(Patient.class);
		Patient patient = patientForm.bindFromRequest().get();
		JPA.em().merge(patient); 
		return list(1);
	}
	
	@Transactional
	public static Result search(final String phrase) {
		String parts[] = phrase.split(" ");
		if (parts.length > 0) {
			String conditions = "";
			for (int i = 0; i < parts.length; i++) {
				String part = parts[i];
				if (i > 0) {
					conditions += " AND";
				}
				conditions += " forename LIKE '" + part + "%' OR surname LIKE '" + part + "%' OR pesel LIKE '" + part + "%'" ;
			}
			List<Patient> patients = JPA.em().createQuery("FROM Patient WHERE" + conditions, Patient.class).getResultList();
			return ok(Json.toJson(patients));
		} else {
			return ok(Json.toJson(new ArrayList<Patient>()));
		}
	}
	
	@Transactional
	public static Result remove(long id) {
		Patient patient = JPA.em().find(Patient.class, id);
		JPA.em().remove(patient);
		return list(1);
	}
	
}
