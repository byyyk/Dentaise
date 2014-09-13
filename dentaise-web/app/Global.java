import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import models.Doctor;
import play.GlobalSettings;
import play.Logger;
import play.data.format.Formatters;
import play.data.format.Formatters.SimpleFormatter;
import play.db.jpa.JPA;
import play.libs.F.Function0;
import controllers.Application;
import controllers.PasswordHashing;
import controllers.Util;

public class Global extends GlobalSettings {
	
	private static final String DEFAULT_ADMIN_USERNAME = "admin";
	private static final String DEFAULT_ADMIN_PASSWORD = "admin";
	
	@Override
	public void onStart(play.Application arg0) {
		initDb();
		Formatters.register(Date.class, new SimpleFormatter<Date>() {
			@Override
			public Date parse(String date, Locale locale) throws ParseException {
				return Application.dateTimeFormat.parse(date);
			}
			@Override
			public String print(Date date, Locale locale) {
				return Application.dateTimeFormat.format(date);
			}
		});
		super.onStart(arg0);
	}
	
	public void initDb() {
		try {
			JPA.withTransaction(new Function0<Void>() {
				@Override
				public Void apply() throws Throwable {
					List<Doctor> doctors = Util.findAll(Doctor.class);
					if (doctors.isEmpty()) {
						Logger.info("creating default admin:admin account");
						Doctor defaultAdmin = new Doctor();
						String salt = PasswordHashing.generateSalt();
						String hashedPwd = PasswordHashing.hash(DEFAULT_ADMIN_PASSWORD, salt);
						defaultAdmin.setUsername(DEFAULT_ADMIN_USERNAME);
						defaultAdmin.setSalt(salt);
						defaultAdmin.setPassword(hashedPwd);
						JPA.em().persist(defaultAdmin);
					}
					return null;
				}
			});
		} catch (Throwable e) {
			Logger.error(null, e);
		}
	}
}
