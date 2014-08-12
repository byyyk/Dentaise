package controllers;

import java.util.List;

import models.Area;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class AreaController extends Controller {
	@Transactional
	public static Result list() {
		List<Area> result = Util.findAll(Area.class);
		return ok(Json.toJson(result));
	}
}
