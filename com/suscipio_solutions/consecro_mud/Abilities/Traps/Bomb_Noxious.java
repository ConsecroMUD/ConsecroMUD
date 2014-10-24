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
public class Bomb_Noxious extends StdBomb
{
	@Override public String ID() { return "Bomb_Noxious"; }
	private final static String localizedName = CMLib.lang().L("stink bomb");
	@Override public String name() { return localizedName; }
	@Override protected int trapLevel(){return 12;}
	@Override public String requiresToSet(){return "an egg";}

	@Override
	public List<Item> getTrapComponents()
	{
		final Vector V=new Vector();
		V.addElement(CMLib.materials().makeItemResource(RawMaterial.RESOURCE_EGGS));
		return V;
	}
	@Override
	public boolean canSetTrapOn(MOB mob, Physical P)
	{
		if(!super.canSetTrapOn(mob,P)) return false;
		if((!(P instanceof Item))
		||(((Item)P).material()!=RawMaterial.RESOURCE_EGGS))
		{
			if(mob!=null)
				mob.tell(L("You an egg to make this out of."));
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
				target.location().show(target,null,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> avoid(s) the stink bomb!"));
			else
			if(target.location().show(invoker(),target,this,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("@x1 explodes stink into <T-YOUPOSS> eyes!",affected.name())))
			{
				super.spring(target);
				Ability A=CMClass.getAbility("Spell_StinkingCloud");
				if(A!=null)
				{
					A.invoke(target,target,true,invoker().phyStats().level()+abilityCode());
					A=target.fetchEffect(A.ID());
					if(A!=null)A.setInvoker(invoker());
				}
			}
		}
	}

}
