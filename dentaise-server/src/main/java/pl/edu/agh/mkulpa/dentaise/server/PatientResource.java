package pl.edu.agh.mkulpa.dentaise.server;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.edu.agh.mkulpa.dentaise.persistence.entity.Patient;
import pl.edu.agh.mkulpa.dentaise.persistence.entity.dao.hibernate.PatientDao;

@Component
@Scope("request")
@Path("/patient")
public class PatientResource {

	@Context
	UriInfo uriInfo;
	@Context
	Request request;

	@Autowired
	private PatientDao patientDao;

	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public Patient getPatient(@PathParam("id") Long id) {
		Patient patient = patientDao.findOne(id);
		System.out.println("kurwo");
		System.out.println(patient.getForename());
		System.out.flush();
		return patient;
	}
}
