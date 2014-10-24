package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.HashSet;
import java.util.Set;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class Prop_ScrapExplode extends Property {

	@Override public String ID() { return "Prop_ScrapExplode"; }
	@Override public String name() { return "Scrap Explode"; }
	@Override protected int canAffectCode() { return Ability.CAN_ITEMS; }

	@Override
	public void executeMsg(Environmental myHost, CMMsg affect)
	{
		super.executeMsg(myHost, affect);
		if((affect.target()!=null)&&(affect.target().equals(affected))
		   &&(affect.tool()!=null)&&(affect.tool().ID().equals("Scrapping")))
		{
			final Item item=(Item)affect.target();
			final MOB mob = affect.source();
			if (mob != null)
			{
				final Room room = mob.location();
				final int damage = 3 * item.phyStats().weight();
				CMLib.combat().postDamage(mob, mob, item, damage*2,  CMMsg.MASK_ALWAYS|CMMsg.TYP_FIRE, Weapon.TYPE_PIERCING,
						"Scrapping " + item.Name() + " causes an explosion which <DAMAGE> <T-NAME>!!!");
				final Set<MOB> theBadGuys=mob.getGroupMembers(new HashSet<MOB>());
				for (final Object element : theBadGuys)
				{
					final MOB inhab=(MOB)element;
					if (mob != inhab)
						CMLib.combat().postDamage(inhab, inhab, item, damage, CMMsg.MASK_ALWAYS|CMMsg.TYP_FIRE, Weapon.TYPE_PIERCING,
								"Fragments from " + item.Name() + " <DAMAGE> <T-NAME>!");
				}
				room.recoverRoomStats();
			}
			item.destroy();
		}
	}
}
