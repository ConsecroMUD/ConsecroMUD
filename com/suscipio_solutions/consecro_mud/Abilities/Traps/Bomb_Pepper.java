package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Bomb_Pepper extends StdBomb
{
	@Override public String ID() { return "Bomb_Pepper"; }
	private final static String localizedName = CMLib.lang().L("pepper bomb");
	@Override public String name() { return localizedName; }
	@Override protected int trapLevel(){return 7;}
	@Override public String requiresToSet(){return "some peppers";}

	@Override
	public List<Item> getTrapComponents()
	{
		final Vector V=new Vector();
		V.addElement(CMLib.materials().makeItemResource(RawMaterial.RESOURCE_PEPPERS));
		return V;
	}
	@Override
	public boolean canSetTrapOn(MOB mob, Physical P)
	{
		if(!super.canSetTrapOn(mob,P)) return false;
		if((!(P instanceof Item))
		||(((Item)P).material()!=RawMaterial.RESOURCE_PEPPERS))
		{
			if(mob!=null)
				mob.tell(L("You need some peppers to make this out of."));
			return false;
		}
		return true;
	}
	@Override
	public void spring(MOB target)
	{
		if(target.location()!=null)
		{
			if((!invoker().mayIFight(target))
			||(isLocalExempt(target))
			||(invoker().getGroupMembers(new HashSet<MOB>()).contains(target))
			||(target==invoker())
			||(doesSaveVsTraps(target)))
				target.location().show(target,null,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> avoid(s) the water bomb!"));
			else
			if(target.location().show(invoker(),target,this,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("@x1 explodes water all over <T-NAME>!",affected.name())))
			{
				super.spring(target);
				final Ability A=CMClass.getAbility("Spell_Irritation");
				if(A!=null) A.invoke(target,target,true,invoker().phyStats().level()+abilityCode());
			}
		}
	}

}
