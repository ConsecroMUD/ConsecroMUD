package com.suscipio_solutions.consecro_mud.Common;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PlayerStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Poll;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.collections.SVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;


public class DefaultPoll implements Poll
{
	@Override public String ID(){return "DefaultPoll";}
	@Override public String name() { return ID();}
	@Override public CMObject newInstance(){try{return getClass().newInstance();}catch(final Exception e){return new DefaultPoll();}}
	@Override public void initializeClass(){}
	@Override public int compareTo(CMObject o){ return CMClass.classID(this).compareToIgnoreCase(CMClass.classID(o));}
	@Override
	public CMObject copyOf()
	{
		try
		{
			return (Poll)this.clone();
		}
		catch(final CloneNotSupportedException e)
		{
			return newInstance();
		}
	}
	public boolean 		   	   loaded=false;
	protected String 		   name="POLL";
	protected String 		   subject="Poll Results Title";
	protected String 		   description="This is a Poll! Choose from the following:";
	protected String 		   author="noone";
	protected long 			   expiration=0;
	protected long 			   bitmap=0;
	protected String 		   qualZapper="";
	protected List<PollOption> options=new Vector<PollOption>();
	protected List<PollResult> results=new SVector<PollResult>();

	@Override public boolean loaded(){return loaded;}
	@Override public void setLoaded(boolean truefalse){ loaded=truefalse;}

	@Override public String getName(){return name;}
	@Override public void setName(String newname){name=newname;}

	@Override public String getSubject(){return subject;}
	@Override public void setSubject(String newsubject){subject=newsubject;}

	@Override public String getDescription(){return description;}
	@Override public void setDescription(String newdescription){description=newdescription;}

	@Override public String getAuthor(){return author;}
	@Override public void setAuthor(String newname){author=newname;}

	@Override public long getFlags(){return bitmap;}
	@Override public void setFlags(long flag){bitmap=flag;}

	@Override public String getQualZapper(){return qualZapper;}
	@Override public void setQualZapper(String newZap){qualZapper=newZap;}

	@Override public long getExpiration(){return expiration;}
	@Override public void setExpiration(long time){expiration=time;}

	@Override public List<PollOption> getOptions(){return options;}
	@Override public void setOptions(List<PollOption> V){ options=V;}

	@Override public List<PollResult> getResults(){return results;}
	@Override public void setResults(List<PollResult> V){results=V;}

	@Override
	public String getOptionsXML()
	{
		if(options.size()==0) return "<OPTIONS />";
		final StringBuffer str=new StringBuffer("<OPTIONS>");
		PollOption PO=null;
		for(int i=0;i<options.size();i++)
		{
			PO=options.get(i);
			str.append("<OPTION>");
			str.append(CMLib.xml().convertXMLtoTag("TEXT",CMLib.xml().parseOutAngleBrackets(PO.text)));
			str.append("</OPTION>");
		}
		str.append("</OPTIONS>");
		return str.toString();
	}

	@Override
	public String getResultsXML()
	{
		if(results.size()==0) return "<RESULTS />";
		final StringBuffer str=new StringBuffer("<RESULTS>");
		PollResult PR=null;
		for(int i=0;i<results.size();i++)
		{
			PR=results.get(i);
			str.append("<RESULT>");
			str.append(CMLib.xml().convertXMLtoTag("USER",PR.user));
			str.append(CMLib.xml().convertXMLtoTag("IP",PR.ip));
			str.append(CMLib.xml().convertXMLtoTag("ANS",PR.answer));
			str.append("</RESULT>");
		}
		str.append("</RESULTS>");
		return str.toString();
	}

	@Override
	public PollResult getMyVote(MOB mob)
	{
		if(mob==null) return null;
		CMLib.polls().loadPollIfNecessary(this);
		PollResult R=null;
		final Session S=mob.session();
		for(int r=0;r<results.size();r++)
		{
			R=results.get(r);
			if((mob.Name().equals(R.user)))
				return R;
			if(R.ip.length()>0)
			{
				final String address=(S!=null)?S.getAddress():"\n\r\t";
				final String accountName;
				final PlayerStats pstats=mob.playerStats();
				if((pstats!=null)&&(pstats.getAccount()!=null))
					accountName="\t"+pstats.getAccount().getAccountName();
				else
					accountName="\t\n";
				if((R.ip.equals(address)||R.ip.startsWith(address+"\t")||R.ip.endsWith(accountName)))
					return R;
			}
		}
		return null;
	}

	@Override
	public void addVoteResult(PollResult R)
	{
		CMLib.polls().loadPollIfNecessary(this);
		results.add(R);
		CMLib.polls().updatePollResults(this);
	}

	@Override
	public boolean mayIVote(MOB mob)
	{
		if(mob==null) return false;
		if(!CMath.bset(bitmap,FLAG_ACTIVE))
			return false;
		if(!CMLib.masking().maskCheck(qualZapper,mob,true))
			return false;
		if((expiration>0)&&(System.currentTimeMillis()>expiration))
		{
			bitmap=CMath.unsetb(bitmap,FLAG_ACTIVE);
			CMLib.polls().updatePoll(name, this);
			return false;
		}
		if(getMyVote(mob)!=null) return false;
		return true;
	}

	@Override
	public boolean mayISeeResults(MOB mob)
	{
		if(mob==null) return false;
		if(!CMLib.masking().maskCheck(qualZapper,mob,true))
			return false;
		if(CMath.bset(bitmap,FLAG_HIDERESULTS)&&(!CMSecurity.isAllowedAnywhere(mob,CMSecurity.SecFlag.POLLS)))
			return false;
		if(CMath.bset(bitmap,FLAG_PREVIEWRESULTS))
			return true;
		if((expiration>0)
		&&(System.currentTimeMillis()<expiration))
			return false;
		if((getMyVote(mob)==null)&&(!CMath.bset(bitmap,FLAG_ABSTAIN)))
			return false;
		return true;
	}
}
