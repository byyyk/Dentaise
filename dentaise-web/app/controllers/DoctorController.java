package controllers;

import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.doctors;

@Security.Authenticated(Secured.class)
public class DoctorController extends Controller {

	@Transactional
	public static Result list() {
		return ok(doctors.render());
	}

}
