package ibd.rxjava.fortune;

import java.util.Date;

public class FortuneData {
	
	// protected String thema;
	protected String text;
	protected Date date;

	FortuneData(String text, Date date) {
		this.text = text;
		this.date = date;
	}
	
	public String getText() {
		return text;
	}

	public Date getDate() {
		return date;
	}
	
	public String toString() {
		return new String(this.date + ":\n" + this.text);
	}
}
