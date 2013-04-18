package pl.edu.agh.mkulpa.dentaise.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.JAXBElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.stereotype.Component;

import pl.edu.agh.mkulpa.dentaise.persistence.entity.Patient;
import pl.edu.agh.mkulpa.dentaise.persistence.entity.dao.hibernate.PatientDao;
import pl.edu.agh.mkulpa.dentaise.server.dto.PatientDto;
import pl.edu.agh.mkulpa.dentaise.server.dto.PatientDtoDsTransformer;

//XXX: NO TEXT_XML MediaType!
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
	private PatientDtoDsTransformer transformer = new PatientDtoDsTransformer();

	@GET
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public List<PatientDto> getPatients() {
		List<Patient> patients = patientDao.findAll();
		List<PatientDto> patientDtos = new ArrayList<PatientDto>();
		for (Patient patient : patients) {
			PatientDto patientDto = transformer.transformDsIntoDto(patient);
			patientDtos.add(patientDto);
		}
		return patientDtos;
	}

	@GET
	@Path("{id}")
	@Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
	public PatientDto getPatient(@PathParam("id") Long id) {
		Patient patient = patientDao.findOne(id);
		PatientDto patientDto = transformer.transformDsIntoDto(patient);
		return patientDto;
	}

	@PUT
	@Path("{id}")
	@Consumes(MediaType.APPLICATION_XML)
	public Response updatePatient(JAXBElement<PatientDto> patientJaxbElement) {
		Response response;
		PatientDto patientDto = patientJaxbElement.getValue();
		Patient patient = patientDao.findOne(patientDto.getId());
		if (patient == null) {
			response = Response.noContent().build();
		} else {
			transformer.copyDtoDataToDs(patientDto, patient);
			patientDao.save(patient);
			response = Response.created(uriInfo.getAbsolutePath()).build();
		}
		return response;
	}

	@POST
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void newPatient(
			@FormParam("forename") String forename,
			@FormParam("surname") String surname) throws IOException {
		Patient patient = new Patient(forename, surname);
		patientDao.save(patient);
	}

	@DELETE
	@Path("{id}")
	public void deletePatient(@PathParam("id") Long id) {
		patientDao.delete(id);
	}
	
}
