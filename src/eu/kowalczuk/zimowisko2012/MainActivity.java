package eu.kowalczuk.zimowisko2012;

import org.json.JSONArray;

import android.app.ListActivity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.Log;

public class MainActivity extends ListActivity {
	private Handler h;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		h = new Handler(new AgendaHandlerCallback());
		AgendaFetcherThread agendaFetcher = new AgendaFetcherThread(this, h);
		agendaFetcher.start();
	}

	private class AgendaHandlerCallback implements Callback {
		@Override
		public boolean handleMessage(Message msg) {
			JSONArray agenda = (JSONArray) msg.obj;

			Log.d("agenda", agenda.toString());
			return false;
		}
	}
}