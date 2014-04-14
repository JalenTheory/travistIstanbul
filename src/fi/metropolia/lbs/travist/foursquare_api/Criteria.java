package fi.metropolia.lbs.travist.foursquare_api;

public class Criteria {
	// Required if ll is null, ex. Istanbul
	private String near;

	// Required if near is null, ex. latitude and longitude 44.3,37.2
	private String latlon;

	// Required to query by categories, adding radius improves results. If top
	// level category is provided, sub-categories will be displayed as well
	private String categoryId;

	// Limit venus by radius (meters), max 100,000 meters.
	private String radius;

	// Limit results, max 50.
	private String limit;
	
	//Top-level category followed by sub-level categories
	public final static String ARTS_AND_ENTERTAIMENT = "4d4b7104d754a06370d81259";
	public final static String ART_GALLERY = "4bf58dd8d48988d1e2931735";
	public final static String HISTORIC_SITE = "4deefb944765f83613cdba6e";
	public final static String MOVIE_THEATER = "4bf58dd8d48988d17f941735";
	public final static String MUSEUMS = "4bf58dd8d48988d181941735";
	public final static String MUSIC_VENUES = "4bf58dd8d48988d1e5931735";
	public final static String PERFORMING_ARTS = "4bf58dd8d48988d1f2931735";
	
	public String getNear() {
		return near;
	}

	public void setNear(String near) {
		this.near = near;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getRadius() {
		return radius;
	}

	public void setRadius(String radius) {
		this.radius = radius;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}
	
	public String getLatlon() {
		return latlon;
	}
	
	public void setLatlon(String latlon) {
		this.latlon = latlon;
	}

	public void setLatLon(String latitude, String longitude) {
		latlon = latitude + "," + longitude;
	}
}
