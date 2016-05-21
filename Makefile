all: TypingTutor

TypingTutor:
	@if ! [ -d bin/ ];then mkdir bin;fi
	@echo "compiling source files..."
	@javac -d bin src/TypingTutor.java
	@echo "compilation complete."

run:
	@echo "running TypingTutor..."
	@java -cp bin/ TypingTutor

clean:
	@echo "cleaning up..."
	@rm -rf bin
