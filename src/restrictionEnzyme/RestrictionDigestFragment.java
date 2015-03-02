package restrictionEnzyme;

import seq.AnnotatedSequence;
import seq.RestrictionSite;
import seq.SeqAnnotation;
import seq.SequenceRange;

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
		
		if(isCircular())
			{
			String supper=origseq.getSequence();
			String slower=origseq.getSequenceLower();
			
			//Cut the from part (which is on the right)
			int diff1=fromSite.cuttingUpperPos-fromSite.cuttingLowerPos;
			String supper1=getSpacePadding(Math.max(0, diff1)) + supper.substring(fromSite.cuttingUpperPos);
			String slower1=getSpacePadding(Math.max(0,-diff1)) + slower.substring(fromSite.cuttingLowerPos);
			
			//Cut the to part (which is on the left)
			int diff=toSite.cuttingUpperPos-toSite.cuttingLowerPos;
			String supper2=supper.substring(0,toSite.cuttingUpperPos) + getSpacePadding(Math.max(0, -diff));
			String slower2=slower.substring(0,toSite.cuttingLowerPos) + getSpacePadding(Math.max(0,  diff));

			supper=supper2+supper1;
			slower=slower2+slower1;
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
				supper=supper.substring(0,toSite.cuttingUpperPos)+getSpacePadding(Math.max(0, -diff));
				slower=slower.substring(0,toSite.cuttingLowerPos)+getSpacePadding(Math.max(0,  diff));
				}
			
			//Cut it on the left
			if(fromSite!=null)
				{
				int diff=fromSite.cuttingUpperPos-fromSite.cuttingLowerPos;
				supper=getSpacePadding(Math.max(0, diff))+supper.substring(fromSite.cuttingUpperPos);
				slower=getSpacePadding(Math.max(0,-diff))+slower.substring(fromSite.cuttingLowerPos);
				}
			
			newseq.setSequence(supper, slower);
			}
		
		//Transfer features
		int featureshift=0;
		if(fromSite!=null)
			featureshift=Math.min(fromSite.cuttingUpperPos,fromSite.cuttingLowerPos);
		for(SeqAnnotation annot:origseq.annotations)
			{
			annot=new SeqAnnotation(annot);
			annot.from-=featureshift;
			annot.to-=featureshift;
			if(annot.from>=0 && annot.to<=newseq.getLength())
				newseq.addAnnotation(annot);
			else
				{
				//For circular sequences, need to rotate around one turn and try again
				annot.from+=origseq.getLength();
				annot.to+=origseq.getLength();
				if(annot.from>=0 && annot.to<=newseq.getLength())
					newseq.addAnnotation(annot);

				}
			}

		
		return newseq;
		}
	
	
	private String getSpacePadding(int n)
		{
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<n;i++)
			sb.append(' ');
		return sb.toString();
		}
	}