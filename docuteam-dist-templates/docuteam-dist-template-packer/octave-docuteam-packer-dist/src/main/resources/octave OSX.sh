#!/bin/sh
#		Shell script template to run docuteam packer under OS X

export	LC_CTYPE="UTF-8"

cd		"$(dirname "$0")/octave.app/Contents/docuteam packer"
java	-Xms512M -Xmx2048M -Xdock:icon=images/DocuteamPacker.ico -Xdock:name="OCTAVE docuteam packer" -Dfile.encoding=utf-8 -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager -cp config/:./:* ch.docuteam.packer.gui.launcher.LauncherView &

#		Additional optional parameters:

#		-configDir="/Users/denis/Documents/Java/Workspace/Docupack/config"
#		-open="/Users/denis/Documents/Java/Workspace/Docupack/files/DifferentFileTypes"
#		-help
