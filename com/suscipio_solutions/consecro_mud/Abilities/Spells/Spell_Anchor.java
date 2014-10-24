package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_Anchor extends Spell
{
	@Override public String ID() { return "Spell_Anchor"; }
	private final static String localizedName = CMLib.lang().L("Anchor");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Anchor)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override protected int canAffectCode(){return CAN_MOBS|CAN_ITEMS;}
	@Override public int classificationCode(){	return Ability.ACODE_SPELL|Ability.DOMAIN_ABJURATION;}

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
		{
			super.unInvoke();
			return;
		}
		final MOB mob=(MOB)affected;
		if(canBeUninvoked())
			mob.tell(L("Your anchor has been lifted."));

		super.unInvoke();

	}


	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;

		if(affected==null)	return true;

		if((msg.tool()!=null)
		&&(msg.tool() instanceof Ability)
		&&((affected==null)
			||((affected instanceof Item)&&(!((Item)affected).amWearingAt(Wearable.IN_INVENTORY))&&(msg.amITarget(((Item)affected).owner())))
			||((affected instanceof MOB)&&(msg.amITarget(affected))))
		&&(CMath.bset(((Ability)msg.tool()).flags(),Ability.FLAG_MOVING)
		   ||CMath.bset(((Ability)msg.tool()).flags(),Ability.FLAG_TRANSPORTING)))
		{
			Room roomS=null;
			Room roomD=null;
			if((msg.target()!=null)&&(msg.target() instanceof MOB))
				roomD=((MOB)msg.target()).location();
			else
			if((msg.target()!=null)&&(msg.target() instanceof Item))
			{
				final Item I=(Item)msg.target();
				if((I.owner()!=null)&&(I.owner() instanceof MOB))
					roomD=((MOB)((Item)msg.target()).owner()).location();
				else
				if((I.owner()!=null)&&(I.owner() instanceof Room))
					roomD=(Room)((Item)msg.target()).owner();
			}
			else
			if((msg.target()!=null)&&(msg.target() instanceof Room))
				roomD=(Room)msg.target();

			if((msg.source()!=null)&&(msg.source().location()!=null))
				roomS=msg.source().location();

			if((roomS!=null)&&(roomD!=null)&&(roomS==roomD))
				roomD=null;

			final Ability A=(Ability)msg.tool();
			if(((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_CHANT)
			||((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_SPELL)
			||((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_PRAYER)
			||((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_SONG))
			{
				if(roomS!=null)
					roomS.showHappens(CMMsg.MSG_OK_VISUAL,L("Magic energy fizzles and is absorbed into the air."));
				if(roomD!=null)
					roomD.showHappens(CMMsg.MSG_OK_VISUAL,L("Magic energy fizzles and is absorbed into the air."));
			}
			return false;
		}
		return true;
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),L(auto?"An magical anchoring field envelopes <T-NAME>!":"^S<S-NAME> invoke(s) an anchoring field of protection around <T-NAMESELF>.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> attempt(s) to invoke an anchoring field, but fail(s)."));

		return success;
	}
}
