package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_Blademouth extends Spell
{
	@Override public String ID() { return "Spell_Blademouth"; }
	private final static String localizedName = CMLib.lang().L("Blademouth");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(blades in your mouth)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_EVOCATION;}
	public Vector limbsToRemove=new Vector();
	protected boolean noRecurse=false;

	@Override
	public void executeMsg(Environmental host, CMMsg msg)
	{
		if((msg.sourceMinor()==CMMsg.TYP_SPEAK)
		&&(!noRecurse)
		&&(affected instanceof MOB)
		&&(invoker!=null)
		&&(msg.amISource((MOB)affected))
		&&(msg.source().location()!=null)
		&&(msg.source().charStats().getMyRace().bodyMask()[Race.BODY_MOUTH]>=0))
		{
			noRecurse=true;
			try{CMLib.combat().postDamage(invoker,msg.source(),this,msg.source().maxState().getHitPoints()/10,CMMsg.MASK_ALWAYS|CMMsg.TYP_CAST_SPELL,Weapon.TYPE_SLASHING,"The blades in <T-YOUPOSS> mouth <DAMAGE> <T-HIM-HER>!");
			}finally{noRecurse=false;}
		}
		super.executeMsg(host,msg);
	}

	@Override
	public int castingQuality(MOB mob, Physical target)
	{
		if(mob!=null)
		{
			if(target instanceof MOB)
			{
				if(((MOB)target).charStats().getMyRace().bodyMask()[Race.BODY_MOUTH]<=0)
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

		if(target.charStats().getMyRace().bodyMask()[Race.BODY_MOUTH]<=0)
		{
			if(!auto)
				mob.tell(L("There is no mouth on @x1 to fill with blades!",target.name(mob)));
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		// now see if it worked
		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),L(auto?"!":"^S<S-NAME> invoke(s) a sharp spell upon <T-NAMESELF>"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				super.maliciousAffect(mob,target,asLevel,0,-1);
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> incant(s) sharply at <T-NAMESELF>, but flub(s) the spell."));


		// return whether it worked
		return success;
	}
}
