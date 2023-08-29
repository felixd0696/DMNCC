/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information regarding copyright
 * ownership. Camunda licenses this file to you under the Apache License,
 * Version 2.0; you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.camunda.bpm.dmn.unittest;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.nio.file.*;

import org.camunda.bpm.dmn.engine.DmnDecision;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.dmn.engine.DmnEngine;
import org.camunda.bpm.dmn.engine.test.DmnEngineRule;
import org.camunda.bpm.engine.variable.VariableMap;
import org.camunda.bpm.engine.variable.Variables;
import org.junit.Rule;
import org.junit.Test;

public class ConformanceTest {

	@Rule
	public DmnEngineRule rule = new DmnEngineRule();

	@Test
	public void shouldEvaluateDecision() throws IOException, ParseException {

		// Get DMN engine
		DmnEngine dmnEngine = rule.getDmnEngine();

		// Parse DMN model and decision
		InputStream inputStream = getClass().getResourceAsStream("CC.dmn");
		DmnDecision decision = dmnEngine.parseDecision("conformanceDecision", inputStream);
		
		// Parse Input File
		File file = new File ("src/main/java/inputCCC.txt");
		Scanner scanner = new Scanner(file);
		Map<String,Integer> inputs = new LinkedHashMap<>();
		// Parse CSV File Location
		String line1 = scanner.nextLine();
		line1 = line1.substring(line1.indexOf(":")+1).trim().replace("\"", "");
		File csvinput = Paths.get(line1).toAbsolutePath().toFile();
		// Parse CSV Seperator
		String line2 = scanner.nextLine();
		String seperator= line2.substring(line2.indexOf(":")+1).trim().replace("\"", "");
		// Parse CSV Column for Casenames
		String line3 = scanner.nextLine();
		Integer cn = Integer.valueOf(line3.substring(line3.indexOf(":")+1).trim());
		// Parse CSV Column for Eventnames
		String line4 = scanner.nextLine();
		Integer en = Integer.valueOf(line4.substring(line4.indexOf(":")+1).trim());
		// Parse CSV Column for Event Timestamps
		String line5 = scanner.nextLine();
		Integer et = Integer.valueOf(line5.substring(line5.indexOf(":")+1).trim());
		// Parse Timestamp DateFormat
		String line6 = scanner.nextLine();
		SimpleDateFormat formatter = new SimpleDateFormat(line6.substring(line6.indexOf(":")+1).trim().replace("\"", ""));
		scanner.nextLine();
		// Parse all the Input Constraints and initialize
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			inputs.put(line.trim().replace("\"", ""), 0);
		}
		scanner.close();
		

		// initialize input variables
		VariableMap variables = Variables.createVariables();

		// Initialize the list of all events that will be passed to the engine
		List<Object> eventlist = new ArrayList<>();


		try (// parsing a CSV file into buffered reader class constructor
				BufferedReader csvread = new BufferedReader(
						new FileReader(csvinput)))

		{
			
			// Do the first readLine here to "remove" the headline
			csvread.readLine();
			Integer casecounter = 0;
			String line = "";

			// initial setup to get the first casename
			String[] first = csvread.readLine().split(seperator);
			String casename = first[cn];
			eventlist.add(new ArrayList<>(Arrays.asList(first[en], formatter.parse(first[et]))));

			// running this while loop over the whole event log
			while ((line = csvread.readLine()) != null) {
				String[] setup = line.split(seperator);
				// if it is the same case as the event before, add it to the eventlist
				if (setup[cn].equals(casename)) {
					List<Object> event = new ArrayList<>(Arrays.asList(setup[en], formatter.parse(setup[et])));
					eventlist.add(event);
				}
				// if its not the same case as the event before, send the eventlist to the
				// engine and clear the list
				if (!setup[cn].equals(casename) || !csvread.ready()) {
					for (Map.Entry<String,Integer> input : inputs.entrySet()) {
						variables.clear();
						// add the list of events (list of lists) to the variables that will be send to
						// the dmn engine
						variables.putValue("eventlist", eventlist);
						String[] temp = input.getKey().split(",");
						variables.putValue("constraint", temp[0]);
						variables.putValue("event1", temp[1]);
						if (temp.length > 2) {
							variables.putValue("event2", temp[2]);
						}
						DmnDecisionTableResult results = dmnEngine.evaluateDecisionTable(decision, variables);
						if ((boolean) results.get(0).get("conformance") == false) {
							input.setValue(input.getValue()+1);
							System.out.println("Conformance problem with: " + input.getKey() + " in Case " + casename);
						}
						
					}
					casecounter++;
					eventlist.clear();
					// add the first element of the new case outside of the loop, to get into the
					// loop of checking casename again
					List<Object> event = new ArrayList<>(Arrays.asList(setup[en], formatter.parse(setup[et])));
					eventlist.add(event);
					casename = setup[cn];
				}
			}
			Integer falsesum=0;
			System.out.println("\n \n \n Overall conformance:");
			for (Map.Entry<String,Integer> input : inputs.entrySet()) {
				System.out.println("For Input: '"+input.getKey()+"' "+input.getValue()+" conformance checks failed.(" + ((float)input.getValue()*100/casecounter)  + "%)");
				
				falsesum += input.getValue();
			}
			System.out.println(
					"Out of a total of " + casecounter * inputs.size() + " conformance checks, " + falsesum + " have failed.(" + ((float)falsesum*100/(casecounter*inputs.size()))+"%)");
			System.out.println(100 - ((100 * falsesum / (float) (casecounter * inputs.size()))) + "% Conformance");
			csvread.close();
		}
	}

}