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
	public static QPixmap imgWindowIcon= getIcon("logo_512.png");

	public static QPixmap moveRight=getIcon("tango-go-next.png");
	public static QPixmap moveLeft=getIcon("tango-go-previous.png");

	public static QPixmap moveUp=getIcon("tango-go-up.png");
	public static QPixmap moveDown=getIcon("tango-go-down.png");
	public static QPixmap remove=getIcon("tango-trash.png");
	public static QPixmap refresh=getIcon("tango-view-refresh.png");

	public static QPixmap search = getIcon("tango-search.png");
	public static QPixmap viewFullscreen = getIcon("tango-view-fullscreen.png");

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
