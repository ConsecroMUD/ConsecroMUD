package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.TriggeredAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Prop_HaveSpellCast extends Prop_SpellAdder
{
	@Override public String ID() { return "Prop_HaveSpellCast"; }
	@Override public String name(){ return "Casting spells when owned";}
	@Override protected int canAffectCode(){return Ability.CAN_ITEMS;}
	protected Item myItem=null;

	@Override public int triggerMask() { return TriggeredAffect.TRIGGER_GET; }

	@Override public long flags(){return Ability.FLAG_CASTER;}

	@Override
	public String accountForYourself()
	{ return spellAccountingsWithMask("Casts "," on the owner.");}

	@Override
	public void setAffectedOne(Physical P)
	{
		if(P==null)
		{
			if((lastMOB instanceof MOB)
			&&(((MOB)lastMOB).location()!=null))
				removeMyAffectsFromLastMOB();
		}
		super.setAffectedOne(P);
	}

	@Override
	public void executeMsg(Environmental host, CMMsg msg)
	{}

	@Override
	public void affectPhyStats(Physical host, PhyStats affectableStats)
	{
		if(processing) return;
		processing=true;
		if(host instanceof Item)
		{
			myItem=(Item)host;

			if((lastMOB instanceof MOB)
			&&((myItem.owner()!=lastMOB)||(myItem.amDestroyed()))
			&&(((MOB)lastMOB).location()!=null))
				removeMyAffectsFromLastMOB();

			if((lastMOB==null)
			&&(myItem.owner() instanceof MOB)
			&&(((MOB)myItem.owner()).location()!=null))
				addMeIfNeccessary(myItem.owner(),myItem.owner(),true,0,maxTicks);
		}
		processing=false;
	}
}
