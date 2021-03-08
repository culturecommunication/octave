rem Batch file template to run docuteam packer under Windows
@echo off

start .\java\bin\javaw -Djava.library.path=lib -Dfile.encoding=utf-8 -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager -Xms512M -Xmx2048M -cp config\;.\;docuteam-packer-${project.version}.jar ch.docuteam.packer.gui.launcher.LauncherView

rem Optional application parameters:
rem -configDir="C:\Users\MrX\Documents\packer-config"
rem -open="C:\Users\MrX\Documents\example\mets.xml"
rem -help
