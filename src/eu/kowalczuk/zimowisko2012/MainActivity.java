package eu.kowalczuk.zimowisko2012;

import android.app.ListActivity;
import android.os.Bundle;

public class MainActivity extends ListActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	}
}