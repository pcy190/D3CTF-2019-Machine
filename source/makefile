# Powered by HAPPY

# Require: apktool jarsigner
OUTPUT_DIR:=output
APK_INPUT_FILE:=app-release.apk
#APK_INPUT_FILE:=app-debug.apk
APK_OUTPUT_FILE:=output.apk
ABI_TARGET:=x86 armeabi-v7a

ORI_SO_FILENAME:=libnative-lib.so
MODIFIED_SO_FILENAME:=libnative-lib_.so

decompile: $(APK_INPUT_FILE)
	rm -rf $(OUTPUT_DIR)
	@ apktool d $(APK_INPUT_FILE) -o $(OUTPUT_DIR)

$(OUTPUT_DIR):
	make decompile


pack: $(OUTPUT_DIR)
	apktool b $(OUTPUT_DIR) -o $(APK_OUTPUT_FILE)
	jarsigner -verbose -keystore keystore_happy.jks  $(APK_OUTPUT_FILE)  happy  


replace: $(OUTPUT_DIR)
	@for t in $(ABI_TARGET); \
	do                        \
	cd $(OUTPUT_DIR)/lib/$$t/ && rm $(ORI_SO_FILENAME) && mv $(MODIFIED_SO_FILENAME) $(ORI_SO_FILENAME) && cd ../../../ ;\
	done

clean:
	rm -rf $(OUTPUT_DIR) $(APK_INPUT_FILE) $(APK_OUTPUT_FILE)

all:
	make decompile && make replace && make pack