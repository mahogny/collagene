package restrictionEnzyme;

import seq.AnnotatedSequence;
import seq.RestrictionSite;
import seq.SeqAnnotation;
import seq.SequenceRange;
import sequtil.NucleotideUtil;

/**
 * One fragment from digest
 */
public class RestrictionDigestFragment
	{
	public AnnotatedSequence origseq;
	public RestrictionSite fromSite;
	public RestrictionSite toSite;

	
	public int getUpperFrom()
		{
		if(fromSite==null)
			return 0;
		else
			return fromSite.cuttingUpperPos;
		}
	
	public int getUpperTo()
		{
		if(toSite==null)
			return origseq.getLength();
		else
			return toSite.cuttingUpperPos;
		}
	
	private boolean isCircular()
		{
		return getUpperFrom()>getUpperTo();
		}
	
	public int getUpperLength()
		{
		SequenceRange r=new SequenceRange();
		r.from=getUpperFrom();
		r.to=getUpperTo();
		return r.getSize(origseq);
		}

	public AnnotatedSequence getFragmentSequence()
		{
		AnnotatedSequence newseq=new AnnotatedSequence();
		newseq.name=origseq.name+"_"+Math.random();
		newseq.isCircular=false;

		int featureshift=0;

		if(isCircular())
			{
			//Pull out upper and lower rang separately
			SequenceRange rangeUpper=new SequenceRange(fromSite.cuttingUpperPos, toSite.cuttingUpperPos);
			SequenceRange rangeLower=new SequenceRange(fromSite.cuttingLowerPos, toSite.cuttingLowerPos);
			String supper=origseq.getSequence(rangeUpper);
			String slower=origseq.getSequenceLower(rangeLower);

			//Now have to find out which strand has the overhang
			int delta=fromSite.cuttingUpperPos-fromSite.cuttingLowerPos;
			if(fromSite.cut.upper<fromSite.cut.lower)
				{
				//Upper overhang. delta should be negative
				if(delta>0)
					delta-=origseq.getLength();
				String padLeft=NucleotideUtil.getRepeatOligo(' ',-delta);
				slower=padLeft+slower;
				featureshift=fromSite.cuttingUpperPos;
				}
			else
				{
				//Negative overhang. delta should be positive
				if(delta>0)
					delta+=origseq.getLength();
				String padLeft=NucleotideUtil.getRepeatOligo(' ',delta);
				supper=padLeft+supper;
				featureshift=fromSite.cuttingLowerPos;
				}

			//Add enough spaces on the right
			supper=supper+NucleotideUtil.getRepeatOligo(' ', Math.max(0,slower.length()-supper.length()));
			slower=slower+NucleotideUtil.getRepeatOligo(' ', Math.max(0,supper.length()-slower.length()));
			
			newseq.setSequence(supper,slower);
			}
		else
			{
			String supper=origseq.getSequence();
			String slower=origseq.getSequenceLower();
			
			//Cut it on the right
			if(toSite!=null)
				{
				int diff=toSite.cuttingUpperPos-toSite.cuttingLowerPos;
				supper=supper.substring(0,toSite.cuttingUpperPos)+NucleotideUtil.getRepeatOligo(' ',Math.max(0, -diff));
				slower=slower.substring(0,toSite.cuttingLowerPos)+NucleotideUtil.getRepeatOligo(' ',Math.max(0,  diff));
				}
			
			//Cut it on the left
			if(fromSite!=null)
				{
				int diff=fromSite.cuttingUpperPos-fromSite.cuttingLowerPos;
				supper=NucleotideUtil.getRepeatOligo(' ',Math.max(0, diff))+supper.substring(fromSite.cuttingUpperPos);
				slower=NucleotideUtil.getRepeatOligo(' ',Math.max(0,-diff))+slower.substring(fromSite.cuttingLowerPos);
				}
			
			newseq.setSequence(supper, slower);
			
			if(fromSite!=null)
				featureshift=Math.min(fromSite.cuttingUpperPos,fromSite.cuttingLowerPos);  
			}
		
		//Transfer features
		for(SeqAnnotation annot:origseq.annotations)
			{
			annot=new SeqAnnotation(annot);
			annot.range=annot.range.toUnwrappedRange(origseq);
			annot.range.shift(-featureshift);
			if(annot.getFrom()>=0 && annot.getTo()<=newseq.getLength())
				newseq.addAnnotation(annot);
			else
				{
				//For circular sequences, need to rotate around one turn and try again. ooor?
				if(annot.getFrom()<0)
					annot.range.shift(+origseq.getLength());
				else
					annot.range.shift(-origseq.getLength());
				if(annot.getFrom()>=0 && annot.getTo()<=newseq.getLength())
					newseq.addAnnotation(annot);
				}
			}
		newseq.normalizeFeaturePos();
		return newseq;
		}
	
	

	}