package gui.colors;

import com.trolltech.qt.core.QSize;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QComboBox;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QPainter;
import com.trolltech.qt.gui.QPixmap;
import com.trolltech.qt.gui.QSizePolicy.Policy;

import seq.SeqColor;

/**
 * 
 * Combo box: List of colors
 * 
 * @author Johan Henriksson
 *
 */
public class QColorCombo extends QComboBox
	{
	private ColorSet colorset=ColorSet.colorset;

	public QColorCombo()
		{
		setSizePolicy(Policy.Minimum, Policy.Expanding);
		fillColorCombo();
		}
	
	
  public void fillColorCombo()
    {
		
		int size=12;
		for(SeqColor col:colorset.colors)
		  {
		  QPixmap pm=new QPixmap(size, size);
		  pm.fill(new QColor(0,0,0,0));   
		  QPainter p=new QPainter(pm);
		  p.setBrush(new QColor(col.r,col.g,col.b));
		  p.drawEllipse(1,1,size-2,size-2);
		  p.end();
		  addItem(new QIcon(pm), null, col);
		  }
		setIconSize(new QSize(size,size));
    }


  public SeqColor getCurrentColor()
  	{
  	return colorset.colors.get(currentIndex());
  	}
	
	}
