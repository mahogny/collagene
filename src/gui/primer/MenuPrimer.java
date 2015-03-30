package gui.primer;

import gui.ProjectWindow;
import gui.digest.SimulatedDigestWindow;
import gui.sequenceWindow.EventSequenceModified;

import java.text.NumberFormat;

import melting.CalcTm;
import melting.CalcTmSanta98;
import melting.TmException;
import primer.Primer;
import primer.PrimerPairInfo;
import restrictionEnzyme.RestrictionDigestFragment;
import seq.AnnotatedSequence;

import com.trolltech.qt.gui.QAction;
import com.trolltech.qt.gui.QMenu;

/**
 * 
 * Menu with options for a primer
 * 
 * @author Johan Henriksson
 *
 */
public class MenuPrimer extends QMenu
	{
	private Primer primer;
	private AnnotatedSequence seq;
	private ProjectWindow w;
	
	
	public MenuPrimer(ProjectWindow w, AnnotatedSequence seq, Primer primer, boolean showInfo)
		{
		this.w=w;
		this.seq=seq;
		this.primer=primer;
		
		if(showInfo)
			{
			String tm=tryCalcTm(new CalcTmSanta98(), primer);
		
			addAction("Sequence: "+primer.sequence);
			addAction("Tm: "+tm);
			addSeparator();
			}
		
		for(PrimerPairInfo other:primer.getPairInfo(seq))
			{
			//could maybe sort here?
			PCRhandler h=new PCRhandler();
			h.pair=other;
			addAction(other.productsize+"bp  =>  "+other.primerB.name, h, "dopcr()");
			}
		
		addSeparator();
		QAction miDeleteAnnot=addAction("Delete primer");
		QAction miEditAnnot=addAction("Delete primer");
			
		miDeleteAnnot.triggered.connect(this,"actionDeletePrimer()");
		miEditAnnot.triggered.connect(this,"actionEditPrimer()");
		}
	
	
	public void actionDeletePrimer()
		{
		if(primer!=null)
			seq.primers.remove(primer);
		w.updateEvent(new EventSequenceModified(seq));
		}
	
	
	public void actionEditPrimer()
		{
		PrimerPropertyWindow w=new PrimerPropertyWindow();
		w.setPrimer(primer);
		w.exec();
		if(w.wasOk)
			this.w.updateEvent(new EventSequenceModified(seq));
		}
	
	public static String formatTemp(double d)
		{
		NumberFormat nf=NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(1);
		nf.setMinimumFractionDigits(1);
		return nf.format(d)+"C";
		}
	
	/**
	 * For PCRing from the pop-up menu
	 */
	public class PCRhandler
		{
		PrimerPairInfo pair;
		public void dopcr()
			{
			
			AnnotatedSequence newseq=pair.dopcr(seq);
			newseq.name=seq.name+"-pcr-"+pair.primerA.name+"_"+pair.primerB.name;
			
			RestrictionDigestFragment frag=new RestrictionDigestFragment();
			frag.setSequence(newseq);
			
			SimulatedDigestWindow w=new SimulatedDigestWindow(MenuPrimer.this.w);
			w.setFragment(frag);
			w.show();
			}
		}

	public static String tryCalcTm(CalcTm tmc, Primer p)
		{
		String tm="?";
		try
			{
			tm=MenuPrimer.formatTemp(tmc.calcTm(p.sequence));
			}
		catch (TmException e)
			{
			e.printStackTrace();
			}
		return tm;
		}
	}