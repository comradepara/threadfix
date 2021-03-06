////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2014 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////
package com.denimgroup.threadfix.importer.impl.upload;

import com.denimgroup.threadfix.data.entities.*;
import com.denimgroup.threadfix.importer.impl.AbstractChannelImporter;
import com.denimgroup.threadfix.importer.interop.ScanCheckResultBean;
import com.denimgroup.threadfix.importer.interop.ScanImportStatus;
import com.denimgroup.threadfix.importer.util.DateUtils;
import com.denimgroup.threadfix.importer.util.HandlerWithBuilder;
import com.denimgroup.threadfix.importer.util.IntegerUtils;
import com.denimgroup.threadfix.importer.util.RegexUtils;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

class DependencyCheckChannelImporter extends AbstractChannelImporter {
	
	private static Map<String, FindingKey> tagMap = new HashMap<>();
	static {
		tagMap.put("cwe", FindingKey.VULN_CODE);
		tagMap.put("severity", FindingKey.SEVERITY_CODE);
		tagMap.put("name", FindingKey.CVE);
	}

    private static final String DEFAULT_VULN = "Configuration";

	public DependencyCheckChannelImporter() {
		super(ScannerType.MANUAL);
	}
	
	@Override
	@Transactional
	public Scan parseInput() {
		return parseSAXInput(new DependencyCheckSAXParser());
	}
	
	public class DependencyCheckSAXParser extends HandlerWithBuilder {
		
		private boolean getDate   = false;
		private boolean inFinding = false;
		
		private FindingKey itemKey = null;
	
		private Map<FindingKey, String> findingMap = null;
					    
	    public void add(Finding finding) {
			if (finding != null) {
//    			finding.setNativeId(getNativeId(finding));
    			finding.setNativeId(finding.getDependency().getCve());
	    		saxFindingList.add(finding);
    		}
	    }

	    ////////////////////////////////////////////////////////////////////
	    // Event handlers.
	    ////////////////////////////////////////////////////////////////////
	    
	    @Override
		public void startElement (String uri, String name,
				      String qName, Attributes atts)
	    {
	    	if ("reportDate".equals(qName)) {
	    		getDate = true;
	    	} else if ("vulnerability".equals(qName)) {
	    		findingMap = new EnumMap<>(FindingKey.class);
	    		inFinding = true;
	    	} else if (inFinding && tagMap.containsKey(qName)) {
	    		itemKey = tagMap.get(qName);
	    	}
	    }
	    
	    @Override
		public void endElement (String uri, String name, String qName)
	    {
	    	if ("vulnerability".equals(qName)) {
	    		updateVulnCode(findingMap);
                findingMap.put(FindingKey.PATH, "Dependencies");
	    		Finding finding = constructFinding(findingMap);
                if (finding != null) {
                    Dependency dependency = new Dependency();
                    dependency.setCve(findingMap.get(FindingKey.CVE));
                    finding.setDependency(dependency);
                    add(finding);
                }
	    		findingMap = null;
	    		inFinding = false;
	    	} else if (inFinding && itemKey != null) {
	    		String currentItem = getBuilderText();
	    		
	    		if (currentItem != null && findingMap.get(itemKey) == null) {
	    			findingMap.put(itemKey, currentItem);
	    		}
	    		itemKey = null;
	    	}
	    	
	    	if (getDate) {
	    		String tempDateString = getBuilderText();

	    		if (tempDateString != null && !tempDateString.trim().isEmpty()) {
	    			date = DateUtils.getCalendarFromString("MMM dd, yyyy kk:mm:ss aa", tempDateString);
	    		}
	    		getDate = false;
	    	}
	    }

	    @Override
		public void characters (char ch[], int start, int length) {
	    	if (getDate || itemKey != null) {
	    		addTextToBuilder(ch, start, length);
	    	}
	    }
	    
	    private void updateVulnCode(Map<FindingKey, String> findingMap) {
	    	if (findingMap.get(FindingKey.VULN_CODE) == null) {
	    		findingMap.put(FindingKey.VULN_CODE, "Configuration");
	    	} else {
	    		String vulnCode = findingMap.get(FindingKey.VULN_CODE);
	    		if (vulnCode.startsWith("CWE") && vulnCode.contains(" ")) {
	    			int i = vulnCode.indexOf(" ");
	    			if (i > 0) {
	    				findingMap.put(FindingKey.VULN_CODE, vulnCode.substring(i+1));
	    			}
	    		} else if (vulnCode.matches("^CWE-[0-9]+$")) {
                    String stringId = RegexUtils.getRegexResult(vulnCode, "^CWE-([0-9]+)$");

                    Integer integerId = IntegerUtils.getIntegerOrNull(stringId);

                    // This code works because of the 1-1 correspondence of manual channel text and cwe text
                    if (integerId != null) {
                        GenericVulnerability genericVulnerability =
                                genericVulnerabilityDao.retrieveById(integerId);
                        if (genericVulnerability != null) {
                            findingMap.put(FindingKey.VULN_CODE, genericVulnerability.getName());
                        }
                    }
                }
	    	}
	    }
	}

	@Override
	public ScanCheckResultBean checkFile() {
		return testSAXInput(new DependencyCheckSAXValidator());
	}
	public class DependencyCheckSAXValidator extends HandlerWithBuilder {
		private boolean hasFindings = false;
		private boolean hasDate = false;
		private boolean correctFormat = false;
		private boolean getDate = false;
		
	    private void setTestStatus() {
	    	if (!correctFormat) {
				testStatus = ScanImportStatus.WRONG_FORMAT_ERROR;
			} else if (hasDate) {
				testStatus = checkTestDate();
			}
	    	if ((testStatus == null || ScanImportStatus.SUCCESSFUL_SCAN == testStatus) && !hasFindings) {
				testStatus = ScanImportStatus.EMPTY_SCAN_ERROR;
			} else if (testStatus == null) {
				testStatus = ScanImportStatus.SUCCESSFUL_SCAN;
			}
	    }

	    ////////////////////////////////////////////////////////////////////
	    // Event handlers.
	    ////////////////////////////////////////////////////////////////////
	    
	    @Override
		public void endDocument() {
	    	setTestStatus();
	    }

	    @Override
		public void startElement (String uri, String name, String qName, Attributes atts) throws SAXException {
	    	if ("analysis".equals(qName)) {
	    		correctFormat = true;
	    	}
	    	
	    	if ("reportDate".equals(qName)) {
	    		getDate = true;
	    	}
	    	
	    	if ("vulnerability".equals(qName)) {
	    		hasFindings = true;
	    		setTestStatus();
	    		throw new SAXException(FILE_CHECK_COMPLETED);
	    	}
	    }
	    
	    @Override
		public void endElement(String uri, String name, String qName) {
	    	if (getDate) {
	    		String tempDateString = getBuilderText();

	    		if (tempDateString != null && !tempDateString.trim().isEmpty()) {
	    			testDate = DateUtils.getCalendarFromString("MMM dd, yyyy kk:mm:ss aa", tempDateString);
	    		}
	    		
	    		hasDate = testDate != null;
	    		getDate = false;
	    	}
	    }
	    
	    @Override
		public void characters (char ch[], int start, int length) {
	    	if (getDate) {
	    		addTextToBuilder(ch, start, length);
	    	}
	    }
	}
}
