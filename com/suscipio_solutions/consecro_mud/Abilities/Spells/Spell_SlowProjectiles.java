package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.AmmunitionWeapon;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_SlowProjectiles extends Spell
{
	@Override public String ID() { return "Spell_SlowProjectiles"; }
	private final static String localizedName = CMLib.lang().L("Slow Projectiles");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Slow Projectiles)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override protected int canAffectCode(){return CAN_ROOMS;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_ALTERATION;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((msg.targetMinor()==CMMsg.TYP_DAMAGE)
		&&(msg.tool()!=null)
		&&(msg.source().getVictim()==msg.target())
		&&(msg.source().rangeToTarget()>0)
		&&(msg.tool() instanceof Weapon)
		&&(((((Weapon)msg.tool()).weaponClassification()==Weapon.CLASS_RANGED)&&(msg.tool() instanceof AmmunitionWeapon)&&(((AmmunitionWeapon)msg.tool()).requiresAmmunition())
			||(((Weapon)msg.tool()).weaponClassification()==Weapon.CLASS_THROWN)))
		&&(msg.source().location()!=null)
		&&(msg.source().location()==affected)
		&&(!msg.source().amDead()))
		{
			if(((Weapon)msg.tool()).weaponClassification()==Weapon.CLASS_THROWN)
				msg.source().location().show(msg.source(),null,msg.tool(),CMMsg.MSG_OK_VISUAL,L("<O-NAME> flies slowly by."));
			else
				msg.source().location().show(msg.source(),null,CMMsg.MSG_OK_VISUAL,L("The shot from @x1 flies slowly by.",msg.tool().name()));
			final int damage=(msg.value())/2;
			msg.setValue(damage);
		}
		return super.okMessage(myHost,msg);
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final Room target=mob.location();
		if(target==null) return false;

		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(mob,null,null,L("Projectiles are already slow here!"));
			return false;
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> incant(s) slowly.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> attempt(s) to invoke a field of slowness, but fail(s)."));

		return success;
	}
}
