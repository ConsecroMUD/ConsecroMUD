package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings("rawtypes")
public class Spell_Light extends Spell
{
	@Override public String ID() { return "Spell_Light"; }
	private final static String localizedName = CMLib.lang().L("Light");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Light)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_SELF;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_EVOCATION;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(!CMLib.flags().canBeSeenBy(mob.location(), mob))
				return super.castingQuality(mob, target,Ability.QUALITY_BENEFICIAL_SELF);
		}
		return super.castingQuality(mob,target);
	}

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
		final Room room=CMLib.map().roomLocation(affected);
		if(canBeUninvoked()&&(room!=null)&&(affected instanceof MOB))
			room.show((MOB)affected,null,CMMsg.MSG_OK_VISUAL,L("The light above <S-NAME> dims."));
		super.unInvoke();
		if(canBeUninvoked()&&(room!=null))
			room.recoverRoomStats();
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;
		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(target,null,null,L("<S-NAME> already <S-HAS-HAVE> light."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		final Room room=mob.location();
		if((success)&&(room!=null))
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("^S<S-NAME> attain(s) a light above <S-HIS-HER> head!"):L("^S<S-NAME> invoke(s) a white light above <S-HIS-HER> head!^?"));
			if(room.okMessage(mob,msg))
			{
				room.send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
				room.recoverRoomStats();
			}
		}
		else
			beneficialWordsFizzle(mob,mob.location(),L("<S-NAME> attempt(s) to invoke light, but fail(s)."));

		return success;
	}
}
