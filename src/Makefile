all:

loc:
	wc -l \
	*/*java \
	*/*/*java \
	*/*/*/*java



######################## todo include R-light in jar

jars: 
	#todo separate qt from core
	cd ../bin; jar cfvm ../madgene.jar ../src/MANIFEST.STARTQTCLIENT \
	*/*.class \
	*/*/*.class \
	*/*/*/*.class \
	*/*.txt \
	*/*/*.png \
	*/*/*.txt \
	*/*/*/*.txt \
	*/*/EPAM*

############################################################################################
############################ osx ###########################################################
############################################################################################

macport: jars
	rm -Rf ../release/mac
	cp -r ../ports/mac ../release/mac
	cp ../madgene.jar ../lib/*jar ../lib/mac/*.jar ../release/mac/madgene.app/
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
	cp -r R labstory.jar labstoryPostload.jar lib/*jar lib/linux32/*jar ../release/linux32/labstory/
	cp translations/* ../release/linux32/labstory/translations/

linuxport64: jars
	mkdir -p ../release/linux64
	rm -Rf ../release/linux64/labstory
	cp -r ports/linux ../release/linux64/labstory
	cp -r R labstory.jar labstoryPostload.jar lib/*jar lib/linux64/*jar ../release/linux64/labstory/
	cp -r translations/* ../release/linux64/labstory/translations/

linuxport: linuxport32 linuxport64 ubuntuport



############################################################################################
############################ windows #######################################################
############################################################################################

winportjre: jars
	rm -Rf ../release/winjre/
	mkdir -p ../release/winjre/madgene
	cp ports/win/starter/madgene_bundledjre.exe ../release/winjre/madgene/madgene.exe
	cp ports/win/installer/* ../release/winjre/
	cp madgene.jar lib/*jar lib/win32/*jar ../release/winjre/madgene/
	cp -r R ../release/winjre/madgene/
	cp -r translations ../release/winjre/madgene/
	cp -r ../support_programs/jre ../release/winjre/
	cd ../release/winjre; "/home/mahogny/.wine/drive_c/Program Files (x86)/NSIS/makensis.exe" bundledjre.nsi
	cd ../release/winjre; mv install.exe ../madgene_winjre.exe


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
	*/*/*java \
	*/*/*/*java \
	*/*/*png

testrest:
	curl http://localhost:8080/sf
