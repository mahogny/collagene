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
			QPen penLine=new QPen();
			penLine.setColor(new QColor(200,200,200));
			//penLine.setWidth(2);
		
			QBrush brush=new QBrush();
			brush.setColor(QColor.fromRgb(0));
			brush.setStyle(BrushStyle.SolidPattern);
			
			
			QFont font=new QFont();
			font.setFamily("Courier");
			font.setPointSize(10);

			int ymid=27;
			int yArrowUp=1;
			int yArrowLow=42;
			int yTextUpper=3;
			int yTextLower=22;
			int arrowSize=4;
			
			//The motif
			String rev=NucleotideUtil.complement(enz.sequence);
			int tox=2;
			for(int i=0;i<enz.sequence.length();i++)
				{
				QGraphicsTextItem tiUpper=new QGraphicsTextItem();
				tiUpper.setPlainText(""+enz.sequence.charAt(i));
				tiUpper.setFont(font);
				tiUpper.setPos(midpos(i)-tox, yTextUpper);
				tiUpper.setZValue(100);
				scene.addItem(tiUpper);
				
				QGraphicsTextItem tiLower=new QGraphicsTextItem();
				tiLower.setPlainText(""+rev.charAt(i));
				tiLower.setFont(font);
				tiLower.setPos(midpos(i)-tox, yTextLower);
				tiUpper.setZValue(100);
				scene.addItem(tiLower);
				}

			//Draw the cuts
			for(RestrictionEnzymeCut cut:enz.cuts)
				{
				Integer cutUpper=cut.upper;
				Integer cutLower=cut.lower;

				//Add Ns on sides if needed
				if(cutUpper!=null && cutUpper>enz.sequence.length())
					{
					cutUpper=enz.sequence.length()+1;
					QGraphicsTextItem tiUpperN=new QGraphicsTextItem();
					tiUpperN.setHtml("N<sub>"+(cut.upper-enz.sequence.length())+"</sub>");
					tiUpperN.setFont(font);
					tiUpperN.setPos(midpos(enz.sequence.length())-tox, yTextUpper);
					tiUpperN.setZValue(100);
					scene.addItem(tiUpperN);
					}
				if(cutLower!=null && cutLower>enz.sequence.length())
					{
					cutLower=enz.sequence.length()+1;
					QGraphicsTextItem tiUpperN=new QGraphicsTextItem();
					tiUpperN.setHtml("N<sub>"+(cut.lower-enz.sequence.length())+"</sub>");
					tiUpperN.setFont(font);
					tiUpperN.setPos(midpos(enz.sequence.length())-tox, yTextLower);
					tiUpperN.setZValue(100);
					scene.addItem(tiUpperN);
					}

				if(cutUpper!=null && cutUpper<0)
					{
					cutUpper=-1;
					QGraphicsTextItem tiUpperN=new QGraphicsTextItem();
					tiUpperN.setHtml("N<sub>"+(-cut.upper)+"</sub>");
					tiUpperN.setFont(font);
					tiUpperN.setPos(midpos(-1)-tox-2, yTextUpper);
					tiUpperN.setZValue(100);
					scene.addItem(tiUpperN);
					}
				if(cutLower!=null && cutLower<0)
					{
					cutLower=-1;
					QGraphicsTextItem tiUpperN=new QGraphicsTextItem();
					tiUpperN.setHtml("N<sub>"+(-cut.lower)+"</sub>");
					tiUpperN.setFont(font);
					tiUpperN.setPos(midpos(-1)-tox-2, yTextLower);
					tiUpperN.setZValue(100);
					scene.addItem(tiUpperN);
					}

				//Draw lines and arrows
				Integer xup=null;
				if(cutUpper!=null)
					{
					xup=midpos(cutUpper);
					
					QPolygonF po=new QPolygonF();
					po.add(xup, yArrowUp+arrowSize);
					po.add(xup+arrowSize, yArrowUp);
					po.add(xup-arrowSize, yArrowUp);
					QGraphicsPolygonItem pi=new QGraphicsPolygonItem();
					pi.setPolygon(po);
					pi.setBrush(brush);
					scene.addItem(pi);
					
					QGraphicsLineItem li=new QGraphicsLineItem();
					li.setPen(penLine);
					li.setLine(xup, yArrowUp+arrowSize, xup, ymid);
					scene.addItem(li);
					}
				Integer xlow=null;
				if(cutLower!=null)
					{
					xlow=midpos(cutLower);
					QPolygonF po=new QPolygonF();
					po.add(xlow, yArrowLow);
					po.add(xlow+arrowSize, yArrowLow+arrowSize);
					po.add(xlow-arrowSize, yArrowLow+arrowSize);
					QGraphicsPolygonItem pi=new QGraphicsPolygonItem();
					pi.setPolygon(po);
					pi.setBrush(brush);
					scene.addItem(pi);
					
					QGraphicsLineItem li=new QGraphicsLineItem();
					li.setPen(penLine);
					li.setLine(xlow, yArrowLow, xlow, ymid);
					scene.addItem(li);
					}
				
				if(xup!=null && xlow!=null)
					{
					QGraphicsLineItem la=new QGraphicsLineItem();
					la.setPen(penLine);
					la.setLine(xup, ymid, xlow, ymid);
					scene.addItem(la);
					}
				}
			setSceneRect(0, 0, midpos(enz.sequence.length())+20,50);
			}
		}

	private int midpos(int i)
		{
		return 20+charw*i +2;
		}
	
	public void setEnzyme(RestrictionEnzyme enz2)
		{
		this.enz=enz2;
		updategraph();
		}

	}
