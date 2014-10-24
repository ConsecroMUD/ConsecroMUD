package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Food;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Trap_Infected extends StdTrap
{
	@Override public String ID() { return "Trap_Infected"; }
	private final static String localizedName = CMLib.lang().L("infected");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int trapLevel(){return 13;}
	@Override public String requiresToSet(){return "some diseased food";}

	public List<Ability> returnOffensiveAffects(Physical fromMe)
	{
		final Vector offenders=new Vector();

		for(final Enumeration<Ability> a=fromMe.effects();a.hasMoreElements();)
		{
			final Ability A=a.nextElement();
			if((A!=null)&&((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_DISEASE))
				offenders.addElement(A);
		}
		return offenders;
	}

	protected Item getPoison(MOB mob)
	{
		if(mob==null) return null;
		if(mob.location()==null) return null;
		for(int i=0;i<mob.location().numItems();i++)
		{
			final Item I=mob.location().getItem(i);
			if((I!=null)
			&&(I instanceof Food))
			{
				final List<Ability> V=returnOffensiveAffects(I);
				if(V.size()>0)
					return I;
			}
		}
		return null;
	}
	@Override
	public List<Item> getTrapComponents()
	{
		final Vector V=new Vector();
		final Item I=CMLib.materials().makeItemResource(RawMaterial.RESOURCE_MEAT);
		Ability A=CMClass.getAbility(text());
		if(A==null) A=CMClass.getAbility("Disease_Cold");
		I.addNonUninvokableEffect(A);
		V.addElement(I);
		return V;
	}

	@Override
	public Trap setTrap(MOB mob, Physical P, int trapBonus, int qualifyingClassLevel, boolean perm)
	{
		if(P==null) return null;
		final Item I=getPoison(mob);
		if(I!=null)
		{
			final List<Ability> V=returnOffensiveAffects(I);
			if(V.size()>0)
				setMiscText(V.get(0).ID());
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
				mob.tell(L("You'll need to set down some diseased food first."));
			return false;
		}
		return true;
	}
	@Override
	public void spring(MOB target)
	{
		if((target!=invoker())&&(target.location()!=null))
		{
			if((!invoker().mayIFight(target))
			||(isLocalExempt(target))
			||(invoker().getGroupMembers(new HashSet<MOB>()).contains(target))
			||(target==invoker())
			||(doesSaveVsTraps(target)))
				target.location().show(target,null,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> avoid(s) setting off an infectous trap!"));
			else
			if(target.location().show(target,target,this,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> notice(s) that @x1 is infected!",affected.name())))
			{
				super.spring(target);
				Ability A=CMClass.getAbility(text());
				if(A==null) A=CMClass.getAbility("Disease_Cold");
				if(A!=null) A.invoke(invoker(),target,true,0);
				if((canBeUninvoked())&&(affected instanceof Item))
					disable();
			}
		}
	}
}
