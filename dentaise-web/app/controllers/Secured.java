package controllers;

import play.libs.ws.WS;
import play.mvc.Call;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class Secured extends Security.Authenticator {

	@Override
	public String getUsername(final Context context) {
		String result = null;
		String sessionId = context.session().get("sessionId");
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
		System.out.println("Current user: [" + result + "]");
		return result;
	}
	
	@Override
	public Result onUnauthorized(Context context) {
		//invoked when getUsername(Context) == null
		return redirect(routes.Application.login());
	}
	
}
