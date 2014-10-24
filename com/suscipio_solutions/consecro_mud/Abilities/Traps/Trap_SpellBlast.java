package com.suscipio_solutions.consecro_mud.Abilities.Traps;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Scroll;
import com.suscipio_solutions.consecro_mud.Items.interfaces.SpellHolder;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Trap_SpellBlast extends StdTrap
{
	@Override public String ID() { return "Trap_SpellBlast"; }
	private final static String localizedName = CMLib.lang().L("spell blast");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return Ability.CAN_EXITS|Ability.CAN_ITEMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override protected int trapLevel(){return 23;}
	@Override public String requiresToSet(){return "a spell scroll";}

	protected Item getPoison(MOB mob)
	{
		if(mob==null) return null;
		if(mob.location()==null) return null;
		for(int i=0;i<mob.location().numItems();i++)
		{
			final Item I=mob.location().getItem(i);
			if((I!=null)
			&&(I instanceof Scroll)
			&&(((SpellHolder)I).getSpells()!=null)
			&&(((SpellHolder)I).getSpells().size()>0)
			&&(I.usesRemaining()>0))
				return I;
		}
		return null;
	}

	@Override
	public List<Item> getTrapComponents()
	{
		final Vector V=new Vector();
		final Scroll I=(Scroll)CMClass.getMiscMagic("StdScroll");
		Ability A=CMClass.getAbility(text());
		if(A==null) A=CMClass.getAbility("Spell_Fireball");
		I.setSpellList(A.ID());
		V.addElement(I);
		return V;
	}
	@Override
	public Trap setTrap(MOB mob, Physical P, int trapBonus, int qualifyingClassLevel, boolean perm)
	{
		if(P==null) return null;
		final Item I=getPoison(mob);
		if((I!=null)&&(I instanceof SpellHolder))
		{
			final List<Ability> V=((SpellHolder)I).getSpells();
			if(V.size()>0)
				setMiscText(V.get(0).ID());
			I.setUsesRemaining(I.usesRemaining()-1);
		}
		return super.setTrap(mob,P,trapBonus,qualifyingClassLevel,perm);
	}

	@Override
	public boolean canSetTrapOn(MOB mob, Physical P)
	{
		if(!super.canSetTrapOn(mob,P)) return false;
		final Item I=getPoison(mob);
		if((I==null)
		&&(mob!=null))
		{
			mob.tell(L("You'll need to set down a scroll with a spell first."));
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
				target.location().show(target,null,null,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> avoid(s) setting off a trap!"));
			else
			if(target.location().show(target,target,this,CMMsg.MASK_ALWAYS|CMMsg.MSG_NOISE,L("<S-NAME> set(s) off a trap!")))
			{
				super.spring(target);
				Ability A=CMClass.getAbility(text());
				if(A==null) A=CMClass.getAbility("Spell_Fireball");
				if(A!=null) A.invoke(invoker(),target,true,0);
				if((canBeUninvoked())&&(affected instanceof Item))
					disable();
			}
		}
	}
}
