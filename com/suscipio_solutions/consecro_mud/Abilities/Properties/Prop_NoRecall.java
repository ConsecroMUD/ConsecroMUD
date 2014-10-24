package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Rideable;


public class Prop_NoRecall extends Property
{
	@Override public String ID() { return "Prop_NoRecall"; }
	@Override public String name(){ return "Recall Neuralizing";}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS|Ability.CAN_AREAS|Ability.CAN_ITEMS;}

	@Override public long flags(){return Ability.FLAG_ZAPPER;}

	@Override
	public String accountForYourself()
	{ return "No Recall Field";	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(msg.sourceMinor()==CMMsg.TYP_RECALL)
		{
			if((msg.source()!=null)
			&&(msg.source().location()!=null))
			{
				if(((myHost instanceof MOB)&&((msg.source()==myHost)||(msg.source().location()==((MOB)myHost).location()))&&(msg.source().isInCombat()))
				||((myHost instanceof Rideable)&&(msg.source().riding()==myHost))
				||((myHost instanceof Item)&&(msg.source()==((Item)myHost).owner()))
				||((myHost instanceof Room)&&(msg.source().location()==myHost))
				||(myHost instanceof Exit)
				||(myHost instanceof Area))
					msg.source().location().show(msg.source(),null,CMMsg.MSG_OK_ACTION,L("<S-NAME> attempt(s) to recall, but the magic fizzles."));
				return false;
			}
		}
		return super.okMessage(myHost,msg);
	}
}
