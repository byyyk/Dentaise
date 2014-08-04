package controllers;

import static play.data.Form.form;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import models.Doctor;
import play.data.Form;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.doctor;
import views.html.doctors;
import controllers.pagination.CriteriaApplier;
import controllers.pagination.CriteriaPaginator;

@Security.Authenticated(Secured.class)
public class DoctorController extends Controller {
	private static final CriteriaPaginator<Doctor> paginator = new CriteriaPaginator<Doctor>(Doctor.class, new CriteriaApplier() {
		@Override
		public <S, T> void applyOrder(CriteriaBuilder builder,
				CriteriaQuery<S> criteriaQuery, Root<T> root) {
			criteriaQuery.orderBy(builder.asc(root.get("surname")));
		}
		
		@Override
		public <S, T> void applyCondition(CriteriaBuilder builder,
				CriteriaQuery<S> criteriaQuery, Root<T> root) {
			//no conditions
		}
	});
	
	@Transactional
	public static Result create() {
		Doctor doctor = new Doctor();
		JPA.em().persist(doctor);
		return get(doctor.getId());
	}
	
	@Transactional
	public static Result list(int page) {
		return ok(doctors.render(page, paginator.get(page),
				paginator.getPageCount()));
	}

	@Transactional
	public static Result get(long id) {
		Doctor doctorEntity = JPA.em().find(Doctor.class, id);
		Form<Doctor> form = form(Doctor.class);
		form = form.fill(doctorEntity);
		return ok(doctor.render(form));
	}

	@Transactional
	public static Result save() {
		Form<Doctor> form = Form.form(Doctor.class);
		Doctor updatedDoctor = form.bindFromRequest().get();
		Doctor realDoctor = JPA.em().find(Doctor.class, updatedDoctor.getId());
		realDoctor.update(updatedDoctor);
		return list(1);
	}
	
	@Transactional
	public static Result remove(long id) {
		Doctor doctor = JPA.em().find(Doctor.class, id);
		JPA.em().remove(doctor);
		return list(1);
	}

}
