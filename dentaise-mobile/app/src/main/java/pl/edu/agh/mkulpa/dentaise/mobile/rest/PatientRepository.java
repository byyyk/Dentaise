package pl.edu.agh.mkulpa.dentaise.mobile.rest;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.json.JSONException;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

public class PatientRepository {
    private static final String TAG = PatientRepository.class.getSimpleName();
    private final AppRepository appRepository;

    public PatientRepository(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    public Patient createPatient() throws IOException, JSONException, AuthenticationFailedException {
        HttpGet httpGet = new HttpGet(appRepository.getBaseUrl() + "/patients-create");
        HttpResponse response = appRepository.execute(httpGet);
        ObjectMapper mapper = new ObjectMapper();
        Patient patient = mapper.readValue(response.getEntity().getContent(), Patient.class);
        return patient;
    }

    public Patient getPatient(long id) throws IOException, JSONException, AuthenticationFailedException {
        HttpGet httpGet = new HttpGet(appRepository.getBaseUrl() + "/patients/" + id);
        HttpResponse response = appRepository.execute(httpGet);
        ObjectMapper mapper = new ObjectMapper();
        Patient patient = mapper.readValue(response.getEntity().getContent(), Patient.class);
        return patient;
    }

    public void savePatient(Patient patient) throws IOException, JSONException, AuthenticationFailedException {
        HttpPost httpPost = new HttpPost(appRepository.getBaseUrl() + "/patients");
        ObjectMapper mapper = new ObjectMapper();
        StringEntity entity = new StringEntity(mapper.writeValueAsString(patient), HTTP.UTF_8);
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        appRepository.execute(httpPost);
    }

    public List<Patient> listPatients(String query) throws IOException, JSONException, AuthenticationFailedException {
        HttpGet httpGet = new HttpGet(appRepository.getBaseUrl() + "/patients" + (query != null ? "?query=" + URLEncoder.encode(query, "UTF-8") : ""));
        HttpResponse response = appRepository.execute(httpGet);
        ObjectMapper mapper = new ObjectMapper();
        List<Patient> patients = mapper.readValue(response.getEntity().getContent(), new TypeReference<List<Patient>>() {});
        return patients;
    }



}
