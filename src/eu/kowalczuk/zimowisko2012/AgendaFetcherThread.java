package eu.kowalczuk.zimowisko2012;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AgendaFetcherThread extends Thread {
	private final String AGENDA_FILENAME = "agenda.json";
	private final String AGENDA_URL = "http://zimowisko2012.konfeo.com/" + AGENDA_FILENAME;
	private Context ctx;
	private Handler handler;

	public AgendaFetcherThread(Context ctx, Handler handler) {
		this.ctx = ctx;
		this.handler = handler;
	}

	@Override
	public void run() {
		String content = "", line;

		try {
			BufferedReader br;
			BufferedWriter bw = null;
			
			File cachedAgenda = new File(ctx.getFilesDir() + "/" + AGENDA_FILENAME);
			if (cachedAgenda.exists()) {
				Log.d("agenda", "loading agenda from file");

				br = new BufferedReader(new FileReader(cachedAgenda));
			} else {
				Log.d("agenda", "loading agenda from web");

				URL url = new URL(AGENDA_URL);
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setDoOutput(true);
				connection.setConnectTimeout(5000);
				connection.setReadTimeout(5000);
				connection.connect();
				br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				bw = new BufferedWriter(new FileWriter(cachedAgenda));
			}
			while ((line = br.readLine()) != null) {
				content += line;
				if (bw != null)
					bw.write(line);
			}
			br.close();
			if (bw != null)
				bw.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		try {
			JSONArray agenda = new JSONArray(content);

			Message msg = new Message();
			msg.obj = agenda;
			
			handler.sendMessage(msg);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}