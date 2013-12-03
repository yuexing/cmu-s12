package beauty.web.util.bean;

import java.util.*;

import com.google.api.client.util.Key;
public class GRetailList {
	@Key
	private String status;
	@Key
	private List<Retail> results;
	
	

	public static class Retail{
		@Key
		private List<AddressComponent> address_components;
		@Key
		private String formatted_address;
		@Key
		private Geometry geometry;
		
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Retail [address_components=" + address_components
					+ ", formatted_address=" + formatted_address
					+ ", geometry=" + geometry + "]";
		}


		/**
		 * @return the address_components
		 */
		public List<AddressComponent> getAddress_components() {
			return address_components;
		}


		/**
		 * @return the formatted_address
		 */
		public String getFormatted_address() {
			return formatted_address;
		}

		/**
		 * @return the geometry
		 */
		public Geometry getGeometry() {
			return geometry;
		}
	}
	
	public static class AddressComponent {
		@Key
		private String long_name;
		@Key
		private String short_name;
		@Key
		private List<String> types;
		
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "AddressComponent [long_name=" + long_name + ", short_name="
					+ short_name + ", types=" + types + "]";
		}


		/**
		 * @return the long_name
		 */
		public String getLong_name() {
			return long_name;
		}


		/**
		 * @return the short_name
		 */
		public String getShort_name() {
			return short_name;
		}


		/**
		 * @return the types
		 */
		public List<String> getTypes() {
			return types;
		}
	}
	
	public static class Geometry {
		@Key
		private Location location;
		
		

		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Geometry [location=" + location + "]";
		}



		/**
		 * @return the location
		 */
		public Location getLocation() {
			return location;
		}
	}
	
	public static class Location {
		@Key
		private double lat;
		@Key
		private double lng;
		
		
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "Location [lat=" + lat + ", lng=" + lng + "]";
		}


		/**
		 * @return the lat
		 */
		public double getLat() {
			return lat;
		}


		/**
		 * @return the lng
		 */
		public double getLng() {
			return lng;
		}
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return the results
	 */
	public List<Retail> getResults() {
		return results;
	}

	/**
	 * @param results the results to set
	 */
	public void setResults(List<Retail> results) {
		this.results = results;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GRetailList [status=" + status + ", results=" + results + "]";
	}
}
