package controllers;

import java.util.Date;

import models.Session;
import play.Logger;
import play.db.jpa.JPA;
import play.libs.F.Function0;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class Secured extends Security.Authenticator {

	private static final long SESSION_TIMEOUT_MILIS = 10 * 60 * 1000;

	@Override
	public String getUsername(final Context context) {
		try {
			return JPA.withTransaction(new Function0<String>() {
				@Override
				public String apply() {
					String result = null;
					String sessionId = context.session().get("sessionId");
					
					if (sessionId != null) {
						Session session = JPA.em().find(Session.class, sessionId);
						if (!expired(session)) {
							session.setLastActivity(new Date());
							JPA.em().merge(session);
							result = session.getDoctor().getUsername();
						} else {
							JPA.em().remove(session);
						}
					}
					
					return result;
				}
			});
		} catch (Throwable e) {
			Logger.error(null, e);
			return null;
		}
	}
	
	private boolean expired(Session session) {
		if (session != null) {
			Date lastActivity = session.getLastActivity();
			Date now = new Date();
			
			long timeDifference = now.getTime() - lastActivity.getTime();
			return timeDifference > SESSION_TIMEOUT_MILIS;
		} else {
			return true;
		}
	}

	@Override
	public Result onUnauthorized(Context context) {
		//invoked when getUsername(Context) == null
		return redirect(routes.Application.login());
	}
	
}
