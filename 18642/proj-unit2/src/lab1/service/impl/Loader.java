package lab1.service.impl;

import java.io.*;
import java.util.ArrayList;

import javax.xml.parsers.*;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import lab1.model.*;
import lab1.exception.*;

public class Loader {
	/**
	 * load the automotive instances from data file
	 * 
	 * @param filename
	 *            The data file
	 * @param autos
	 *            The ArrayList to hold the automotives
	 * @throws LoaderException
	 */
	public static void loadData(String filename, ArrayList<Automotive> autos)
			throws LoaderException {

		try (ObjectInputStream inputStream = new ObjectInputStream(
				new FileInputStream(filename))) {
			/*
			 * try-catch with resource this is much better since the resource will
			 * be taken care by JVM.
			 */
			
			Object obj;

			/* read until eof */
			while (true) {
				obj = inputStream.readObject();
				if (obj instanceof Automotive) {
					autos.add((Automotive) obj);
				}
			}

		} catch (FileNotFoundException ex) {
			throw new LoaderException("dbfile("
					+ new File(filename).getAbsolutePath() + ")does not exist!");
		} catch (EOFException e) {
			/* this really means file ends */
			return;
		} catch (IOException ex) {
			throw new LoaderException("datafile read error:\n"
					+ ex.getMessage());
		} catch (ClassNotFoundException e) {
			throw new LoaderException("datafile has error:\n" + e.getMessage());
		}
	}

	/**
	 * persist automotive instances to a data file
	 * 
	 * @param file
	 *            The file we will persist automotive instances to
	 * @param autos
	 *            The Arraylist of automotives we will persist
	 * @throws LoaderException
	 */
	public static void persist(String filename, ArrayList<Automotive> autos)
			throws LoaderException {
		File file = new File(filename);
		if (!file.exists()) {
			/* If file does not exist create one first */
			try {
				file.createNewFile();
			} catch (IOException e) {
				throw new LoaderException("Create data file error!");
			}
		}

		try (ObjectOutputStream outputStream = new ObjectOutputStream(
				new FileOutputStream(filename))) {
			/*
			 * try-catch with resource this is much better since the resource will
			 * be taken care by JVM.
			 */
			for (Automotive auto : autos) {
				outputStream.writeObject(auto);
			}

		} catch (FileNotFoundException ex) {
			throw new LoaderException("dbfile("
					+ new File(filename).getAbsolutePath() + ")does not exist!");
		} catch (IOException ex) {
			throw new LoaderException("dbfile write error:\n" + ex.getMessage());
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
	public static void loadXML(String file, ArrayList<Automotive> autos)
			throws LoaderException {
		File fXmlFile = new File(file);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			NodeList automotives = doc.getElementsByTagName("automotive");
			NodeList optionsets = null;
			NodeList options = null;
			Node tmpNode = null;
			Element tmpEle = null;
			Automotive tmpAuto = null;
			String optionSetName = null;
			
			/* procss automotives */
			for (int i = 0; i < automotives.getLength(); i++) {
				tmpNode = automotives.item(i);
				if (tmpNode.getNodeType() != Node.ELEMENT_NODE)
					continue;
				/* process an automotive */
				tmpEle = (Element) tmpNode;
				tmpAuto = new Automotive(getTagValue("name", tmpEle),
						getTagValue("maker", tmpEle),
						Integer.parseInt(getTagValue("baseprice", tmpEle)));
				autos.add(tmpAuto);

				/* process its option set */
				optionsets = tmpEle.getElementsByTagName("optionset");
				for (int j = 0; j < optionsets.getLength(); j++) {
					tmpNode = optionsets.item(j);
					if (tmpNode.getNodeType() != Node.ELEMENT_NODE)
						continue;
					/* process an optionset */
					tmpEle = (Element) tmpNode;
					optionSetName = getTagValue("name", tmpEle);

					/* process its options */
					options = tmpEle.getElementsByTagName("option");
					for (int k = 0; k < options.getLength(); k++) {
						tmpNode = options.item(k);
						if (tmpNode.getNodeType() != Node.ELEMENT_NODE)
							continue;
						/* process an option */
						tmpEle = (Element) tmpNode;
						tmpAuto.addOption(optionSetName,
								getTagValue("name", tmpEle),
								Integer.parseInt(getTagValue("price", tmpEle)));
					}
				}
			}
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new LoaderException(e.getMessage());
		} catch (NumberFormatException e) {
			throw new LoaderException("please make sure the price is integer");
		}
	}

	private static String getTagValue(String tagName, Element ele) {
		return ele.getElementsByTagName(tagName).item(0).getChildNodes()
				.item(0).getNodeValue().trim();
	}
}
