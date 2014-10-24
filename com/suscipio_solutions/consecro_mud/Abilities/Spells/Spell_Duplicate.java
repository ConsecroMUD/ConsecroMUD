package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.ClanItem;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Coins;
import com.suscipio_solutions.consecro_mud.Items.interfaces.ImmortalOnly;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Pill;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Potion;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wand;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_Duplicate extends Spell
{
	@Override public String ID() { return "Spell_Duplicate"; }
	private final static String localizedName = CMLib.lang().L("Duplicate");
	@Override public String name() { return localizedName; }
	@Override protected int canTargetCode(){return CAN_ITEMS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_ALTERATION;}
	@Override protected int overrideMana(){return Ability.COST_ALL;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Item target=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_UNWORNONLY);
		if(target==null) return false;

		if(!mob.isMine(target))
		{
			mob.tell(L("You'll need to pick it up first."));
			return false;
		}
		if(target instanceof ClanItem)
		{
			mob.tell(L("Clan items can not be duplicated."));
			return false;
		}
		if(target instanceof ImmortalOnly)
		{
			mob.tell(L("That item can not be duplicated."));
			return false;
		}

		final int value=(target instanceof Coins)?(int)Math.round(((Coins)target).getTotalValue()):target.value();
		int multiPlier=5+(((target.phyStats().weight())+value)/2);
		multiPlier+=(target.numEffects()*10);
		multiPlier+=(target instanceof Potion)?10:0;
		multiPlier+=(target instanceof Pill)?10:0;
		multiPlier+=(target instanceof Wand)?5:0;

		int level=target.phyStats().level();
		if(level<=0) level=1;
		int expLoss=(level*multiPlier);
		if((mob.getExperience()-expLoss)<0)
		{
			mob.tell(L("You don't have enough experience to cast this spell."));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		expLoss=getXPCOSTAdjustment(mob,-expLoss);
		mob.tell(L("You lose @x1 experience points.",""+(-expLoss)));
		CMLib.leveler().postExperience(mob,null,null,expLoss,false);

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> hold(s) <T-NAMESELF> and cast(s) a spell.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final Item newTarget=(Item)target.copyOf();
				mob.location().show(mob,target,CMMsg.MSG_OK_VISUAL,L("@x1 blurs and divides into two!",target.name()));
				CMLib.utensils().disenchantItem(newTarget);
				if(newTarget.amDestroyed())
					mob.location().show(mob,target,CMMsg.MSG_OK_VISUAL,L("<T-NAME> fades away!"));
				else
				{
					newTarget.recoverPhyStats();
					if(target.owner() instanceof MOB)
						((MOB)target.owner()).addItem(newTarget);
					else
					if(target.owner() instanceof Room)
						((Room)target.owner()).addItem(newTarget,ItemPossessor.Expire.Player_Drop);
					else
						mob.addItem(newTarget);
					if(newTarget instanceof Coins)
						((Coins)newTarget).putCoinsBack();
					else
					if(newTarget instanceof RawMaterial)
						((RawMaterial)newTarget).rebundle();
					target.recoverPhyStats();
					mob.recoverPhyStats();
				}
			}

		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> hold(s) <T-NAMESELF> tightly and incant(s), the spell fizzles."));


		// return whether it worked
		return success;
	}
}
