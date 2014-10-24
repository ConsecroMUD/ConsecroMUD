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


@SuppressWarnings({"unchecked","rawtypes"})
public class Prop_ReqClasses extends Property implements TriggeredAffect
{
	@Override public String ID() { return "Prop_ReqClasses"; }
	@Override public String name(){ return "Class Limitations";}
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
		final Vector parms=CMParms.parse(txt.toUpperCase());
		String s;
		for(final Enumeration p=parms.elements();p.hasMoreElements();)
		{
			s=(String)p.nextElement();
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

		final int x=text().toUpperCase().indexOf("ALL");
		int y=text().toUpperCase().indexOf(mob.charStats().displayClassName().toUpperCase());
		if(y<0) y=text().toUpperCase().indexOf(mob.charStats().getCurrentClass().baseClass().toUpperCase());
		if(((x>0)
			&&(text().charAt(x-1)=='-')
			&&((y<=0)
			   ||((y>0)&&(text().charAt(y-1)!='+'))))
		 ||((y>0)&&(text().charAt(y-1)=='-')))
			return false;
		return true;
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected!=null)
		   &&(msg.target()!=null)
		   &&(((msg.target() instanceof Room)&&(msg.targetMinor()==CMMsg.TYP_ENTER))
			  ||((msg.target() instanceof Rideable)&&(msg.targetMinor()==CMMsg.TYP_SIT)))
		   &&(!CMLib.flags().isFalling(msg.source()))
		   &&((msg.amITarget(affected))||(msg.tool()==affected)||(affected instanceof Area)))
		{
			final HashSet H=new HashSet();
			if(noFollow)
				H.add(msg.source());
			else
			{
				msg.source().getGroupMembers(H);
				final HashSet H2=(HashSet)H.clone();
				for(final Iterator e=H2.iterator();e.hasNext();)
					((MOB)e.next()).getRideBuddies(H);
			}
			for(final Iterator e=H.iterator();e.hasNext();)
			{
				final Environmental E=(Environmental)e.next();
				if((E instanceof MOB)
				&&(passesMuster((MOB)E)))
					return super.okMessage(myHost,msg);
			}
			msg.source().tell(L("You are not allowed to go that way."));
			return false;
		}
		return super.okMessage(myHost,msg);
	}
}
