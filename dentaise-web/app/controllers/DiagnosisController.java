package controllers;

import java.util.List;

import models.Diagnosis;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class DiagnosisController extends Controller {
	@Transactional
	public static Result list() {
		List<Diagnosis> result = Util.findAll(Diagnosis.class);
		return ok(Json.toJson(result));
	}
}
