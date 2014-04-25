package fi.metropolia.lbs.travist.exchange;

public class ExchangeItem {
	
	private int icon;
	private String currency;
	private String rate;
	
	public ExchangeItem (int icon, String currency, String rate) {
		super();
		this.icon = icon;
		this.currency = currency;
		this.rate = rate;
	}
	
	public int getIcon() {
		return this.icon;
	}
	
	public String getCurrency() {
		return this.currency;
	}
	
	public String getRate() {
		return this.rate;
	}	
}
