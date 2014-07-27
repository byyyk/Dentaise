package controllers;

import static play.data.Form.form;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.persistence.TypedQuery;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import models.Doctor;
import models.Session;
import models.Token;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.changePassword;
import views.html.index;
import views.html.login;
import views.html.passwordNotice;
import views.html.resetPassword;

import com.typesafe.plugin.MailerAPI;
import com.typesafe.plugin.MailerPlugin;

public class Application extends Controller {
	public static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("dd.MM.yyyy HH:mm");
	private static final long SESSION_TIMEOUT_MILIS = 30 * 60 * 1000;
	private static final long TOKEN_TIMEOUT_MILIS = 15 * 60 * 1000;
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
    
    public static Result resetPassword() {
    	return ok(resetPassword.render(form(PasswordResetRequest.class)));
    }
    
    @Transactional
    public static Result sendPasswordChangeLink() {
    	Form<PasswordResetRequest> emailForm = form(PasswordResetRequest.class).bindFromRequest();
    	return sendPasswordChangeLinkLogic(emailForm);
    }
    
    @Transactional
    public static Result sendPasswordChangeLinkGet(final String email) {
    	@SuppressWarnings("serial")
		Form<PasswordResetRequest> emailForm = form(PasswordResetRequest.class).bind(new HashMap<String, String>(){{put("email", email);}});
    	return sendPasswordChangeLinkLogic(emailForm);
    }
    
    private static Result sendPasswordChangeLinkLogic(Form<PasswordResetRequest> emailForm) {
    	if (emailForm.hasErrors()) {
    		return badRequest(resetPassword.render(emailForm));
    	} else {
    		Doctor doctor = emailForm.get().getDoctor();
    		Token token = new Token();
    		token.setDoctor(doctor);
    		token.setCreated(new Date());
    		token.setId(PasswordHashing.generateSalt()); //this has nothing to do with salt but it is used as id for simplicity
    		JPA.em().persist(token);
    		
    		String baseUrl = play.Play.application().configuration().getString("application.outerBaseUrl");
    		String link = baseUrl + routes.Application.changePassword(token.getId()).url();
    		
    		MailerAPI mail = play.Play.application().plugin(MailerPlugin.class).email();
    		mail.setSubject("Dentaise - Zmiana hasła");
    		mail.setRecipient(doctor.getEmail());
    		mail.setFrom(play.Play.application().configuration().getString("smtp.from"));
    		mail.sendHtml("Skorzystaj z linku aby zresetować hasło: <a href=\"" + link + "\">"+link+"</a>. Link będzie aktywny tylko przez 15 minut.");
    		
    		return ok(passwordNotice.render("Link do zmiany hasła został wysłany w wiadomości na podany adres e-mail. Postępuj zgodnie z przekazanymi w niej informacjami."));
    	}
    }
    
    public static Result changePassword(String token) {
    	Map<String, String> data = new HashMap<String, String>();
    	data.put("token", token);
    	Form<PasswordChangeRequest> passChangeForm = form(PasswordChangeRequest.class).bind(data);
    	return ok(changePassword.render(passChangeForm));
    }
    
    @Transactional
    public static Result savePassword() {
        Form<PasswordChangeRequest> passChangeForm = form(PasswordChangeRequest.class).bindFromRequest();
        if (passChangeForm.hasErrors()) {
        	return badRequest(changePassword.render(passChangeForm));
        } else {
        	PasswordChangeRequest req = passChangeForm.get();
        	String newPassword = req.password;
			String newSalt = PasswordHashing.generateSalt();
			String hashedNewPassword = PasswordHashing.hash(newPassword, newSalt);
			
			Token token = JPA.em().find(Token.class, req.token);
			Doctor doctor = token.getDoctor();
			JPA.em().remove(token);
			
			doctor.setSalt(newSalt);
			doctor.setPassword(hashedNewPassword);
			JPA.em().merge(doctor);
        }
		
    	return ok(passwordNotice.render("Twoje hasło zostało zmienione. Możesz z niego skorzystać przy następnym logowaniu."));
    }
    
    
    public static String generateSessionId() {
    	return new BigInteger(130, random).toString();
    }
    
    @Transactional
    public static Result processSession(String sessionId) {
    	String username = null;
		Session session = JPA.em().find(Session.class, sessionId);
		if (session != null) {
			if (expired(session.getLastActivity(), SESSION_TIMEOUT_MILIS)) {
				JPA.em().remove(session);
			} else {
				session.setLastActivity(new Date());
				JPA.em().merge(session);
				username = session.getDoctor().getUsername();
			}
		}
		return ok(username == null ? "" : username);
    }
    
	private static boolean expired(Date date, long timeout) {
		Date now = new Date();
		long timeDifference = now.getTime() - date.getTime();
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
        	Doctor doctor = findDoctorByUsername(username);
        	Session session = new Session();
        	session.setId(generateSessionId());
        	session.setDoctor(doctor);
        	session.setLastActivity(new Date());
        	JPA.em().merge(session);
        	session().put("sessionId", session.getId());
        	return redirect(routes.Application.index());
        }
    }

    public static Doctor getLoggedInDoctor() {
    	String username = Secured.readUsernameFromSession(session());
    	return findDoctorByUsername(username);
    }
    
	private static Doctor findDoctorByUsername(String username) {
		TypedQuery<Doctor> query = JPA.em().createQuery("FROM Doctor WHERE username=:username", Doctor.class);
		query.setParameter("username", username);
		List<Doctor> doctors = query.getResultList();
		return doctors.size() > 0 ? doctors.get(0) : null;
	}
	
	public static class PasswordResetRequest {
		public String email;

		public String validate() {
			if (getDoctor() == null) {
				return "Podany adres e-mail nie istnieje w bazie.";
			} else {
				return null;
			}
		}
		
		public Doctor getDoctor() {
			System.out.println(email);
			TypedQuery<Doctor> query = JPA.em().createQuery("FROM Doctor d WHERE d.email=:email", Doctor.class);
			query.setParameter("email", email);
			List<Doctor> doctors = query.getResultList();
			System.out.println(doctors.size());
			return doctors.size() > 0 ? doctors.get(0) : null;
		}
	}
	
	public static class PasswordChangeRequest {
		public String password;
		public String passwordRepeat;
		public String token;

		public String validate() {
			if (passwordTooWeak()) {
				return "Hasło musi mieć przynajmniej 6 znaków";
			}
			if (passwordsNotMatching()) {
				return "Podane hasła nie są identyczne.";
			} 
			if (badToken()) {
				return "Token wygasł lub nie istnieje. Ponów żądanie o reset hasła.";
			}
			return null;
		}

		private boolean passwordTooWeak() {
			return password == null || password.length() < 6;
		}

		private boolean passwordsNotMatching() {
			return !password.equals(passwordRepeat);
		}
		
		private boolean badToken() {
			Token t = JPA.em().find(Token.class, token);
			return t == null || expired(t.getCreated(), TOKEN_TIMEOUT_MILIS);
		}
	}
    
	public static class Login {
		public String username;
		public String password;

		public String validate() {
			if (passwordMatches()) {
				return null;
			} else {
				return "Błędny login lub hasło";
			}
		}

		private boolean passwordMatches() {
			System.out.println(username);
			Doctor doctor = findDoctorByUsername(username);
			if (doctor != null) {
				String checkedPasswordHash = PasswordHashing.hash(password, doctor.getSalt());
				String actualPasswordHash = doctor.getPassword();
				
				return checkedPasswordHash.equals(actualPasswordHash);
			}
			return false;
		}
	}

}
