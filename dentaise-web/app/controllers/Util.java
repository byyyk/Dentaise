package controllers;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import models.Area;
import models.Diagnosis;
import models.Doctor;
import models.Patient;
import models.Treatment;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class Util extends Controller {
	@Transactional
	public static Result testInit() {
		for (int i=1; i <= 4; i++) {
			Area area = new Area();
			area.setName("Ćwiartka " + i);
			JPA.em().persist(area);
		}
		for (int i=1; i <= 8; i++) {
			for (int j=1; j <= 8; j++) {
				Area area = new Area();
				area.setName("Ząb " + i + j);
				JPA.em().persist(area);
			}
		}
		return ok(index.render("Baza zainicjowana"));
	}
	
	public static void initWorkTypes() {
		for (int i=1; i <= 4; i++) {
			Area area = new Area();
			area.setName("Ćwiartka " + i);
			JPA.em().persist(area);
		}
		for (int i=1; i <= 8; i++) {
			for (int j=1; j <= 8; j++) {
				Area area = new Area();
				area.setName("Ząb " + i + j);
				JPA.em().persist(area);
			}
		}
		
		String[] diagnosisNames = new String[] {"Mała dziura", "Duuuża dziura", "Próchnica"};
		String[] treatmentNames = new String[] {"Borowanie", "Lakowanie", "Usunięcie"};
		
		for (String diagnosisName : diagnosisNames) {
			Diagnosis diagnosis = new Diagnosis();
			diagnosis.setName(diagnosisName);
			JPA.em().persist(diagnosis);
		}
		
		for (String treatmentName : treatmentNames) {
			Treatment treatment = new Treatment();
			treatment.setName(treatmentName);
			JPA.em().persist(treatment);
		}
	}
	
	public static void oldInit() {
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
	}

	public static void main(String[] args) {
		System.out.println("test" + 1 + 2);
	}
	
	public static <T> List<T> findAll(Class<T> type) {
        CriteriaBuilder cb = JPA.em().getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(type);
        Root<T> rootEntry = cq.from(type);
        CriteriaQuery<T> select = cq.select(rootEntry);
        TypedQuery<T> allQuery = JPA.em().createQuery(select);
        return allQuery.getResultList();
	}
	
}
