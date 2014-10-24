package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Amputator;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Spell_LimbRack extends Spell
{
	@Override public String ID() { return "Spell_LimbRack"; }
	private final static String localizedName = CMLib.lang().L("Limb Rack");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Being pulled apart)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public int classificationCode(){ return Ability.ACODE_SPELL|Ability.DOMAIN_EVOCATION;}
	public List<String> limbsToRemove=new Vector<String>();

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if(invoker==null) return false;
		final MOB mob=(MOB)affected;
		if((mob.location()!=null)
		&&(mob.charStats().getMyRace().bodyMask()[Race.BODY_ARM]>=0)
		&&(mob.charStats().getMyRace().bodyMask()[Race.BODY_LEG]>=0))
		{
			final String str=(text().equalsIgnoreCase("ARMSONLY"))?
				L("<T-NAME> <T-IS-ARE> having <T-HIS-HER> arms pulled from <T-HIS-HER> body!"):L("<T-NAME> <T-IS-ARE> having <T-HIS-HER> arms and legs pulled from <T-HIS-HER> body!");
			CMLib.combat().postDamage(invoker,mob,this,mob.maxState().getHitPoints()/(10-(getXLEVELLevel(invoker)/2)),CMMsg.MASK_ALWAYS|CMMsg.TYP_JUSTICE,Weapon.TYPE_BURSTING,str);
		}

		return true;
	}

	@Override
	public void unInvoke()
	{
		if((affected instanceof MOB)
		&&(((MOB)affected).amDead()))
		{
			final MOB mob=(MOB)affected;
			if((mob.location()!=null)
			&&(mob.charStats().getMyRace().bodyMask()[Race.BODY_ARM]>0)
			&&(mob.charStats().getMyRace().bodyMask()[Race.BODY_LEG]>0))
			{
				if(text().equalsIgnoreCase("ARMSONLY"))
					mob.location().show(mob,null,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> has <S-HIS-HER> arms TORN OFF!"));
				else
					mob.location().show(mob,null,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> has <S-HIS-HER> arms and legs TORN OFF!"));
				Amputator A=(Amputator)mob.fetchEffect("Amputation");
				if(A==null) A=(Amputator)CMClass.getAbility("Amputation");
				boolean success=true;
				for(int i=0;i<limbsToRemove.size();i++)
					success=success && (A.amputate(mob,A,limbsToRemove.get(i))!=null);
				if(success)
				{
					if(mob.fetchEffect(A.ID())==null)
						mob.addNonUninvokableEffect(A);
				}
			}
			CMLib.utensils().confirmWearability(mob);
		}
		super.unInvoke();
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		Amputator A=(Amputator)target.fetchEffect("Amputation");
		if(A==null)	A=(Amputator)CMClass.getAbility("Amputation");
		final List<String> remainingLimbList=A.remainingLimbNameSet(target);
		for(int i=remainingLimbList.size()-1;i>=0;i--)
		{
			final String gone=remainingLimbList.get(i);
			if((!gone.toUpperCase().endsWith(" ARM"))
			&&(!gone.toUpperCase().endsWith(" LEG")))
				remainingLimbList.remove(i);
		}
		if((remainingLimbList.size()==0)
		||((target.charStats().getMyRace().bodyMask()[Race.BODY_ARM]<=0)
		&&(target.charStats().getMyRace().bodyMask()[Race.BODY_LEG]<=0)))
		{
			if(!auto)
				mob.tell(L("There is nothing left on @x1 to rack off!",target.name(mob)));
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
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),L(auto?"!":"^S<S-NAME> invoke(s) a stretching spell upon <T-NAMESELF>"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					super.maliciousAffect(mob,target,asLevel,12,-1);
					final Ability A2=target.fetchEffect(ID());
					if(A2!=null)
					{
						((Spell_LimbRack)A2).limbsToRemove=new Vector<String>();
						((Spell_LimbRack)A2).limbsToRemove.addAll(remainingLimbList);
					}
				}
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> incant(s) stretchingly at <T-NAMESELF>, but flub(s) the spell."));


		// return whether it worked
		return success;
	}
}
