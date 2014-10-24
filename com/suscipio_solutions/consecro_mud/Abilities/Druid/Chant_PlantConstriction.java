package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharState;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class Chant_PlantConstriction extends Chant
{
	@Override public String ID() { return "Chant_PlantConstriction"; }
	private final static String localizedName = CMLib.lang().L("Plant Constriction");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PLANTCONTROL;}
	private final static String localizedStaticDisplay = CMLib.lang().L("(Plant Constriction)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int maxRange(){return adjustedMaxInvokerRange(10);}
	@Override public int minRange(){return 0;}
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override public boolean bubbleAffect(){return true;}
	@Override protected int canAffectCode(){return CAN_ITEMS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}

	@Override
	public void unInvoke()
	{
		Item I=null;
		if(affected instanceof Item)
			I=(Item)affected;
		super.unInvoke();
		if((canBeUninvoked())&&(I!=null)&&(I.owner() instanceof MOB)
		&&(!I.amWearingAt(Wearable.IN_INVENTORY)))
		{
			final MOB mob=(MOB)I.owner();
			if((!mob.amDead())
			&&(CMLib.flags().isInTheGame(mob,false)))
			{
				mob.tell(L("@x1 loosens its grip on you and falls off.",I.name(mob)));
				I.setRawWornCode(0);
				mob.location().moveItemTo(I,ItemPossessor.Expire.Player_Drop);
			}
		}
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		Item I=null;
		if(affected instanceof Item)
			I=(Item)affected;
		if((canBeUninvoked())&&(I!=null)&&(I.owner() instanceof MOB)
		&&(I.amWearingAt(Wearable.WORN_LEGS)||I.amWearingAt(Wearable.WORN_ARMS)))
		{
			final MOB mob=(MOB)I.owner();
			if((!mob.amDead())
			&&(mob.isMonster())
			&&(CMLib.flags().isInTheGame(mob,false)))
				CMLib.commands().postRemove(mob,I,false);
		}
		return super.tick(ticking,tickID);
	}

	@Override
	public boolean okMessage(Environmental host, CMMsg msg)
	{
		if(!super.okMessage(host,msg)) return false;
		if((msg.targetMinor()==CMMsg.TYP_REMOVE)
		&&(msg.target()==affected)
		&&(affected instanceof Item)
		&&(((Item)affected).amWearingAt(Wearable.WORN_LEGS)||((Item)affected).amWearingAt(Wearable.WORN_ARMS)))
		{
			if(CMLib.dice().rollPercentage()>(msg.source().charStats().getStat(CharStats.STAT_STRENGTH)*4))
			{
				msg.source().location().show(msg.source(),affected,CMMsg.MSG_OK_VISUAL,L("<S-NAME> struggle(s) to remove <T-NAME> and fail(s)."));
				return false;
			}
		}
		return true;
	}

	@Override
	public void affectPhyStats(Physical aff, PhyStats affectableStats)
	{
		if((aff instanceof MOB)&&(affected instanceof Item)
		&&(((MOB)aff).isMine(affected))
		&&((Item)affected).amWearingAt(Wearable.WORN_ARMS))
			affectableStats.setSpeed(affectableStats.speed()/2.0);
	}

	@Override
	public void affectCharState(MOB aff, CharState affectableState)
	{
		if((affected instanceof Item)
		&&(aff.isMine(affected))
		&&((Item)affected).amWearingAt(Wearable.WORN_LEGS))
			affectableState.setMovement(affectableState.getMovement()/2);
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			final Item myPlant=Druid_MyPlants.myPlant(mob.location(),mob,0);
			if(myPlant==null)
				return Ability.QUALITY_INDIFFERENT;
			if(target instanceof MOB)
			{
				final Vector positionChoices=new Vector();
				if(((MOB)target).getWearPositions(Wearable.WORN_ARMS)>0)
					positionChoices.addElement(Long.valueOf(Wearable.WORN_ARMS));
				if(((MOB)target).getWearPositions(Wearable.WORN_LEGS)>0)
					positionChoices.addElement(Long.valueOf(Wearable.WORN_LEGS));
				if(positionChoices.size()==0)
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
		Item myPlant=Druid_MyPlants.myPlant(mob.location(),mob,0);
		if(myPlant==null)
		{
			if(auto)
				myPlant=new Chant_SummonPlants().buildPlant(mob,mob.location());
			else
			{
				mob.tell(L("There doesn't appear to be any of your plants here to choke with."));
				return false;
			}
		}
		final Vector positionChoices=new Vector();
		if(target.getWearPositions(Wearable.WORN_ARMS)>0)
			positionChoices.addElement(Long.valueOf(Wearable.WORN_ARMS));
		if(target.getWearPositions(Wearable.WORN_LEGS)>0)
			positionChoices.addElement(Long.valueOf(Wearable.WORN_LEGS));
		if(positionChoices.size()==0)
		{
			if(!auto)
				mob.tell(L("Ummm, @x1 doesn't have arms or legs to constrict...",target.name(mob)));
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

			final CMMsg msg = CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> chant(s) at <T-NAME> while pointing at @x1!^?",myPlant.name()));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				target.moveItemTo(myPlant);
				final Long II=(Long)positionChoices.elementAt(CMLib.dice().roll(1,positionChoices.size(),-1));
				myPlant.setRawWornCode(II.longValue());
				if(II.longValue()==Wearable.WORN_ARMS)
					mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("@x1 jumps up and wraps itself around <S-YOUPOSS> arms!",myPlant.name()));
				else
					mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("@x1 jumps up and wraps itself around <S-YOUPOSS> legs!",myPlant.name()));
				beneficialAffect(mob,myPlant,asLevel,20);
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> chant(s) at <T-NAME>, but the magic fizzles."));

		// return whether it worked
		return success;
	}
}
