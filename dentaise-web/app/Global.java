import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import play.GlobalSettings;
import play.data.format.Formatters;
import play.data.format.Formatters.SimpleFormatter;
import controllers.Application;

public class Global extends GlobalSettings {
	
	@Override
	public void onStart(play.Application arg0) {
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
}
