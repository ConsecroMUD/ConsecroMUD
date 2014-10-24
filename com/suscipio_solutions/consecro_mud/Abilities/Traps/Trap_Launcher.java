package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Trap_Launcher extends StdTrap
{
	@Override public String ID() { return "Trap_Launcher"; }
	private final static String localizedName = CMLib.lang().L("launcher trap");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_EXITS|Ability.CAN_ITEMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int trapLevel(){return 6;}
	@Override public String requiresToSet(){return "a ranged weapon";}

	protected Item getPoison(MOB mob)
	{
		if(mob==null) return null;
		if(mob.location()==null) return null;
		for(int i=0;i<mob.location().numItems();i++)
		{
			final Item I=mob.location().getItem(i);
			if((I!=null)
			&&(I instanceof Weapon)
			&&(((Weapon)I).weaponClassification()==Weapon.CLASS_RANGED))
				return I;
		}
		return null;
	}

	@Override
	public List<Item> getTrapComponents()
	{
		final Vector V=new Vector();
		final Item I=CMClass.getWeapon("StdBow");
		V.addElement(I);
		return V;
	}
	@Override
	public Trap setTrap(MOB mob, Physical P, int trapBonus, int qualifyingClassLevel, boolean perm)
	{
		if(P==null) return null;
		final Item I=getPoison(mob);
		setMiscText("3/a projectile");
		if(I!=null)
		{
			setMiscText(""+I.basePhyStats().damage()+"/"+I.name());
			I.destroy();
		}
		return super.setTrap(mob,P,trapBonus,qualifyingClassLevel,perm);
	}

	@Override
	public boolean canSetTrapOn(MOB mob, Physical P)
	{
		if(!super.canSetTrapOn(mob,P)) return false;
		final Item I=getPoison(mob);
		if(I==null)
		{
			if(mob!=null)
				mob.tell(L("You'll need to set down a ranged weapon first."));
			return false;
		}
		return true;
	}
	@Override
	public void spring(MOB target)
	{
		if((target!=invoker())&&(target.location()!=null))
		{
			final int x=text().indexOf('/');
			int dam=3;
			String name="a projectile";
			if(x>=0)
			{
				dam=CMath.s_int(text().substring(0,x));
				name=text().substring(x+1);
			}
			if((!invoker().mayIFight(target))
			||(isLocalExempt(target))
			||(invoker().getGroupMembers(new HashSet<MOB>()).contains(target))
			||(target==invoker())
			||(doesSaveVsTraps(target)))
				target.location().show(target,null,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> avoid(s) setting off @x1 trap!",name));
			else
			if(target.location().show(target,target,this,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> <S-IS-ARE> struck by @x1 trap!",name)))
			{
				super.spring(target);
				final int damage=CMLib.dice().roll(trapLevel()+abilityCode(),dam,1);
				CMLib.combat().postDamage(invoker(),target,this,damage,CMMsg.MASK_MALICIOUS|CMMsg.MASK_ALWAYS|CMMsg.TYP_JUSTICE,-1,null);
				if((canBeUninvoked())&&(affected instanceof Item))
					disable();
			}
		}
	}
}
