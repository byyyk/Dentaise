package controllers;

import static play.data.Form.form;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Random;

import models.Doctor;
import models.Session;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;
import views.html.login;

public class Application extends Controller {
	private static final long SESSION_TIMEOUT_MILIS = 10 * 60 * 1000;
	private static Random random = new SecureRandom();
	
	@Security.Authenticated(Secured.class)
    public static Result index() {
        return ok(index.render("For the swarm."));
    }
	
    public static Result login() {
        return ok(login.render(form(Login.class)));
    }
    
    public static Result logout() {
    	session().clear();
        return login();
    }
    
    public static String generateSessionId() {
    	return new BigInteger(130, random).toString();
    }
    
    @Transactional
    public static Result processSession(String sessionId) {
    	String username = null;
		Session session = JPA.em().find(Session.class, sessionId);
		if (session != null) {
			if (expired(session)) {
				JPA.em().remove(session);
			} else {
				session.setLastActivity(new Date());
				JPA.em().merge(session);
				username = session.getDoctor().getUsername();
			}
		}
		return ok(username == null ? "" : username);
    }
    
	private static boolean expired(Session session) {
		Date lastActivity = session.getLastActivity();
		Date now = new Date();
		
		long timeDifference = now.getTime() - lastActivity.getTime();
		return timeDifference > SESSION_TIMEOUT_MILIS;
	}
    
    @Transactional
    public static Result authenticate() {
        Form<Login> loginForm = form(Login.class).bindFromRequest();
        if (loginForm.hasErrors()) {
        	return badRequest(login.render(loginForm));
        } else {
        	session().clear();
        	String username = loginForm.get().username;
        	Doctor doctor = findDoctor(username);
        	Session session = new Session();
        	session.setId(generateSessionId());
        	session.setDoctor(doctor);
        	session.setLastActivity(new Date());
        	JPA.em().merge(session);
        	session().put("sessionId", session.getId());
        	return redirect(routes.Application.index());
        }
    }

	private static Doctor findDoctor(String username) {
		List<Doctor> doctors = JPA.em().createQuery("FROM Doctor WHERE username='" + username + "'", Doctor.class).getResultList();
		return doctors.size() > 0 ? doctors.get(0) : null;
	}
    
	public static class Login {
		public String username;
		public String password;

		public String validate() {
			if (passwordMatches()) {
				return null;
			} else {
				return "Invalid username or password";
			}
		}

		private boolean passwordMatches() {
			Doctor doctor = findDoctor(username);
			if (doctor != null) {
				String checkedPasswordHash = PasswordHashing.hash(password, doctor.getSalt());
				String actualPasswordHash = doctor.getPassword();
				
				return checkedPasswordHash.equals(actualPasswordHash);
			}
			return false;
		}
	}
}
