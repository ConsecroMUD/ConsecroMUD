package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.TriggeredAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


@SuppressWarnings({"unchecked","rawtypes"})
public class Prop_AbilityImmunity extends Property implements TriggeredAffect
{
	@Override public String ID() { return "Prop_AbilityImmunity"; }
	@Override public String name(){ return "Ability Immunity";}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS|Ability.CAN_ITEMS|Ability.CAN_ROOMS|Ability.CAN_EXITS;}
	@Override public String accountForYourself() { return "Immunity";	}
	protected List<String> diseases=new Vector();
	protected Vector messages=new Vector();
	protected boolean owner = false;
	protected boolean wearer = false;

	@Override
	public int triggerMask()
	{
		return TriggeredAffect.TRIGGER_ALWAYS;
	}

	@Override
	public void setMiscText(String newText)
	{
		messages=new Vector();
		diseases=CMParms.parseSemicolons(newText.toUpperCase(),true);
		owner = false;
		wearer = false;
		for(int d=0;d<diseases.size();d++)
		{
			final String s=diseases.get(d);
			if(s.equalsIgnoreCase("owner"))
				owner=true;
			else
			if(s.equalsIgnoreCase("wearer"))
				wearer=true;
			else
			{
				final int x=s.indexOf('=');
				if(x<0)
					messages.addElement("");
				else
				{
					diseases.set(d,s.substring(0,x).trim());
					messages.addElement(s.substring(x+1).trim());
				}
			}
		}
		super.setMiscText(newText);
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if ( (msg.source() != null)
		&& (msg.target() != null)
		&& (msg.tool() != null)
		&& ((msg.amITarget(affected))
				||(owner && (affected instanceof Item)&&(msg.target()==((Item)affected).owner()))
				||(owner && (affected instanceof Item)&&(msg.target()==((Item)affected).owner())&&(!((Item)affected).amWearingAt(Wearable.IN_INVENTORY))))
		&& (msg.tool() instanceof Ability ))
		{
			final Ability d = (Ability)msg.tool();
			for(int i = 0; i < diseases.size(); i++)
			{
				if((CMLib.english().containsString(d.ID(),diseases.get(i)))
				||(CMLib.english().containsString(d.name(),diseases.get(i))))
				{
					if(msg.target() instanceof MOB)
						((MOB)msg.target()).tell(L("You are immune to @x1.",msg.tool().name()));
					if(msg.source()!=msg.target())
					{
						final String s=(String)messages.elementAt(i);
						if(s.length()>0)
							msg.source().tell(msg.source(),msg.target(),msg.tool(),s);
						else
							msg.source().tell(msg.source(),msg.target(),msg.tool(),L("<T-NAME> seem(s) immune to <O-NAME>."));
					}
					return false;
				}
			}
		}
		return super.okMessage(myHost, msg);
	}
}
