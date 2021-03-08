# Setup Maven

## Credentials for cloudsmith artifact repository

You can get your Cloudsmith API Key from [Cloudsmith Settings](https://cloudsmith.io/user/settings/api/). Use it as password in your personal maven settings ~/.m2/settings.xml:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">

    <servers>
        <server>
            <id>docuteam-releases</id>
                <username>{cloudsmith-user}</username>
                <password>{cloudsmith-api-key}</password>
        </server>
        <server>
            <id>docuteam-snapshots</id>
                <username>{cloudsmith-user}</username>
                <password>{cloudsmith-api-key}</password>
        </server>
        <server>
            <id>docuteam-installers</id>
                <username>{cloudsmith-user}</username>
                <password>{cloudsmith-api-key}</password>
        </server>
        <server>
            <id>docuteam-third-party</id>
                <username>{cloudsmith-user}</username>
                <password>{cloudsmith-api-key}</password>
        </server>
    </servers>

</settings>
```

A working settings file is provided in this folder.

# Checkstyle and Code Formatting in Eclipse

The coding style is automatically checked during a maven build. In Eclipse there exists a plugin to check the code.

## Install and configure Eclipse Checkstyle Plugin
https://raw.githubusercontent.com/duraspace/codestyle/master/ide-support/eclipse.md

*These instructions were created using Eclipse Neon.  Note: Eclipse Che (Codenvy) does not seem to support checkstyle.*

* Install the Eclipse Checkstyle plugin:  http://eclipse-cs.sourceforge.net/
  * Note that you can drag the plugin into your Eclipse installation
* New Check Configuration: Window > Preferences > Checkstyle; click on 'New...'
  * Create a 'Project Relative Configuration'
  * Name: `Docuteam Checkstyle`
  * Location: `/build-tools/checkstyle.xml`
  * An "Unresolved Properties Error" box will appear; click 'Edit Properties'
  	* Add a new property
  	  * Name: checkstyle.suppressions.file
  	  * Value: `${config_loc}/checkstyle-suppressions.xml`
  	  NOTE: If an error occurs at checkstyle run ("Internal Error", see .log -> "Unable to find: checkstyle-suppressions.xml", change the path to be an absolute path)
  			* Click OK
  * Set as Default: 'Docuteam Checkstyle'
  * Clear checkstyle cache by pressing the yellow refresh button on the top right, if anything chages in the checkstyle profile
* Right click your project; select Properties → Checkstyle
* Select the tab 'Main'
  * Select "Docuteam Checkstyle" from the drop down
  * Click OK
* Right click your project; select Checkstyle > Clear checkstyle violations
* Right click your project; select Checkstyle > Check Code with checkstyle
* Test: Edit a java file.  Place a curly brace on a line by itself.
  * The line of code should get highlighted in red.

## Setup Java Formatting

Configure convetions for variable names:
* Window > Preferences > Java > Code Style
* Check "Use 'is' prefix for getters that return boolean"
* Check "Add '@Override' annotation for new overriding methods"
* Click on "Apply"

Import clean up configuration:
* Window > Preferences > Java > Code Style > Clean Up; click on 'Import...'
  * Select the file `build-tools/eclipse-formatting/docuteam-cleanup.xml`
* Make sure, that the active profile is 'Fedora (adapted by Docuteam)'
* Make sure, that the the checkbox 'Show profile selection dialog for the 'Source > Clean Up'' action is unchecked
* Click on 'Apply'

Import formatting configuration:
* Window > Preferences > Java > Code Style > Formatter; click on 'Import...'
  * Select the file `build-tools/eclipse-formatting/docuteam-formatter.xml`
* Make sure, that the active profile is 'Fedora (adapted by Docuteam)'
* Click on 'Apply'

Import code templates:
* Window > Preferences > Java > Code Style > Code Templates; click on 'Import...'
  * Select the file `build-tools/eclipse-formatting/docuteam-codetemplates.xml`
* Click on 'Apply'

Import import order configuration:
* Window > Preferences > Java > Code Style > Organize Imports; click on 'Import...'
  * Select the file `build-tools/eclipse-formatting/docuteam.importorder`
* Click on 'Apply'

Instructions adapted from: https://github.com/fcrepo4/fcrepo4/tree/master/src/site/eclipse

### Configure formatting at save

* Window > Preferences > Java > Editor > Save Actions
* Check "Perform the selected actions on save"
* Check "Format source code" and select "Format edited lines" only
* Check "Organize imports"
* Check "Additional actions"
  * Click on "Configure..."
    * Select tab "Code Organizing"
      * Check "Remove trailing whitespace" and select "All lines"
      * Check "Correct indentation"
    * Select tab "Code Style"
      * Check "Use blocks in if/while/for/do statements" and select "Always"
      * Check "Use modifier 'final' where possible" and select "Private Fields" and "Local variable"
      * Check "Remove unused imports"
    * Select tab "Missing Code"
      * Check "Add missing Annotations" and select "@Override"
    * Select tab "Unnecessary Code"
      * Check "Remove unused imports"

Instructions adapted from: https://github.com/fcrepo4/fcrepo4/tree/master/src/site/eclipse


## Refresh checkstyle

These steps should be performed after every update to `build-tools/checkstyle.xml`.

* Window > Preferences > Checkstyle
* Clear checkstyle cache by pressing the yellow refresh button on the top right, if anything chages in the checkstyle profile
* Update the Formatter-Profile according to next section

## Create/update the Formatter-Profile from the checkstyle configuration

These steps only have to be performed if the checkstyle configuration has changed and the Formatter-Profile needs to be updated as well.

To format you must generate a formatter style following the [steps here](https://stackoverflow.com/questions/984778/how-to-generate-an-eclipse-formatter-configuration-from-a-checkstyle-configurati). It seems impossible to force eclipse to use braces on sinle-line if statement

* Right-click on your project in the Package view (cosmos); select Checkstyle > Create Formatter-Profile.
* Export the create Formatter-Profile to eclipse-conf (replace the old files)
* Update the Editor settings according to eclipse-conf/README.txt
