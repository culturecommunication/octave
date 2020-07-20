# docuteam packer

This is a Maven project, see pom.xml


## maven commands:

- generate jars and install them in the local maven repo (all other commands uses these jars)
```
  mvn clean -DskipUnitTests=true -DskipITs=true -DskipGUITests=true install
```

- create packer distribution (run install before, if java classes changed in another module - e.g. tools):
```
  mvn -pl docuteam-packer -DskipGUITests=true clean package assembly:single
```

- check file header
```
  mvn license:check
```

- update file header
```
  mvn license:format
```
  s. http://code.mycila.com/license-maven-plugin/

- run one test via maven
```
  mvn -Dtest=XMLTransformerTest test
```
