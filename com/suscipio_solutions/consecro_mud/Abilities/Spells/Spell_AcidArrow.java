package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Spell_AcidArrow extends Spell
{
	@Override public String ID() { return "Spell_AcidArrow"; }
	private final static String localizedName = CMLib.lang().L("Acid Arrow");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Acid Arrow)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int maxRange(){return adjustedMaxInvokerRange(2);}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_CONJURATION;}
	@Override public long flags(){return Ability.FLAG_EARTHBASED;}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((tickID==Tickable.TICKID_MOB)
		&&(affected!=null)
		&&(affected instanceof MOB))
		{
			final MOB vic=(MOB)affected;
			if((!vic.amDead())&&(vic.location()!=null))
				CMLib.combat().postDamage(invoker,vic,this,CMLib.dice().roll(2,4+super.getXLEVELLevel(invoker())+(2*super.getX1Level(invoker())),0),CMMsg.MASK_ALWAYS|CMMsg.TYP_ACID,-1,"<T-NAME> sizzle(s) from the acid arrow residue!");
		}
		return super.tick(ticking,tickID);
	}
	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;
		Room R=CMLib.map().roomLocation(target);
		if(R==null) R=mob.location();

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,somanticCastCode(mob,target,auto),L(auto?"An arrow made of acid appears zooming towards <T-NAME>!":"^S<S-NAME> point(s) at <T-NAMESELF>, conjuring an acid arrow!^?")+CMLib.protocol().msp("spelldam1.wav",40));
			final CMMsg msg2=CMClass.getMsg(mob,target,this,CMMsg.MSK_CAST_MALICIOUS_VERBAL|CMMsg.TYP_ACID|(auto?CMMsg.MASK_ALWAYS:0),null);
			if((R.okMessage(mob,msg))&&((R.okMessage(mob,msg2))))
			{
				R.send(mob,msg);
				R.send(mob,msg2);
				invoker=mob;
				final int numDice = (adjustedLevel(mob,asLevel)+(2*super.getX1Level(invoker())))/2;
				int damage = CMLib.dice().roll(1, numDice+10, 5);
				if((msg2.value()>0)||(msg.value()>0))
					damage = (int)Math.round(CMath.div(damage,2.0));
				CMLib.combat().postDamage(mob,target,this,damage,CMMsg.MASK_ALWAYS|CMMsg.TYP_ACID,Weapon.TYPE_MELTING,"The acidic blast <DAMAGES> <T-NAME>!");
				maliciousAffect(mob,target,asLevel,3,-1);
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> point(s) and conjur(s) at <T-NAMESELF>, but nothing more happens."));

		return success;
	}
}
