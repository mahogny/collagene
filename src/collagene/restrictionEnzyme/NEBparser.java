package collagene.restrictionEnzyme;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;


/**
 * URL to download: https://www.neb.com/tools-and-resources/usage-guidelines/nebuffer-performance-chart-with-restriction-enzymes
 *
 * BsrGI parsed wrong
 *
 * cat nebuffer.txt | sed 's/&[a-zA-Z0-9]*;//' > nebuffer2.txt
 * 
 * @author Johan Henriksson
 *
 */
public class NEBparser
	{
	
	public static void parse(RestrictionEnzymeSet db) throws IOException
		{
		InputStream is=NEBparser.class.getResourceAsStream("nebuffer2.txt");
		parse(is,db);
		is.close();

		HashSet<String> w=new HashSet<String>();
		for(RestrictionEnzyme e:db.enzymes)
			w.add(e.name);

		//Get rid of the non-HF
		for(RestrictionEnzyme e:new LinkedList<RestrictionEnzyme>(db.enzymes))
			{
			if(w.contains(e.name+"-HF") || e.name.contains("RE-Mix"))
				db.removeEnzyme(e);
			}
		}
	
	public static void parse(InputStream is, RestrictionEnzymeSet db) throws IOException
		{
		try
			{
			SAXBuilder sax = new SAXBuilder();
			sax.setEntityResolver(null);// new NoOpEntityResolver());
			Document doc = sax.build(is);
			
			for(Element e:doc.getRootElement().getChild("tbody").getChildren())
				{
				if(e.getName().equals("tr"))
					{
					List<Element> els=e.getChildren();
					
					RestrictionEnzyme enz=new RestrictionEnzyme();
					enz.name=els.get(0).getChildText("a");
					for(Element ee:els.get(1).getChildren())
						{
						if(ee.getName().equals("img"))
							{
							String alt=ee.getAttributeValue("alt");
							if(alt.equals("cpg"))
								enz.affectedBy.add("CpG");
							/*else if(alt.contains("timesaver"))
								enz.affectedBy.add("timesaver");*/
							else if(alt.contains("dam"))
								{
								enz.affectedBy.add("dam"); //source does not tell which!!! need a different source
								enz.affectedBy.add("dcm");
								}
							}
						}
					
					enz.url="https://www.neb.com"+els.get(0).getChild("a").getAttributeValue("href");
					String seq=els.get(2).getText().trim();
					if(seq.equals(""))
						continue;  //AgaSI is wrong in the table!
					
					if(!enz.parseSequence(seq))
						{
						System.out.println("Skipping "+enz.name);
						continue;
						}
					
					cleaneff(enz, "1.1", els.get(4).getText());
					cleaneff(enz, "2.1", els.get(5).getText());
					cleaneff(enz, "3.1", els.get(6).getText());
					cleaneff(enz, "CS",  els.get(7).getText());
					
					enz.tempInactivation=cleantemp(els.get(8).getText());
					enz.tempIncubation=cleantemp(els.get(9).getText());
					
					db.addEnzyme(enz);
					
					//Could also include info on Dam, Dcm, CpG. These could be made into notes
					}
				else
					throw new IOException("wut");
				}
			}
		catch (Exception e)
			{
			e.printStackTrace();
			throw new IOException(e.getMessage());
			}

		}

	private static void cleaneff(RestrictionEnzyme enz, String buf, String s)
		{
		if(!s.equals("-") && !s.equals("N/R"))
			enz.bufferEfficiency.put(buf, Double.parseDouble(s.replace("*", "")));
		}

	private static Double cleantemp(String s)
		{
		if(s.equals("No") || s.equals("") || s.equals("N/R"))
			return null;
		else
			return Double.parseDouble(s.replace("C", ""));
		}

	
	/*
	public static void main(String[] args) throws IOException
		{
		parse(new File("bin/restrictionEnzyme/nebuffer2.txt"), new RestrictionEnzymeDatabase());
		}*/

	}
