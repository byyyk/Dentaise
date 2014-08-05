package pl.edu.agh.mkulpa.dentaise.mobile;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import pl.edu.agh.mkulpa.dentaise.mobile.rest.Patient;

/**
 * Created by byyyk on 24.05.14.
 */
public class PatientDataFragment extends Fragment {
    EditPatientActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (EditPatientActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_patient_data, container, false);
    }

    public void updateView() {
        Patient patient = activity.patient;

        EditText forenameEditText = (EditText) getView().findViewById(R.id.patient_forename);
        EditText surnameEditText = (EditText) getView().findViewById(R.id.patient_surname);
        EditText peselEditText = (EditText) getView().findViewById(R.id.patient_pesel);
        EditText phoneEditText = (EditText) getView().findViewById(R.id.patient_phone);
        EditText streetEditText = (EditText) getView().findViewById(R.id.patient_street);
        EditText homeNumberEditText = (EditText) getView().findViewById(R.id.patient_home_number);
        EditText flatNumberEditText = (EditText) getView().findViewById(R.id.patient_flat_number);
        EditText cityEditText = (EditText) getView().findViewById(R.id.patient_city);
        EditText postCodeEditText = (EditText) getView().findViewById(R.id.patient_post_code);
        forenameEditText.setText(patient.getForename());
        surnameEditText.setText(patient.getSurname());
        peselEditText.setText(patient.getPesel());
        phoneEditText.setText(patient.getPhone());
        streetEditText.setText(patient.getStreet());
        homeNumberEditText.setText(patient.getHomeNumber());
        flatNumberEditText.setText(patient.getFlatNumber());
        cityEditText.setText(patient.getCity());
        postCodeEditText.setText(patient.getPostCode());
    }

    public void savePatientData() {
        Patient patient = activity.patient;
        EditText forenameEditText = (EditText) getView().findViewById(R.id.patient_forename);
        EditText surnameEditText = (EditText) getView().findViewById(R.id.patient_surname);
        EditText peselEditText = (EditText) getView().findViewById(R.id.patient_pesel);
        EditText phoneEditText = (EditText) getView().findViewById(R.id.patient_phone);
        EditText streetEditText = (EditText) getView().findViewById(R.id.patient_street);
        EditText homeNumberEditText = (EditText) getView().findViewById(R.id.patient_home_number);
        EditText flatNumberEditText = (EditText) getView().findViewById(R.id.patient_flat_number);
        EditText cityEditText = (EditText) getView().findViewById(R.id.patient_city);
        EditText postCodeEditText = (EditText) getView().findViewById(R.id.patient_post_code);
        patient.setForename(forenameEditText.getText().toString());
        patient.setSurname(surnameEditText.getText().toString());
        patient.setPesel(peselEditText.getText().toString());
        patient.setPhone(phoneEditText.getText().toString());
        patient.setStreet(streetEditText.getText().toString());
        patient.setHomeNumber(homeNumberEditText.getText().toString());
        patient.setFlatNumber(flatNumberEditText.getText().toString());
        patient.setCity(cityEditText.getText().toString());
        patient.setPostCode(postCodeEditText.getText().toString());
    }

}
