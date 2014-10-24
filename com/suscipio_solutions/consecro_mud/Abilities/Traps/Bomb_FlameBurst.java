package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Bomb_FlameBurst extends StdBomb
{
	@Override public String ID() { return "Bomb_FlameBurst"; }
	private final static String localizedName = CMLib.lang().L("flame burst bomb");
	@Override public String name() { return localizedName; }
	@Override protected int trapLevel(){return 17;}
	@Override public String requiresToSet(){return "some lamp oil";}

	@Override
	public List<Item> getTrapComponents()
	{
		final Vector V=new Vector();
		V.addElement(CMClass.getBasicItem("OilFlask"));
		return V;
	}
	@Override
	public boolean canSetTrapOn(MOB mob, Physical P)
	{
		if(!super.canSetTrapOn(mob,P)) return false;
		if((!(P instanceof Item))
		||(!(P instanceof Drink))
		||(!((((Drink)P).containsDrink())||(((Drink)P).liquidType()!=RawMaterial.RESOURCE_LAMPOIL)))
		   &&(((Item)P).material()!=RawMaterial.RESOURCE_LAMPOIL))
		{
			if(mob!=null)
				mob.tell(L("You need some lamp oil to make this out of."));
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
				target.location().show(target,null,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> avoid(s) the flame burst!"));
			else
			if(target.location().show(invoker(),target,this,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,(affected.name()+" flames all over <T-NAME>!")+CMLib.protocol().msp("fireball.wav",30)))
			{
				super.spring(target);
				CMLib.combat().postDamage(invoker(),target,null,CMLib.dice().roll(trapLevel()+abilityCode(),12,1),CMMsg.MASK_ALWAYS|CMMsg.TYP_FIRE,Weapon.TYPE_BURNING,"The flames <DAMAGE> <T-NAME>!");
			}
		}
	}

}
