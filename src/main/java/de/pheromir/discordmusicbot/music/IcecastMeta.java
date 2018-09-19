package de.pheromir.discordmusicbot.music;

import java.io.InputStream;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

public class IcecastMeta {

	private URL url;
	private DocumentBuilderFactory factory;
	private DocumentBuilder builder;
	private Document doc;
	private XPathFactory xpathFactory;
	private XPath xpath;

	public IcecastMeta(String xspfUrl) {
		factory = DocumentBuilderFactory.newInstance();
		doc = null;
		try {
			url = new URL(xspfUrl);
			InputStream is = url.openStream();
			builder = factory.newDocumentBuilder();
			doc = builder.parse(is);
			xpathFactory = XPathFactory.newInstance();

			xpath = xpathFactory.newXPath();
		} catch (Exception e1) {
			e1.printStackTrace();
			xpath = null;
		}
	}

	public String getTitle() {
		if (xpath == null) {
			return "Unbekannt (Fehler)";
		}
		try {
			XPathExpression expr = xpath.compile("//track/title/text()");
			String res = expr.evaluate(doc);
			if (res != null) {
				return res;
			} else
				return "Unbekannt";
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getCurrentListeners() {
		if (xpath == null) {
			return -1;
		}
		try {
			XPathExpression expr = xpath.compile("//annotation/text()");
			String res = expr.evaluate(doc);
			if (res != null) {
				Pattern p = Pattern.compile("Current Listeners: (\\d+)");
				Matcher m = p.matcher(res);
				if (m.find()) {
					return Integer.parseInt(m.group(1));
				}
			}
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		return -1;
	}

}
