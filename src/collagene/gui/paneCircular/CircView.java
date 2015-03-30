package collagene.gui.paneCircular;



import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import collagene.gui.ProjectWindow;
import collagene.gui.paneRestriction.EventSelectedRestrictionEnzyme;
import collagene.gui.qt.QTutil;
import collagene.gui.sequenceWindow.EventSelectedAnnotation;
import collagene.gui.sequenceWindow.MenuAnnotation;
import collagene.gui.sequenceWindow.SeqViewSettingsMenu;
import collagene.primer.Primer;
import collagene.restrictionEnzyme.RestrictionEnzyme;
import collagene.seq.AnnotatedSequence;
import collagene.seq.Orientation;
import collagene.seq.RestrictionSite;
import collagene.seq.SeqAnnotation;
import collagene.seq.SequenceRange;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.QTimer;
import com.trolltech.qt.core.Qt.MouseButton;
import com.trolltech.qt.core.Qt.ScrollBarPolicy;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QContextMenuEvent;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QFontMetricsF;
import com.trolltech.qt.gui.QGraphicsEllipseItem;
import com.trolltech.qt.gui.QGraphicsLineItem;
import com.trolltech.qt.gui.QGraphicsPathItem;
import com.trolltech.qt.gui.QGraphicsScene;
import com.trolltech.qt.gui.QGraphicsTextItem;
import com.trolltech.qt.gui.QGraphicsView;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QPainterPath;
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
	
	double plasmidRadius=100000;
	private QTimer timerAnimation=new QTimer();
	
	public AnnotatedSequence seq=new AnnotatedSequence();
	private HashMap<SeqAnnotation, QGraphicsCircSeqAnnotationItem> mapAnnot=new HashMap<SeqAnnotation, QGraphicsCircSeqAnnotationItem>();

	public abstract class EmittedText
		{
		public QRectF rect;
		public String txt;
		public QColor col;
		public abstract void leftclick(QMouseEvent event);
		}
	
	private QFont emittedTextFont=new QFont();
	private ArrayList<EmittedText> allEmittedText=new ArrayList<EmittedText>();
	private ArrayList<EmittedText> emittedText=new ArrayList<EmittedText>();
	private double emittedAngle;
	private ArrayList<QRectF> emittedTextRegions=new ArrayList<QRectF>();

	private boolean isSelecting=false;

	
	public double circPan=0; //From 0 to 1
	public double circZoom=1;  //1 means to fit it all into the window

	private double targetCircPan=circPan;
	private double targetCircZoom=circZoom;
	
	protected QPointF currentViewCenter = new QPointF();

	
	private Collection<QGraphicsEllipseItem> selectionItems=new LinkedList<QGraphicsEllipseItem>();
	private SequenceRange selection=null;

	public SeqViewSettingsMenu settings=new SeqViewSettingsMenu();
	
	
	public QSignalEmitter.Signal1<Object> signalUpdated=new Signal1<Object>();

	EventSelectedRestrictionEnzyme selectedEnz=new EventSelectedRestrictionEnzyme();
	private ProjectWindow pw;

	
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
			double r=plasmidRadius+plasmidRadius*0.1/circZoom;
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
	public CircView(ProjectWindow pw)
		{
		this.pw=pw;
		setBackgroundBrush(new QBrush(QColor.fromRgb(255,255,255)));
		
    setHorizontalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOff);  //is there a newer version??
    setVerticalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOff);

		setMouseTracking(true);
		setEnabled(true);

		setSizePolicy(QSizePolicy.Policy.Expanding,QSizePolicy.Policy.Expanding);

		setSceneRect(-10000000, -10000000, 10000000*2, 10000000*2);
		setScene(new QGraphicsScene());
		setCameraFromCirc();
		
		timerAnimation.setSingleShot(false);
		timerAnimation.setInterval(1000/30);
		timerAnimation.timeout.connect(this,"timertimeout()");
		}
	
	
	public void setCameraFromCirc()
		{
		//Set the right scale
		double sWidth=width()/(double)(plasmidRadius*2);
		double sHeight=height()/(double)(plasmidRadius*2);
		double scale=circZoom*Math.min(sWidth, sHeight);
		QTransform trans=QTransform.fromScale(scale,scale);
		setTransform(trans,false);
	
		double dy=Math.max(0,(plasmidRadius+plasmidRadius*0.5/circZoom)*scale-height()/2)/scale;
		double y=-dy;
		
		//and here another thing. when getting the line to mid, should keep it in the mid
		
		//Center on mid point
		setViewCenter(new QPointF(0,  y));
		
		buildSceneFromDoc();
		}

	/**
	 * Get a bounding box for a text item (taking position into account)
	 */
	public static QRectF textBR(QGraphicsTextItem ti)
		{
		QFontMetricsF m=new QFontMetricsF(ti.font());
		double w=m.width(ti.toPlainText())*1.1;  //not sure this is a good idea!
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
			ArrayList<String> partialStrings=new ArrayList<String>(emittedText.size()+1);
			partialStrings.add("");
			StringBuilder tottext=new StringBuilder();
			StringBuilder tottext2=new StringBuilder();
			for(EmittedText t:emittedText)
				{
				if(tottext.length()>0)
					{
					tottext.append(",");
					tottext2.append(",");
					}
				tottext.append("<font color=\""+t.col.name()+"\">"+t.txt+"</font>");
				tottext2.append(t.txt);//.replace("-", " ")); //work around for qt bug?
				partialStrings.add(tottext2.toString());
				}
			
			QPen pen=new QPen();
			pen.setColor(new QColor(0,0,0));

			double rad=plasmidRadius+plasmidRadius*0.05/circZoom;
			emittedAngle=emittedAngle-(int)(emittedAngle);

			QGraphicsTextItem itemText=new QGraphicsTextItem();
			itemText.setFont(emittedTextFont);
			itemText.setHtml(tottext.toString());
			scene().addItem(itemText);

			QPointF textPos=new QPointF(rad*Math.cos(emittedAngle*2*Math.PI), rad*Math.sin(emittedAngle*2*Math.PI));
			QPointF textHandlePos=new QPointF(textPos.x(),textPos.y());
			if(emittedAngle>0.25 && emittedAngle<0.75)
				textPos.setX(textPos.x()-itemText.boundingRect().width()+3);
			else
				textPos.setX(textPos.x()-3);
			textPos.setY(textPos.y()-itemText.boundingRect().height()/2);
			itemText.setPos(textPos);

			//Find a suitable location for the text
			textpositions: for(;;)
				{
				QRectF cur=textBR(itemText);
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
						itemText.setPos(textPos);
						continue textpositions;
						}
					}
				break;
				}
			emittedTextRegions.add(textBR(itemText));

			//Put text on screen
			QGraphicsLineItem itemS=new QGraphicsLineItem();
			itemS.setPen(pen);
			itemS.setLine(
					plasmidRadius*Math.cos(emittedAngle*2*Math.PI),   plasmidRadius*Math.sin(emittedAngle*2*Math.PI),
					textHandlePos.x(),textHandlePos.y());
			scene().addItem(itemS);
			
			//Figure out bounding boxes for each item
			QFontMetricsF m=new QFontMetricsF(itemText.font());
			double textH=m.height();
			ArrayList<Double> tx=new ArrayList<Double>(partialStrings.size());
			for(int i=0;i<partialStrings.size();i++)
				tx.add(m.boundingRect(partialStrings.get(i)).width());
			for(int i=0;i<emittedText.size();i++)
				{
				double w=tx.get(i+1) - tx.get(i);
				emittedText.get(i).rect=new QRectF(itemText.x()+tx.get(i), itemText.y(), w, textH);
				}
			
			emittedText.clear();
			}
		}
	
	/**
	 * Add annotation text at given angle. Emit text if it is time
	 */
	private void addAnnotationText(double ang, final RestrictionEnzyme enz)
		{
		double minang=0.01/circZoom;
		
		EmittedText txt=new EmittedText(){
			public void leftclick(QMouseEvent e)
				{
				EventSelectedRestrictionEnzyme sel=new EventSelectedRestrictionEnzyme();
				if(QTutil.addingKey(e))
					{
					selectedEnz.add(enz);
					sel.enzymes.addAll(selectedEnz.enzymes);
					}
				else
					sel.add(enz);
				signalUpdated.emit(sel);
				}
		};
		txt.txt=enz.name;
		if(selectedEnz.enzymes.contains(enz))
			txt.col=QColor.fromRgb(255,0,0);
		else
			txt.col=QColor.fromRgb(0,0,0);
		
		if(Math.abs(emittedAngle-ang)>minang || emittedText.size()>4)
			emitAnnotationText();
		
		if(emittedText.isEmpty())
			emittedAngle=ang;
		emittedText.add(txt);
		allEmittedText.add(txt);
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

		allEmittedText.clear();
		emittedTextFont.setPointSizeF(plasmidRadius*0.04/circZoom);
		emittedTextFont.setFamily("Arial");

		//Note - it is good to have a separate scene builder class, for making PDFs
		
		//Add the plasmid
		QGraphicsCircPlasmidSequence ic=new QGraphicsCircPlasmidSequence();
		ic.view=this;
		scene.addItem(ic);
		
		//Add all annotations
		addsceneAnnotation();
		
		//Add all primers
		QPen penPrimer=new QPen();
		penPrimer.setColor(QColor.blue);
		double fzoom=getFeatureZoom();
		for(Primer p:seq.primers)
			{
			double pdist=plasmidRadius*0.015/fzoom;
			double primerRadius=plasmidRadius;
			if(p.orientation==Orientation.FORWARD)
				primerRadius+=pdist;
			else
				primerRadius-=pdist;
			
			SequenceRange r=p.getRange().toNormalizedRange(seq);
			
			double rfrom=r.from/(double)seq.getLength();
			double rto=  r.to  /(double)seq.getLength();
			
			int numstep=10;
			QPainterPath poly=new QPainterPath();
			//Reverse arrow
			if(p.orientation==Orientation.REVERSE)
				{
				double primerRadius2=primerRadius-pdist;
				double ang=rfrom+circPan+pdist/(primerRadius2*2*Math.PI);
				double x=Math.cos(Math.PI*2*ang)*primerRadius2;
				double y=Math.sin(Math.PI*2*ang)*primerRadius2;
				poly.moveTo(x, y);
				}
			else
				{
				double ang=rfrom+circPan;
				double x=Math.cos(Math.PI*2*ang)*primerRadius;
				double y=Math.sin(Math.PI*2*ang)*primerRadius;
				poly.moveTo(x, y);
				}
			//Midsegments
			for(int i=0;i<=numstep;i++)
				{
				double ang=(rto-rfrom)*i/(double)numstep+circPan+rfrom;
				double x=Math.cos(Math.PI*2*ang)*primerRadius;
				double y=Math.sin(Math.PI*2*ang)*primerRadius;
				poly.lineTo(x, y);
				}
			//Forward arrow
			if(p.orientation==Orientation.FORWARD)
				{
				double primerRadius2=primerRadius+pdist;
				double ang=rto+circPan-pdist/(primerRadius2*2*Math.PI);
				double x=Math.cos(Math.PI*2*ang)*primerRadius2;
				double y=Math.sin(Math.PI*2*ang)*primerRadius2;
				poly.lineTo(x, y);
				}
			QGraphicsPathItem item=new QGraphicsPathItem();
			item.setPath(poly);
			item.setPen(penPrimer);
			scene.addItem(item);
			}
			
		
		
		//Find restriction sites to draw
		ArrayList<RestrictionSite> totSites=new ArrayList<RestrictionSite>(200);
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
		//Now place them all
		mapAnnot.clear();
		QGraphicsCircSeqAnnotationItem[] annotlist=new QGraphicsCircSeqAnnotationItem[seq.annotations.size()];
		for(int i=0;i<seq.annotations.size();i++)
			{
			//Create the annotation
			SeqAnnotation annot=seq.annotations.get(i);
			QGraphicsCircSeqAnnotationItem it=new QGraphicsCircSeqAnnotationItem();
			annotlist[i]=it;
			mapAnnot.put(annot, it);
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

	
	/**
	 * Get angle at mouse position, 0..2PI
	 */
	double getAngle(QMouseEvent event)
		{
		QPointF p = mapToScene(event.pos());
		return getAngle(p);
		}
	
	/**
	 * Get angle at scene position, 0..2PI
	 */
	double getAngle(QPointF p)
		{
		double angle=Math.atan2(p.y(), p.x());
		angle-=circPan*2*Math.PI;
		while(angle<0)
			angle+=Math.PI*2;
		return angle;
		}

	/**
	 * Get annotation at position
	 */
	public SeqAnnotation getAnnotationAt(QPointF p)
		{
		for(SeqAnnotation annot:mapAnnot.keySet())
			{
			QGraphicsCircSeqAnnotationItem it=mapAnnot.get(annot);
			if(it.pointWithin(p))
				return annot;
			}
		return null;
		}

	
	/**
	 * Get emitted text at position
	 */
	public EmittedText getEmittedTextAt(QPointF p)
		{
		for(EmittedText e:allEmittedText)
			if(e.rect.contains(p))
				return e;
		return null;
		}
	
	
	/**
	 * Handle mouse button pressed events 
	 */
	public void mousePressEvent(QMouseEvent event)
		{
		if(event.button()==MouseButton.LeftButton)
			{
			QPointF p = mapToScene(event.pos());
			SeqAnnotation annot=getAnnotationAt(p);
			EmittedText et=getEmittedTextAt(p);
			
			if(annot!=null)
				signalUpdated.emit(new EventSelectedAnnotation(annot));
			else if(et!=null)
				{
				et.leftclick(event);
				}
			else
				{
				isSelecting=true;
				double angle=getAngle(event);
				
				selection=new SequenceRange();
				selection.from=selection.to=(int)(seq.getLength()*angle/(Math.PI*2));
				signalUpdated.emit(selection);

				updateSelectionGraphics();
				}
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


	public double getFeatureZoom()
		{
		return Math.max(1.5,circZoom);
		}


	public SequenceRange getSelection()
		{
		return selection;
		}


	public AnnotatedSequence getSequence()
		{
		return seq;
		}


	public void moveto(double pan, double zoom)
		{
		targetCircPan=pan;
		targetCircZoom=zoom;
		timerAnimation.start();
		setCameraFromCirc();
		}

	public void timertimeout()
		{
		boolean happyPan=false, happyZoom=false;
		
		//Handle panning
		double dpan=(targetCircPan-circPan)*0.15;
		double minpan=0.001;
		if(Math.abs(dpan)<minpan)
			dpan=Math.signum(dpan)*minpan;
		double newpan=circPan+dpan;
		if(Math.abs(targetCircPan-newpan)<minpan)
			{
			circPan=targetCircPan;
			happyPan=true;
			}
		else
			circPan=newpan;
		
		//Handle zooming
		double dzoom=(targetCircZoom-circZoom)*0.1;
		double minzoom=0.02;
		if(Math.abs(dzoom)<minzoom)
			dzoom=Math.signum(dzoom)*minzoom;
		double newZoom=circZoom+dzoom;
		if(Math.abs(targetCircZoom-newZoom)<minzoom)
			{
			circZoom=targetCircZoom;
			happyZoom=true;
			}
		else
			circZoom=newZoom;
		
		setCameraFromCirc();

		if(happyPan && happyZoom)
			timerAnimation.stop();
		}


	public void movetoinstantaneous(double pan,
			double zoom)
		{
		targetCircPan=circPan=pan;
		targetCircZoom=circZoom=zoom;
		}



	/**
	 * Create a context menu if right-clicking
	 */
	@Override
	protected void contextMenuEvent(QContextMenuEvent event)
		{
		QPointF pos=mapToScene(event.pos());
		SeqAnnotation curAnnotation=getAnnotationAt(pos);
		if(curAnnotation!=null)
			{
			MenuAnnotation mPopup=new MenuAnnotation(pw, seq, curAnnotation);
			mPopup.exec(event.globalPos());
			}
		}
	}
