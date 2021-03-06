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

package com.denimgroup.threadfix.webapp.controller.rest;

import javax.servlet.http.HttpServletRequest;

import com.denimgroup.threadfix.remote.response.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.denimgroup.threadfix.data.entities.Finding;
import com.denimgroup.threadfix.service.APIKeyService;
import com.denimgroup.threadfix.service.FindingService;
import com.denimgroup.threadfix.service.ManualFindingService;

@Controller
@RequestMapping("/rest/applications/{appId}/addFinding")
public class AddFindingRestController extends RestController {
	
	public static final String CREATION_FAILED = "New Finding creation failed.";
	public static final String INVALID_DESCRIPTION = "The longDescription parameter " +
			"needs to be set to a String between 1 and " + 
			Finding.LONG_DESCRIPTION_LENGTH + " characters long.";
	
	public static final String INVALID_VULN_NAME = "The vulnType parameter needs to be " +
			"set to a valid CWE vulnerability name.";
	public static final String PASSED_CHECK = "The request passed the check for Finding parameters.";

    @Autowired
	private ManualFindingService manualFindingService;
    @Autowired
	private FindingService findingService;
	
	private final static String NEW = "newFinding";

	/**
	 * Create a new manual finding.
	 * @see com.denimgroup.threadfix.remote.ThreadFixRestClient#addDynamicFinding()
	 */
	@RequestMapping(headers="Accept=application/json", value="", method=RequestMethod.POST)
	public @ResponseBody RestResponse<Finding> createFinding(HttpServletRequest request,
			@PathVariable("appId") int appId) {
		log.info("Received REST request for a new Finding.");

		String result = checkKey(request, NEW);
		if (!result.equals(API_KEY_SUCCESS)) {
			return RestResponse.failure(result);
		}
		// By not using @RequestParam notations, we can catch the error in the code
		// and provide better error messages.
		 
		String checkResult = findingService.checkRequestForFindingParameters(request);
		if (!checkResult.equals(PASSED_CHECK)) {
            return RestResponse.failure(checkResult);
        }
		
		Finding finding = findingService.parseFindingFromRequest(request);
		boolean mergeResult = manualFindingService.processManualFinding(finding, appId);
		
		if (mergeResult) {
			return RestResponse.success(finding);
		} else {
			return RestResponse.failure("There was an error merging the new Finding.");
		}
	}
}
