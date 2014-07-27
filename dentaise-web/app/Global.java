import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import play.GlobalSettings;
import play.data.format.Formatters;
import play.data.format.Formatters.SimpleFormatter;

public class Global extends GlobalSettings {
	public static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm");
	@Override
	public void onStart(play.Application arg0) {
		Formatters.register(Date.class, new SimpleFormatter<Date>() {
			@Override
			public Date parse(String date, Locale locale) throws ParseException {
				return dateTimeFormat.parse(date);
			}
			@Override
			public String print(Date date, Locale locale) {
				return dateTimeFormat.format(date);
			}
		});
		super.onStart(arg0);
	}
}
