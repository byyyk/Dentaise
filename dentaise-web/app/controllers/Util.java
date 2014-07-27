package controllers;

import models.Doctor;
import models.Patient;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Util extends Controller {
	@Transactional
	public static Result testInit() {
		for (int i = 0; i < 15; i++) {
			Doctor doctor = new Doctor();
			doctor.setEmail("lekarz" + i + "@gmail.com");
			doctor.setUsername("lekarz" + i);
			doctor.setForename("Jan");
			doctor.setSurname("Kowalski");
			String salt = PasswordHashing.generateSalt();
			doctor.setPassword(PasswordHashing.hash("s3cret", salt));
			doctor.setSalt(salt);
			JPA.em().persist(doctor);
		}
		
		for (int i = 0; i < 160; i++) {
			Patient patient = new Patient();
			patient.setCity("Kraków");
			patient.setFlatNumber("20");
			patient.setForename("Adam");
			patient.setHomeNumber("19A");
			patient.setPesel("88120801321");
			patient.setPhone("693971990");
			patient.setPostCode("30-121");
			patient.setStreet("Zmyślona");
			patient.setSurname("Nowak");
			JPA.em().persist(patient);
		}
		
		return ok(index.render("Baza zainicjowana"));
	}
}
