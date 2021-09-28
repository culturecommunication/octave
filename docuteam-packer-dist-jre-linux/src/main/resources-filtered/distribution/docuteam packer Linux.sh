#!/bin/sh
# Shell script to run docuteam packer under Linux

./java/bin/java -Xms512M -Xmx2048M -Dfile.encoding=utf-8 -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager -cp config/:./:docuteam-packer-${project.version}.jar ch.docuteam.packer.gui.launcher.LauncherView &

# Optional application parameters:
# -configDir="/home/user/packer-config"
# -open="/home/user/SIPs/example.zip"
# -help