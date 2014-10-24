package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Bomb_Water extends StdBomb
{
	@Override public String ID() { return "Bomb_Water"; }
	private final static String localizedName = CMLib.lang().L("water bomb");
	@Override public String name() { return localizedName; }
	@Override protected int trapLevel(){return 1;}
	@Override public String requiresToSet(){return "a water container";}

	@Override
	public List<Item> getTrapComponents()
	{
		final Vector V=new Vector();
		V.addElement(CMLib.materials().makeItemResource(RawMaterial.RESOURCE_FRESHWATER));
		return V;
	}
	@Override
	public boolean canSetTrapOn(MOB mob, Physical P)
	{
		if(!super.canSetTrapOn(mob,P)) return false;
		if((!(P instanceof Drink))
		||(((Drink)P).liquidHeld()!=((Drink)P).liquidRemaining())
		||(((Drink)P).liquidType()!=RawMaterial.RESOURCE_FRESHWATER))
		{
			if(mob!=null)
				mob.tell(L("You need a full water container to make this out of."));
			return false;
		}
		return true;
	}
	@Override
	public void spring(MOB target)
	{
		if(target.location()!=null)
		{
			if((target==invoker())
			||(invoker().getGroupMembers(new HashSet<MOB>()).contains(target))
			||(doesSaveVsTraps(target)))
				target.location().show(target,null,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> avoid(s) the water bomb!"));
			else
			if(target.location().show(invoker(),target,this,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("@x1 explodes water all over <T-NAME>!",affected.name())))
			{
				super.spring(target);
				CMLib.utensils().extinguish(invoker(),target,true);
			}
		}
	}

}
