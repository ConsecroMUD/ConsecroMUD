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
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


@SuppressWarnings({"unchecked","rawtypes"})
public class Prop_ReqEntry extends Property implements TriggeredAffect
{
	@Override public String ID() { return "Prop_ReqEntry"; }
	@Override public String name(){ return "All Room/Exit Limitations";}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS|Ability.CAN_AREAS|Ability.CAN_EXITS;}
	private boolean noFollow=false;
	private boolean noSneak=false;
	private String maskS="";
	private String message="";

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
		maskS=txt;
		message="";
		final Vector parms=CMParms.parse(txt);
		String s;
		for(final Enumeration p=parms.elements();p.hasMoreElements();)
		{
			s=(String)p.nextElement();
			if("NOFOLLOW".startsWith(s.toUpperCase()))
			{
				maskS=CMStrings.replaceFirst(maskS, s, "");
				noFollow=true;
			}
			else
			if(s.toUpperCase().startsWith("NOSNEAK"))
			{
				maskS=CMStrings.replaceFirst(maskS, s, "");
				noSneak=true;
			}
			else
			if((s.toUpperCase().startsWith("MESSAGE"))
			&&(s.substring(7).trim().startsWith("=")))
			{
				message=s.substring(7).trim().substring(1);
				maskS=CMStrings.replaceFirst(maskS, s, "");
			}
		}
		super.setMiscText(txt);
	}

	@Override
	public String accountForYourself()
	{
		return "Entry restricted as follows: "+CMLib.masking().maskDesc(maskS);
	}

	public boolean passesMuster(MOB mob)
	{
		if(mob==null) return false;
		if(CMLib.flags().isATrackingMonster(mob))
			return true;
		if(CMLib.flags().isSneaking(mob)&&(!noSneak))
			return true;
		return CMLib.masking().maskCheck(maskS,mob,false);
	}
	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected!=null)&&(msg.target()!=null))
		{
			if((msg.target() instanceof Room)
			&&(msg.targetMinor()==CMMsg.TYP_ENTER)
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
					if(passesMuster((MOB)e.next()))
						return super.okMessage(myHost,msg);
				msg.source().tell((message.length()==0)?L("You can not go that way."):message);
				return false;
			}
			else
			if((msg.target() instanceof Rideable)
			&&(msg.amITarget(affected)))
			{
				switch(msg.targetMinor())
				{
				case CMMsg.TYP_SIT:
				case CMMsg.TYP_ENTER:
				case CMMsg.TYP_SLEEP:
				case CMMsg.TYP_MOUNT:
					{
						HashSet H=new HashSet();
						if(noFollow)
							H.add(msg.source());
						else
						{
							msg.source().getGroupMembers(H);
							final HashSet H2=(HashSet)H.clone();
							for(final Iterator e=H.iterator();e.hasNext();)
								((MOB)e.next()).getRideBuddies(H2);
							H=H2;
						}
						for(final Iterator e=H.iterator();e.hasNext();)
						{
							final Environmental E=(Environmental)e.next();
							if((E instanceof MOB)
							&&(passesMuster((MOB)E)))
								return super.okMessage(myHost,msg);
						}
						msg.source().tell((message.length()==0)?L("You are not permitted in there."):message);
						return false;
					}
				default:
					break;
				}
			}
		}
		return super.okMessage(myHost,msg);
	}
}
