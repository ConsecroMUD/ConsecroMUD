package com.suscipio_solutions.consecro_mud.Libraries;
import java.util.Enumeration;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Common.interfaces.PlayerStats;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.AutoTitlesLibrary;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.MaskingLibrary;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.Resources;
import com.suscipio_solutions.consecro_mud.core.collections.Triad;
import com.suscipio_solutions.consecro_mud.core.collections.TriadSVector;


public class AutoTitles extends StdLibrary implements AutoTitlesLibrary
{
	@Override public String ID(){return "AutoTitles";}
	private TriadSVector<String,String,MaskingLibrary.CompiledZapperMask> autoTitles=null;

	@Override
	public String evaluateAutoTitle(String row, boolean addIfPossible)
	{
		if(row.trim().startsWith("#")||row.trim().startsWith(";")||(row.trim().length()==0))
			return null;
		int x=row.indexOf('=');
		while((x>=1)&&(row.charAt(x-1)=='\\')) x=row.indexOf('=',x+1);
		if(x<0)
			return "Error: Invalid line! Not comment, whitespace, and does not contain an = sign!";
		final String title=row.substring(0,x).trim();
		final String mask=row.substring(x+1).trim();

		if(title.length()==0)return "Error: Blank title: "+title+"="+mask+"!";
		if(mask.length()==0)return "Error: Blank mask: "+title+"="+mask+"!";
		if(addIfPossible)
		{
			if(autoTitles==null) reloadAutoTitles();
			for(final Triad<String,String,MaskingLibrary.CompiledZapperMask> triad : autoTitles)
				if(triad.first.equalsIgnoreCase(title))
					return "Error: Duplicate title: "+title+"="+mask+"!";
			autoTitles.add(new Triad<String,String,MaskingLibrary.CompiledZapperMask>(title,mask,CMLib.masking().maskCompile(mask)));
		}
		return null;
	}
	@Override
	public boolean isExistingAutoTitle(String title)
	{
		if(autoTitles==null) reloadAutoTitles();
		title=title.trim();
		for(final Triad<String,String,MaskingLibrary.CompiledZapperMask> triad : autoTitles)
			if(triad.first.equalsIgnoreCase(title))
				return true;
		return false;
	}

	@Override
	public Enumeration<String> autoTitles()
	{
		if(autoTitles==null) reloadAutoTitles();
		return autoTitles.firstElements();
	}

	@Override
	public String getAutoTitleMask(String title)
	{
		if(autoTitles==null) reloadAutoTitles();
		for(final Triad<String,String,MaskingLibrary.CompiledZapperMask> triad : autoTitles)
			if(triad.first.equalsIgnoreCase(title))
				return triad.second;
		return "";
	}

	@Override
	public boolean evaluateAutoTitles(MOB mob)
	{
		if(mob==null) return false;
		final PlayerStats P=mob.playerStats();
		if(P==null) return false;
		if(autoTitles==null) reloadAutoTitles();
		String title=null;
		MaskingLibrary.CompiledZapperMask mask=null;
		int pdex=0;
		final List<String> ptV=P.getTitles();
		boolean somethingDone=false;
		synchronized(ptV)
		{
			for(final Triad<String,String,MaskingLibrary.CompiledZapperMask> triad : autoTitles)
			{
				mask=triad.third;
				title=triad.first;
				pdex=ptV.indexOf(title);
				if(pdex<0)
				{
					final String fixedTitle = CMStrings.removeColors(title).replace('\'', '`');
					for(int p=ptV.size()-1;p>=0;p--)
					{
						try
						{
							final String tit=CMStrings.removeColors(ptV.get(p)).replace('\'', '`');
							if(tit.equalsIgnoreCase(fixedTitle))
							{ pdex=p; break;}
						}catch(final java.lang.IndexOutOfBoundsException ioe){}
					}
				}

				if(CMLib.masking().maskCheck(mask,mob,true))
				{
					if(pdex<0)
					{
						if(ptV.size()>0)
							ptV.add(0,title);
						else
							ptV.add(title);
						somethingDone=true;
					}
				}
				else
				if(pdex>=0)
				{
					somethingDone=true;
					ptV.remove(pdex);
				}
			}
		}
		return somethingDone;
	}

	@Override
	public void dispossesTitle(String title)
	{
		final List<String> list=CMLib.database().getUserList();
		final String fixedTitle = CMStrings.removeColors(title).replace('\'', '`');
		for(final String playerName : list)
		{
			final MOB M=CMLib.players().getLoadPlayer(playerName);
			if(M.playerStats()!=null)
			{
				final List<String> ptV=M.playerStats().getTitles();
				synchronized(ptV)
				{
					int pdex=ptV.indexOf(title);
					if(pdex<0)
					{
						for(int p=ptV.size()-1;p>=0;p--)
						{
							try
							{
								final String tit=CMStrings.removeColors(ptV.get(p)).replace('\'', '`');
								if(tit.equalsIgnoreCase(fixedTitle))
								{ pdex=p; break;}
							}catch(final java.lang.IndexOutOfBoundsException ioe){}
						}
					}
					if(pdex>=0)
					{
						ptV.remove(pdex);
						if(!CMLib.flags().isInTheGame(M,true))
							CMLib.database().DBUpdatePlayerPlayerStats(M);
					}
				}
			}
		}
	}

	@Override
	public void reloadAutoTitles()
	{
		autoTitles=new TriadSVector<String,String,MaskingLibrary.CompiledZapperMask>();
		final List<String> V=Resources.getFileLineVector(Resources.getFileResource("titles.txt",true));
		String WKID=null;
		for(int v=0;v<V.size();v++)
		{
			final String row=V.get(v);
			WKID=evaluateAutoTitle(row,true);
			if(WKID==null) continue;
			if(WKID.startsWith("Error: "))
				Log.errOut("CharCreation",WKID);
		}
		for(final Enumeration<MOB> e=CMLib.players().players();e.hasMoreElements();)
		{
			final MOB M=e.nextElement();
			if(M.playerStats()!=null)
			{
				if((evaluateAutoTitles(M))&&(!CMLib.flags().isInTheGame(M,true)))
					CMLib.database().DBUpdatePlayerPlayerStats(M);
			}
		}
	}

}
