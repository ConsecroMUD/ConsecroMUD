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
public class Bomb_Spores extends StdBomb
{
	@Override public String ID() { return "Bomb_Spores"; }
	private final static String localizedName = CMLib.lang().L("spore bomb");
	@Override public String name() { return localizedName; }
	@Override protected int trapLevel(){return 15;}
	@Override public String requiresToSet(){return "some diseased meat";}

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
	public boolean canSetTrapOn(MOB mob, Physical P)
	{
		if(!super.canSetTrapOn(mob,P)) return false;
		final List<Ability> V=returnOffensiveAffects(P);
		if((!(P instanceof Food))||(V.size()==0))
		{
			if(mob!=null)
				mob.tell(L("You need some diseased meat to make this out of."));
			return false;
		}
		return true;
	}
	@Override
	public Trap setTrap(MOB mob, Physical P, int trapBonus, int qualifyingClassLevel, boolean perm)
	{
		if(P==null) return null;
		final List<Ability> V=returnOffensiveAffects(P);
		if(V.size()>0)
			setMiscText(V.get(0).ID());
		return super.setTrap(mob,P,trapBonus,qualifyingClassLevel,perm);
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
				target.location().show(target,null,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> avoid(s) the poison gas!"));
			else
			if(target.location().show(invoker(),target,this,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("@x1 spews poison gas all over <T-NAME>!",affected.name())))
			{
				super.spring(target);
				Ability A=CMClass.getAbility(text());
				if(A==null) A=CMClass.getAbility("Disease_Cold");
				if(A!=null)
					A.invoke(invoker(),target,true,0);
			}
		}
	}

}
