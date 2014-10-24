package com.suscipio_solutions.consecro_mud.Behaviors;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.Behavior;
import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.MaskingLibrary;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TrackingLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings({"unchecked","rawtypes"})
public class ProtectedCitizens extends ActiveTicker
{
	@Override public String ID(){return "ProtectedCitizens";}
	@Override protected int canImproveCode(){return Behavior.CAN_MOBS|Behavior.CAN_AREAS|Behavior.CAN_ROOMS;}
	protected static MaskingLibrary.CompiledZapperMask citizenZapper=null;
	protected static MaskingLibrary.CompiledZapperMask helperZapper=null;
	protected static String[] defclaims={"Help! I'm being attacked!","Help me!!"};
	protected String[] claims=null;
	protected int radius=7;
	protected int maxAssistance=1;
	protected Map<MOB,List<MOB>> assisters=new Hashtable<MOB,List<MOB>>();

	public ProtectedCitizens()
	{
		super();
		minTicks=1;
		maxTicks=3;
		chance=99;
		radius=7;
		maxAssistance=1;
		tickReset();
	}

	@Override
	public String accountForYourself()
	{
		return "whiney citizen";
	}

	@Override
	public void setParms(String parms)
	{
		super.setParms(parms);
		citizenZapper=null;
		helperZapper=null;
		radius=CMParms.getParmInt(parms,"radius",radius);
		maxAssistance=CMParms.getParmInt(parms,"maxassists",maxAssistance);
		claims=null;
	}

	public MaskingLibrary.CompiledZapperMask getProtectedZapper()
	{
		if(citizenZapper!=null) return citizenZapper;
		final String s=getParmsNoTicks();
		if(s.length()==0){ citizenZapper=MaskingLibrary.CompiledZapperMask.EMPTY(); return citizenZapper;}
		final char c=';';
		final int x=s.indexOf(c);
		if(x<0){ citizenZapper=MaskingLibrary.CompiledZapperMask.EMPTY(); return citizenZapper;}
		citizenZapper=CMLib.masking().getPreCompiledMask(s.substring(0,x));
		return citizenZapper;
	}

	public MaskingLibrary.CompiledZapperMask getCityguardZapper()
	{
		if(helperZapper!=null) return helperZapper;
		String s=getParmsNoTicks();
		if(s.length()==0){ helperZapper=MaskingLibrary.CompiledZapperMask.EMPTY(); return helperZapper;}
		final char c=';';
		int x=s.indexOf(c);
		if(x<0){ helperZapper=MaskingLibrary.CompiledZapperMask.EMPTY(); return helperZapper;}
		s=s.substring(x+1).trim();
		x=s.indexOf(c);
		if(x<0){ helperZapper=MaskingLibrary.CompiledZapperMask.EMPTY(); return helperZapper;}
		helperZapper=CMLib.masking().getPreCompiledMask(s.substring(0,x));
		return helperZapper;
	}

	public String[] getClaims()
	{
		if(claims!=null) return claims;
		String s=getParmsNoTicks();
		if(s.length()==0)
		{ claims=defclaims; return claims;}

		final char c=';';
		int x=s.indexOf(c);
		if(x<0)	{ claims=defclaims; return claims;}
		s=s.substring(x+1).trim();
		x=s.indexOf(c);
		if(x<0)	{ claims=defclaims; return claims;}
		s=s.substring(x+1).trim();
		if(s.length()==0)
		{ claims=defclaims; return claims;}
		final Vector V=new Vector();
		x=s.indexOf(c);
		while(x>=0)
		{
			final String str=s.substring(0,x).trim();
			s=s.substring(x+1).trim();
			if(str.length()>0)V.addElement(str);
			x=s.indexOf(c);
		}
		if(s.length()>0)V.addElement(s);
		claims=new String[V.size()];
		for(int i=0;i<V.size();i++)
			claims[i]=(String)V.elementAt(i);
		return claims;
	}

	public boolean assistMOB(MOB mob)
	{
		if(mob==null)
			return false;

		if((!mob.isMonster())
		||(!mob.isInCombat())
		||(!CMLib.flags().aliveAwakeMobileUnbound(mob,true))
		||(mob.location()==null))
		{
			if(assisters.containsKey(mob))
				assisters.remove(mob);
			return false;
		}

		if(!CMLib.masking().maskCheck(getProtectedZapper(),mob,false))
			return false;

		int assistance=0;
		for(int i=0;i<mob.location().numInhabitants();i++)
		{
			final MOB M=mob.location().fetchInhabitant(i);
			if((M!=null)
			&&(M!=mob)
			&&(M.getVictim()==mob.getVictim()))
			   assistance++;
		}
		if(assistance>=maxAssistance)
			return true;

		final String claim=getClaims()[CMLib.dice().roll(1,getClaims().length,-1)].trim();
		if(claim.startsWith(","))
			mob.doCommand(CMParms.parse("EMOTE \""+claim.substring(1).trim()+"\""),Command.METAFLAG_FORCED);
		else
			mob.doCommand(CMParms.parse("YELL \""+claim+"\""),Command.METAFLAG_FORCED);

		final Room thisRoom=mob.location();
		final Vector rooms=new Vector();
		List<MOB> assMOBS=assisters.get(mob);
		if(assMOBS==null)
		{
			assMOBS=new Vector();
			assisters.put(mob,assMOBS);
		}
		for(int a=0;a<assMOBS.size();a++)
		{
			final MOB M=assMOBS.get(a);
			if((M!=null)
			&&(M.mayIFight(mob.getVictim()))
			&&(M!=mob.getVictim())
			&&(M.location()!=null)
			&&(CMLib.flags().aliveAwakeMobileUnbound(M,true)
			&&(!M.isInCombat())
			&&(!BrotherHelper.isBrother(mob.getVictim(),M,false))
			&&(canFreelyBehaveNormal(M))
			&&(!CMLib.flags().isATrackingMonster(M))
			&&(CMLib.flags().canHear(M))))
			{
				if(M.location()==thisRoom)
					CMLib.combat().postAttack(M,mob.getVictim(),M.fetchWieldedItem());
				else
				{
					final int dir=CMLib.tracking().radiatesFromDir(M.location(),rooms);
					if(dir>=0)
						CMLib.tracking().walk(M,dir,false,false);
				}
				assistance++;
			}
		}

		if(assistance>=maxAssistance)
			return true;

		TrackingLibrary.TrackingFlags flags;
		flags = new TrackingLibrary.TrackingFlags()
				.plus(TrackingLibrary.TrackingFlag.OPENONLY)
				.plus(TrackingLibrary.TrackingFlag.AREAONLY);
		CMLib.tracking().getRadiantRooms(thisRoom,rooms,flags,null,radius,null);
		for(int r=0;r<rooms.size();r++)
		{
			final Room R=(Room)rooms.elementAt(r);
			if(R.getArea().Name().equals(thisRoom.getArea().Name()))
				for(int i=0;i<R.numInhabitants();i++)
				{
					final MOB M=R.fetchInhabitant(i);
					if((M!=null)
					&&(M.mayIFight(mob.getVictim()))
					&&(M!=mob.getVictim())
					&&(CMLib.flags().aliveAwakeMobileUnbound(M,true)
					&&(!M.isInCombat())
					&&((CMLib.flags().isMobile(M))||(M.location()==thisRoom))
					&&(!assMOBS.contains(M))
					&&(canFreelyBehaveNormal(M))
					&&(!BrotherHelper.isBrother(mob.getVictim(),M,false))
					&&(CMLib.masking().maskCheck(getCityguardZapper(),M,false))
					&&(!CMLib.flags().isATrackingMonster(M))
					&&(CMLib.flags().canHear(M))))
					{
						boolean notAllowed=false;
						for(final MOB hostM : assisters.keySet())
						{
							final List<MOB> assers = assisters.get(hostM);
							if(assers.contains(M))
							{ notAllowed=true; break;}
						}
						if(!notAllowed)
						{
							assMOBS.add(M);
							if(M.location()==thisRoom)
								CMLib.combat().postAttack(M,mob.getVictim(),M.fetchWieldedItem());
							else
							{
								final int dir=CMLib.tracking().radiatesFromDir(M.location(),rooms);
								if(dir>=0)
									CMLib.tracking().walk(M,dir,false,false);
							}
							assistance++;
						}
					}
					if(assistance>=maxAssistance)
						return true;
				}
		}
		return true;
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		super.tick(ticking,tickID);
		if(canAct(ticking,tickID))
		{
			if(ticking instanceof MOB)
				assistMOB((MOB)ticking);
			else
			if(ticking instanceof Room)
				for(int i=0;i<((Room)ticking).numInhabitants();i++)
					assistMOB(((Room)ticking).fetchInhabitant(i));
			else
			if(ticking instanceof Area)
				for(final Enumeration r=((Area)ticking).getMetroMap();r.hasMoreElements();)
				{
					final Room R=(Room)r.nextElement();
					for(int i=0;i<R.numInhabitants();i++)
						assistMOB(R.fetchInhabitant(i));
				}
		}
		return true;
	}
}
