package eu.kowalczuk.zimowisko2012;

public class AgendaEvent {
	public String startTime;
	public String title;
	public String speakerName;
	public String summary;

	public boolean summaryVisible = false;

	public AgendaEvent(String startTime, String title, String speakerName, String summary) {
		this.startTime = startTime;
		this.title = title;
		this.speakerName = speakerName;
		this.summary = summary;
	}

	public String toString() {
		String retValue;
		retValue = startTime + ": " + title;
		if (speakerName.length() > 0)
			retValue += " (" + speakerName + ")";

		return retValue;
	}
}