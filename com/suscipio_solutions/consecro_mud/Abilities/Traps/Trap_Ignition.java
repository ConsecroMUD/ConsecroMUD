package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Trap_Ignition extends StdTrap
{
	@Override public String ID() { return "Trap_Ignition"; }
	private final static String localizedName = CMLib.lang().L("ignition trap");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int trapLevel(){return 8;}
	@Override public String requiresToSet(){return "a container of lamp oil";}

	protected Item getPoison(MOB mob)
	{
		if(mob==null) return null;
		if(mob.location()==null) return null;
		for(int i=0;i<mob.location().numItems();i++)
		{
			final Item I=mob.location().getItem(i);
			if((I!=null)
			&&(I instanceof Drink)
			&&(((((Drink)I).containsDrink())
					&&(((Drink)I).liquidType()==RawMaterial.RESOURCE_LAMPOIL))
						||(I.material()==RawMaterial.RESOURCE_LAMPOIL)))
				return I;
		}
		return null;
	}

	@Override
	public Trap setTrap(MOB mob, Physical P, int trapBonus, int qualifyingClassLevel, boolean perm)
	{
		if(P==null) return null;
		final Item I=getPoison(mob);
		if((I!=null)&&(I instanceof Drink))
		{
			((Drink)I).setLiquidHeld(0);
			I.destroy();
		}
		return super.setTrap(mob,P,trapBonus,qualifyingClassLevel,perm);
	}
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
		final Item I=getPoison(mob);
		if((I==null)
		&&(mob!=null))
		{
			mob.tell(L("You'll need to set down a container of lamp oil first."));
			return false;
		}
		return true;
	}
	@Override
	public void spring(MOB target)
	{
		if((target!=invoker())&&(target.location()!=null))
		{
			if((doesSaveVsTraps(target))
			||(invoker().getGroupMembers(new HashSet<MOB>()).contains(target)))
				target.location().show(target,null,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> avoid(s) setting off a trap!"));
			else
			if(target.location().show(target,target,this,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> set(s) off a trap! @x1 ignites!",CMStrings.capitalizeAndLower(affected.name()))))
			{
				super.spring(target);
				final Ability B=CMClass.getAbility("Burning");
				if(B!=null)
					B.invoke(invoker(),affected,true,(trapLevel()/5)+abilityCode());
				if(affected instanceof Item)
				{
					if(target.isMine(affected))
					{
						target.location().show(target,affected,null,CMMsg.MSG_DROP,L("<S-NAME> drop(s) the burning <T-NAME>!"));
						if(target.isMine(affected))
							target.location().moveItemTo((Item)affected,ItemPossessor.Expire.Player_Drop);
					}
					if(canBeUninvoked())
						disable();
				}
			}
		}
	}
}
