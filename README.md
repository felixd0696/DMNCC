# DMN-CC
This repository contains tests and examples for conformance checking with the help of DMN. The Camunda DMN Engine is used for this purpose.
## Structure 
In the workspace ([here](Workspace/camunda-engine-dmn-unittest-master/src/test/java/org/camunda/bpm/dmn/unittest/ConformanceTest.java)), the java class is located, which needs to be run to run the programm. <br>
Also in the workspace ([here](Workspace/camunda-engine-dmn-unittest-master/src/test/resources/org/camunda/bpm/dmn/unittest/CC.dmn)), the CC.dmn file is located, which has all the templates for the declarative conformance checking. <br>
In the Event Logs folder ([here](Workspace/camunda-engine-dmn-unittest-master/src/main/java/EventLogs)), the example event logs used to test the different constraints are located. <br>
Structure of the unittest is inspired by https://github.com/camunda/camunda-engine-dmn-unittest <br>
To be able to look at the CC.dmn file and tables, https://camunda.com/download/modeler/ is advised. <br>
## Usage
- Import the project by selecting: File / Import -> Existing Maven Project.
- Change the [input.txt](Workspace/camunda-engine-dmn-unittest-master/src/main/java) to your needs (you can also use other files (i.e. [inputBPI.txt](Workspace/camunda-engine-dmn-unittest-master/src/main/java) as input. To do that, change Line55 of the ConformanceTest Class). 
- To check the conformance of a specified event log for constraints specified in the [input.txt](Workspace/camunda-engine-dmn-unittest-master/src/main/java), run the ConformanceTest Java class. <br>
## How to change the input.txt
- 1st line is the path to your event log. The event log needs to be a CSV file and the path needs to be with '\\' as seperators in the file path. **YOU NEED TO CHANGE THIS PATH FOR YOUR FIRST USE**
- 2nd line is the seperator of the CSV file, since some are seperated by comma and some by semicolon.
- 3rd line is the column of your CSV that contains the casenames. Counting of columns starts at 0.
- 4th line is the column of your CSV that contains the eventnames. Counting of columns starts at 0.
- 5th line is the column of your CSV that contains the event timestamps. Counting of columns starts at 0.
- 6th line is the format that the timestamps are in. [Here](https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html) is the documentation of this format
- After the 7th line you can put as many constraints as you want. Please keep them in the format of "Template,EventA" or "Template,EventA,EventB" if two events are needed as input and use a new line for every constraint that you want to check. (**The input is also case sensitive**)
## List of implemented constraints
- "Participation,Event1" - Event1 occurs at least once
- "AtMostOne,Event1" - Event1 occurs at most once
- "Init,Event1" - Event1 is the first to occur
- "End,Event1" - Event1 is the last to occur
- "RespondedExistence,Event1,Event2" - If event1 occurs, then event2 occurs too
- "Response,Event1,Event2" - If event1 occurs, then event2 occurs after event1
- "AlternateResponse,Event1,Event2" - If event1 occurs, event2 occurs afterwards before event1 recurs
- "ChainResponse,Event1,Event2" - If event1 occurs, event2 occurs immediately after it
- "Precedence,Event1,Event2" - event2 occurs only if preceeded by event1
- "AlternatePrecedence,Event1,Event2" - event2 occurs only if preceeded by event1 with no other event2 in between
- "ChainPrecedence,Event1,Event2" - event2 occurs only if event1 occurs immediately before it
- "CoExistence,Event1,Event2" - event1 occurs iff. event2 occurs 
- "Succession,Event1,Event2" - event1 occurs iff. It is followed by event2
- "AlternateSuccession,Event1,Event2" - event1 and event2 occurs iff. They follow one another, alternating
- "ChainSuccession,Event1,Event2" - event1 and event2 occur iff. event2 immediately follows event1
- "NotChainSuccession,Event1,Event2" - event1 and event2 occur iff. Event2 does not immediately follow event1
- "NotSuccession,Event1,Event2" - event1 can never occur before event2
- "NotCoExistence,Event1,Event2" - event1 and event2 can never co-occur
- "Choice,Event1,Event2" - event1 or event2 eventually occur in the process instance
- "ExclusiveChoice,Event1,Event2" - event1 or event2 eventually occur in the process instance, but not together
- "Absence,Event1" - event1 does not occur
- "Absence2,Event1" - event1 does not occur more than once 
- "Absence3,Event1" - event1 does not occur more than twice
- "Exactly1,Event1" - event1 does occur exactly once
- "Exactly2,Event1" - event1 does occur exactly twice
- "Existence,Event1" - event1 does occur at least once
- "Existence2,Event1" - event1 does occur at least twice
- "Existence3,Event1" - event1 does occur at least three times

To find out more about those Templates, you can also read more [here](https://research.wu.ac.at/ws/files/19829871/Rev2-20161007.pdf)
