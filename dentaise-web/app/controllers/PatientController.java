package controllers;

import static play.data.Form.form;
import models.Patient;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.patient;
import views.html.patients;

@Security.Authenticated(Secured.class)
public class PatientController extends Controller {
	
	@Transactional
	public static Result create() {
		Patient patient = new Patient();
		JPA.em().persist(patient);
		return get(patient.getId());
	}
	
	@Transactional
	public static Result get(long id) {
		Patient patientEntity = JPA.em().find(Patient.class, id);
		Form<Patient> form = form(Patient.class);
		form = form.fill(patientEntity);
		return ok(patient.render(form));
	}
	
	//TODO: return all if page=0 (for mobile client which won't have pagination)
	@Transactional
	public static Result list(int page, String query) {
		String conditions = null;
		if (query != null) {
			System.out.println("query: " + query);
			String parts[] = query.split(" ");
			if (parts.length > 0) {
				conditions = "";
				for (int i = 0; i < parts.length; i++) {
					String part = parts[i];
					if (i > 0) {
						conditions += " AND";
					}
					conditions += " (forename LIKE '" + part + "%' OR surname LIKE '" + part + "%' OR pesel LIKE '" + part + "%')" ;
				}
			}
			System.out.println("conditions: " + conditions);
		}
		Paginator<Patient> paginator = new Paginator<Patient>(Patient.class.getName(), conditions, "surname", "ASC");
		return ok(patients.render(page, paginator.get(page), paginator.getPageCount(), query));
	}
	
	@Transactional
	public static Result save() {
		Form<Patient> patientForm = Form.form(Patient.class);
		Patient patient = patientForm.bindFromRequest().get();
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
