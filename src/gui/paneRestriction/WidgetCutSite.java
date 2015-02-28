package gui.paneRestriction;

import restrictionEnzyme.RestrictionEnzyme;
import restrictionEnzyme.RestrictionEnzymeCut;
import sequtil.NucleotideUtil;

import com.trolltech.qt.core.Qt.BrushStyle;
import com.trolltech.qt.core.Qt.ScrollBarPolicy;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QGraphicsLineItem;
import com.trolltech.qt.gui.QGraphicsPolygonItem;
import com.trolltech.qt.gui.QGraphicsScene;
import com.trolltech.qt.gui.QGraphicsTextItem;
import com.trolltech.qt.gui.QGraphicsView;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QPolygonF;

/**
 * Widget displaying the cut site of an enzyme
 * 
 * @author Johan Henriksson
 *
 */
public class WidgetCutSite extends QGraphicsView
	{
	private RestrictionEnzyme enz=null;
	
	int charw=12;
	
	
	public WidgetCutSite()
		{
		setScene(new QGraphicsScene());
		updategraph();
//		setSizePolicy(QSizePolicy.Policy.Expanding,QSizePolicy.Policy.Expanding);
//		setSizePolicy(QSizePolicy.Policy.Expanding,QSizePolicy.Policy.Expanding);
    setHorizontalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOff);  
    setVerticalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOff);

    int height=60;
    setMaximumHeight(height);
    setMinimumHeight(height);
		}


	private void updategraph()
		{
		QGraphicsScene scene=scene();
		scene.clear();
		if(enz!=null)
			{
			//Note - it is good to have a separate scene builder class, for making PDFs
			
			
			QPen penSelect=new QPen();
			penSelect.setColor(new QColor(0,0,0));
			penSelect.setWidth(2);
		
			QBrush brush=new QBrush();
			brush.setColor(QColor.fromRgb(0));
			brush.setStyle(BrushStyle.SolidPattern);
			
			
			QFont font=new QFont();
			font.setFamily("Courier");
			font.setPointSize(10);

			int ymid=27;
			int yArrowUp=1;
			int yArrowLow=42;
			int yTextLower=24;

			
			String rev=NucleotideUtil.complement(enz.sequence);
			for(int i=0;i<enz.sequence.length();i++)
				{
				QGraphicsTextItem tiUpper=new QGraphicsTextItem();
				tiUpper.setPlainText(""+enz.sequence.charAt(i));
				tiUpper.setFont(font);
				tiUpper.setPos(10+charw*i, 5);
				scene.addItem(tiUpper);
				
				QGraphicsTextItem tiLower=new QGraphicsTextItem();
				tiLower.setPlainText(""+rev.charAt(i));
				tiLower.setFont(font);
				tiLower.setPos(10+charw*i, yTextLower);
				scene.addItem(tiLower);
				}


			int arrowSize=6;
			
			for(RestrictionEnzymeCut cut:enz.cuts)
				{
				Integer xup=null;
				if(cut.upper!=null)
					{
					xup=midpos(cut.upper);
					
					QGraphicsPolygonItem pi=new QGraphicsPolygonItem();
					QPolygonF po=new QPolygonF();
					po.add(xup, yArrowUp+arrowSize);
					po.add(xup+arrowSize, yArrowUp);
					po.add(xup-arrowSize, yArrowUp);
					pi.setPolygon(po);
					pi.setBrush(brush);
					scene.addItem(pi);
					
					QGraphicsLineItem li=new QGraphicsLineItem();
					li.setPen(penSelect);
					li.setLine(xup, yArrowUp+arrowSize, xup, ymid);
					scene.addItem(li);
					}
				Integer xlow=null;
				if(cut.lower!=null)
					{
					xlow=midpos(cut.lower);
					QGraphicsPolygonItem pi=new QGraphicsPolygonItem();
					QPolygonF po=new QPolygonF();
					po.add(xlow, yArrowLow);
					po.add(xlow+arrowSize, yArrowLow+arrowSize);
					po.add(xlow-arrowSize, yArrowLow+arrowSize);
					pi.setPolygon(po);
					pi.setBrush(brush);
					scene.addItem(pi);
					
					QGraphicsLineItem li=new QGraphicsLineItem();
					li.setPen(penSelect);
					li.setLine(xlow, yArrowLow, xlow, ymid);
					scene.addItem(li);
					}
				
				if(xup!=null && xlow!=null)
					{
					QGraphicsLineItem la=new QGraphicsLineItem();
					la.setPen(penSelect);
					la.setLine(xup, ymid, xlow, ymid);
					scene.addItem(la);
					}
				}
			}
		setSceneRect(0, 0, 150,50);
		}

	private int midpos(int i)
		{
		return 10+charw*i +2;
		}
	
	public void setEnzyme(RestrictionEnzyme enz2)
		{
		this.enz=enz2;
		updategraph();
		}

	}
