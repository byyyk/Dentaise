package pl.edu.agh.mkulpa.dentaise.mobile.rest;

/**
 * Created by byyyk on 05.08.14.
 */
public class Repositories {
    public static AppRepository app;
    public static PatientRepository patient;

    static {
        //TODO configure in settings
        app = new AppRepository("http://192.168.2.123:9000", "mckulpa", "s3cret");
        patient = new PatientRepository(app);
    }
}
