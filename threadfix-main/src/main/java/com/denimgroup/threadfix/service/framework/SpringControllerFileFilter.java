////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2013 Denim Group, Ltd.
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
package com.denimgroup.threadfix.service.framework;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StreamTokenizer;

import org.apache.commons.io.filefilter.IOFileFilter;

import com.denimgroup.threadfix.service.SanitizedLogger;

/**
 * This class can be used with Commons FileUtils to filter for finding Spring @Controllers.
 *
 * It actually just finds any file with an uncommented @Controller in it.
 * 
 * @author mcollins
 *
 */
public class SpringControllerFileFilter implements IOFileFilter {
	
	private final SanitizedLogger log = new SanitizedLogger("ControllerFileFilter");
	
	@Override
	public boolean accept(File file) {
		boolean returnValue = false;
		boolean hasArroba = false;
		
		if (file != null && file.exists() && file.isFile() && file.getName().endsWith(".java")) {
			Reader reader = null;
			
			try {
				reader = new FileReader(file);
			
				StreamTokenizer tokenizer = new StreamTokenizer(reader);
				tokenizer.slashSlashComments(true);
				tokenizer.slashStarComments(true);
				
				while (tokenizer.nextToken() != StreamTokenizer.TT_EOF) {
					if (hasArroba && tokenizer.sval != null && tokenizer.sval.equals("Controller")) {
						returnValue = true;
						break;
					} else if (tokenizer.sval != null && tokenizer.sval.equals("class")) {
						// we've gone too far
						break;
					}
					
					hasArroba = tokenizer.ttype == '@';
				}
			} catch (FileNotFoundException e) {
				// shouldn't happen, we check to make sure it exists
				log.error("Encountered FileNotFoundException while looking for @Controllers", e);
			} catch (IOException e) {
				log.warn("Encountered IOException while tokenizing file.", e);
			} finally {
				if (reader != null) {
					try {
						reader.close();
					} catch (IOException e) {
						log.error("IOException encountered while trying to close the FileReader.");
					}
				}
			}
		}
		
		return returnValue;
	}

	/**
	 * This should just proxy to the other method
	 */
	@Override
	public boolean accept(File file, String name) {
		return accept(new File(file.getAbsolutePath() + File.pathSeparator + name));
	}
}