package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Chant_FurCoat extends Chant
{
	@Override public String ID() { return "Chant_FurCoat"; }
	private final static String localizedName = CMLib.lang().L("Fur Coat");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Fur Coat)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_SHAPE_SHIFTING;}
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override protected int canAffectCode(){return CAN_MOBS;}

	Item theArmor=null;

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		if(canBeUninvoked())
		if(theArmor!=null)
		{
			theArmor.destroy();
			mob.location().recoverRoomStats();
		}
		super.unInvoke();
		if(canBeUninvoked())
			if((mob.location()!=null)&&(!mob.amDead()))
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-YOUPOSS> fur coat vanishes."));
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((affected!=null)&&(affected instanceof MOB))
		{
			if((msg.amISource((MOB)affected))||msg.amISource(invoker))
			{
				if(msg.sourceMinor()==CMMsg.TYP_QUIT)
				{
					unInvoke();
					if(msg.source().playerStats()!=null) msg.source().playerStats().setLastUpdated(0);
				}
				else
				if(msg.sourceMinor()==CMMsg.TYP_DEATH)
				{
					unInvoke();
				}
			}
		}
	}
	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;
		if(theArmor==null) return true;

		if((msg.source()==theArmor.owner())
		&&(msg.tool() instanceof Druid_ShapeShift))
		{
			unInvoke();
			return true;
		}

		if((theArmor.amWearingAt(Wearable.IN_INVENTORY)
		||(theArmor.owner()==null)
		||(theArmor.owner() instanceof Room)))
			unInvoke();

		final MOB mob=msg.source();
		if(!msg.amITarget(theArmor))
			return true;
		else
		if((msg.targetMinor()==CMMsg.TYP_REMOVE)
		||(msg.targetMinor()==CMMsg.TYP_GET))
		{
			mob.tell(L("The fur coat cannot be removed from where it is."));
			return false;
		}
		return true;
	}


	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(target instanceof MOB)
			{
				if(Druid_ShapeShift.isShapeShifted((MOB)target))
					return Ability.QUALITY_INDIFFERENT;
				if(((MOB)target).freeWearPositions(Wearable.WORN_TORSO,(short)-2048,(short)0)<=0)
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;

		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(target,null,null,L("<S-NAME> already <S-HAS-HAVE> a fur coat."));
			return false;
		}

		if(Druid_ShapeShift.isShapeShifted(target))
		{
			mob.tell(L("You cannot invoke this chant in your present form."));
			return false;
		}

		if(target.freeWearPositions(Wearable.WORN_TORSO,(short)-2048,(short)0)<=0)
		{
			mob.tell(L("You are already wearing something on your torso!"));
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			invoker=mob;
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("A thick coat of fur appears on <T-NAME>."):L("^S<S-NAME> chant(s) for a thick coat of fur!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				theArmor=CMClass.getArmor("GenArmor");
				theArmor.setName(L("a fur coat"));
				theArmor.setDisplayText("");
				theArmor.setDescription(L("The coat is made of thick black fur."));
				theArmor.setMaterial(RawMaterial.RESOURCE_FUR);
				theArmor.basePhyStats().setArmor(2*CMLib.ableMapper().qualifyingClassLevel(mob,this));
				final long wornCode=(Wearable.WORN_TORSO|Wearable.WORN_ARMS|Wearable.WORN_FEET|Wearable.WORN_WAIST|Wearable.WORN_LEGS);
				theArmor.setRawProperLocationBitmap(wornCode);
				theArmor.setRawLogicalAnd(true);
				for(int i=target.numItems()-1;i>=0;i--)
				{
					final Item I=mob.getItem(i);
					if((I.rawWornCode()&wornCode)>0)
						I.unWear();
				}
				final Ability A=CMClass.getAbility("Prop_WearResister");
				if( A != null )
				{
				  A.setMiscText("cold");
				  theArmor.addNonUninvokableEffect(A);
				}
				theArmor.recoverPhyStats();
				theArmor.text();
				target.addItem(theArmor);
				theArmor.wearAt(wornCode);
				success=beneficialAffect(mob,target,asLevel,0)!=null;
				mob.location().recoverRoomStats();
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> chant(s) for a thick coat of fur, but nothing happen(s)."));

		// return whether it worked
		return success;
	}
}
