package beauty.web.util.bean;

import java.util.*;
public class ParsedProduct {
	
	public static class ParsedBrand{
		private String name;
		private byte[] photo;
		
		/**
		 * @return the photo
		 */
		public byte[] getPhoto() {
			return photo;
		}
		/**
		 * @param photo the photo to set
		 */
		public void setPhoto(byte[] photo) {
			this.photo = photo;
		}
		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}
		/**
		 * @param name the name to set
		 */
		public void setName(String name) {
			this.name = name;
		}
		/* (non-Javadoc)
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "ParsedBrand [name=" + name + "]";
		}
	}

	private String name;
	private ParsedBrand brand = new ParsedBrand();
	private String category;
	private String introduction;
	private double price; 
	private Set<String> benefits = new HashSet<String>();
	private Set<String> tags = new HashSet<String>();
	private byte[] photo;
	
	public ParsedProduct(String name, String introduction, String category, double price) {
		this.name = name;
		this.introduction = introduction;
		this.category = category;
		this.price = price;
	}
	
	public void addBenefit(String benefit){
		this.benefits.add(benefit);
	}
	
	public void addTag(String tag){
		this.tags.add(tag);
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the category
	 */
	public String getCategory() {
		return category;
	}
	/**
	 * @param category the category to set
	 */
	public void setCategory(String category) {
		this.category = category;
	}
	/**
	 * @return the photo
	 */
	public byte[] getPhoto() {
		return photo;
	}
	/**
	 * @param photo the photo to set
	 */
	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}


	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}


	/**
	 * @param price the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * @return the benefits
	 */
	public Set<String> getBenefits() {
		return benefits;
	}

	/**
	 * @return the tags
	 */
	public Set<String> getTags() {
		return tags;
	}

	/**
	 * @return the introduction
	 */
	public String getIntroduction() {
		return introduction;
	}

	/**
	 * @param introduction the introduction to set
	 */
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}

	/**
	 * @return the brand
	 */
	public ParsedBrand getBrand() {
		return brand;
	}

	/**
	 * @param brand the brand to set
	 */
	public void setBrand(ParsedBrand brand) {
		this.brand = brand;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ParsedProduct [name=" + name + ", brand=" + brand
				+ ", category=" + category + ", introduction=" + introduction
				+ ", price=" + price + ", benefits=" + benefits + ", tags="
				+ tags + "]";
	}
}
