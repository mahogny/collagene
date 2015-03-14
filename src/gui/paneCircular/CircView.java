package gui.paneCircular;



import gui.paneRestriction.SelectedRestrictionEnzyme;
import gui.sequenceWindow.SeqViewSettingsMenu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import restrictionEnzyme.RestrictionEnzyme;
import seq.AnnotatedSequence;
import seq.RestrictionSite;
import seq.SeqAnnotation;
import seq.SequenceRange;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.Qt.MouseButton;
import com.trolltech.qt.core.Qt.ScrollBarPolicy;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QFontMetricsF;
import com.trolltech.qt.gui.QGraphicsEllipseItem;
import com.trolltech.qt.gui.QGraphicsLineItem;
import com.trolltech.qt.gui.QGraphicsScene;
import com.trolltech.qt.gui.QGraphicsTextItem;
import com.trolltech.qt.gui.QGraphicsView;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QSizePolicy;
import com.trolltech.qt.gui.QTransform;


/**
 * Display sequence as a circular plasmid
 * 
 * @author Johan Henriksson
 *
 */
public class CircView extends QGraphicsView
	{
	//Might be best to work from ChemView. Support arbitrary transformations. Then have a special one on top for common use
	
	double plasmidRadius=100;
	
	public AnnotatedSequence seq=new AnnotatedSequence();

	public class EmittedText
		{
		String txt;
		QColor col;
		}
	
	private QFont emittedTextFont=new QFont();
	private LinkedList<EmittedText> emittedText=new LinkedList<EmittedText>();
	private double emittedAngle;
	private ArrayList<QRectF> emittedTextRegions=new ArrayList<QRectF>();

	private boolean isSelecting=false;

	
	public double circPan=0; //From 0 to 1
	public double circZoom=1;  //1 means to fit it all into the window

	
	protected QPointF currentViewCenter = new QPointF();

	
	private Collection<QGraphicsEllipseItem> selectionItems=new LinkedList<QGraphicsEllipseItem>();
	private SequenceRange selection=null;

	public SeqViewSettingsMenu settings=new SeqViewSettingsMenu();
	
	
	public QSignalEmitter.Signal1<Object> signalUpdated=new Signal1<Object>();

	SelectedRestrictionEnzyme selectedEnz=new SelectedRestrictionEnzyme();

	
	public void setSelection(SequenceRange r)
		{
		selection=r;
		updateSelectionGraphics();
		}
	
	
	/**
	 * Update the graphics elements for the selection
	 */
	private void updateSelectionGraphics()
		{
		//Remove previous selection
		for(QGraphicsEllipseItem i:selectionItems)
			scene().removeItem(i);
		selectionItems.clear();

		//Draw selection
		if(selection!=null)
			{
			SequenceRange rangeun=selection.toUnwrappedRange(seq);
			
			QPen penSelect=new QPen();
			penSelect.setColor(new QColor(200,100,200));
			
			int ang1=(int)((circPan + rangeun.from/(double)seq.getLength())*360*16);
			int ang2=(int)((circPan + rangeun.to/(double)seq.getLength())*360*16);
			QGraphicsEllipseItem itemSelect=new QGraphicsEllipseItem();
			itemSelect.setPen(penSelect);
			double r=plasmidRadius+10/circZoom;
			itemSelect.setRect(-r,-r,2*r,2*r);
			itemSelect.setStartAngle(-ang1);
			itemSelect.setSpanAngle(ang1-ang2);
			itemSelect.setZValue(10000);
			scene().addItem(itemSelect);
			selectionItems.add(itemSelect);
			}
		}
	
	
	
	/**
	 * Constructor
	 */
	public CircView()
		{
		setBackgroundBrush(new QBrush(QColor.fromRgb(255,255,255)));
		
    setHorizontalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOff);  //is there a newer version??
    setVerticalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOff);

		setMouseTracking(true);
		setEnabled(true);

		setSizePolicy(QSizePolicy.Policy.Expanding,QSizePolicy.Policy.Expanding);

		setSceneRect(-10000000, -10000000, 10000000*2, 10000000*2);
		setScene(new QGraphicsScene());
		setCameraFromCirc();
		}
	
	
	public void setCameraFromCirc()
		{
		//Set the right scale
		double sWidth=width()/(double)(plasmidRadius*2);
		double sHeight=height()/(double)(plasmidRadius*2);
		double scale=circZoom*Math.min(sWidth, sHeight);
		QTransform trans=QTransform.fromScale(scale,scale);
		setTransform(trans,false);
	
		double dy=Math.max(0,(plasmidRadius+30/circZoom)*scale-height()/2)/scale;
		double y=-dy;
		
		//and here another thing. when getting the line to mid, should keep it in the mid
		
//		if(scale>sHeight)
//			y=-plasmidRadius;
		
		//Center on mid point
		setViewCenter(new QPointF(0,  y));

		
		
//		centerOnRect(new QRectF(-cr,-cr-1,cr*2,2), 0.6*circZoom);
		buildSceneFromDoc();
		}

	/**
	 * Get a bounding box for a text item (taking position into account)
	 */
	public static QRectF textBR(QGraphicsTextItem ti)
		{
		QFontMetricsF m=new QFontMetricsF(ti.font());
		double w=m.width(ti.toPlainText());
		double h=m.height();
		return new QRectF(ti.x(), ti.y(), w, h);
		}
	
	/**
	 * Put the text emitted so far on screen
	 */
	private void emitAnnotationText()
		{
		if(!emittedText.isEmpty())
			{
			StringBuilder tottext=new StringBuilder();
//			String tottext=LabnoteUtil.commaSeparateLowlevel(emittedText);
			for(EmittedText t:emittedText)
				{
				if(tottext.length()>0)
					tottext.append(",");
				tottext.append("<font color=\""+t.col.name()+"\">"+t.txt+"</font>");
				}
			
			QPen pen=new QPen();
			pen.setColor(new QColor(0,0,0));

			double rad=plasmidRadius+5/circZoom;
			emittedAngle=emittedAngle-(int)(emittedAngle);

			QGraphicsTextItem itemt=new QGraphicsTextItem();
			itemt.setFont(emittedTextFont);
			itemt.setHtml(tottext.toString());
			scene().addItem(itemt);

			QPointF textPos=new QPointF(rad*Math.cos(emittedAngle*2*Math.PI), rad*Math.sin(emittedAngle*2*Math.PI));
			QPointF textHandlePos=new QPointF(textPos.x(),textPos.y());
			if(emittedAngle>0.25 && emittedAngle<0.75)
				textPos.setX(textPos.x()-itemt.boundingRect().width()+3);
			else
				textPos.setX(textPos.x()-3);
			textPos.setY(textPos.y()-itemt.boundingRect().height()/2);
			itemt.setPos(textPos);

			//Find a suitable location for the text
			textpositions: for(;;)
				{
				QRectF cur=textBR(itemt);
				for(int i=emittedTextRegions.size()-1;i>=0;i--) //Scanning backwards I suspect is more likely to give hits early
					{
					QRectF reg=emittedTextRegions.get(i);
					if(reg.intersects(cur))
						{
						//If text hits another text, move it up/down and try again
						double dy;
						if(emittedAngle>0.5)
							dy=reg.top()-cur.bottom() - 1;
						else
							dy=reg.bottom()-cur.top() + 1;
						
						textPos.setY(textPos.y()+dy);
						textHandlePos.setY(textHandlePos.y()+dy);
						itemt.setPos(textPos);
						continue textpositions;
						}
					}
				break;
				}
			emittedTextRegions.add(textBR(itemt));

			//Put text on screen
			QGraphicsLineItem itemS=new QGraphicsLineItem();
			itemS.setPen(pen);
			itemS.setLine(
					plasmidRadius*Math.cos(emittedAngle*2*Math.PI),   plasmidRadius*Math.sin(emittedAngle*2*Math.PI),
					textHandlePos.x(),textHandlePos.y());//rad2*Math.cos(lastAng*2*Math.PI), rad2*Math.sin(lastAng*2*Math.PI));
			scene().addItem(itemS);
			
			emittedText.clear();
			}
		}
	
	/**
	 * Add annotation text at given angle. Emit text if it is time
	 */
	private void addAnnotationText(double ang, RestrictionEnzyme enz)
		{
		double minang=0.01/circZoom;
		
		EmittedText txt=new EmittedText();
		txt.txt=enz.name;
		if(selectedEnz.enzymes.contains(enz))
			txt.col=QColor.fromRgb(255,0,0);
		else
			txt.col=QColor.fromRgb(0,0,0);
		
		if(Math.abs(emittedAngle-ang)>minang)
			emitAnnotationText();
		
		if(emittedText.isEmpty())
			emittedAngle=ang;
		emittedText.add(txt);

		}
	
	/**
	 * Start a new round of text emission
	 */
	private void resetEmittedText()
		{
		emittedText.clear();
		emittedTextRegions.clear();
		}
		
	
	/**
	 * Build the scene from the document. This is equivalent to repainting
	 */
	public void buildSceneFromDoc()
		{
		QGraphicsScene scene=scene();
		scene.clear();
		selectionItems.clear();

		emittedTextFont.setPointSizeF(4.0/circZoom);
		emittedTextFont.setFamily("Arial");

		//Note - it is good to have a separate scene builder class, for making PDFs
		
		//Add the plasmid
		QGraphicsCircPlasmidSequence ic=new QGraphicsCircPlasmidSequence();
		ic.view=this;
		scene.addItem(ic);
		
		//Add all annotations
		addsceneAnnotation();
		

		//Find restriction sites to draw
		LinkedList<RestrictionSite> totSites=new LinkedList<RestrictionSite>();
		for(RestrictionEnzyme enz:seq.restrictionSites.keySet())
			{
			Collection<RestrictionSite> sites=seq.restrictionSites.get(enz);
			if(settings.allowsRestrictionSiteCount(enz,sites.size()) || selectedEnz.enzymes.contains(enz))
				totSites.addAll(sites);
			}

		
		//Render restriction sites
		Collections.sort(totSites, new Comparator<RestrictionSite>()
			{
			public int compare(RestrictionSite o1, RestrictionSite o2)
				{
				return Double.compare(o1.cuttingUpperPos, o2.cuttingUpperPos);
				}
			});
		resetEmittedText();
		for(RestrictionSite site:totSites)
			{
			double ang=(circPan+site.cuttingUpperPos/(double)seq.getLength());
			addAnnotationText(ang, site.enzyme);
			}
		emitAnnotationText();
		
		
		updateSelectionGraphics();
		}	

	
	private void addsceneAnnotation()
		{

		//Figure out the level of each annotation, to avoid overlap.
		//First step is to sort from left to right. BUT NOT REALLY NEEDED
		/*
		Collections.sort(seq.annotations, new Comparator<SeqAnnotation>()
			{
			public int compare(SeqAnnotation o1, SeqAnnotation o2)
				{
				return Double.compare(o1.from, o2.from);
				}
			});
			*/
		//Now place them all
		QGraphicsCircSeqAnnotationItem[] annotlist=new QGraphicsCircSeqAnnotationItem[seq.annotations.size()];
		for(int i=0;i<seq.annotations.size();i++)
			{
			//Create the annotation
			SeqAnnotation annot=seq.annotations.get(i);
			QGraphicsCircSeqAnnotationItem it=new QGraphicsCircSeqAnnotationItem();
			annotlist[i]=it;
			it.view=this;
			it.annot=annot;
			it.seq=seq;
			it.height=0;

			//Try to find an overlapping on in the past. TODO circular ones
			int thish=0;
			for(int j=0;j<i;)
				{
				if(it.isOverlapping(annotlist[j]))
					{
					//TODO2 - on the lower half it makes sense to turn the text
					thish++;
					it.height=thish;
					j=0;
					}
				else
					j++;
				}

			//Place the annotation
			it.height=thish;
			scene().addItem(it);
			}

		}
	
	
	
	

	public void setViewCenter(QPointF centerPoint)
		{
		// Get the rectangle of the visible area in scene coords
		QRectF visibleArea = mapToScene(rect()).boundingRect();

		// Get the scene area
		QRectF sceneBounds = sceneRect();

		double boundX = sceneBounds.left()+visibleArea.width()/2.0;
		double boundY = sceneBounds.top()+visibleArea.height()/2.0;
		double boundWidth = sceneBounds.width()-2.0*boundX;
		double boundHeight = sceneBounds.height()-2.0*boundY;

		// The max boundary that the centerPoint can be to
		QRectF bounds = new QRectF(boundX, boundY, boundWidth, boundHeight);

		if (bounds.contains(centerPoint))
			{
			// We are within the bounds
			currentViewCenter = centerPoint;
			}
		else
			{
			System.out.println("outside bounds");
			// We need to clamp or use the center of the screen
			if (visibleArea.contains(sceneBounds))
				{
				// Use the center of scene ie. we can see the whole scene
				currentViewCenter = sceneBounds.center();
				}
			else
				{
				currentViewCenter = centerPoint;

				// We need to clamp the center. The centerPoint is too large
				if (centerPoint.x()>bounds.x()+bounds.width())
					currentViewCenter.setX(bounds.x()+bounds.width());
				else if (centerPoint.x()<bounds.x())
					currentViewCenter.setX(bounds.x());

				if (centerPoint.y()>bounds.y()+bounds.height())
					currentViewCenter.setY(bounds.y()+bounds.height());
				else if (centerPoint.y()<bounds.y())
					currentViewCenter.setY(bounds.y());
				}
			}

		// Update the scrollbars
		centerOn(currentViewCenter);
		}

	
	double getAngle(QMouseEvent event)
		{
		QPointF p = mapToScene(event.pos());
		double angle=Math.atan2(p.y(), p.x());
		angle-=circPan*2*Math.PI;
		while(angle<0)
			angle+=Math.PI*2;
		return angle;
		}
	
	/**
	 * Handle mouse button pressed events 
	 */
	public void mousePressEvent(QMouseEvent event)
		{
		if(event.button()==MouseButton.LeftButton)
			{
			isSelecting=true;
			double angle=getAngle(event);
			
			selection=new SequenceRange();
			selection.from=selection.to=(int)(seq.getLength()*angle/(Math.PI*2));
			signalUpdated.emit(selection);

			updateSelectionGraphics();
			}
		}

	/**
	 * Handle mouse button release
	 */
	public void mouseReleaseEvent(QMouseEvent event)
		{
		isSelecting=false;
		}



	/**
	 * Handle mouse move events
	 */
	public void mouseMoveEvent(QMouseEvent event)
		{
		if(isSelecting)
			{
			double angle=getAngle(event);

			selection.to=(int)(seq.getLength()*(angle)/(Math.PI*2));
			signalUpdated.emit(selection);

			updateSelectionGraphics();
			}
		}
	

	
	/**
	 * Handle resize events
	 */
	public void resizeEvent(QResizeEvent event)
		{
		// Get the rectangle of the visible area in scene coords
		QRectF visibleArea = mapToScene(rect()).boundingRect();
		setViewCenter(visibleArea.center());

		// Call the subclass resize so the scrollbars are updated correctly
		super.resizeEvent(event);
		}


	}
