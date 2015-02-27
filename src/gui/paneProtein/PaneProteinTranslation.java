package gui.paneProtein;

import seq.AnnotatedSequence;
import sequtil.ProteinTranslator;

import com.trolltech.qt.core.Qt.BrushStyle;
import com.trolltech.qt.core.Qt.ScrollBarPolicy;
import com.trolltech.qt.gui.QBrush;
import com.trolltech.qt.gui.QColor;
import com.trolltech.qt.gui.QFont;
import com.trolltech.qt.gui.QGraphicsRectItem;
import com.trolltech.qt.gui.QGraphicsScene;
import com.trolltech.qt.gui.QGraphicsTextItem;
import com.trolltech.qt.gui.QGraphicsView;
import com.trolltech.qt.gui.QPen;
import com.trolltech.qt.gui.QSizePolicy;


/**
 * Show sequence with 3 translations beneath. Scrolling view horizontally
 * 
 * @author Johan Henriksson
 *
 */
public class PaneProteinTranslation extends QGraphicsView
	{
	private AnnotatedSequence seq=new AnnotatedSequence();
	
	public void setSequence(AnnotatedSequence seq)
		{
		this.seq=seq;
		buildSceneFromDoc();
		}

	
	public PaneProteinTranslation()
		{
		setBackgroundBrush(new QBrush(QColor.fromRgb(255,255,255)));
		
    setHorizontalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOn);  //is there a newer version??
    setVerticalScrollBarPolicy(ScrollBarPolicy.ScrollBarAlwaysOff);

		setMouseTracking(true);
		setEnabled(true);

		setSizePolicy(QSizePolicy.Policy.Expanding,QSizePolicy.Policy.Expanding);
		setScene(new QGraphicsScene());
		
		buildSceneFromDoc();
		}
	
	/**
	 * Build the scene from the document. This is equivalent to repainting
	 */
	public void buildSceneFromDoc()
		{
		QGraphicsScene scene=scene();
		scene.clear();
		
		//Note - it is good to have a separate scene builder class, for making PDFs
		
		
		QPen penSelect=new QPen();
		penSelect.setColor(new QColor(100,100,100));
		penSelect.setWidth(2);
	
		QFont font=new QFont();
		font.setFamily("Courier");
		font.setPointSize(10);
		
		int charwidth=10;
		int charheight=15;
		
		for(int i=0;i<seq.getLength();i++)
			{
			char letter=seq.getSequence().charAt(i);

			QGraphicsTextItem it=new QGraphicsTextItem();
			it.setFont(font);
			it.setPlainText(""+letter);
			it.setPos(5+i*charwidth, 5);
			scene.addItem(it);
			}

		ProteinTranslator trans=new ProteinTranslator();
				
		QBrush brush=new QBrush();
		brush.setStyle(BrushStyle.SolidPattern);
		brush.setColor(QColor.fromRgb(200,255,200));
		QPen pen=new QPen();
		pen.setColor(QColor.fromRgba(0));
		
		//Translation frames
		for(int transframe=0;transframe<3;transframe++)
			for(int i=transframe;i<seq.getLength();i+=3)
				if(i+3<=seq.getLength())
					{
					String triplet=seq.getSequence().substring(i,i+3);

					int from=5+i*charwidth;
					int to=5+(i+3)*charwidth;
					int y1=20+charheight*transframe;
					int y2=20+charheight*(transframe+1);

					
					QGraphicsRectItem ri=new QGraphicsRectItem();
					ri.setBrush(brush);
					ri.setRect(from+5,y1+5,to-from-2, y2-y1-2);
					scene.addItem(ri);
					ri.setPen(pen);
					
					String pletter=trans.tripletToAminoLetter(triplet);
					if(pletter==null)
						pletter="?";
					
					QGraphicsTextItem it=new QGraphicsTextItem();
					it.setFont(font);
					it.setPlainText(pletter);
					it.setPos(from+charwidth, y1);
					scene.addItem(it);
					}
		
		setSceneRect(0, 0, 40+seq.getLength()*charwidth, 100);
		}
	

	
	
	}
