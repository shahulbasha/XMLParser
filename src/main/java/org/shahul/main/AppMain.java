package org.shahul.main;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AppMain {

	public static void main(String[] args) {
		
		
		String sourcefilePath="D:\\Developers.xml";
		String destinationfilePath="D:\\Developers_updated.xml";
		File xmlFile=new File(sourcefilePath);
		try {
			DocumentBuilderFactory documentFactory=DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder=documentFactory.newDocumentBuilder();
			Document document = documentBuilder.parse(xmlFile);
			//remove unwanted white spaces and reduce redundancies
			document.getDocumentElement().normalize();
			
			//using xPath API to query XML
			XPath xPath = XPathFactory.newInstance().newXPath();
			
			//XPath expression to get the list of Job nodes inside the root Job node
			NodeList jobList = (NodeList) xPath.compile("/Jobs/Job").evaluate(document, XPathConstants.NODESET);
			
			//update attribute value for a node with id=0 to id=1
			for(int i=0;i<jobList.getLength();i++) {
				Node idAttribute = jobList.item(i).getAttributes().getNamedItem("id");
				if(idAttribute.getTextContent().equalsIgnoreCase("0")) {
					idAttribute.setTextContent("1");
				}
			}
			
			
			NodeList vacancies = (NodeList) xPath.compile("/Jobs/Job/vacancies").evaluate(document, XPathConstants.NODESET);
			for (int i = 0; i < vacancies.getLength(); i++) {
				Node vacancy = vacancies.item(i).getFirstChild();
				int newVacancy=Integer.parseInt(vacancy.getNodeValue())+1;
				vacancy.setTextContent(String.valueOf(newVacancy));
			}
			
			//create new element salary and insert to all
			for(int i=0;i<jobList.getLength();i++) {
				Element salary = document.createElement("salary");
				salary.appendChild(document.createTextNode("100K"));
				jobList.item(i).appendChild(salary);
			}
			
			
			//to remove salary tag from the first job  
			NodeList childNodes = jobList.item(0).getChildNodes();
			for(int j=0;j<childNodes.getLength();j++) {
				if(childNodes.item(j).getNodeName().equalsIgnoreCase("salary")) {
					//jobList.item(0) is the <Job> node and pass the child node to be removed
					jobList.item(0).removeChild(childNodes.item(j));
				}
			}
			
			
			// write the content back into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "8");
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(new File(destinationfilePath));
			transformer.transform(source, result);

			System.out.println("XML File update completed");
		} catch (SAXException | ParserConfigurationException  | IOException  | TransformerException | XPathExpressionException e) {
			e.printStackTrace();
		} 
	}

}
