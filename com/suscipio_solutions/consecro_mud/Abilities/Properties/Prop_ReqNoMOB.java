package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.TriggeredAffect;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


@SuppressWarnings("rawtypes")
public class Prop_ReqNoMOB extends Property implements TriggeredAffect
{
	@Override public String ID() { return "Prop_ReqNoMOB"; }
	@Override public String name(){ return "Monster Limitations";}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS|Ability.CAN_AREAS|Ability.CAN_EXITS;}
	private boolean noFollow=false;
	private boolean noSneak=false;

	@Override public long flags(){return Ability.FLAG_ZAPPER;}

	@Override
	public int triggerMask()
	{
		return TriggeredAffect.TRIGGER_ENTER;
	}

	@Override
	public void setMiscText(String txt)
	{
		noFollow=false;
		noSneak=false;
		final Vector<String> parms=CMParms.parse(txt.toUpperCase());
		String s;
		for(final Enumeration<String> p=parms.elements();p.hasMoreElements();)
		{
			s=p.nextElement();
			if("NOFOLLOW".startsWith(s))
				noFollow=true;
			else
			if(s.startsWith("NOSNEAK"))
				noSneak=true;
		}
		super.setMiscText(txt);
	}


	public boolean passesMuster(MOB mob)
	{
		if(mob==null) return false;
		if(CMLib.flags().isATrackingMonster(mob))
			return true;
		if(CMLib.flags().isSneaking(mob)&&(!noSneak))
			return true;
		return !mob.isMonster();
	}
	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected!=null)
		&&(msg.target()!=null)
		&&(((msg.target() instanceof Room)&&(msg.targetMinor()==CMMsg.TYP_ENTER))
		   ||((msg.target() instanceof Rideable)&&(msg.targetMinor()==CMMsg.TYP_SIT)))
		&&((msg.amITarget(affected))||(msg.tool()==affected)||(affected instanceof Area))
		&&(!CMLib.flags().isFalling(msg.source())))
		{
			final HashSet<MOB> H=new HashSet<MOB>();
			if(noFollow)
				H.add(msg.source());
			else
			{
				msg.source().getGroupMembers(H);
				int hsize=0;
				while(hsize!=H.size())
				{
					hsize=H.size();
					final HashSet H2=(HashSet)H.clone();
					for(final Iterator e=H2.iterator();e.hasNext();)
					{
						final Object O=e.next();
						if(O instanceof MOB)
							((MOB)O).getRideBuddies(H);
					}
				}
			}
			for (final Object O : H)
			{
				if((!(O instanceof MOB))||(passesMuster((MOB)O)))
					return super.okMessage(myHost,msg);
			}
			msg.source().tell(L("You are not allowed in there."));
			return false;
		}
		return super.okMessage(myHost,msg);
	}
}
