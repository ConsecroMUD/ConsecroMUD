package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Armor;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Food;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Spell_MagicItem extends Spell
{
	@Override public String ID() { return "Spell_MagicItem"; }
	private final static String localizedName = CMLib.lang().L("Magic Item");
	@Override public String name() { return localizedName; }
	@Override protected int canTargetCode(){return CAN_ITEMS;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_ENCHANTMENT;}
	@Override public long flags(){return Ability.FLAG_NOORDERING;}
	@Override protected int overrideMana(){return Ability.COST_ALL;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(commands.size()<2)
		{
			mob.tell(L("Enchant which spell onto what?"));
			return false;
		}
		final Physical target=mob.location().fetchFromMOBRoomFavorsItems(mob,null,(String)commands.lastElement(),Wearable.FILTER_UNWORNONLY);
		if((target==null)||(!CMLib.flags().canBeSeenBy(target,mob)))
		{
			mob.tell(L("You don't see '@x1' here.",((String)commands.lastElement())));
			return false;
		}
		if(!(target instanceof Item))
		{
			mob.tell(mob,target,null,L("You can't enchant <T-NAME>."));
			return false;
		}

		commands.removeElementAt(commands.size()-1);
		final Item wand=(Item)target;

		final String spellName=CMParms.combine(commands,0).trim();
		Spell wandThis=null;
		final Vector ables=new Vector();
		for(final Enumeration<Ability> a=mob.allAbilities();a.hasMoreElements();)
		{
			final Ability A=a.nextElement();
			if((A!=null)
			&&(A instanceof Spell)
			&&((!A.isSavable())||(CMLib.ableMapper().qualifiesByLevel(mob,A)))
			&&(!A.ID().equals(this.ID())))
				ables.addElement(A);
		}
		wandThis = (Spell)CMLib.english().fetchEnvironmental(ables,spellName,true);
		if(wandThis==null)
			wandThis = (Spell)CMLib.english().fetchEnvironmental(ables,spellName,false);
		if(wandThis==null)
		{
			mob.tell(L("You don't know how to enchant anything with '@x1'.",spellName));
			return false;
		}


		if((wandThis.ID().equals("Spell_Stoneskin"))
		||(wandThis.ID().equals("Spell_MirrorImage"))
		||(CMath.bset(wandThis.flags(), FLAG_SUMMONING)))
		{
			mob.tell(L("That spell cannot be used to enchant anything."));
			return false;
		}

		if((CMLib.ableMapper().lowestQualifyingLevel(wandThis.ID())>24)
		||(((StdAbility)wandThis).usageCost(null,true)[0]>45))
		{
			mob.tell(L("That spell is too powerful to enchant into anything."));
			return false;
		}

		if((wand.numEffects()>0)||(!wand.isGeneric()))
		{
			mob.tell(L("You can't enchant '@x1'.",wand.name()));
			return false;
		}

		int experienceToLose=1000;
		experienceToLose+=(100*CMLib.ableMapper().lowestQualifyingLevel(wandThis.ID()));
		if((mob.getExperience()-experienceToLose)<0)
		{
			mob.tell(L("You don't have enough experience to cast this spell."));
			return false;
		}
		// lose all the mana!
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;


		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			experienceToLose=getXPCOSTAdjustment(mob,experienceToLose);
			CMLib.leveler().postExperience(mob,null,null,-experienceToLose,false);
			mob.tell(L("You lose @x1 experience points for the effort.",""+experienceToLose));
			setMiscText(wandThis.ID());
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),L("^S<S-NAME> move(s) <S-HIS-HER> fingers around <T-NAMESELF>, incanting softly.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.location().show(mob,target,null,CMMsg.MSG_OK_VISUAL,L("<T-NAME> glow(s) brightly!"));
				wand.basePhyStats().setDisposition(target.basePhyStats().disposition()|PhyStats.IS_BONUS);
				wand.basePhyStats().setLevel(wand.basePhyStats().level()+(CMLib.ableMapper().lowestQualifyingLevel(wandThis.ID())/2));
				//Vector V=CMParms.parseCommas(CMLib.utensils().wornList(wand.rawProperLocationBitmap()),true);
				if(wand instanceof Armor)
				{
					final Ability A=CMClass.getAbility("Prop_WearSpellCast");
					A.setMiscText("LAYERED;"+wandThis.ID()+";");
					wand.addNonUninvokableEffect(A);
				}
				else
				if(wand instanceof Weapon)
				{
					final Ability A=CMClass.getAbility("Prop_FightSpellCast");
					A.setMiscText("25%;MAXTICKS=12;"+wandThis.ID()+";");
					wand.addNonUninvokableEffect(A);
				}
				else
				if((wand instanceof Food)
				||(wand instanceof Drink))
				{
					final Ability A=CMClass.getAbility("Prop_UseSpellCast2");
					A.setMiscText(wandThis.ID()+";");
					wand.addNonUninvokableEffect(A);
				}
				else
				if(wand.fitsOn(Wearable.WORN_HELD)||wand.fitsOn(Wearable.WORN_WIELD))
				{
					final Ability A=CMClass.getAbility("Prop_WearSpellCast");
					A.setMiscText("LAYERED;"+wandThis.ID()+";");
					wand.addNonUninvokableEffect(A);
				}
				else
				{
					final Ability A=CMClass.getAbility("Prop_WearSpellCast");
					A.setMiscText("LAYERED;"+wandThis.ID()+";");
					wand.addNonUninvokableEffect(A);
				}
				wand.recoverPhyStats();
			}

		}
		else
		{
			experienceToLose=getXPCOSTAdjustment(mob,experienceToLose);
			CMLib.leveler().postExperience(mob,null,null,-experienceToLose,false);
			mob.tell(L("You lose @x1 experience points for the effort.",""+experienceToLose));
			beneficialWordsFizzle(mob,target,L("<S-NAME> move(s) <S-HIS-HER> fingers around <T-NAMESELF>, incanting softly, and looking very frustrated."));
		}


		// return whether it worked
		return success;
	}
}
