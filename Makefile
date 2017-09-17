LIB_ANTLR := lib/antlr-4.4-complete.jar
# LIB_ANTLR := lib/antlr.jar
ANTLR_SCRIPT := Micro.g4

all: team compile

team:
	@echo ""
	@echo " Team: Just-In-Time Droids"
	@echo ""
	@echo " Rahul Patni"
	@echo " patnir"
	@echo ""
	@echo " Mahesh Babu Gorantla"
	@echo " maheshbabugorantla"
	@echo ""

compiler:
				rm -rf build
				mkdir build
				java -cp $(LIB_ANTLR) org.antlr.v4.Tool -o build $(ANTLR_SCRIPT)
				rm -rf classes
				mkdir classes
				javac -source 1.7 -target 1.7 -cp $(LIB_ANTLR) -d classes src/*.java build/*.java

clean:
	rm -rf build classes

.PHONY: all team compiler clean
