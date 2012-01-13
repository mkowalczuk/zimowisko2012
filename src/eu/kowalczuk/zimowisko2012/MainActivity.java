package eu.kowalczuk.zimowisko2012;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;

public class MainActivity extends ListActivity {
	private final List<Integer> DAYS_CALENDAR = Arrays.asList(Calendar.FRIDAY, Calendar.SATURDAY,
			Calendar.SUNDAY);
	private final int[] DAYS_STRING_ID = { R.string.friday, R.string.saturday, R.string.sunday };

	private Handler h;
	private AgendaEventAdapter[] adapters;
	private int displayedDay = -1;
	TextToSpeech tts;
	boolean ttsInitialized = false;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ListView lv = getListView();
		lv.setOnItemClickListener(new MyOnItemClickListener());
		lv.setOnItemLongClickListener(new MyOnItemLongClickListener());

		h = new Handler(new AgendaHandlerCallback());
		refreshAgenda(false);
		
		tts = new TextToSpeech(MainActivity.this, new OnInitListener() {
			@Override
			public void onInit(int status) {
				tts.setLanguage(new Locale(getString(R.string.tts_lang)));
				ttsInitialized = true;
			}
		});
	}

	private void refreshAgenda(boolean forceUpdate) {
		AgendaFetcherThread agendaFetcher = new AgendaFetcherThread(this, h, forceUpdate);
		agendaFetcher.start();
	}

	private void loadDay(int day) {
		setTitle(getString(R.string.app_name) + " - " + getString(DAYS_STRING_ID[day]));
		getListView().setAdapter(adapters[day]);
		displayedDay = day;
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
					dayEvents
							.add(new AgendaEvent(moment, event.getString("title"), attendee, body));
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		for (int i = 0; i < 3; i++)
			menu.add(Menu.NONE, i, i, DAYS_STRING_ID[i]).setIcon(
					android.R.drawable.ic_menu_my_calendar);

		menu.add(Menu.NONE, R.string.refresh, 3, R.string.refresh).setIcon(
				android.R.drawable.ic_menu_rotate);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.string.refresh) {
			refreshAgenda(true);
		} else {
			loadDay(id);
		}

		return true;
	}

	private AgendaEvent getEventFromAdapter(int position) {
		AgendaEventAdapter adapter = adapters[displayedDay];

		return (AgendaEvent) adapter.getItem(position);
	}
	
	private class MyOnItemClickListener implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			AgendaEventAdapter adapter = adapters[displayedDay];
			AgendaEvent current = getEventFromAdapter(position);
			
			boolean previousState = current.summaryVisible;

			for (int i = 0; i < adapter.getCount(); i++) {
				((AgendaEvent) adapter.getItem(i)).summaryVisible = false;
			}
			current.summaryVisible = !previousState;

			adapter.notifyDataSetChanged();
		}
	}
	
	private class MyOnItemLongClickListener implements OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			final AgendaEvent current = getEventFromAdapter(position);
			
			if (ttsInitialized) {
				tts.speak(current.title, TextToSpeech.QUEUE_FLUSH, null);
				if (current.speakerName.length() > 0)
					tts.speak(getString(R.string.speaker) + current.speakerName, TextToSpeech.QUEUE_ADD, null);
				tts.speak(current.summary, TextToSpeech.QUEUE_ADD, null);
			}
									
			return true;
		}
	}
}