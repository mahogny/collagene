package collagene.gui.paneLinear;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import collagene.gui.ProjectWindow;
import collagene.gui.paneLinear.tracks.LinTrack;
import collagene.gui.paneLinear.tracks.LinTrackAnnotation;
import collagene.gui.paneLinear.tracks.LinTrackPrimer;
import collagene.gui.paneLinear.tracks.LinTrackSequence;
import collagene.gui.paneLinear.tracks.LinTrackTraces;
import collagene.gui.paneLinear.tracks.QGraphicsLinSeqPositionItem;
import collagene.gui.paneRestriction.ViewSettingsRestrictionEnzymes;
import collagene.gui.sequenceWindow.CollageneEvent;
import collagene.gui.sequenceWindow.EventSelectedRegion;
import collagene.gui.sequenceWindow.ViewSettingsSequence;
import collagene.seq.AnnotatedSequence;
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
import com.trolltech.qt.gui.QGraphicsLineItem;
import com.trolltech.qt.gui.QGraphicsRectItem;
import com.trolltech.qt.gui.QGraphicsScene;
import com.trolltech.qt.gui.QGraphicsView;
import com.trolltech.qt.gui.QMouseEvent;
import com.trolltech.qt.gui.QResizeEvent;
import com.trolltech.qt.gui.QSizePolicy;

/**
 * 
 * Display sequence linearly, broken over several lines
 * 
 * @author Johan Henriksson
 *
 */
public class ViewLinearSequence extends QGraphicsView
	{
	private AnnotatedSequence seq=new AnnotatedSequence();
	
	LinTrackSequence trackSequence=new LinTrackSequence(this);
	
	private QTimer timerAnimation=new QTimer();
	
	public ViewSettingsRestrictionEnzymes settingsRS=new ViewSettingsRestrictionEnzymes();
	public ViewSettingsSequence settingsSeq=new ViewSettingsSequence();

	public QSignalEmitter.Signal1<CollageneEvent> signalUpdated=new Signal1<CollageneEvent>();

	private Collection<Object> selectionItems=new LinkedList<Object>();
	public SequenceRange selection=null;
	private boolean isSelecting=false;

	public int charsPerLine;
//	public int widthInChars=-1; //-1 means to choose automatically. 
	public double charWidth;// set externally;
	
	private boolean isEditable=true;
//	public boolean showPositionRuler=true;
	private double currentMovetopos=0;
	//private boolean isFullsizeMode=false;

	private int showWidthInChars;
	
	public ProjectWindow w;

	public LinkedList<LinTrack> tracks=new LinkedList<LinTrack>();


	
	public AnnotatedSequence getSequence()
		{
		return seq;
		}

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
		ArrayList<Integer> sequenceLineY=trackSequence.sequenceLineY;

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

		tracks.add(trackSequence);
		tracks.add(new LinTrackPrimer(this));
		tracks.add(new LinTrackAnnotation(this));
		tracks.add(new LinTrackTraces(this));

    setHorizontalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOff); 
    setVerticalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOn);
		setSizePolicy(QSizePolicy.Policy.Expanding,QSizePolicy.Policy.Expanding);
		setMouseTracking(true);
		setEnabled(true);

		//charsPerLine=80; //not proper
		
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
		
		//Note - it is good to have a separate scene builder class, for making PDFs
		if(settingsSeq.fullsize)
			{
			//Adapt to width
	    setHorizontalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOff);
			//widthInChars=seq.getLength();
			setMinimumWidth(0);
			charsPerLine=(int)(1.1*seq.getLength());
			showWidthInChars=charsPerLine;
			charWidth=width()/(double)charsPerLine;
			}
		else
			{
			//Update how many chars fit on each line
			if(settingsSeq.charsPerLine==-1 || settingsSeq.charsPerLine==-2)
				{
				//Never fullsize here
				charsPerLine=seq.getLength()+10;
				showWidthInChars=100;
				setMinimumWidth((int)mapCharToX(showWidthInChars)+50);
				setMaximumWidth((int)mapCharToX(showWidthInChars)+50);
				//charsPerLine=80; //Try and figure out optimum?
		    setHorizontalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOn);
				}
			else
				{
				charsPerLine=settingsSeq.charsPerLine;
				showWidthInChars=charsPerLine;
		    setHorizontalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOff);
				setMinimumWidth((int)mapCharToX(showWidthInChars)+50);
				setMaximumWidth((int)mapCharToX(showWidthInChars)+50);
				}
			//charwidth set externally from pane. bad?
			}


		//Render each line. Remember position
		int currentY=10;
		for(LinTrack track:tracks)
			track.initPlacing();
		for(int curline=0;curline<seq.getLength()/charsPerLine+1;curline++)
			{
			int cposLeft=curline*charsPerLine;
			int cposRight=(curline+1)*charsPerLine;
			
			//Draw all tracks
			for(LinTrack track:tracks)
				currentY=track.place(scene, currentY, cposLeft, cposRight);

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
	public void updateSelectionGraphics()
		{
		//Remove previous selection
		for(Object i:selectionItems)
			if(i instanceof QGraphicsRectItem)
				scene().removeItem((QGraphicsRectItem)i);
			else if(i instanceof QGraphicsLineItem)
				scene().removeItem((QGraphicsLineItem)i);
		selectionItems.clear();
		
		for(LinTrack track:tracks)
			track.updateSelectionGraphics(selectionItems);
		}
	

	
	
	
	public double mapCharToX(double pos)
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
		ArrayList<Integer> sequenceLineY=trackSequence.sequenceLineY;
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
		if(isEditable)
			{
			QPointF pos=mapToScene(event.pos());
			for(LinTrack track:tracks)
				{
				if(track.contextMenuEvent(event, pos))
					return;
				}
			}
		}
	


	
	
	
	/**
	 * Handle mouse button pressed events 
	 */
	public void mousePressEvent(QMouseEvent event)
		{
		if(isEditable)
			{
			QPointF pos=mapToScene(event.pos());
			if(event.button()==MouseButton.LeftButton)
				{
				//See if tracks wants to do something
				for(LinTrack track:tracks)
					if(track.mousePressEvent(event, pos))
						return;
				
				//Try to select a region
				int curindex=mapXYtoPos(pos.x(), pos.y());
				if(curindex!=-1)
					{
					selection=new SequenceRange();
					selection.from=selection.to=curindex;
					isSelecting=true;
					emitNewSelection(selection);
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
			emitNewSelection(selection);
			updateSelectionGraphics();
			}

		for(LinTrack track:tracks)
			track.mouseMoveEvent(event, pos);
		}
	

	
	/**
	 * Handle resize events
	 */
	public void resizeEvent(QResizeEvent event)
		{
		// Call the subclass resize so the scrollbars are updated correctly
		super.resizeEvent(event);
		}


	/**
	 * Handle cloning events
	 */
	public void handleEvent(Object ob)
		{
		for(LinTrack track:tracks)
			track.handleEvent(ob);
		}


	public void setEditable(boolean b)
		{
		isEditable=b;
		}

	public void emitNewSelection(SequenceRange range)
		{
		signalUpdated.emit(new EventSelectedRegion(getSequence(), range));
		}

	
	}
