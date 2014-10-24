package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Bomb_AcidBurst extends StdBomb
{
	@Override public String ID() { return "Bomb_AcidBurst"; }
	private final static String localizedName = CMLib.lang().L("acid burst bomb");
	@Override public String name() { return localizedName; }
	@Override protected int trapLevel(){return 20;}
	@Override public String requiresToSet(){return "some lemons";}

	@Override
	public List<Item> getTrapComponents()
	{
		final Vector V=new Vector();
		V.addElement(CMLib.materials().makeItemResource(RawMaterial.RESOURCE_LEMONS));
		return V;
	}
	@Override
	public boolean canSetTrapOn(MOB mob, Physical P)
	{
		if(!super.canSetTrapOn(mob,P)) return false;
		if((!(P instanceof Item))
		||(((Item)P).material()!=RawMaterial.RESOURCE_LEMONS))
		{
			if(mob!=null)
				mob.tell(L("You need some lemons to make this out of."));
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
				target.location().show(target,null,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> avoid(s) the acid burst!"));
			else
			if(target.location().show(invoker(),target,this,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("@x1 sprays acid all over <T-NAME>!",affected.name())))
			{
				super.spring(target);
				CMLib.combat().postDamage(invoker(),target,null,CMLib.dice().roll(trapLevel()+abilityCode(),24,1),CMMsg.MASK_ALWAYS|CMMsg.TYP_ACID,Weapon.TYPE_MELTING,"The acid <DAMAGE> <T-NAME>!");
			}
		}
	}

}
