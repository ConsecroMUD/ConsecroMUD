package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class Prop_LocationBound extends Property
{
	@Override public String ID() { return "Prop_LocationBound"; }
	@Override public String name(){ return "Leave the specified area, or room";}
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS|Ability.CAN_MOBS;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;

		if((msg.sourceMinor()!=CMMsg.TYP_ENTER)
		&&(msg.target() instanceof Room)
		&&((msg.source()==affected)
			||((affected instanceof Item)&&(msg.source()==((Item)affected).owner()))))
		{
			final Room whereTo=(Room)msg.target();
			final Room R=CMLib.map().roomLocation(affected);
			if((whereTo==null)||(R==null))
				return true;

			if(text().length()==0)
			{
				if(affected instanceof MOB)
					msg.source().tell(L("You are not allowed to leave this place."));
				else
					msg.source().tell(L("@x1 prevents you from taking it that way.",affected.name()));
				return false;
			}
			else
			if(text().equalsIgnoreCase("ROOM"))
			{
				if(whereTo!=R)
				{
					if(affected instanceof MOB)
						msg.source().tell(L("You are not allowed to leave this place."));
					else
						msg.source().tell(L("@x1 prevents you from taking it that way.",affected.name()));
					return false;
				}
			}
			else
			if(text().equalsIgnoreCase("AREA"))
			{
				if(whereTo.getArea()!=R.getArea())
				{
					if(affected instanceof MOB)
						msg.source().tell(L("You are not allowed to leave this place."));
					else
						msg.source().tell(L("@x1 prevents you from taking it that way.",affected.name()));
					return false;
				}
			}
			else
			{
				final Room tR=CMLib.map().getRoom(text());
				if((tR!=null)&&(whereTo!=tR))
				{
					if(R!=tR)
					{
						if(affected instanceof MOB)
						{
							msg.source().tell(L("You are whisked back home!"));
							tR.bringMobHere((MOB)affected,false);
						}
						else
						{
							msg.source().tell(L("@x1 is whisked from you and back to its home.",affected.name()));
							tR.moveItemTo((Item)affected);
							return true;
						}
					}
					else
					{
						if(affected instanceof MOB)
							msg.source().tell(L("You are not allowed to leave this place."));
						else
							msg.source().tell(L("@x1 prevents you from taking it that way.",affected.name()));
					}
					return false;
				}
				final Area A=CMLib.map().getArea(text());
				if((A!=null)&&(!A.inMyMetroArea(whereTo.getArea())))
				{
					if(!A.inMyMetroArea(R.getArea()))
					{
						if(affected instanceof MOB)
						{
							msg.source().tell(L("You are whisked back home!"));
							A.getRandomMetroRoom().bringMobHere((MOB)affected,false);
						}
						else
						{
							msg.source().tell(L("@x1 is whisked from you and back to its home.",affected.name()));
							A.getRandomMetroRoom().moveItemTo((Item)affected);
							return true;
						}
					}
					else
					{
						if(affected instanceof MOB)
							msg.source().tell(L("You are not allowed to leave this place."));
						else
							msg.source().tell(L("@x1 prevents you from taking it that way.",affected.name()));
					}
					return false;
				}
			}
		}
		return true;
	}
}
