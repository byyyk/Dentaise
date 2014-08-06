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

public class VisitRepository {
    private static final String TAG = VisitRepository.class.getSimpleName();
    private final AppRepository appRepository;

    public VisitRepository(AppRepository appRepository) {
        this.appRepository = appRepository;
    }

    public Visit getVisit(long id) throws IOException, JSONException, AuthenticationFailedException {
        HttpGet httpGet = new HttpGet(appRepository.getBaseUrl() + "/visits/" + id);
        HttpResponse response = appRepository.execute(httpGet);
        ObjectMapper mapper = new ObjectMapper();
        Visit visit = mapper.readValue(response.getEntity().getContent(), Visit.class);
        return visit;
    }

    public void saveVisit(Visit patient) throws IOException, JSONException, AuthenticationFailedException {
        HttpPost httpPost = new HttpPost(appRepository.getBaseUrl() + "/visits");
        ObjectMapper mapper = new ObjectMapper();
        StringEntity entity = new StringEntity(mapper.writeValueAsString(patient), HTTP.UTF_8);
        entity.setContentType("application/json");
        httpPost.setEntity(entity);
        appRepository.execute(httpPost);
    }

    public List<Visit> listVisits() throws IOException, JSONException, AuthenticationFailedException {
        HttpGet httpGet = new HttpGet(appRepository.getBaseUrl() + "/visits");
        HttpResponse response = appRepository.execute(httpGet);
        ObjectMapper mapper = new ObjectMapper();
        List<Visit> visits = mapper.readValue(response.getEntity().getContent(), new TypeReference<List<Visit>>() {});
        return visits;
    }



}
