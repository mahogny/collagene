all:

loc:
	wc -l \
	*/*/*java \
	*/*/*/*java \
	*/*/*/*/*java

jars: 
	cd ../bin; jar cfvm ../collagene.jar ../src/MANIFEST.STARTQTCLIENT \
	*/*/*.class \
	*/*/*/*.class \
	*/*/*/*/*.class \
	*/*/*.txt \
	*/*/*/*.png \
	*/*/*/*.txt \
	*/*/*/EPAM* \
	*/*/*/EBLO*


gitaddall:
	git add \
	*/*/*java \
	*/*/*/*java \
	*/*/*/*/*java \
	*/*/*/*png

############################################################################################
############################ osx ###########################################################
############################################################################################

macport: jars
	rm -Rf ../release/mac
	cp -r ../ports/mac ../release/mac
	cp ../collagene.jar ../lib/*jar ../lib/mac/*.jar ../release/mac/collagene.app/
#	cp translations/* ../release/mac/collagene.app/translations/
	cd ../release/mac/; make


############################################################################################
############################ linux #########################################################
############################################################################################

ubuntuport: jars
	sudo rm -f ../release/collagene*deb
	cd ..; sudo java -cp bin port.ubuntu.Main
	sudo mv ../release/collagene*deb ../release/collagene.deb

linuxport32: jars
	mkdir -p ../release/linux32
	rm -Rf ../release/linux32/collagene
	cd ..; cp -r ports/linux release/linux32/collagene
	cd ..; cp -r collagene.jar lib/*jar lib/linux32/*jar release/linux32/collagene/
	cd ../release/linux32; zip -r collagene_linux32 collagene; cp collagene_linux32.zip ../
	#cd ..; cp translations/* ../release/linux32/collagene/translations/

linuxport64: jars
	mkdir -p ../release/linux64
	rm -Rf ../release/linux64/collagene
	cd ..; cp -r ports/linux release/linux64/collagene
	cd ..; cp -r collagene.jar lib/*jar lib/linux64/*jar release/linux64/collagene/
	cd ../release/linux64; zip -r collagene_linux64 collagene; cp collagene_linux64.zip ../
#	cp -r translations/* ../release/linux64/collagene/translations/

linuxport: linuxport32 linuxport64 ubuntuport



############################################################################################
############################ windows #######################################################
############################################################################################

winportjre: jars
	rm -Rf ../release/winjre/
	mkdir -p ../release/winjre/collagene
	cd ..; cp ports/win/starter/collagene_bundledjre.exe release/winjre/collagene/collagene.exe
	cd ..; cp ports/win/installer/* release/winjre/
	cd ..; cp collagene.jar lib/*jar lib/win32/*jar release/winjre/collagene/
	#cd ..; cp -r translations ../release/winjre/collagene/
	cd ..; cp -r ../support_programs/jre release/winjre/
	cd ../release/winjre; "/home/mahogny/.wine/drive_c/Program Files (x86)/NSIS/makensis.exe" bundledjre.nsi
	cd ../release/winjre; mv install.exe ../collagene_winjre.exe

winportzip: jars
	rm -Rf ../release/collagene_win
	mkdir -p ../release/collagene_win
	cd ..; cp ports/win/starter/collagene_bundledjre.exe release/collagene_win/collagene.exe
	cd ..; cp ports/win/installer/* release/collagene_win/
	cd ..; cp collagene.jar lib/*jar lib/win32/*jar release/collagene_win/
	cd ..; cp -r ../support_programs/jre/* release/collagene_win/
	cd ../release/; zip -r collagene_win collagene_win


winport: winportzip # winportjre # winport32 winport64 winportjre


############################################################################################
############################ release and upload ############################################
############################################################################################

release: macport linuxport winport #docs
	mkdir -p ../release
	rm -f ../release/collagene*zip 
	cd ../release/linux64; zip -r ../collagene_linux64.zip  collagene
	cd ../release/linux32; zip -r ../collagene_linux32.zip  collagene
	#cd ports; zip -r ../../release/docs.zip            documentation
	cd .. ; cp -f src/collagene/gui/version.txt release/qtversion.txt

cleanports:
	cd ports; rm collagene.app/*.jar
#	cd ports; rm linux/*.jar
#	cd ports; rm win32/*.jar
	cd ports; rm documentation/*


upload:
	cd ../release; scp *.dmg *.zip *.deb qtversion.txt 177655_mahogny@ssh.binero.se:/storage/content/55/177655/collagene.org/public_html/release/
	#mahogny@www.endrov.net:/home/mahogny/hp/collagene/

uploadwin:
	cd ../release; scp *.exe 177655_mahogny@ssh.binero.se:/storage/content/55/177655/collagene.se/public_html/release/


timestamp:
	cd ..; date +%s > bin/collagene/gui/timestamp.txt

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





testrest:
	curl http://localhost:8080/sf
