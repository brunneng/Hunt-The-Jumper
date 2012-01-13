call mvn clean install -Dmaven.test.skip=true -e
call copy target\*.jar program
call copy target\dependency\*.* program\lib
call copy target\natives\*.* program\lib
pause