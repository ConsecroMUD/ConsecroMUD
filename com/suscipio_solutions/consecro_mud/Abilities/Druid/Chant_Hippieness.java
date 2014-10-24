package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Clan;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Container;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Light;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.collections.Pair;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings("rawtypes")
public class Chant_Hippieness extends Chant
{
	@Override public String ID() { return "Chant_Hippieness"; }
	private final static String localizedName = CMLib.lang().L("Hippieness");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Feeling Groovy)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_ENDURING;}
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	protected List<Pair<Clan,Integer>> oldClans=null;

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		affectableStats.setStat(CharStats.STAT_WISDOM,affectableStats.getStat(CharStats.STAT_WISDOM)-2);
		if(affectableStats.getStat(CharStats.STAT_WISDOM)<1)
			affectableStats.setStat(CharStats.STAT_WISDOM,1);
		for(final Pair<Clan,Integer> p : affected.clans())
			oldClans.add(p);
		affected.setClan("",Integer.MIN_VALUE); // deletes all clans
	}

	@Override
	public boolean okMessage(Environmental host, CMMsg msg)
	{
		if(affected instanceof MOB)
		{
			for(final Pair<Clan,Integer> p : ((MOB)affected).clans())
				oldClans.add(p);
			((MOB)affected).setClan("",Integer.MIN_VALUE); // deletes all clans
		}

		if((msg.source()==affected)
		&&(msg.tool() instanceof Ability)
		&&(!msg.tool().ID().equals("FoodPrep"))
		&&(!msg.tool().ID().equals("Cooking"))
		&&(((((Ability)msg.tool()).classificationCode()&Ability.ALL_DOMAINS)==Ability.DOMAIN_CRAFTINGSKILL)
			||((((Ability)msg.tool()).classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_COMMON_SKILL)))
		{
			msg.source().tell(L("No, man... work is so bourgeois..."));
			return false;
		}
		return super.okMessage(host,msg);
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if(affected instanceof MOB)
		{
			final MOB mob=(MOB)affected;
			for(final Pair<Clan,Integer> p : mob.clans())
				oldClans.add(p);
			mob.setClan("",Integer.MIN_VALUE); // deletes all clans

			final boolean mouthed=mob.fetchFirstWornItem(Wearable.WORN_MOUTH)!=null;
			final Room R=mob.location();
			if((!mouthed)&&(R!=null)&&(R.numItems()>0))
			{
				final Item I=R.getRandomItem();
				if((I!=null)&&(I.fitsOn(Wearable.WORN_MOUTH)))
					CMLib.commands().postGet(mob,I.container(),I,false);
			}

			Ability A=mob.fetchEffect("Fighter_Bezerk");
			if(A!=null) A.unInvoke();
			A=mob.fetchEffect("Song_Rage");
			if(A!=null) A.unInvoke();

			if(mob.numItems()>0)
			{
				final Item I=mob.getRandomItem();
				if(mouthed)
				{
					if((I!=null)&&(!I.amWearingAt(Wearable.IN_INVENTORY))&&(!I.amWearingAt(Wearable.WORN_MOUTH)))
						CMLib.commands().postRemove(mob,I,false);
				}
				else
				if((I!=null)&&(I instanceof Light)&&(I.fitsOn(Wearable.WORN_MOUTH)))
				{
					if((I instanceof Container)
					&&(((Container)I).containTypes()==Container.CONTAIN_SMOKEABLES)
					&&(((Container)I).getContents().size()==0))
					{
						final Item smoke=CMClass.getItem("GenResource");
						if(smoke!=null)
						{
							smoke.setName(L("some smoke"));
							smoke.setDescription(L("Looks liefy and green."));
							smoke.setDisplayText(L("some smoke is sitting here."));
							smoke.setMaterial(RawMaterial.RESOURCE_HEMP);
							smoke.basePhyStats().setWeight(1);
							smoke.setBaseValue(25);
							smoke.recoverPhyStats();
							smoke.text();
							mob.addItem(smoke);
							smoke.setContainer((Container)I);
						}
					}
					mob.doCommand(CMParms.parse("WEAR \""+I.Name()+"\""),Command.METAFLAG_FORCED);
				}
				else
				if((I!=null)&&(!I.amWearingAt(Wearable.IN_INVENTORY))&&(!I.amWearingAt(Wearable.WORN_MOUTH)))
					CMLib.commands().postRemove(mob,I,false);
			}
		}
		return true;
	}


	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		super.unInvoke();

		if(canBeUninvoked())
		{
			for(final Pair<Clan,Integer> p : oldClans)
				mob.setClan(p.first.clanID(),p.second.intValue());
			mob.tell(L("You don't feel quite so groovy."));
		}
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(mob.isInCombat())
				return Ability.QUALITY_INDIFFERENT;
			if(target instanceof MOB)
			{
				if(CMLib.flags().isAnimalIntelligence((MOB)target))
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=getTarget(mob,commands,givenTarget);
		if(target==null) return false;
		if(CMLib.flags().isAnimalIntelligence(target))
		{
			mob.tell(L("@x1 is not smart enough to be a hippy.",target.name(mob)));
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			invoker=mob;
			final CMMsg msg=CMClass.getMsg(mob,target,this,(target.isMonster()?0:CMMsg.MASK_MALICIOUS)|verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> chant(s) to <T-NAMESELF>!^?"));
			final CMMsg msg2=CMClass.getMsg(mob,target,this,(target.isMonster()?0:CMMsg.MASK_MALICIOUS)|CMMsg.MSK_CAST_VERBAL|CMMsg.TYP_DISEASE|(auto?CMMsg.MASK_ALWAYS:0),null);
			if((mob.location().okMessage(mob,msg))&&(mob.location().okMessage(mob,msg2)))
			{
				mob.location().send(mob,msg);
				mob.location().send(mob,msg2);
				if((msg.value()<=0)&&(msg2.value()<=0))
				{
					oldClans=new LinkedList<Pair<Clan,Integer>>();
					for(final Pair<Clan,Integer> p : target.clans())
						oldClans.add(p);
					target.setClan("",Integer.MIN_VALUE); // deletes all clans
					CMLib.commands().postSay(target,null,L("Far out..."),false,false);
					maliciousAffect(mob,target,asLevel,0,verbalCastMask(mob,target,auto)|CMMsg.TYP_MIND);
				}
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> chant(s) to <T-NAMESELF>, but nothing more happens."));

		// return whether it worked
		return success;
	}
}
