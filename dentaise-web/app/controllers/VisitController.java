package controllers;

import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.visits;

@Security.Authenticated(Secured.class)
public class VisitController extends Controller {

	@Transactional
	public static Result list() {
		return ok(visits.render());
	}

}
