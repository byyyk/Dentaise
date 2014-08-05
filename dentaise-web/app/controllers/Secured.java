package controllers;

import play.Logger;
import play.libs.ws.WS;
import play.mvc.Call;
import play.mvc.Http.Context;
import play.mvc.Http.Session;
import play.mvc.Result;
import play.mvc.Security;

public class Secured extends Security.Authenticator {

	@Override
	public String getUsername(final Context context) {
		Session session = context.session();
		return readUsernameFromSession(session);
	}

	public static String readUsernameFromSession(Session session) {
		String result = null;
		String sessionId = session.get("sessionId");
		Logger.debug("sessionId is " + sessionId);
		if (sessionId != null) {
			Call call = routes.Application.processSession(sessionId);
			String baseUrl = play.Play.application().configuration().getString("application.baseUrl");
			String url = baseUrl + call.url();
			String username = WS.url(url).get().get(3000).getBody();
			if (username == null || username.isEmpty()) {
				result = null;
			} else {
				result = username;
			}
		}
		return result;
	}

	@Override
	public Result onUnauthorized(Context context) {
		//invoked when getUsername(Context) == null
		if (Application.requestFromMobilePhone()) {
			return unauthorized();
		} else {
			return redirect(routes.Application.login());
		}
	}
	
}
