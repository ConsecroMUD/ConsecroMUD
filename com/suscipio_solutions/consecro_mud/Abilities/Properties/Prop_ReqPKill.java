package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.TriggeredAffect;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


public class Prop_ReqPKill extends Property implements TriggeredAffect
{
	@Override public String ID() { return "Prop_ReqPKill"; }
	@Override public String name(){ return "Playerkill ONLY Zone";}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS|Ability.CAN_AREAS|Ability.CAN_EXITS;}

	@Override public long flags(){return Ability.FLAG_ZAPPER;}

	@Override
	public int triggerMask()
	{
		return TriggeredAffect.TRIGGER_ENTER;
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
			if((!msg.source().isMonster())
			   &&(!msg.source().isAttribute(MOB.Attrib.PLAYERKILL)))
			{
				msg.source().tell(L("You must have your playerkill flag set to enter here."));
				return false;
			}
		}
		if((!msg.source().isMonster())
 		&&(!msg.source().isAttribute(MOB.Attrib.PLAYERKILL)))
		{
			final Room R=CMLib.map().roomLocation(msg.source());
			if((R!=null)&&((R==affected)||(R.getArea()==affected)||((affected instanceof Area)&&(((Area)affected).inMyMetroArea(R.getArea())))))
			{
				msg.source().tell(L("Your PLAYERKILL flag is now ON!"));
				msg.source().setAttribute(MOB.Attrib.PLAYERKILL,true);
			}
		}
		return super.okMessage(myHost,msg);
	}
}
