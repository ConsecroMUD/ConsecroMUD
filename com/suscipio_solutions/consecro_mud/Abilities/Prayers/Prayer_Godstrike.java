package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
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



@SuppressWarnings("rawtypes")
public class Prayer_Godstrike extends Prayer
{
	@Override public String ID() { return "Prayer_Godstrike"; }
	private final static String localizedName = CMLib.lang().L("Godstrike");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_VEXING;}
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override public long flags(){return Ability.FLAG_HOLY;}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(target instanceof MOB)
			{
				if(!CMLib.flags().isEvil((target)))
					return Ability.QUALITY_INDIFFERENT;
			}
		}
		return super.castingQuality(mob,target);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;
		final boolean undead=target.charStats().getMyRace().racialCategory().equals("Undead");

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if((success)&&(CMLib.flags().isEvil(target)))
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final Prayer_Godstrike newOne=(Prayer_Godstrike)this.copyOf();
			final CMMsg msg=CMClass.getMsg(mob,target,newOne,verbalCastCode(mob,target,auto)|CMMsg.MASK_MALICIOUS,L(auto?"<T-NAME> is filled with holy fury!":"^S<S-NAME> "+prayWord(mob)+" for power against the evil inside <T-NAMESELF>!^?")+CMLib.protocol().msp("spelldam1.wav",40));
			final CMMsg msg2=CMClass.getMsg(mob,target,this,CMMsg.MSK_CAST_MALICIOUS_VERBAL|CMMsg.TYP_JUSTICE|(auto?CMMsg.MASK_ALWAYS:0),null);
			final Room R=target.location();
			if((R.okMessage(mob,msg))&&((R.okMessage(mob,msg2))))
			{
				R.send(mob,msg);
				R.send(mob,msg2);
				int harming=CMLib.dice().roll(3,adjustedLevel(mob,asLevel)+8,adjustedLevel(mob,asLevel));
				if((msg.value()>0)||(msg2.value()>0))
					harming=(int)Math.round(CMath.div(harming,2.0));
				if(undead) harming=harming*2;
				if(CMLib.flags().isEvil(target))
					CMLib.combat().postDamage(mob,target,this,harming,CMMsg.MASK_ALWAYS|CMMsg.TYP_JUSTICE,Weapon.TYPE_BURSTING,"^SThe holy STRIKE of the gods <DAMAGE> <T-NAME>!^?");
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> @x1, but nothing happens.",prayWord(mob)));


		// return whether it worked
		return success;
	}
}
