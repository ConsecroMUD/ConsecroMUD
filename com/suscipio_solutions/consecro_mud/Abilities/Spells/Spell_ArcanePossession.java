package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_ArcanePossession extends Spell
{
	@Override public String ID() { return "Spell_ArcanePossession"; }
	private final static String localizedName = CMLib.lang().L("Arcane Possession");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return CAN_ITEMS;}
	@Override protected int canTargetCode(){return CAN_ITEMS;}
	@Override public int classificationCode(){	return Ability.ACODE_SPELL|Ability.DOMAIN_ABJURATION;}
	protected MOB owner=null;

	@Override
	public boolean okMessage(Environmental host, CMMsg msg)
	{
		if(!super.okMessage(host,msg))
			return false;
		if((msg.tool()==affected)
		&&(msg.sourceMinor()==CMMsg.TYP_SELL))
		{
			unInvoke();
			if(affected!=null)	affected.delEffect(this);
		}
		else
		if(msg.amITarget(affected)
		&&(owner!=null)
		&&((msg.targetMinor()==CMMsg.TYP_WEAR)
		   ||(msg.targetMinor()==CMMsg.TYP_HOLD)
		   ||(msg.targetMinor()==CMMsg.TYP_WIELD))
		&&(msg.source()!=owner))
		{
			msg.source().location().show(msg.source(),null,affected,CMMsg.MSG_OK_ACTION,L("<O-NAME> flashes and flies out of <S-HIS-HER> hands!"));
			CMLib.commands().postDrop(msg.source(),affected,true,false,false);
			return false;
		}
		return true;
	}

	@Override
	public void executeMsg(Environmental host, CMMsg msg)
	{
		super.executeMsg(host,msg);
		if((affected instanceof Item)&&(text().length()>0))
		{
			final Item I=(Item)affected;
			if((owner==null)&&(I.owner()!=null)
			&&(I.owner() instanceof MOB)
			&&(I.owner().Name().equals(text())))
				owner=(MOB)I.owner();
		}
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Item target=getTarget(mob,null,givenTarget,commands,Wearable.FILTER_ANY);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> hold(s) a <T-NAMESELF> tightly and cast(s) a spell.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				mob.location().show(mob,target,CMMsg.MSG_OK_VISUAL,L("<T-NAME> glows slightly!"));
				setMiscText(mob.Name());
				beneficialAffect(mob,target,asLevel,0);
				target.recoverPhyStats();
				mob.recoverPhyStats();
			}

		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> hold(s) <T-NAMESELF> tightly and whisper(s), but fail(s) to cast a spell."));


		// return whether it worked
		return success;
	}
}
