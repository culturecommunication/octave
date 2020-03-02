Run packer in Eclipse with the default configuration:
----------------------------------------------------

Main class: ch.docuteam.packer.gui.launcher.LauncherView

Programm arguments: NONE
VM arguments: -Dlog4j.configurationFile=config/log4j2.xml -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager
Configuration file: docuteam-packer\src\main\resources\config/docuteamPacker.properties

Run in debugging mode by setting:
docuteamPacker.isDevMode = true

# switch on/off reports, when running from eclipse, by adding/outcommenting to docuteamPacker.properties:
docuteamPacker.reportsDir.Win = ./src/main/resources/templates/reports


Run tests via maven:
-------------------
mvn test


Configuration Notes:
-------------------
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