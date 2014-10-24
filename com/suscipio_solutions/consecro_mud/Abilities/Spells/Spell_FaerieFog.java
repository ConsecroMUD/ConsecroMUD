package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_FaerieFog extends Spell
{
	@Override public String ID() { return "Spell_FaerieFog"; }
	private final static String localizedName = CMLib.lang().L("Faerie Fog");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Faerie Fog)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_ROOMS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode() {	return Ability.ACODE_SPELL|Ability.DOMAIN_ILLUSION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	private Room theRoom=null;

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(affected==null)
			return;
		if(!(affected instanceof Room))
			return;
		if(canBeUninvoked())
		{
			final Room room=(Room)affected;
			room.showHappens(CMMsg.MSG_OK_VISUAL, L("The faerie fog starts to clear out."));
		}
		super.unInvoke();
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if((affected instanceof MOB)||(affected instanceof Item))
		{
			final Room R=CMLib.map().roomLocation(affected);
			if((R!=null)&&(R==theRoom)&&(!unInvoked)&&(R.fetchEffect(ID())==this))
			{
				if((affectableStats.disposition()&PhyStats.IS_INVISIBLE)==PhyStats.IS_INVISIBLE)
					affectableStats.setDisposition(affectableStats.disposition()-PhyStats.IS_INVISIBLE);
				affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_GLOWING);
				affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_BONUS);
				affectableStats.setArmor(affectableStats.armor()+10);
			}
			else
			{
				affected.delEffect(this);
				affected.recoverPhyStats();
			}
		}
		else
		if((affected instanceof Room)&&(!unInvoked))
		{
			final Room R=(Room)affected;
			theRoom=R;
			MOB M=null;
			for(int i=0;i<R.numInhabitants();i++)
			{
				M=R.fetchInhabitant(i);
				if((M!=null)&&(M.fetchEffect(ID())==null))
				{
					M.addEffect(this);
					setAffectedOne(R);
				}
			}
			Item I=null;
			for(int i=0;i<R.numItems();i++)
			{
				I=R.getItem(i);
				if((I!=null)&&(I.fetchEffect(ID())==null))
				{
					I.addEffect(this);
					setAffectedOne(R);
				}
			}
		}

	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(target instanceof MOB)
			{
				if(!CMLib.flags().isInvisible(target))
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final Physical target = mob.location();

		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(mob,null,null,L("A faerie fog is already here."));
			return false;
		}


		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.

			final CMMsg msg = CMClass.getMsg(mob, target, this, verbalCastCode(mob,target,auto),L((auto?"A ":"^S<S-NAME> speak(s) and gesture(s) and a ")+"sparkling fog envelopes the area.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,mob.location(),asLevel,0);
			}
		}
		else
			return beneficialWordsFizzle(mob,null,L("<S-NAME> mutter(s) about a faerie fog, but the spell fizzles."));

		// return whether it worked
		return success;
	}
}
