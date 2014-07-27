package controllers;

import static play.data.Form.form;
import models.Doctor;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.doctor;
import views.html.doctors;

@Security.Authenticated(Secured.class)
public class DoctorController extends Controller {
	private static final Paginator<Doctor> paginator = new Paginator<Doctor>(Doctor.class.getName(), null, "surname", "ASC");
	
	@Transactional
	public static Result create() {
		Doctor doctor = new Doctor();
		JPA.em().persist(doctor);
		return get(doctor.getId());
	}
	
	@Transactional
	public static Result list(int page) {
		return ok(doctors.render(page, paginator.get(page),
				paginator.getPageCount()));
	}

	@Transactional
	public static Result get(long id) {
		Doctor doctorEntity = JPA.em().find(Doctor.class, id);
		Form<Doctor> form = form(Doctor.class);
		form = form.fill(doctorEntity);
		return ok(doctor.render(form));
	}

	@Transactional
	public static Result save() {
		Form<Doctor> form = Form.form(Doctor.class);
		Doctor updatedDoctor = form.bindFromRequest().get();
		Doctor realDoctor = JPA.em().find(Doctor.class, updatedDoctor.getId());
		realDoctor.update(updatedDoctor);
		return list(1);
	}
	
	@Transactional
	public static Result remove(long id) {
		Doctor doctor = JPA.em().find(Doctor.class, id);
		JPA.em().remove(doctor);
		return list(1);
	}

}
