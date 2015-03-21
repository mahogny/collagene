package gui.paneLinear;

import gui.ProjectWindow;
import gui.paneCircular.CircView;
import gui.paneRestriction.EventSelectedRestrictionEnzyme;
import gui.primer.MenuPrimer;
import gui.sequenceWindow.EventSelectedAnnotation;
import gui.sequenceWindow.MenuAnnotation;
import gui.sequenceWindow.SeqViewSettingsMenu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import primer.Primer;
import restrictionEnzyme.RestrictionEnzyme;
import seq.AnnotatedSequence;
import seq.Orientation;
import seq.RestrictionSite;
import seq.SequenceRange;
import seq.SeqAnnotation;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.core.QPointF;
import com.trolltech.qt.core.QRectF;
import com.trolltech.qt.core.QTimer;
import com.trolltech.qt.core.Qt.BrushStyle;
import com.trolltech.qt.core.Qt.MouseButton;
import com.trolltech.qt.core.Qt.ScrollBarPolicy;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QContextMenuEvent;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QGraphicsLineItem;
import com.trolltech.qt.gui.QGraphicsPathItem;
import com.trolltech.qt.gui.QGraphicsPolygonItem;
import com.trolltech.qt.gui.QGraphicsRectItem;
import com.trolltech.qt.gui.QGraphicsScene;
import com.trolltech.qt.gui.QGraphicsTextItem;
import com.trolltech.qt.gui.QGraphicsView;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QPainterPath;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QPolygonF;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QSizePolicy;

/**
 * Display sequence, broken over several lines
 * 
 * @author Johan Henriksson
 *
 */
public class ViewLinearSequence extends QGraphicsView
	{
	public AnnotatedSequence seq=new AnnotatedSequence();
	
	public int widthInChars=-1; //-1 means to choose automatically. 

	private QTimer timerAnimation=new QTimer();
	
	private HashMap<QRectF,SeqAnnotation> mapAnnotations=new HashMap<QRectF, SeqAnnotation>();
	
	public SeqViewSettingsMenu settings=new SeqViewSettingsMenu();

	private HashMap<RestrictionSite, QRectF> revsitePosition=new HashMap<RestrictionSite, QRectF>();
	private HashMap<Primer, QRectF> primerPosition=new HashMap<Primer, QRectF>();

	public QSignalEmitter.Signal1<Object> signalUpdated=new Signal1<Object>();

	private Collection<Object> selectionItems=new LinkedList<Object>();
	private SequenceRange selection=null;
	private boolean isSelecting=false;

	QFont fontSequence=new QFont();
	private ArrayList<Integer> sequenceLineY=new ArrayList<Integer>();
	int charsPerLine;
	public double charWidth=10;
	public double charHeight=17;
	
	private RestrictionSite hoveringRestrictionSite=null;

	EventSelectedRestrictionEnzyme selectedEnz=new EventSelectedRestrictionEnzyme();

	public boolean showProteinTranslation=true;

	public boolean showPositionRuler=true;

	public int currentReadingFrame=0;

	private ProjectWindow w;


	/**
	 * Set current sequence
	 */
	public void setSequence(AnnotatedSequence seq)
		{
		this.seq=seq;
		buildSceneFromDoc();
		}


	/**
	 * Get current selection
	 */
	public SequenceRange getSelection()
		{
		return selection;
		}
	
	/**
	 * Set current selection
	 */
	public void setSelection(SequenceRange s)
		{
		selection=s;
		if(!isSelecting && selection!=null)
			{
			//Compute y-bounds of selected region
			int lineFrom=selection.from/charsPerLine;
			if(lineFrom<0)
				lineFrom=0;
			else if(lineFrom>=sequenceLineY.size())
				lineFrom=sequenceLineY.size()-1;
			lineFrom=sequenceLineY.get(lineFrom);
			
			int lineTo=selection.to/charsPerLine;
			if(lineTo<0)
				lineTo=0;
			else if(lineTo>=sequenceLineY.size())
				lineTo=sequenceLineY.size()-1;
			lineTo=sequenceLineY.get(lineTo);

			//Check if current view covers selection
			QRectF currentSceneRect = mapToScene(rect()).boundingRect();
			boolean currentlySeesAnnotation=false;
			if(lineFrom<=lineTo)
				{
				//Continuous block of selection
				QRectF frect=new QRectF(0, lineFrom, width(), lineTo-lineFrom);
				if(frect.intersects(currentSceneRect))
					currentlySeesAnnotation=true;
					moveToPosition(lineFrom);
				}
			else
				{
				//Selection goes around the boundary
				QRectF frect1=new QRectF(0, 0, width(), lineTo);
				QRectF frect2=new QRectF(0, lineFrom, width(), sequenceLineY.get(sequenceLineY.size()-1));
				if(frect1.intersects(currentSceneRect) || frect2.intersects(currentSceneRect))
					currentlySeesAnnotation=true;
				}
			
			//Move to beginning of selection if none of it is in the view currently
			if(!currentlySeesAnnotation)
				moveToPosition(lineFrom);
			}
		updateSelectionGraphics();
		}
	
	
	
	
	/**
	 * Constructor
	 */
	public ViewLinearSequence(ProjectWindow w)
		{
		this.w=w;
		setBackgroundBrush(new QBrush(QColor.fromRgb(255,255,255)));
		
    setHorizontalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOff);  //is there a newer version??
    setVerticalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOn);

		setMouseTracking(true);
		setEnabled(true);

		setSizePolicy(QSizePolicy.Policy.Expanding,QSizePolicy.Policy.Expanding);

		charsPerLine=80; //not proper
		
		setScene(new QGraphicsScene());
		
		buildSceneFromDoc();
		
		timerAnimation.timeout.connect(this,"timermove()");
		timerAnimation.setInterval(1000/30);
		timerAnimation.setSingleShot(false);
		}

	/**
	 * Event: animation tick
	 */
	public void timermove()
		{
		QRectF currentSceneRect = mapToScene(rect()).boundingRect();
		double currentY=currentSceneRect.top()+currentSceneRect.height()/2;

		double dy=(currentMovetopos-currentY)*0.2;

		double minspeed=3;
		if(Math.abs(dy)<minspeed)
			dy=Math.signum(dy)*minspeed;
		
		double newy=currentY+dy;
		if(Math.abs(newy-currentMovetopos)<minspeed)
			{
			newy=currentMovetopos;
			timerAnimation.stop();
			}

		centerOn(width()/2, newy);
		
		//Out of bounds check
		QRectF newSceneRect = mapToScene(rect()).boundingRect();
		if(newSceneRect.top()==currentSceneRect.top())
			timerAnimation.stop();
		}

	
	private double currentMovetopos=0;
	
	/**
	 * Scroll to a certain position, with animation
	 */
	public void moveToPosition(double ypos)
		{
		currentMovetopos=ypos;
		timerAnimation.start();
		}
	

	
	/**
	 * Build the scene from the document. This is equivalent to repainting
	 */
	public void buildSceneFromDoc()
		{
		QGraphicsScene scene=scene();
		scene.clear();
		mapAnnotations.clear();
		
		//Note - it is good to have a separate scene builder class, for making PDFs
				
		QPen penSequence=new QPen();
		penSequence.setColor(new QColor(100,100,100));
		penSequence.setWidth(2);
		
		fontSequence.setFamily("Courier");
		fontSequence.setPointSizeF(charWidth);
		charHeight=fontSequence.pointSizeF()*2;
		
		QFont fontAnnotation=new QFont();
		fontAnnotation.setPointSizeF(10);

		QFont fontRestriction=new QFont();
		fontRestriction.setPointSize(10);

		QFont fontPrimer=new QFont();
		fontPrimer.setPointSize(10);

		//Update how many chars fit on each line
		charsPerLine=widthInChars;
		if(charsPerLine==-1)
			charsPerLine=80; //TODO try and figure out optimum
		
		setMinimumWidth((int)mapCharToX(charsPerLine)+100);    //TODO need to be redone once in a while!

		//Render each line. Remember position
		sequenceLineY.clear();
		int currentY=10;
		revsitePosition.clear();
		primerPosition.clear();
		for(int curline=0;curline<seq.getLength()/charsPerLine+1;curline++)
			{
			int cposLeft=curline*charsPerLine;
			int cposRight=(curline+1)*charsPerLine;
			
			//////////////////////////////////////////////// Place enzymes
			
			//Find all relevant restriction enzymes, and sort by position
			LinkedList<RestrictionSite> rsites=new LinkedList<RestrictionSite>();
			for(RestrictionEnzyme enz:seq.restrictionSites.keySet())
				{
				Collection<RestrictionSite> sites=seq.restrictionSites.get(enz);
				if(settings.allowsRestrictionSiteCount(enz,sites.size()) || selectedEnz.enzymes.contains(enz))
					{
					for(RestrictionSite site:sites)
						if(site.cuttingUpperPos>=cposLeft && site.cuttingUpperPos<=cposRight)
							rsites.add(site);
					}
				}
			Collections.sort(rsites,new Comparator<RestrictionSite>()
				{
				public int compare(RestrictionSite o1, RestrictionSite o2)
					{
					return Double.compare(o1.cuttingUpperPos, o2.cuttingUpperPos);
					}
				});
			
			//Render the enzymes
			int siteHeightPx=20;
			int maxSiteHeight=0;
			int siteDx=-2;
			HashMap<RestrictionSite, QRectF> revsitePositionPerLine=new HashMap<RestrictionSite, QRectF>();
			for(RestrictionSite site:rsites)
				{
				QGraphicsTextItem it=new QGraphicsTextItem();
				it.setFont(fontRestriction);
				if(selectedEnz.enzymes.contains(site.enzyme))
					it.setDefaultTextColor(QColor.fromRgb(255,0,0));
				else
					it.setDefaultTextColor(QColor.fromRgb(0,0,0));
				it.setPlainText(site.enzyme.name);
				it.setPos(mapCharToX(site.cuttingUpperPos-cposLeft)+siteDx, currentY);
				
				//If it overlaps with other sites, move it down
				QRectF thisbr=CircView.textBR(it);
				thisbr.adjust(0, 0, 40, 0);
				int thish=1;
				sitesretry: for(;;)
					{
					for(QRectF otherbr:revsitePositionPerLine.values())
						{
						if(otherbr.intersects(thisbr))
							{
							thish++;
							it.setY(it.y()+siteHeightPx);
							thisbr=CircView.textBR(it);
							thisbr.adjust(0, 0, 40, 0);
							continue sitesretry;
							}
						}
					break;
					}
				scene.addItem(it);
				revsitePosition.put(site,thisbr);
				revsitePositionPerLine.put(site,thisbr);
				maxSiteHeight=Math.max(maxSiteHeight,thish);
				}

			//Allocate space for the restriction sites
			currentY+=maxSiteHeight*siteHeightPx+2;			
			sequenceLineY.add(currentY); //reference to sequence line
			
			//Draw arrows down onto sequence
			for(QRectF r:revsitePositionPerLine.values())
				{
				QGraphicsLineItem li=new QGraphicsLineItem();
				li.setPen(penSequence);
				li.setLine(r.left()-siteDx,r.bottom(),r.left()-siteDx,currentY);
				scene.addItem(li);
				}

			//Draw the sequence text
			QGraphicsLinSequenceItem titem=new QGraphicsLinSequenceItem();
			titem.curline=curline;
			titem.currentY=currentY;
			titem.seq=seq;
			titem.view=this;
			scene.addItem(titem);
			currentY+=titem.boundingRect().height();

			//////////////////////////////////////////////// Place primers
			int primerh=0;
			LinkedList<QRectF> prevprimerplaced=new LinkedList<QRectF>();
			double oneprimerh=charHeight;
			for(Primer p:seq.primers)
				{
				if(p.targetPosition>=cposLeft && p.targetPosition<=cposRight)
					{
					int basey=currentY;

					double x1;
					double x2=mapCharToX(p.targetPosition-cposLeft);
					double x3;
					int arrowsize=5;
					double texty=basey-3;
					if(p.orientation==Orientation.FORWARD)
						{
						x1=x2-charWidth*p.sequence.length();
						x3=x2-arrowsize;
						}
					else //REVERSE
						{
						x1=x2+charWidth*p.sequence.length();
						x3=x2+arrowsize;
						}
			
					
					//Initial placement of text
					QGraphicsTextItem ptext=new QGraphicsTextItem();
					ptext.setPlainText(p.name);
					ptext.setFont(fontRestriction);
					if(p.orientation==Orientation.FORWARD)
						ptext.setPos(x2-ptext.boundingRect().width()-arrowsize,texty);
					else
						ptext.setPos(x2+arrowsize,texty);

					//Find suitable height for primer
					QRectF thisbb=CircView.textBR(ptext);
					double minx=Math.min(x1, x2);
					double maxx=Math.max(x1, x2);
					if(thisbb.left()>minx)
						thisbb.setLeft(minx);
					if(thisbb.right()<maxx)
						thisbb.setRight(maxx);
					int curprimerh=1;
					retryplace: for(;;)
						{
						for(QRectF oldr:prevprimerplaced)
							{
							if(oldr.intersects(thisbb))
								{
								thisbb.adjust(0, oneprimerh, 0, oneprimerh);
								texty+=oneprimerh;
								ptext.setY(texty);
								basey+=oneprimerh;
								curprimerh+=1;
								continue retryplace;
								}
							}
						prevprimerplaced.add(thisbb);
						break;
						}
					if(curprimerh>primerh)
						primerh=curprimerh;
					primerPosition.put(p, thisbb);
					
					//Add arrow
					QPainterPath pp=new QPainterPath();
					pp.moveTo(x1, basey);
					pp.lineTo(x2, basey);
					pp.lineTo(x3, basey+arrowsize);
					QGraphicsPathItem path=new QGraphicsPathItem();
					path.setPath(pp);			
					scene.addItem(ptext);
					scene.addItem(path);
					}
				}
			currentY+=primerh*oneprimerh+2;
			
/*
			QGraphicsLineItem li=new QGraphicsLineItem();
			li.setLine(0, currentY, 100, currentY);
			li.setPen(penSequence);
			scene.addItem(li);*/

			//////////////////////////////////////////////// Draw annotation
			int currentAnnotationHeight=-1;
			int oneannoth=20;
			for(SeqAnnotation annot:seq.annotations)
				{
				//Check if this feature is in range
				if(annot.getTo()>cposLeft && annot.getFrom()<cposRight)
					{
					//Annotation should go beneath sequence, above position line
					currentAnnotationHeight++;
//					if(currentAnnotationHeight==-1)
//						currentAnnotationHeight=0;  //write a better allocator!
					int thisannoth=currentAnnotationHeight;
					
					
					int basey=currentY+thisannoth*oneannoth;
					int polyyup=basey;
					int polyydown=basey+18;
					int polyymid=basey+9;
					
					QPolygonF poly=new QPolygonF();
					
					double frompos;
					if(annot.getFrom()>=cposLeft)
						{
						//Annotation starts here
						frompos=mapCharToX(annot.getFrom()-cposLeft);
						poly.add(frompos, polyydown);
						if(annot.orientation==Orientation.REVERSE)
							poly.add(mapCharToX(annot.getFrom()-1-cposLeft)+5, polyymid);
						poly.add(frompos, polyyup); 
						}
					else
						{
						//Annotation continued from before
						frompos=mapCharToX(0);
						poly.add(frompos, polyydown); 
						poly.add(frompos-3, polyyup+7); 
						poly.add(frompos+3, polyyup+3); 
						poly.add(frompos, polyyup); 
						}

					double topos;
					if(annot.getTo()<=cposRight)
						{
						//Annotation ends here
						topos=mapCharToX(annot.getTo()+1-cposLeft);
						poly.add(topos, polyyup);  
						if(annot.orientation==Orientation.FORWARD)
							poly.add(mapCharToX(annot.getTo()+1-cposLeft)+5, polyymid);
						poly.add(mapCharToX(annot.getTo()+1-cposLeft), polyydown); 
						}
					else
						{
						//Continues on next line
						topos=mapCharToX(charsPerLine+1);
						poly.add(topos, polyyup); 
						poly.add(topos+3, polyyup+3); 
						poly.add(topos-3, polyyup+7); 
						poly.add(topos, polyydown); 
						}
					
					QRectF newrect=new QRectF();
					newrect.setRect(frompos, polyyup, topos-frompos, polyydown-polyyup);
					mapAnnotations.put(newrect, annot);

					QPen pen=new QPen();
					pen.setColor(QColor.fromRgb(0,0,0));
					
					QBrush brush=new QBrush();
					brush.setStyle(BrushStyle.SolidPattern);
					brush.setColor(QColor.fromRgb(annot.color.r, annot.color.g, annot.color.b));
					
					QGraphicsPolygonItem pi=new QGraphicsPolygonItem();
					pi.setPolygon(poly);
					pi.setPen(pen);
					pi.setBrush(brush);
					scene.addItem(pi);
					
					QGraphicsTextItem ti=new QGraphicsTextItem();
					ti.setPlainText(annot.name);
					ti.setPos(frompos+2, polyyup-2);
					scene.addItem(ti);
					}
				}
			currentY+=(currentAnnotationHeight+1)*oneannoth;

			//Add position item
			QGraphicsLinSeqPositionItem pitem=new QGraphicsLinSeqPositionItem();
			pitem.curline=curline;
			pitem.currentY=currentY;
			pitem.seq=seq;
			pitem.view=this;
			scene.addItem(pitem);
			currentY+=pitem.boundingRect().height();

			
			//Move to next line
			currentY+=10;
			}
		
		//Update view size
		setSceneRect(0, 0, mapCharToX(charsPerLine)+30, currentY+50);
		
		selectionItems.clear();
		updateSelectionGraphics();
		}	
	
	
	/**
	 * Update the graphics for the current selection
	 */
	private void updateSelectionGraphics()
		{
		//Remove previous selection
		for(Object i:selectionItems)
			if(i instanceof QGraphicsRectItem)
				scene().removeItem((QGraphicsRectItem)i);
			else if(i instanceof QGraphicsLineItem)
				scene().removeItem((QGraphicsLineItem)i);
		selectionItems.clear();

		//Draw selection
		if(selection!=null)
			{
			SequenceRange selection=this.selection.toNormalizedRange(seq);
			int selectFrom=selection.from;
			int selectTo=selection.to;

			QPen penSelect=new QPen();
			penSelect.setColor(new QColor(200,100,200));
			penSelect.setWidth(2);

			//this could be made a lot more efficient
			for(int curline=0;curline<sequenceLineY.size();curline++)
				{
				int cposLeft=curline*charsPerLine;
				int cposRight=(curline+1)*charsPerLine;

				int lyUpper=sequenceLineY.get(curline)+3; 
				int lyLower=lyUpper+3;

				//From-|
				if(cposLeft<=selectFrom && cposRight>selectFrom)
					{
					double x1=mapCharToX(selectFrom-cposLeft);
					QGraphicsLineItem li=new QGraphicsLineItem();
					li.setPen(penSelect);
					li.setLine(x1, lyUpper, x1, lyLower);
					li.setZValue(10000);
					scene().addItem(li);
					selectionItems.add(li);
					}
				//To-|
				if(cposLeft<=selectTo && cposRight>selectTo)
					{
					double x1=mapCharToX(selectTo-cposLeft);
					QGraphicsLineItem li=new QGraphicsLineItem();
					li.setPen(penSelect);
					li.setLine(x1, lyUpper, x1, lyLower);
					li.setZValue(10000);
					scene().addItem(li);
					selectionItems.add(li);
					}

				//Horizontal line
				if(selection.from<=selection.to)
					{
					//There can only be one line here
					if(cposLeft<selectTo && cposRight>selectFrom)
						{
						//Find boundaries, and label them
						double x1,x2;
						if(selectFrom<cposLeft)
							x1=mapCharToX(0);
						else
							x1=mapCharToX(selectFrom-cposLeft);
						if(selectTo>cposRight)
							x2=mapCharToX(charsPerLine);
						else
							x2=mapCharToX(selectTo-cposLeft);
						
						QGraphicsLineItem li=new QGraphicsLineItem();
						li.setPen(penSelect);
						li.setLine(x1, lyUpper, x2, lyUpper);
						li.setZValue(10000);
						scene().addItem(li);
						selectionItems.add(li);
						}
					}
				else
					{
					int rightmax=charsPerLine;
					if(cposRight>seq.getLength())
						rightmax=seq.getLength()-cposLeft;
					//If selection is wrapped, there can be up to two lines.
					//The left horizontal line
					if(cposLeft<selectTo) 
						{
						//Find boundaries, and label them
						double x1=mapCharToX(0),x2;
						if(selectTo>cposRight)
							x2=mapCharToX(rightmax);
						else
							x2=mapCharToX(selectTo-cposLeft); 
						
						QGraphicsLineItem li=new QGraphicsLineItem();
						li.setPen(penSelect);
						li.setLine(x1, lyUpper, x2, lyUpper);
						li.setZValue(10000);
						scene().addItem(li);
						selectionItems.add(li);
						}
					//The right horizontal line
					if(cposRight>selectFrom)
						{
						//Find boundaries, and label them
						double x1,x2=mapCharToX(rightmax);
						if(selectFrom<cposLeft)
							x1=mapCharToX(0);
						else
							x1=mapCharToX(selectFrom-cposLeft);
						
						QGraphicsLineItem li=new QGraphicsLineItem();
						li.setPen(penSelect);
						li.setLine(x1, lyUpper, x2, lyUpper);
						li.setZValue(10000);
						scene().addItem(li);
						selectionItems.add(li);
						}
					}
				}
			}
		
		//Draw restriction site if hovering
		if(hoveringRestrictionSite!=null)
			{			
			QPen penRS=new QPen();
			penRS.setColor(new QColor(255,50,50));
			penRS.setWidth(2);

			//Draw upper cut position
			if(hoveringRestrictionSite.cuttingUpperPos!=null)
				{
				int lineIndex=hoveringRestrictionSite.cuttingUpperPos/charsPerLine;
				int cposLeft=lineIndex*charsPerLine;
				double localUpper=mapCharToX(hoveringRestrictionSite.cuttingUpperPos-cposLeft);
				double y1=sequenceLineY.get(lineIndex);
				double y2=y1+charHeight+4;

				QGraphicsLineItem liUpper=new QGraphicsLineItem();
				liUpper.setPen(penRS);
				liUpper.setLine(localUpper, y1, localUpper, y2);
				liUpper.setZValue(10000);
				scene().addItem(liUpper);
				selectionItems.add(liUpper);				
				
				//Draw horizontal line
				if(hoveringRestrictionSite.cuttingLowerPos!=null)
					{
					double localLower=mapCharToX(Math.max(0,Math.min(charsPerLine,hoveringRestrictionSite.cuttingLowerPos-cposLeft)));
					QGraphicsLineItem liMid=new QGraphicsLineItem();
					liMid.setPen(penRS);
					liMid.setLine(localUpper, y2, localLower, y2);
					liMid.setZValue(10000);
					scene().addItem(liMid);
					selectionItems.add(liMid);
					}
				}

			//Draw lower cut position
			if(hoveringRestrictionSite.cuttingLowerPos!=null)
				{
				int lineIndex=hoveringRestrictionSite.cuttingLowerPos/charsPerLine;
				int cposLeft=lineIndex*charsPerLine;
				double localLower=mapCharToX(hoveringRestrictionSite.cuttingLowerPos-cposLeft);
				double y1=sequenceLineY.get(lineIndex);
				double y2=y1+charHeight+4;
				double y3=y1+charHeight*2;

				QGraphicsLineItem liLower=new QGraphicsLineItem();
				liLower.setPen(penRS);
				liLower.setLine(localLower, y2, localLower, y3);
				liLower.setZValue(10000);
				scene().addItem(liLower);
				selectionItems.add(liLower);
				
				//Draw horizontal line
				if(hoveringRestrictionSite.cuttingUpperPos!=null)
					{
					double localUpper=mapCharToX(Math.max(0,Math.min(charsPerLine,hoveringRestrictionSite.cuttingUpperPos-cposLeft)));
					QGraphicsLineItem liMid=new QGraphicsLineItem();
					liMid.setPen(penRS);
					liMid.setLine(localUpper, y2, localLower, y2);
					liMid.setZValue(10000);
					scene().addItem(liMid);
					selectionItems.add(liMid);
					}
				}

			//Also draw a background around motif position
			for(SequenceRange segment:hoveringRestrictionSite.motif.segmentRanges(seq, charsPerLine))
				{
				int curline=segment.from/charsPerLine;
				int cposLeft=curline*charsPerLine;
				double local1=mapCharToX(segment.from-cposLeft);
				double local2=mapCharToX(segment.to-cposLeft);
				double y1=sequenceLineY.get(curline);
				double y3=y1+charHeight*2;
				
				QGraphicsRectItem rect=new QGraphicsRectItem();
				rect.setZValue(-10);
				rect.setBrush(new QBrush(QColor.fromRgb(230,230,200)));
				rect.setPen(new QPen(QColor.fromRgba(0)));
				rect.setRect(local1, y1, local2-local1, y3-y1);
				scene().addItem(rect);
				selectionItems.add(rect);
				}
			}
		}
	

	
	
	
	double mapCharToX(int pos)
		{
		return 10+pos*charWidth;
		}
	private int mapXtoChar(double x)
		{
		return (int)((x-10)/charWidth);
		}
	private int mapXYtoPos(double x, double y)
		{
		//Find which line
		for(int i=0;i<sequenceLineY.size();i++)
			{
			int y1=sequenceLineY.get(i);
			int y2=y1+30; 
			if(y>y1 && y<y2)
				{
				//Find where on line
				int c=mapXtoChar(x);
				if(c<0)
					c=0;
				else if(c>charsPerLine)
					c=charsPerLine;
				
				int total=i*charsPerLine + c;
				if(total>seq.getLength())
					return -1;
				else
					return total;  
				}
			}
		return -1;
		}





	
	/**
	 * Create a context menu if right-clicking
	 */
	@Override
	protected void contextMenuEvent(QContextMenuEvent event)
		{
		QPointF pos=mapToScene(event.pos());
		SeqAnnotation curAnnotation=getAnnotationAt(pos);
		Primer curPrimer=getPrimerAt(pos);
		if(curAnnotation!=null)
			{
			MenuAnnotation mPopup=new MenuAnnotation(w, seq, curAnnotation);
			mPopup.exec(event.globalPos());
			}
		else if(curPrimer!=null)
			{
			MenuPrimer mPopup=new MenuPrimer(w, seq, curPrimer, true);
			mPopup.exec(event.globalPos());
			}
		}

	/**
	 * Get primer at location
	 */
	private Primer getPrimerAt(QPointF pos)
		{
		for(Primer p:primerPosition.keySet())
			if(primerPosition.get(p).contains(pos))
				return p;
		return null;
		}
	
	/**
	 * Find if cursor overlaps a future
	 */
	private SeqAnnotation getAnnotationAt(QPointF pos)
		{
		for(QRectF region:mapAnnotations.keySet())
			if(region.contains(pos))
				return mapAnnotations.get(region);
		return null;
		}

	private RestrictionSite getRestrictionSiteAt(QPointF pos)
		{
		for(RestrictionSite s:revsitePosition.keySet())
			{
			QRectF r=revsitePosition.get(s);
			if(r.contains(pos))
				return s;
			}
		return null;
		}

	
	
	
	/**
	 * Handle mouse button pressed events 
	 */
	public void mousePressEvent(QMouseEvent event)
		{
		QPointF pos=mapToScene(event.pos());
		if(event.button()==MouseButton.RightButton)
			{

			}
		
		if(event.button()==MouseButton.LeftButton)
			{
			//Look for annotation
			SeqAnnotation curAnnotation=getAnnotationAt(pos);
			Primer curPrimer=getPrimerAt(pos);
			hoveringRestrictionSite=getRestrictionSiteAt(pos);

			
			
			if(curAnnotation!=null)
				signalUpdated.emit(new EventSelectedAnnotation(curAnnotation));
			else if(curPrimer!=null)
				{
				isSelecting=false;
				signalUpdated.emit(curPrimer.getRange());
				updateSelectionGraphics();  
				}
			else if(hoveringRestrictionSite!=null)
				{
				EventSelectedRestrictionEnzyme s=new EventSelectedRestrictionEnzyme();
				s.add(hoveringRestrictionSite.enzyme);
				signalUpdated.emit(s);
				}
			else
				{
				//Try to select a region
				int curindex=mapXYtoPos(pos.x(), pos.y());
				if(curindex!=-1)
					{
					selection=new SequenceRange();
					selection.from=selection.to=curindex;
					isSelecting=true;
					signalUpdated.emit(selection);
					updateSelectionGraphics();  
					}
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
		QPointF pos=mapToScene(event.pos());
		
		//Update selection if currently selecting
		int curindex=mapXYtoPos(pos.x(), pos.y());
		if(isSelecting && curindex!=-1)
			{
			selection.to=curindex;
			signalUpdated.emit(selection);
			updateSelectionGraphics();
			}
		

		//Check if hovering a restriction site
		RestrictionSite lastHover=hoveringRestrictionSite;
		hoveringRestrictionSite=getRestrictionSiteAt(pos);
		if(lastHover!=hoveringRestrictionSite)
			updateSelectionGraphics();
		}
	

	/**
	 * Handle mouse wheel events
	 */
	/*
	public void wheelEvent(QWheelEvent event)
		{
		}
*/
	
	/**
	 * Handle resize events
	 */
	public void resizeEvent(QResizeEvent event)
		{
		// Call the subclass resize so the scrollbars are updated correctly
		super.resizeEvent(event);
		}


	public void handleEvent(Object ob)
		{
		if(ob instanceof EventSelectedAnnotation)
			{
			SeqAnnotation annot=((EventSelectedAnnotation)ob).annot;
			if(annot!=null)
				{
				currentReadingFrame=annot.getFrame();
				buildSceneFromDoc();
				}
			}
		}

	
	}
