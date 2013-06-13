package com.denimgroup.threadfix.service.framework;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class WebXMLParser {
	
	private WebXMLParser() {
		// intentionally inaccessible
	}
	
	public static ServletMappings getServletMappings(File file) {
		if (file == null || !file.exists()) {
			return new ServletMappings(null,null);
		}
		
		ServletParser parser = new WebXMLParser.ServletParser();
		
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			saxParser.parse(file, parser);
		} catch (SAXException e) {
			
		} catch (IOException e) {

		} catch (ParserConfigurationException e) {

		}
		
		return new ServletMappings(parser.mappings, parser.servlets);
	}
	
	// this class is private static so that it doesn't share state with its parent class
	// but is only accessible to this class.
	private static class ServletParser extends DefaultHandler {
		
		List<ClassMapping> servlets = new ArrayList<>();
		List<UrlPatternMapping> mappings = new ArrayList<>();
		
		String servletName = null, urlPattern = null, servletClass = null;
		StringBuilder builder = null;
		
		private static Set<String> tagsToGrab = new HashSet<>(Arrays.asList(
			new String[] { "servlet-name", "url-pattern", "servlet-class" }));
		
		@Override
		public void startElement(String uri, String localName,
				String qName, Attributes attributes)
				throws SAXException {

			if (tagsToGrab.contains(qName)) {
				builder = new StringBuilder();
			}
		}

		@Override
		public void endElement(String uri, String localName,
				String qName) throws SAXException {
			if (qName.equals("servlet-mapping")) {
				mappings.add(new UrlPatternMapping(servletName, urlPattern));
			} else if (qName.equals("servlet")) {
				servlets.add(new ClassMapping(servletName, servletClass));
			} else if (qName.equals("servlet-name")) {
				servletName = builder.toString();
				builder = null;
			} else if (qName.equals("url-pattern")) {
				urlPattern = builder.toString();
				builder = null;
			} else if (qName.equals("servlet-class")) {
				servletClass = builder.toString();
				builder = null;
			}
		}

		@Override
		public void characters(char ch[], int start, int length)
				throws SAXException {
			if (builder != null) {
				builder.append(ch, start, length);
			}
		}
	}
}