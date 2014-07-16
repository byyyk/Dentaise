package controllers;

import static play.data.Form.form;
import models.Doctor;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.F.Function0;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.doctors;
import views.html.doctor;
import views.html.index;

@Security.Authenticated(Secured.class)
public class DoctorController extends Controller {

	private static final Paginator<Doctor> paginator = new Paginator<Doctor>(
			Doctor.class.getName());

	@Transactional
	public static Result list(int page) {
		return ok(doctors.render(page, paginator.get(page),
				paginator.getPageCount()));
	}

	@Transactional
	public static Result get(final long id) throws Throwable {
		return JPA.withTransaction(new Function0<Result>() {
			@Override
			public Result apply() throws Throwable {
				Doctor doctorEntity = JPA.em().find(Doctor.class, id);
				Form<Doctor> form = form(Doctor.class);
				form = form.fill(doctorEntity);
				return ok(doctor.render(form));
			}
		});
	}

	@Transactional
	public static Result save() throws Throwable {
		return JPA.withTransaction(new Function0<Result>() {
			@Override
			public Result apply() throws Throwable {
				Form<Doctor> form = Form.form(Doctor.class);
				Doctor doctor = form.bindFromRequest().get();
				JPA.em().persist(doctor);
				return list(1);
			}
		});
	}

}
