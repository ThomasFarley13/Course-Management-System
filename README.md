# COMP3004_CMS_GROUP
COMP3004 Repository for the Group Project

### Dependencies

-----------------------
- Java 1.8 or greater `install from https://www.oracle.com/ca-en/java/technologies/javase-downloads.html`
- MongoDB `brew install mongodb` or `https://docs.mongodb.com/manual/installation/`
- JMeter for testing only `https://jmeter.apache.org/`
### how to run locally 

--------------------
1.Run the Mongo daemon run the command `mongod` from the terminal. If MongoDB is not part of your path, navigate to the bin folder of mongo and run `mongod`
2.Refresh maven dependencies
3.Build 
4.Run the `server.java` class

### How to Run JMeterTests

---------------------

method 1: 
1. navigate to where jmeter was installed
2. run the file `jmeter.bat`
3. Using the gui open the test file stored in `src/test/jmeter`
4. `run` the file using the Gui and check the results 

method 2: 
1. reload maven dependecies
2. run the command `mvn verify`
3. you will see the results of the test in a csv file stored in `target\jmeter\results`
