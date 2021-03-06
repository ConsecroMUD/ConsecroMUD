package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Spell_ContinualLight extends Spell
{
	@Override public String ID() { return "Spell_ContinualLight"; }
	private final static String localizedName = CMLib.lang().L("Continual Light");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Continual Light)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_SELF;}
	@Override protected int canTargetCode(){return CAN_MOBS|CAN_ITEMS;}
	@Override protected int canAffectCode(){return CAN_MOBS|CAN_ITEMS;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_EVOCATION;}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		if(!(affected instanceof Room))
			affectableStats.setDisposition(affectableStats.disposition()|PhyStats.IS_LIGHTSOURCE);
		if(CMLib.flags().isInDark(affected))
			affectableStats.setDisposition(affectableStats.disposition()-PhyStats.IS_DARK);
	}
	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		final Room room=((MOB)affected).location();
		if(canBeUninvoked())
			room.show(mob,null,CMMsg.MSG_OK_VISUAL,L("The light above <S-NAME> dims."));
		super.unInvoke();
		if(canBeUninvoked())
			room.recoverRoomStats();
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if((mob==target)&&(!CMLib.flags().canBeSeenBy(mob.location(),mob)))
				return super.castingQuality(mob, target,Ability.QUALITY_BENEFICIAL_SELF);
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		Physical target=null;
		if(commands.size()==0) target=mob;
		else
		target=getAnyTarget(mob,commands,givenTarget,Wearable.FILTER_UNWORNONLY);

		if(target==null) return false;
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			String str=L("^S<S-NAME> invoke(s) a continual light toward(s) <T-NAMESELF>!^?");
			if(!(target instanceof MOB))
				str=L("^S<S-NAME> invoke(s) a continual light into <T-NAME>!^?");
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),str);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,Ability.TICKS_FOREVER);
				mob.location().recoverRoomStats(); // attempt to handle followers
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> attempt(s) to invoke light, but fail(s)."));

		return success;
	}
}
