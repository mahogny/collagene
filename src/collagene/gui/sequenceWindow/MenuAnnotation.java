package collagene.gui.sequenceWindow;

import collagene.gui.ProjectWindow;
import collagene.seq.AnnotatedSequence;
import collagene.seq.SeqAnnotation;

import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QMenu;

/**
 * 
 * Menu with options for annotations
 * 
 * @author Johan Henriksson
 *
 */
public class MenuAnnotation extends QMenu
	{
	private AnnotatedSequence seq;
	private SeqAnnotation annot;
	private ProjectWindow pw;
	
	public MenuAnnotation(ProjectWindow pw, AnnotatedSequence seq, SeqAnnotation annot)
		{
		this.pw=pw;
		this.seq=seq;
		this.annot=annot;
		QAction miEdit=addAction("Edit annotation");
		QAction miDeleteAnnot=addAction("Delete annotation");
		
		miEdit.triggered.connect(this,"actionEditAnnotation()");
		miDeleteAnnot.triggered.connect(this,"actionDeleteAnnotation()");
		}
	
	/**
	 * Action: Edit this annotation
	 */
	public void actionEditAnnotation()
		{
		AnnotationWindow w=new AnnotationWindow();
		w.setAnnotation(annot);
		w.exec();
		if(w.getAnnotation()!=null)
			pw.updateEvent(new EventSequenceModified(seq));
		}
	
	/**
	 * Action: Delete this annotation
	 */
	public void actionDeleteAnnotation()
		{
		seq.annotations.remove(annot);
		pw.updateEvent(new EventSequenceModified(seq));
		}
	
	}