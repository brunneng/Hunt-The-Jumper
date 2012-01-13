call mvn clean install -Dmaven.test.skip=true -e
call copy target\*.jar program
pause