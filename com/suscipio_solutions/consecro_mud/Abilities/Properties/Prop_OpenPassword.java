package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Prop_OpenPassword extends Property
{
	@Override public String ID() { return "Prop_OpenPassword"; }
	@Override public String name(){ return "Opening Password";}
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS|Ability.CAN_EXITS;}

	@Override
	public String accountForYourself()
	{ return "";	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((msg.sourceMinor()==CMMsg.TYP_SPEAK)
		&&(affected!=null)
		&&(msg.sourceMessage()!=null)
		&&((msg.sourceMajor()&CMMsg.MASK_MAGIC)==0))
		{
			final int start=msg.sourceMessage().indexOf("\'");
			final int end=msg.sourceMessage().lastIndexOf("\'");
			if((start>0)&&(end>start))
			{
				final String str=msg.sourceMessage().substring(start+1,end).trim();
				final MOB mob=msg.source();
				if(str.equalsIgnoreCase(text())
				&&(text().length()>0)
				&&(mob.location()!=null))
				{
					final Room R=mob.location();
					if(affected instanceof Exit)
					{
						final Exit E=(Exit)affected;
						if(!E.isOpen())
						{
							int dirCode=-1;
							for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
								if(R.getExitInDir(d)==E)
								{ dirCode=d; break;}
							if(dirCode>=0)
							{
								CMMsg msg2=CMClass.getMsg(mob,E,null,CMMsg.MSG_UNLOCK,null);
								CMLib.utensils().roomAffectFully(msg2,R,dirCode);
								msg2=CMClass.getMsg(mob,E,null,CMMsg.MSG_OPEN,L("<T-NAME> opens."));
								CMLib.utensils().roomAffectFully(msg2,R,dirCode);
							}
						}
					}
					else
					if(affected instanceof Container)
					{
						CMMsg msg2=CMClass.getMsg(mob,affected,null,CMMsg.MSG_UNLOCK,null);
						affected.executeMsg(mob,msg2);
						msg2=CMClass.getMsg(mob,affected,null,CMMsg.MSG_OPEN,L("<T-NAME> opens."));
						affected.executeMsg(mob,msg2);
					}
				}
			}
		}
		super.executeMsg(myHost,msg);
	}
}
