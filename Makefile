compileServer:
	javac -cp .:../junit5.jar *.java

startServer: compileServer
	java WebApp 8000

runAllTests: compileServer
	java -jar ../junit5.jar --class-path=. --select-class=BackendTests

clean:
	rm *.class
