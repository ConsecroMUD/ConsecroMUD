package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class Prop_NoOrdering extends Property
{
	@Override public String ID() { return "Prop_NoOrdering"; }
	@Override public String name(){ return "Group/Ordering Neutralizing";}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS|Ability.CAN_AREAS|Ability.CAN_MOBS;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;

		if((msg.targetMinor()==CMMsg.TYP_ORDER)
		&&(msg.source().location()!=null)
		&&(msg.target() instanceof MOB)
		&&((msg.source().location()==affected)
		   ||((affected instanceof Area)&&(((Area)affected).inMyMetroArea(msg.source().location().getArea())))
		   ||(msg.target()==affected))
		&&(!CMSecurity.isAllowed(msg.source(),msg.source().location(),CMSecurity.SecFlag.CMDMOBS)))
		{
			if(affected instanceof MOB)
				msg.source().tell(L("You don't feel very commanding around here."));
			else
				msg.source().tell(msg.source(),msg.target(),null,L("<T-NAME> isn't paying any attention to you."));
			return false;
		}
		return true;
	}
}
