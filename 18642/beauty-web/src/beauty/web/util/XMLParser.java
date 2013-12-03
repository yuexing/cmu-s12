package beauty.web.util;

import java.io.*;
import java.util.zip.*;
import java.util.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import beauty.web.exception.ParserException;
import beauty.web.util.bean.*;

public class XMLParser {

	private static final String productXml = "product.xml";
	
	public void loadZipByteProduct(byte[] bytes, ArrayList<ParsedProduct> products) throws ParserException{		
		String tmpFileName = null;
		try {
			File tmpFile = File.createTempFile("beauty", ".zip");	
			tmpFileName = tmpFile.getAbsolutePath();
			ByteArrayInputStream is = new ByteArrayInputStream(bytes);
			FileOutputStream out = new FileOutputStream(tmpFile);
			byte[] buffer = new byte[1024];
			int n = 0;
			while((n = is.read(buffer, 0, 1024)) != -1){
					out.write(buffer, 0, n);
			}
			is.close();
			out.close();
		} catch (IOException e) {
			throw new ParserException(e);
		}		
		//System.out.println(tmpFileName);
		loadZipProduct(tmpFileName, products);
		//System.out.println(products);
	}
	
	public void loadZipProduct(String zip, ArrayList<ParsedProduct> products) throws ParserException
	{
		try {
			ZipFile zipFile = new ZipFile(zip);
			this.loadXMLProduct(zipFile, products);
		} catch (IOException e) {
			throw new ParserException(e);
		} 
	}
	
	public void loadXMLRetail(String file, ArrayList<ParsedRetail> retails) throws ParserException{
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new File(file));

			NodeList node_retails = doc.getElementsByTagName("retail");
			// products
			NodeList products = null;
			Node tmpNode = null;
			Element tmpEle = null;
			ParsedRetail tmp_retail = null;
			
			/* procss products */
			for (int i = 0; i < node_retails.getLength(); i++) {
				tmpNode = node_retails.item(i);
				
				if (tmpNode.getNodeType() != Node.ELEMENT_NODE)
					continue;
				
				/* process a retail */
				tmpEle = (Element) tmpNode;
				
				tmp_retail = new ParsedRetail(getTagValue("name",tmpEle), 
						getTagValue("url",tmpEle), getTagValue("street",tmpEle), getTagValue("city",tmpEle), 
						getTagValue("state",tmpEle), getTagValue("country",tmpEle), getTagValue("postcode",tmpEle),
						getTagValue("phone",tmpEle));
				
				products = tmpEle.getElementsByTagName("product");
				for(int j = 0; j < products.getLength(); j++){
					tmpNode = products.item(i);
					
					if(tmpNode.getNodeType() != Node.ELEMENT_NODE)
						continue;
					
					tmpEle = (Element) tmpNode;
					tmp_retail.addProduct(getText(tmpEle));
				}
				new AddressFormat().format(tmp_retail);
				retails.add(tmp_retail);
			}
		}catch (ParserConfigurationException | SAXException | IOException e) {
			throw new ParserException(e);
		} 
	}
	
	/**
	 * load automotive instances from a XML file
	 * 
	 * @param file
	 *            The XML file full of well-formatted data
	 * @param autos
	 *            The ArrayList to hold the automotives
	 * @throws LoaderException
	 */
	private void loadXMLProduct(ZipFile zip, ArrayList<ParsedProduct> products)
			throws ParserException {
		
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(zip.getInputStream(zip.getEntry(productXml)));
			// product
			NodeList node_products = doc.getElementsByTagName("product");
			// benefits and tags
			NodeList optionsets = null;
			Node tmpNode = null;
			Element tmpEle = null;
			ParsedProduct tmp_product = null;
			ParsedProduct.ParsedBrand tmp_brand = null;
			String tmp_photo = null;		
			
			/* procss products */
			for (int i = 0; i < node_products.getLength(); i++) {
				tmpNode = node_products.item(i);
				
				if (tmpNode.getNodeType() != Node.ELEMENT_NODE)
					continue;
				
				/* process a product */
				tmpEle = (Element) tmpNode;
				tmp_product = new ParsedProduct(getTagValue("name", tmpEle),
						getTagValue("introduction", tmpEle),
						getTagValue("category", tmpEle),
						Double.parseDouble(getTagValue("price", tmpEle)));
				
				/* process its photo*/
				tmp_photo = getTagValue("photo", tmpEle);
				InputStream input= zip.getInputStream(zip.getEntry(tmp_photo));
				ByteArrayOutputStream output = new ByteArrayOutputStream();
				int n = 0;
				byte[] buffer = new byte[1024];
				while(-1 != (n=input.read(buffer))){
					output.write(buffer, 0, n);
				}
				input.close();
				
				tmp_product.setPhoto(output.toByteArray());

				/* process its brand*/
				optionsets = tmpEle.getElementsByTagName("brand");
				for(int j = 0; j < optionsets.getLength(); j++){
					tmpNode = optionsets.item(j);
					if (tmpNode.getNodeType() != Node.ELEMENT_NODE)
						continue;
					
					tmp_brand = new ParsedProduct.ParsedBrand();
					tmp_brand.setName(getTagValue("bname", (Element) tmpNode));
					
					/* process its photo*/
					tmp_photo = getTagValue("bphoto", (Element) tmpNode);
					input= zip.getInputStream(zip.getEntry(tmp_photo));
					output = new ByteArrayOutputStream();
					n = 0;
					buffer = new byte[1024];
					while(-1 != (n=input.read(buffer))){
						output.write(buffer, 0, n);
					}
					input.close();
					tmp_brand.setPhoto(output.toByteArray());
					tmp_product.setBrand(tmp_brand);
				}
				
				/* process its benefits */
				optionsets = tmpEle.getElementsByTagName("benefit");
				for (int j = 0; j < optionsets.getLength(); j++) {
					tmpNode = optionsets.item(j);
					if (tmpNode.getNodeType() != Node.ELEMENT_NODE)
						continue;
					
					/* process an optionset */
					tmp_product.addBenefit(getText((Element) tmpNode));
				}
				
				/* process its tags */
				optionsets = tmpEle.getElementsByTagName("tag");
				for (int j = 0; j < optionsets.getLength(); j++) {
					tmpNode = optionsets.item(j);
					if (tmpNode.getNodeType() != Node.ELEMENT_NODE)
						continue;
					
					/* process an optionset */
					tmp_product.addTag(getText((Element) tmpNode));
				}	
				
				products.add(tmp_product);
			}
		} catch (ParserConfigurationException | SAXException | IOException  e) {
			throw new ParserException(e);
		} catch (NumberFormatException e) {
			throw new ParserException("please make sure the price is double");
		}
		//	catch (NullPointerException e) {
//			throw new ParserException("please check the file format, you must have lost some tag");
//		}
	}

	private static String getTagValue(String tagName, Element ele) {
		return ele.getElementsByTagName(tagName).item(0).getChildNodes()
				.item(0).getNodeValue().trim();
	}
	
	private static String getText(Element ele){
		return ele.getChildNodes().item(0).getNodeValue().trim();
	}
	
	public static void main(String[] args) throws Exception{
		ArrayList<ParsedProduct> products = new ArrayList<ParsedProduct>();
		new XMLParser().loadZipProduct("test.zip", products);
		for(ParsedProduct p : products){
			System.out.println(p);
//			FileOutputStream fo = new FileOutputStream(new File("tmp.jpg"));
//			fo.write(p.getPhoto());
//			fo.close();
		}
		
		ArrayList<ParsedRetail> retails = new ArrayList<ParsedRetail>();
		new XMLParser().loadXMLRetail("retail.xml", retails);
		for(ParsedRetail r: retails){
			System.out.println(r);
		}
	}
}
