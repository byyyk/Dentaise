package pl.edu.agh.mkulpa.dentaise.persistence.entity.dao.hibernate;

import org.springframework.data.jpa.repository.JpaRepository;

import pl.edu.agh.mkulpa.dentaise.persistence.entity.Patient;

public interface PatientDao extends JpaRepository<Patient, Long> {

}
