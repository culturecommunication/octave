# Run packer in Eclipse with the default configuration

Main class: ch.docuteam.packer.gui.launcher.LauncherView

Programm arguments: NONE
VM arguments: -Dlog4j.configurationFile=config/log4j2.xml -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager
Configuration file: docuteam-packer\src\main\resources\config/docuteamPacker.properties

Run in debugging mode by setting in docuteamPacker.properties:
- docuteamPacker.isDevMode = true

switch on/off reports, when running from eclipse, by adding/outcommenting to docuteamPacker.properties:
docuteamPacker.reportsDir.Win = ./src/main/resources/templates/reports


# Run tests via maven
mvn test

## Run Swift GUI tests
Click on the "LauncherViewSwingIT.java" file in Java and select "Run as" -> "JUnit test".

### Preparation
#### Mac OS X
You're IDE needs to be able to control your mouse and keyboard to successfully run the tests. Try an initial run according to the instructions
above. It'll fail because your IDE won't have the permissions yet but one run is required, else the option in the system preferences won't
be shown. Open the System preferences, open "Security & Privacy", click on the "Privacy" tab , click on "Accessibility" and check either
"IntelliJ" or "Eclipse" on the right side.

#Configuration Notes
Sample files for skos and csv files:
docuteam-packer\src\main\resources\config/skos -> docuteam-dist-templates/docuteam-dist-template-packer/octave-docuteam-packer-dist/src/main/resources/docuteam+packer.app/Contents/docuteam+packer/config/skos
docuteam-packer\src\main\resources\config/csv -> docuteam-dist-templates/docuteam-dist-template-packer/octave-docuteam-packer-dist/src/main/resources/docuteam+packer.app/Contents/docuteam+packer/config/csv

Logger configuration:
docuteam-packer\src\main\resources\config/log4j2.xml

Default levels.xml
docuteam-packer\src\main\resources\config/levels.xml

FileNameNormalizer uses:
docuteam-packer\src\main\resources\config/fileNameNormalizer.properties
docuteam-packer\src\main\resources\config/charConversionMap.properties