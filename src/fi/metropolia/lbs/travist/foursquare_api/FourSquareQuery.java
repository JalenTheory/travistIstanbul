package fi.metropolia.lbs.travist.foursquare_api;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.util.Log;

public class FourSquareQuery extends Criteria{

	// Do we always use Eetu's id and secret?
	private final String FOURSQUARE_ID = "LTVFBAO5CGUDU5GFKTRGTNWII1HQBLLYRXX2J5E2ONUMJA20";
	private final String FOURSQUARE_SECRET = "LFRZC3HOVDEBXI3LPFSD4JQW23ZCWDNIO3T4YCVCFYJ1TXM1";
	private final String baseUrl = "https://api.foursquare.com/v2/venues/search";
	private String queryUrl;

	public String createQuery(Criteria crit) {
		StringBuilder strBuilder = new StringBuilder();

		if (crit.getNear() == null && crit.getLatlon() == null) {
			logD("Test", "Near and ll both cannot be null");
			return null;
		} else {
			strBuilder.append(baseUrl);
			strBuilder.append("?client_id=" + FOURSQUARE_ID);
			strBuilder.append("&client_secret=" + FOURSQUARE_SECRET);
			strBuilder.append("&v=" + getVersion());

			if (crit.getNear() != null) {
				logD("Test", "Near: " + crit.getNear());
				strBuilder.append("&near=" + crit.getNear());
			}

			if (crit.getLatlon() != null) {
				logD("Test", "LatLon: " + crit.getLatlon());
				strBuilder.append("&ll=" + crit.getLatlon());
			}

			if (crit.getCategoryId() != null) {
				logD("Test", "categoryId: " + crit.getCategoryId());
				strBuilder.append("&categoryId=" + crit.getCategoryId());
			}
			
			if (crit.getLimit() != null) {
				logD("Test", "Limit: " + crit.getLimit());
				strBuilder.append("&limit=" + crit.getLimit());
			}
			
			this.queryUrl = strBuilder.toString();
			return queryUrl;
		}
	}

	// Query to foursquare requires "&v=(date)".
	@SuppressLint("SimpleDateFormat")
	private String getVersion() {
		Date date = new Date();
		String curDate = new SimpleDateFormat("yyyyMMdd").format(date);
		return curDate;
	}

	private void logD(String extra, String txt) {
		Log.d(getClass().getSimpleName(), extra + ": " + txt);
	}
}