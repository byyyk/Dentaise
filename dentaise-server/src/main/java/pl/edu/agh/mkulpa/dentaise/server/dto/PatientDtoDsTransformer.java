package pl.edu.agh.mkulpa.dentaise.server.dto;

import pl.edu.agh.mkulpa.dentaise.persistence.entity.Patient;

public class PatientDtoDsTransformer implements DtoDsTransformer<Patient, PatientDto>{

	@Override
	public PatientDto transformDsIntoDto(Patient ds) {
		PatientDto patientDto = new PatientDto(ds.getForename(), ds.getSurname());
		patientDto.setId(ds.getId());
		return patientDto;
	}

	@Override
	public void copyDtoDataToDs(PatientDto dto, Patient ds) {
		ds.setId(dto.getId());
		ds.setForename(dto.getForename());
		ds.setSurname(dto.getSurname());
	}
	
}
