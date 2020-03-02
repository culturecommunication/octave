
rem		Batch file template to run docuteam packer under Windows

cd		"docuteam packer.app\Contents\docuteam packer"
start	javaw -Djava.library.path=lib -Dfile.encoding=utf-8 -Dlog4j.configurationFile=config/log4j2.xml -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager -Xms512M -Xmx2048M -cp config\;.\;${project.artifactId}-${project.version}.jar ch.docuteam.packer.gui.launcher.LauncherView

rem pause

rem		Additional optional parameters:

rem		-configDir="C:\Documents and Settings\user\My Documents\Workspace1\config"
rem		-open="C:\Documents and Settings\user\My Documents\Workspace1\archive\Test1\mets.xml"
rem		-help
