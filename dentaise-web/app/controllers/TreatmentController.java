package controllers;

import java.util.List;

import models.Treatment;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class TreatmentController extends Controller {
	@Transactional
	public static Result list() {
		List<Treatment> result = Util.findAll(Treatment.class);
		return ok(Json.toJson(result));
	}
}
