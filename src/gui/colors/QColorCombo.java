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

	int size=12;

	public QColorCombo()
		{
		setSizePolicy(Policy.MinimumExpanding, Policy.Minimum);
		fillColorCombo();
		}
	
	
  public void fillColorCombo()
    {
		for(SeqColor col:colorset.colors)
			addColor(col);
		setIconSize(new QSize(size,size));
    }

  private void addColor(SeqColor col)
	  {
	  QPixmap pm=new QPixmap(size, size);
	  pm.fill(new QColor(0,0,0,0));   
	  QPainter p=new QPainter(pm);
	  p.setBrush(new QColor(col.r,col.g,col.b));
	  p.drawEllipse(1,1,size-2,size-2);
	  p.end();
	  addItem(new QIcon(pm), null, col);
	  }
  

  public SeqColor getCurrentColor()
  	{
  	return colorAt(currentIndex());
  	}

  private SeqColor colorAt(int index)
  	{
		SeqColor o=(SeqColor)itemData(index);
		return o;
  	}

	public void setColor(SeqColor color)
		{
		for(int i=0;i<count();i++)
			{
			SeqColor o=(SeqColor)itemData(i);
			if(o.equals(color))
				{
				setCurrentIndex(i);
				break;
				}
			}
		addColor(color);
		setCurrentIndex(count()-1);
		}
	
	}
