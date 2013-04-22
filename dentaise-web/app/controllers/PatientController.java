package controllers;

import org.springframework.transaction.annotation.Transactional;

import models.Patient;
import models.PatientDao;
import play.mvc.Controller;
import play.mvc.Result;
import settings.Global;
import views.html.index;

public class PatientController extends Controller {

	private static PatientDao patientDao = Global.getBean(PatientDao.class);

	@Transactional
	public static Result list() {
		String content = "";
		
		for (Patient patient : patientDao.findAll()) {
			content += patient.getId() + " ";
		}
		return ok(index.render(content));
	}

}
