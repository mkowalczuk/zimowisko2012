package eu.kowalczuk.zimowisko2012;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AgendaEventAdapter extends ArrayAdapter<AgendaEvent> {
	int resource;

	public AgendaEventAdapter(Context context, int resource, ArrayList<AgendaEvent> agendaEvents) {
		super(context, resource, agendaEvents);
		this.resource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout row;
		AgendaEvent currentEvent = (AgendaEvent) getItem(position);

		if (convertView == null) {
			row = new LinearLayout(getContext());
			String inflater = Context.LAYOUT_INFLATER_SERVICE;
			LayoutInflater li;
			li = (LayoutInflater) getContext().getSystemService(inflater);
			li.inflate(resource, row, true);
		} else {
			row = (LinearLayout) convertView;
		}

		((TextView) row.findViewById(R.id.start_time)).setText(currentEvent.startTime);
		((TextView) row.findViewById(R.id.title)).setText(currentEvent.title);
		((TextView) row.findViewById(R.id.speaker_name)).setText(currentEvent.speakerName);

		TextView tv = (TextView) row.findViewById(R.id.summary);
		tv.setText(currentEvent.summary);
		tv.setVisibility((currentEvent.summaryVisible && currentEvent.summary.length() > 0) ? View.VISIBLE : View.GONE);

		row.setBackgroundColor(position % 2 == 0 ? 0x80000000 : 0x80606060); // ARGB

		return row;
	}
}