package collagene.gui.sequenceWindow;

import collagene.gui.resource.ImgResource;
import collagene.seq.AnnotatedSequence;
import collagene.seq.SequenceRange;

import com.trolltech.qt.gui.QHBoxLayout;
import com.trolltech.qt.gui.QIcon;
import com.trolltech.qt.gui.QLabel;
import com.trolltech.qt.gui.QLineEdit;
import com.trolltech.qt.gui.QPushButton;

/**
 * 
 * Sequence window: sequence searching widget
 * 
 * @author Johan Henriksson
 *
 */
public class SequenceWindowSeqSearch extends QHBoxLayout
	{
	private QLineEdit tfSearch=new QLineEdit();
	private QPushButton bSearchNext=new QPushButton(new QIcon(ImgResource.moveRight),"");
	private QPushButton bSearchPrev=new QPushButton(new QIcon(ImgResource.moveLeft),"");
	
	public SequenceSearcher currentSearchString=null;

	private SequenceWindow w;
	public SequenceWindowSeqSearch(SequenceWindow w)
		{
		this.w=w;
		
		tfSearch.textChanged.connect(this,"actionSearch()");
		tfSearch.returnPressed.connect(this,"actionSearchNext()");
		bSearchNext.clicked.connect(this,"actionSearchNext()");
		bSearchPrev.clicked.connect(this,"actionSearchPrev()");

		addWidget(new QLabel(tr("Search:")));
		addWidget(tfSearch);
		addWidget(bSearchPrev);
		addWidget(bSearchNext);
		setMargin(0);
		setSpacing(0);
		}
	
	/**
	 * Action: Perform a new search
	 */
	public void actionSearch()
		{
		if(tfSearch.text().length()==0)
			currentSearchString=null;
		else
			currentSearchString=new SequenceSearcher(getSequence(), tfSearch.text().toUpperCase());
		emitNewSelection(SequenceRange.getNoRange());
		actionSearchNext();
		}
	

	/**
	 * Action: Go to next search position
	 */
	public void actionSearchNext()
		{
		if(currentSearchString!=null)
			{
			SequenceRange r=currentSearchString.next(getSelection());
			emitNewSelection(r);
			}
		}


	/**
	 * Action: Go to next search position
	 */
	public void actionSearchPrev()
		{
		if(currentSearchString!=null)
			{
			SequenceRange r=currentSearchString.prev(getSelection());
			emitNewSelection(r);
			}
		}

	
	private SequenceRange getSelection()
		{
		return w.getSelection();
		}

	private void emitNewSelection(SequenceRange r)
		{
		w.emitNewSelection(r);
		}

	private AnnotatedSequence getSequence()
		{
		return w.getSequence();
		}

	}
