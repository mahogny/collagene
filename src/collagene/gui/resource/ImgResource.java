package collagene.gui.resource;

import java.io.IOException;
import java.io.InputStream;

import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QWidget;

/**
 * Common icons. By loading them once, memory is conserved
 * 
 * @author Johan Henriksson
 *
 */
public class ImgResource
	{
	/**
	 * Read a stream into a byte array
	 */
	public static byte[] readStreamIntoArray(InputStream is) throws IOException
		{
		if(is==null)
			throw new IOException("Inputstream is null");
		byte[] arr=LabnoteUtil.readStreamToArray(is);
		is.close();
		return arr;
		}
	
	/**
	 * Get an icon as a resource - this will work even if the icons are embedded into the jar file
	 */
	private static QPixmap getIcon(String name)
		{
		try 
			{
			QPixmap pm=new QPixmap();
			pm.loadFromData(readStreamIntoArray(ImgResource.class.getResourceAsStream(name)));
			return pm;
			} 
		catch (IOException e) 
			{
			System.out.println("Unable to read "+name+" "+e.getMessage());
			return null;
			}
		}
	

	public static QPixmap imgSettings = getIcon("tango-applications-system.png");

	public static QPixmap imgCut = getIcon("tango-edit-cut.png");
	public static QPixmap imgCopy = getIcon("tango-edit-copy.png");
	public static QPixmap imgPaste = getIcon("tango-edit-paste.png");
	/*
	public static QPixmap imgFormatBold = getIcon("tango-format-text-bold.png");
	public static QPixmap imgFormatItalics = getIcon("tango-format-text-italic.png");
	public static QPixmap imgFormatSubscript = getIcon("silk-text-subscript.png");
	public static QPixmap imgFormatSuperscript = getIcon("silk-text-superscript.png");

	public static QPixmap imgFormatUL = getIcon("silk-text-list-bullets.png");
	public static QPixmap imgFormatOL = getIcon("silk-text-list-numbers.png");
	*/
/*
	public static QPixmap attach = getIcon("tango-new-file.png");
	public static QPixmap obText = getIcon("new-text.png");
	public static QPixmap obTable = getIcon("silk-new-table.png");
	public static QPixmap obImage = getIcon("silk-new-image.png");
	public static QPixmap obSignature = getIcon("silk-signature.png");
	public static QPixmap obSound = getIcon("silk-sound.png");
	public static QPixmap obMovie = getIcon("silk-film.png");
	public static QPixmap obChemSketch = getIcon("chem_benzen2.png");
	public static QPixmap obScript = getIcon("fugue-script.png");
	public static QPixmap obLimsTable = getIcon("silk-lims-table.png");
	public static QPixmap obLimsRecordInsert = getIcon("silk-table-row-insert.png");
	public static QPixmap obLimsRecordDelete = getIcon("silk-table-row-remove.png");
	public static QPixmap obScheduleRelatedTo = getIcon("fugue_schedule_relatedto.png");

	public static QPixmap authorAdd = getIcon("tango-author-add.png");

	public static QPixmap roleAdd = getIcon("silk-user-add.png");
	public static QPixmap roleDelete = getIcon("silk-user-delete.png");
	public static QPixmap roleGroup = getIcon("silk-user-group.png");

	public static QPixmap serverConnect = getIcon("silk-connect.png");
	public static QPixmap serverDisconnect = getIcon("silk-disconnect.png");
	*/
	/*
	/home/mahogny/javaproj/labnote3.git/labnote/bin/org/ecobima/labnote/icons/tango-media-seek-backward.png
	/home/mahogny/javaproj/labnote3.git/labnote/bin/org/ecobima/labnote/icons/tango-media-seek-forward.png
	/home/mahogny/javaproj/labnote3.git/labnote/bin/org/ecobima/labnote/icons/tango-media-skip-backward.png
	/home/mahogny/javaproj/labnote3.git/labnote/bin/org/ecobima/labnote/icons/tango-media-skip-forward.png
	*/
	/*
	public static QPixmap mediaPlay = getIcon("tango-media-playback-start.png");
	public static QPixmap mediaStop = getIcon("tango-media-playback-stop.png");
	public static QPixmap mediaPause = getIcon("tango-media-playback-pause.png");
	public static QPixmap mediaRewind = getIcon("tango-media-skip-backward.png");
	public static QPixmap mediaRecord = getIcon("tango-media-record.png");
	
	public static QPixmap drawCircle = getIcon("fugue-shape-ellipse.png");
	public static QPixmap drawEraser = getIcon("fugue-eraser.png");
	public static QPixmap drawFreehand = getIcon("silk-drawFreehand.png");
	public static QPixmap drawImage = getIcon("silk-drawImage.png");
	public static QPixmap drawRect = getIcon("fugue-shape-rect.png");
	public static QPixmap drawSelect = getIcon("drawSelect.png");
	public static QPixmap drawStraightline = getIcon("fugue-shape-line.png");
	public static QPixmap drawText = getIcon("fugue-shape-text.png");
	
	

	*/
	public static QPixmap imgWindowIcon= getIcon("programlogo_256.png");

	public static QPixmap moveRight=getIcon("tango-go-next.png");
	public static QPixmap moveLeft=getIcon("tango-go-previous.png");

	public static QPixmap moveUp=getIcon("tango-go-up.png");
	public static QPixmap moveDown=getIcon("tango-go-down.png");
	public static QPixmap remove=getIcon("tango-trash.png");
	public static QPixmap refresh=getIcon("tango-view-refresh.png");

	/*
	public static QPixmap notificationSend = getIcon("silk-comment.png");
	public static QPixmap notificationNew = getIcon("fugue-notification-new.png");
*/
	public static QPixmap search = getIcon("tango-search.png");
	public static QPixmap viewFullscreen = getIcon("tango-view-fullscreen.png");

	/*
	public static QPixmap calendar=getIcon("fugue-calendar.png");
	public static QPixmap duration=getIcon("jh-stopwatch.png");
	public static QPixmap manualcount=getIcon("jh-manualcount.png");
	public static QPixmap barcode=getIcon("jh-barcode.png");
	public static QPixmap mapcoord=getIcon("fugue_mapcoord.png");
	public static QPixmap citation=getIcon("fugue-citation.png");

	public static QPixmap vcsMerge=getIcon("fugue-vcs-merge.png");
	public static QPixmap vcsCommit=getIcon("fugue-vcs-commit.png");
	public static QPixmap vcsRevert=getIcon("fugue-vcs-revert.png");

	public static QPixmap imgNewDocument=getIcon("fugue-new-document.png");
	public static QPixmap imgNewDocumentFromTemplate=getIcon("fugue-new-document-from-template.png");
	
	public static QPixmap imgTableAdd = getIcon("fugue-table-plus.png");
	public static QPixmap imgTableRemove = getIcon("fugue-table-minus.png");
	public static QPixmap imgTableCrossref = getIcon("silk-table-crossref.png");
	public static QPixmap imgTableImport=getIcon("fugue-table-import.png");
	public static QPixmap imgTableExport=getIcon("fugue-table-export.png");
	public static QPixmap imgTableInsertColumn=getIcon("fugue-table-insert-column.png");
	public static QPixmap imgTableInsertRow=getIcon("fugue-table-insert-row.png");
	public static QPixmap imgTableRemoveColumn=getIcon("fugue-table-delete-column.png");
	public static QPixmap imgTableRemoveRow=getIcon("fugue-table-delete-row.png");

	public static QPixmap docModified=getIcon("silk-docmodified.png");
	public static QPixmap docConflict=getIcon("fugue-vcs-split.png");
	public static QPixmap docStoredCompleteLocal=getIcon("silk-docstoredcomplete-local.png");
	public static QPixmap docStoredPartialLocal=getIcon("silk-docstoredpartial-local.png");
	public static QPixmap docStoredCompleteRemote=getIcon("silk-docstoredcomplete-remote.png");
	public static QPixmap docStoredPartialRemote=getIcon("silk-docstoredpartial-remote.png");

	public static QPixmap updownload=getIcon("fugue-updownload.png");
	public static QPixmap download=getIcon("fugue-download.png");
	public static QPixmap upload=getIcon("fugue-upload.png");
	public static QPixmap permissions=getIcon("fugue-lock.png");
	public static QPixmap key=getIcon("silk-key.png");
	public static QPixmap save=getIcon("silk-save.png");

	public static QPixmap paneDocuments=getIcon("silk-documents.png");
	public static QPixmap paneDatabase=getIcon("fugue-tables.png");
	public static QPixmap paneNotifications=getIcon("fugue-notifications.png");
	public static QPixmap paneSchedule=getIcon("fugue-schedule.png");
	public static QPixmap paneServer=getIcon("fugue_server_network.png");
	public static QPixmap paneProfiles=getIcon("silk-user-group.png");

	public static QPixmap print=getIcon("fugue-printer.png");

	public static QPixmap chemBenzene1 = getIcon("chem_benzen1.png");
	public static QPixmap chemBenzene2 = getIcon("chem_benzen2.png");

	public static QPixmap chemN1 = getIcon("chem_n1.png");
	public static QPixmap chemN2 = getIcon("chem_n2.png");
	public static QPixmap chemN3 = getIcon("chem_n3.png");
	public static QPixmap chemPenta1 = getIcon("chem_penta1.png");
	public static QPixmap chemPenta2 = getIcon("chem_penta2.png");

	public static QPixmap chemSquare = getIcon("chem_square.png");
	public static QPixmap chemTri = getIcon("chem_tri.png");

	public static QPixmap giftFile = getIcon("fugue-present.png");
	public static QPixmap giftExport = getIcon("fugue-present-arrow.png");

	public static QPixmap imgLabelDesign = getIcon("fugue-labeldesign.png");;

	public static QPixmap imgQR = getIcon("fugue-qrcode.png");
	*/
	public static void setWindowIcon(QWidget w)
		{
		System.out.println(imgWindowIcon);
		w.setWindowIcon(new QIcon(imgWindowIcon));
		}

	public static QLabel label(QPixmap p)
		{
		QLabel lab=new QLabel();
		lab.setPixmap(p);
		return lab;
		}
	
	
	}
