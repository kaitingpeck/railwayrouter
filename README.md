#Railway router
## Description
Based on user input of source, destination station and travel start date time, the application produces 
the fastest possible route. Only stations open at that time are considered. 
Source and destination inputs are case-insensitive, but space-sensitive.

The time of day (peak, non-peak, night hours) in the response refers to the start date time.
During the duration of travel, the time of day may change (e.g. peak to non-peak). The result
takes this into consideration.

## Assumptions
A station can be ignored if it's built only after the query date time, since it is
not known whether it has to pass through the unbuilt station at the time, depending on how the
station map is planned and constructed at the time.

## Usage
### Set up
Download the jdk-11.0.10_linux-x64_bin.tar.gz file from https://www.oracle.com/java/technologies/javase-jdk11-downloads.html

Run `sudo cp <location of jdk .tar.gz file> /var/cache/oracle-jdk11-installer-local/`
If you get an error message when running this, create the folder `oracle-jdk11-installer-local`
first by running `sudo mkdir /var/cache/oracle-jdk11-installer-local`

Run the follow commands
```
sudo add-apt-repository ppa:linuxuprising/java
sudo apt update
sudo apt install oracle-java11-set-default-local
```

Run `java -version`. You should see a message starting with `Java version "11.0.10" 2021-01-19 LTS`. 
Once you see this, java11 has been installed onto the machine.

### Running the application
Go to the directory containing this project. 

Run `java -jar railwayrouter-all.jar`

The application takes as input a `source` station, `destination` station and 
travel `start date time` in the format `yyyy-mm-ddThh:mm`, using 24-hour time.
It outputs the fastest route possible from source to destination, when starting
travel at `start date time`. Key in the inputs upon prompt from the command line.

e.g. `source`: Holland Village, `destination`: Boon Keng, 
`travel date time`: 2021-01-28T07:00 gives the result:

```aidl
Time: 104 minutes
Route: [CC21, CC20, CC19, DT9, DT10, DT11, DT12, NE7, NE8, NE9]

Take CC line from Holland Village to Farrer Road
Take CC line from Farrer Road to Botanic Gardens
Change from CC line to DT line
Take DT line from Botanic Gardens to Stevens
Take DT line from Stevens to Newton
Take DT line from Newton to Little India
Change from DT line to NE line
Take NE line from Little India to Farrer Park
Take NE line from Farrer Park to Boon Keng
```

## Development
### Building the application
To run tests and build the project, run `./gradlew build`. 
A jar file called `railwayrouter-all.jar` will be created in `build/libs`.
You can run the jar with the command `java -jar railwayrouter-all.jar`.

