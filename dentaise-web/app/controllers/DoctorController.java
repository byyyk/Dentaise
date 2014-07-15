package controllers;

import static play.data.Form.form;

import models.Doctor;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.doctors;
import views.html.doctor;
import views.html.index;

@Security.Authenticated(Secured.class)
public class DoctorController extends Controller {

	private static final Paginator<Doctor> paginator = new Paginator<Doctor>(Doctor.class.getName());
	
	@Transactional
	public static Result list(int page) {
		return ok(doctors.render(page, paginator.get(page), paginator.getPageCount()));
	}
	
	@Transactional
	public static Result get(long id) {
		Doctor doctorEntity = JPA.em().find(Doctor.class, id);
		Form<Doctor> form = form(Doctor.class);
		form.fill(doctorEntity);
		return ok(doctor.render(form));
	}

	@Transactional
	public static Result save() {
		Form<Doctor> form = Form.form(Doctor.class);
		Doctor doctor = form.bindFromRequest().get();
		JPA.em().merge(doctor);
		return ok(index.render("Zapisano!"));
	}
	
}
