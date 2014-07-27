import java.text.ParseException;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import play.GlobalSettings;
import play.data.format.Formatters;
import play.data.format.Formatters.SimpleFormatter;

public class Global extends GlobalSettings {
	@Override
	public void onStart(play.Application arg0) {
		Formatters.register(DateTime.class, new SimpleFormatter<DateTime>() {
			@Override
			public DateTime parse(String date, Locale locale) throws ParseException {
				System.out.println("str2date: " + date);
				return controllers.Application.dateTimeFormatter.parseDateTime(date);
			}
			@Override
			public String print(DateTime date, Locale locale) {
				System.out.println("date2str"); //FIXME somehow this does not get called
				return date.toString(controllers.Application.dateTimeFormatter);
			}
		});
		super.onStart(arg0);
	}
}
