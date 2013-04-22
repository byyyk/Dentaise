package controllers;

import models.Patient;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class PatientController extends Controller {

	@Transactional
	public static Result list() {
		//TODO: ebean...
		Patient patient = JPA.em().find(Patient.class, Long.valueOf(1));
		
		return ok(index.render(patient.getForename() + " " + patient.getSurname()));
	}

}
