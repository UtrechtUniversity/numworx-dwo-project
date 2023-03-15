svn -u status
mvn -N install
mvn -N -B -Darguments=-N release:prepare release:perform
