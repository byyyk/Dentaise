package controllers;

import models.Doctor;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.doctors;

@Security.Authenticated(Secured.class)
public class DoctorController extends Controller {

	private static final Paginator<Doctor> paginator = new Paginator<Doctor>(Doctor.class.getName()); //TODO T.getName inside??
	
	@Transactional
	public static Result list(int page) {
		return ok(doctors.render(page, paginator.get(page)));
	}

}
