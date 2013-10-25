package me.s3for.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Validator;
import org.custommonkey.xmlunit.XMLUnit;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public class XmlUtils {

	Document doc;
	XPath xpath;

	public Diff diff;

	public XmlUtils() {
	}

	public XmlUtils(String path) {

		try {
			DocumentBuilderFactory dFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dFactory.newDocumentBuilder();
			doc = dBuilder.parse(new File(path));

			XPathFactory xpf = XPathFactory.newInstance();
			xpath = xpf.newXPath();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void setNode(String xPathString, String value) {

		XPathExpression expr;
		try {
			expr = xpath.compile(xPathString);
			Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);

			if (node == null) {
				createNode(xPathString, value);
			}
			else {
				node.setTextContent(value);
			}

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

	}

	public void setAttribute(String xPathString, String attributeName,
			String attributeValue) {

		XPathExpression expr;
		try {
			expr = xpath.compile(xPathString);
			Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);

			if (node == null) {
				node = createNode(xPathString, "");
			}

			((Element) node).setAttribute(attributeName, attributeValue);

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

	}

	public Node createNode(String xPathString, String value) {

		XPathExpression expr;
		try {
			expr = xpath.compile(xPathString);
			Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);

			if (node != null) {
				return node;
			}

			String[] nodeArray = xPathString.replace("//", "").split("/");
			String xPathNew = "//";
			for (int i = 0; i < nodeArray.length; i++) {
				if (i > 0) {
					xPathNew = xPathNew + "/";
				}
				xPathNew = xPathNew + nodeArray[i];

				expr = xpath.compile(xPathNew);
				Node nodeNew = (Node) expr.evaluate(doc, XPathConstants.NODE);
				if (nodeNew == null) {
					Element elementNew = doc.createElement(nodeArray[i]);
					node.appendChild(elementNew);
				} else {
					node = nodeNew;
				}

			}
			node.setTextContent(value);
			return node;

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void removeNode(String xPathString) {

		XPathExpression expr;
		try {
			expr = xpath.compile(xPathString);
			Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);

			node.getParentNode().removeChild(node);

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

	}

	public String getNodeTextContent(String xPathString) {

		XPathExpression expr;
		String nodeValue = "";
		try {
			expr = xpath.compile(xPathString);
			Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);

			nodeValue = node.getTextContent();

		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}

		return nodeValue;
	}

	public String transform() {

		String text = "";

		try {
			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();

			Transformer transformer = transformerFactory.newTransformer();

			StreamResult result = new StreamResult(new StringWriter());
			transformer.transform(new DOMSource(doc), result);

			text = result.getWriter().toString();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return text;

	}

	public boolean identical(String xmlText1, String xmlText2) {

		boolean result = false;

		try {
			diff = XMLUnit.compareXML(xmlText1, xmlText2);
			result = diff.identical();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}

	public boolean similar(String xmlText1, String xmlText2) {

		boolean result = false;

		try {
			diff = XMLUnit.compareXML(xmlText1, xmlText2);
			result = diff.similar();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;

	}

	/*
	 * ignore differences IgnoreTextAndAttributeValuesDifferenceListener -
	 * checks only the structure of two xmls
	 * http://stackoverflow.com/questions/1241593
	 * /java-how-do-i-ignore-certain-elements-when-comparing-xml
	 * 
	 * - ignores id tag
	 * http://stackoverflow.com/questions/5249031/xmlunit-ignoring
	 * -id-attribute-in-comparison
	 */

	public static boolean validate(String xmlPath, String xsdPath) {

		boolean result = false;

		InputSource is;
		try {
			is = new InputSource(new FileInputStream(xmlPath));
			Validator v = new Validator(is);
			v.useXMLSchema(true);
			v.setJAXP12SchemaSource(new File(xsdPath));
			result = v.isValid();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	// public static void convertToXsd(String path) {
	//
	// File dir = new File(path);
	// RuntimeUtils runtimeUtils = new RuntimeUtils();
	// runtimeUtils.run(new String[] { "cmd", "/D", "dir" });
	//
	// runtimeUtils.run(new String[] { "cmd", "cd " + path }); // + path
	//
	// for (File child : dir.listFiles()) {
	// if (child.getName().contains(".xml")) {
	// runtimeUtils
	// .run(new String[] {
	// "cmd",
	// "java -jar trang.jar " + child + " "
	// + child.getName().replace(".xml", "")
	// + ".xsd" });
	// }
	// }
	//
	// }
	//
	// public static boolean validateJaxB(String xmlPath, String xsdPath)
	// throws Exception {
	//
	// ValidationEventCollector vec = new ValidationEventCollector();
	//
	// SchemaFactory sf = SchemaFactory
	// .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	//
	// Schema schema = sf.newSchema(new StreamSource(new File(xsdPath)));
	//
	// // You should change your jaxbContext here for your stuff....
	// Unmarshaller um = JAXBContext.newInstance(Customer.class)
	// .createUnmarshaller();
	// um.setSchema(schema);
	//
	// try {
	//
	// StringReader reader = new StringReader(xmlPath);
	// um.setEventHandler(vec);
	// um.unmarshal(reader);
	// } catch (Exception e) {
	//
	// }
	// return false;

	// SchemaFactory schemaFactory =
	// SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	//
	// Schema schema = schemaFactory.newSchema(new StreamSource(new
	// File(xsdPath)));
	// JAXBContext jaxbContext = JAXBContext.newInstance("");
	//
	// Marshaller marshaller = jaxbContext.createMarshaller();
	// marshaller.setSchema(schema);
	//
	// marshaller.marshal(objectToMarshal, new DefaultHandler());

	// FileInputStream inputStream = null;
	// try{
	// SchemaFactory sf =
	// SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	// Schema schema = sf.newSchema(new File(xsdPath));
	// JAXBContext context = JAXBContext.newInstance(PackageLabel.class);
	// Unmarshaller unmarshaller = context.createUnmarshaller();
	// unmarshaller.setSchema(schema);
	// inputStream = new FileInputStream(xmlPath);
	// pl = (PackageLabel) unmarshaller.unmarshal(inputStream);
	// } catch (JAXBException e) {
	// if(pl.getAddress1() == null){
	// System.out.println("Invalid Mailing Address");
	// }
	// //EDIT: CANNOT DO THIS, SINCE pl IS NULL AT THIS POINT
	// //Some more logics on how to handle important missing-tags
	// ...
	// }finally{
	// if(inputStream != null) inputStream.close();
	// }
	// }

	// public static void xsdGenerator(String xmlPath, String xsdPath)
	// throws IOException, com.qvc.common.ParseException {
	//
	// File file = new File(xmlPath);
	//
	// new XsdUtils().parse(file).write(new FileOutputStream(xsdPath));
	//
	// // JAXBContext jc = JAXBContext.newInstance( "com.acme.foo:com.acme.bar"
	// // );
	// // Unmarshaller u = jc.createUnmarshaller();
	// // FooObject fooObj = (FooObject)u.unmarshal( new File( "foo.xml" ) );
	// // // ok
	//
	// }

}
