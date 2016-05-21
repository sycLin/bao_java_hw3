all: TypingTutor

TypingTutor:
	if ! [ -d bin/ ];then mkdir bin;fi
	javac -d bin src/TypingTutor.java

run:
	java -cp bin/ TypingTutor

clean:
	rm -rf bin
