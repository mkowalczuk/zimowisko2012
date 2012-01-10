package eu.kowalczuk.zimowisko2012;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;

public class MainActivity extends ListActivity {
	private final List<Integer> DAYS_CALENDAR = Arrays.asList(Calendar.FRIDAY, Calendar.SATURDAY,
			Calendar.SUNDAY);

	private Handler h;
	private AgendaEventAdapter[] adapters;
	private int displayedDay = -1;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		h = new Handler(new AgendaHandlerCallback());
		AgendaFetcherThread agendaFetcher = new AgendaFetcherThread(this, h);
		agendaFetcher.start();
	}

	private void loadDay(int day) {
		getListView().setAdapter(adapters[day]);
	}
	
	private void loadCurrentDay() {
		Calendar c = Calendar.getInstance();
		int day = DAYS_CALENDAR.indexOf(c.get(Calendar.DAY_OF_WEEK));
		if (day < 0)
			day = 0;
		loadDay(day);
	}
	
	private void createAdapters(JSONArray agenda) {
		adapters = new AgendaEventAdapter[agenda.length()];

		try {
			Log.d("agenda", "" + agenda.length());
			for (int i = 0; i < agenda.length(); i++) {
				ArrayList<AgendaEvent> dayEvents = new ArrayList<AgendaEvent>();
				
				JSONArray dayArray = agenda.getJSONArray(i);
				JSONArray events = dayArray.getJSONArray(1);
				for (int j = 0; j < events.length(); j++) {
					JSONObject event = events.getJSONObject(j);
					Log.d("agenda", event.toString());
					
					String attendee = "";
					String body = "";
					try {
						 attendee = event.getString("attendee");
					} catch (JSONException e) {
					}
					try {
						 body = event.getString("body");
					} catch (JSONException e) {
					}
					String[] time = event.getString("moment").split("T")[1].split(":");
					String moment = time[0] + ":" + time[1];
					dayEvents.add(new AgendaEvent(moment,
							event.getString("title"), 
							attendee,
							body));
				}
				adapters[i] = new AgendaEventAdapter(this, R.layout.list_element, dayEvents);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private class AgendaHandlerCallback implements Callback {
		@Override
		public boolean handleMessage(Message msg) {
			JSONArray agenda = (JSONArray) msg.obj;

			createAdapters(agenda);

			if (displayedDay < 0)
				loadCurrentDay();
			else
				loadDay(displayedDay);
			
			return true;
		}
	}
}