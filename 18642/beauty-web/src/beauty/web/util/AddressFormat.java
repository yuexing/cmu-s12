package beauty.web.util;

import java.util.*;

import org.apache.log4j.Logger;

import beauty.web.util.bean.*;
import beauty.web.util.bean.GRetailList.AddressComponent;
import beauty.web.util.bean.GRetailList.Geometry;
import beauty.web.util.bean.GRetailList.Location;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpParser;
import com.google.api.client.json.jackson.JacksonFactory;

@SuppressWarnings("deprecation")
public class AddressFormat {

	private static final Logger log = Logger.getLogger(AddressFormat.class);
	private static final HttpTransport httpTransport = new NetHttpTransport();
	private static final String ok = "OK";
	private static final String street_number = "street_number";
	private static final String route = "route";
	@SuppressWarnings("unused")
	private static final String neighborhood = "neighborhood";
	private static final String locality = "locality";
	private static final String administrative_area_level_3 = "administrative_area_level_3";
	private static final String administrative_area_level_1 = "administrative_area_level_1";
	private static final String country = "country";
	private static final String postal_code = "postal_code";

	// Google geocoding url's
	private static final String geo_stand_url_format = "http://maps.googleapis.com/maps/api/geocode/json?address=%s&sensor=false";

	private GRetailList search(beauty.web.model.Retail r) {
		try {
			HttpRequestFactory httpRequestFactory = createRequestFactory(httpTransport);
			String url = String.format(
					geo_stand_url_format,
					r.getStreetAddress() + " " + r.getCity() + " "
							+ r.getState() + " " + r.getCountry()).replaceAll(
					"\\s", "+");
			System.out.println(url);
			HttpRequest request = httpRequestFactory
					.buildGetRequest(new GenericUrl(url));

			GRetailList list = request.execute().parseAs(GRetailList.class);
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void format(beauty.web.model.Retail r) {
		GRetailList list = this.search(r);
		if (list != null) {
			if (list.getStatus().equals(ok) && list.getResults() != null
					&& list.getResults().size() > 0) {

				GRetailList.Retail retail = list.getResults().get(0);

				// geo info
				if (retail.getGeometry() != null) {
					Geometry geo = retail.getGeometry();
					if (geo.getLocation() != null) {
						Location loc = geo.getLocation();
						r.setLat(loc.getLat());
						r.setLng(loc.getLng());
					}
				}

				if (retail.getFormatted_address() != null)
					r.setFormatted_address(retail.getFormatted_address());

				if (retail.getAddress_components() != null)
					for (AddressComponent ac : retail.getAddress_components()) {
						List<String> types = ac.getTypes();
						String str_no = null, str = null;
						if (types != null) {
							if (types.contains(administrative_area_level_3)
									|| types.contains(locality)) {
								r.setCity(ac.getLong_name());
							} else if (types.contains(country)) {
								r.setCountry(ac.getLong_name());
							} else if (types.contains(street_number)) {
								str_no = ac.getLong_name();
							} else if (types.contains(route)) {
								str = ac.getLong_name();
							} else if (types
									.contains(administrative_area_level_1)) {
								r.setState(ac.getLong_name());
							} else if (types.contains(postal_code)) {
								r.setPostcode(ac.getLong_name());
							}
							if (str_no != null && str != null) {
								r.setStreetAddress(str_no + " " + route);
								str_no = null;
								str = null;
							}
						}
					}
			} else {
				processGoogleStatus(list.getStatus(), r);
			}
		} else {
			log.warn("no place found for " + r);
		}
	}

	private void processGoogleStatus(String status, beauty.web.model.Retail r) {
		if (status.equals("ZERO_RESULTS")) {
			log.warn("no place found for " + r);
		} else if (status.equals("UNKNOWN_ERROR")) {
			log.warn("unknown error for " + r);
		} else if (status.equals("OVER_QUERY_LIMIT")) {
			log.warn("query limit to google places is reached " + r);
		} else if (status.equals("REQUEST_DENIED")) {
			log.warn("Request is denied for " + r);
		} else if (status.equals("INVALID_REQUEST")) {
			log.warn("Invalid Request for " + r);
		} else {
			log.warn("unknown error occured for " + r);
		}
	}

	/**
	 * Creating http request Factory
	 * */
	private static HttpRequestFactory createRequestFactory(
			final HttpTransport transport) {
		return transport.createRequestFactory(new HttpRequestInitializer() {
			public void initialize(HttpRequest request) {
				GoogleHeaders headers = new GoogleHeaders();
				headers.setApplicationName("AndroidHive-Places-Test");
				request.setHeaders(headers);
				JsonHttpParser parser = new JsonHttpParser(new JacksonFactory());
				request.addParser(parser);
			}
		});
	}
}
