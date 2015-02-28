all:

loc:
	wc -l \
	*/*java \
	*/*/*java



######################## todo include R-light in jar

jars: compileciteproc
	#todo separate qt from core
	jar cfvm ../madgene.jar MANIFEST.STARTQTCLIENT \
	-C ../bin \
	*/*/*.class \
	*/*/*/*.class \
	*/*/*.txt 


############################################################################################
############################ osx ###########################################################
############################################################################################

macport: jars
	rm -Rf ../release/mac
	cp -r ../ports/mac ../release/mac
	cp ../labstory.jar labstoryPostload.jar ../libs/*jar ../release/mac/labstory.app/
#	cp translations/* ../release/mac/labstory.app/translations/
	cd ../release/mac/; make


############################################################################################
############################ linux #########################################################
############################################################################################

ubuntuport: jars
	sudo rm -f ../release/labstory*deb
	sudo java -cp bin ports.ubuntu.Main
	sudo mv ../release/labstory*deb ../release/labstory.deb

linuxport32: jars
	mkdir -p ../release/linux32
	rm -Rf ../release/linux32/labstory
	cp -r ports/linux ../release/linux32/labstory
	cp -r R labstory.jar labstoryPostload.jar libs/*jar libs/linux32/*jar ../release/linux32/labstory/
	cp translations/* ../release/linux32/labstory/translations/

linuxport64: jars
	mkdir -p ../release/linux64
	rm -Rf ../release/linux64/labstory
	cp -r ports/linux ../release/linux64/labstory
	cp -r R labstory.jar labstoryPostload.jar libs/*jar libs/linux64/*jar ../release/linux64/labstory/
	cp -r translations/* ../release/linux64/labstory/translations/

linuxport: linuxport32 linuxport64 ubuntuport



############################################################################################
############################ windows #######################################################
############################################################################################

winport32: jars
	rm -Rf ../release/win32/
	mkdir -p ../release/win32/labstory
	cp ports/win/starter/labstory_sysjre.exe ../release/win32/labstory/labstory.exe
	cp ports/win/installer/* ../release/win32/
	cp labstory.jar labstoryPostload.jar libs/*jar libs/win32/*jar ../release/win32/labstory/
	cp -r R ../release/win32/labstory/
	cp -r translations ../release/win32/labstory/
	cd ../release/win32; "/home/mahogny/.wine/drive_c/Program Files (x86)/NSIS/makensis.exe" sysjre.nsi
	cd ../release/win32; mv install.exe ../labstory_win32.exe

winport64: jars
	rm -Rf ../release/win64/
	mkdir -p ../release/win64/labstory
	cp ports/win/starter/labstory_sysjre.exe ../release/win64/labstory/labstory.exe
	cp ports/win/installer/* ../release/win64/
	cp labstory.jar labstoryPostload.jar libs/*jar libs/win64/*jar ../release/win64/labstory/
	cp -r R ../release/win64/labstory/
	cp -r translations ../release/win64/labstory/
	cd ../release/win64; "/home/mahogny/.wine/drive_c/Program Files (x86)/NSIS/makensis.exe" sysjre.nsi
	cd ../release/win64; mv install.exe ../labstory_win64.exe

winportjre: jars
	rm -Rf ../release/winjre/
	mkdir -p ../release/winjre/labstory
	cp ports/win/starter/labstory_bundledjre.exe ../release/winjre/labstory/labstory.exe
	cp ports/win/installer/* ../release/winjre/
	cp labstory.jar labstoryPostload.jar libs/*jar libs/win32/*jar ../release/winjre/labstory/
	cp -r R ../release/winjre/labstory/
	cp -r translations ../release/winjre/labstory/
	cp -r ../support_programs/jre ../release/winjre/
	cd ../release/winjre; "/home/mahogny/.wine/drive_c/Program Files (x86)/NSIS/makensis.exe" bundledjre.nsi
	cd ../release/winjre; mv install.exe ../labstory_winjre.exe


winport: winportjre # winport32 winport64 winportjre


############################################################################################
############################ release and upload ############################################
############################################################################################

release: macport linuxport winport #docs
	mkdir -p ../release
	rm -f ../release/labstory*zip 
	cd ../release/linux64; zip -r ../labstory_linux64.zip  labstory
	cd ../release/linux32; zip -r ../labstory_linux32.zip  labstory
	cd ports; zip -r ../../release/docs.zip            documentation
	cp -f org/ecobima/labnote/qtclient/version.txt ../release/qtversion.txt

cleanports:
	cd ports; rm labstory.app/*.jar
#	cd ports; rm linux/*.jar
#	cd ports; rm win32/*.jar
	cd ports; rm documentation/*


upload:
	cd ../release; scp *.dmg *.zip *.deb labstory_winjre.exe qtversion.txt 177655_mahogny@ssh.binero.se:/storage/content/55/177655/labstory.se/public_html/release/

uploadwin:
	cd ../release; scp *.exe 177655_mahogny@ssh.binero.se:/storage/content/55/177655/labstory.se/public_html/release/


timestamp:
	date +%s > org/ecobima/labnote/qtclient/timestamp.txt

uploadbackup:
	cd ../..; scp -r labnote3.git 177655_mahogny@ssh.binero.se:/storage/content/55/177655/



############################################################################################
############################ misc ##########################################################
############################################################################################

lupdate:
	lupdate -no-obsolete -extensions java \
	*/*java \
	org/ecobima/labnote/*/*java \
	org/ecobima/labnote/*/*/*java \
	org/ecobima/labnote/*/*/*/*java \
	org/ecobima/labnote/*/*/*/*/*java \
	-ts translations/*ts





gitaddall:
	git add \
	*/*java \
	*/*/*java 


testrest:
	curl http://localhost:8080/sf
