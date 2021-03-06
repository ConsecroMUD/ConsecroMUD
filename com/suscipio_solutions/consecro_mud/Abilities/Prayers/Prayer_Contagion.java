package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.DiseaseAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class Prayer_Contagion extends Prayer implements DiseaseAffect
{
	@Override public String ID() { return "Prayer_Contagion"; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Contagion)");
	@Override public String displayText() { return localizedStaticDisplay; }
	private final static String localizedName = CMLib.lang().L("Contagion");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_CORRUPTION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_MALICIOUS;}
	@Override public long flags(){return Ability.FLAG_HOLY|Ability.FLAG_UNHOLY;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int difficultyLevel(){return 0;}

	@Override
	public String getHealthConditionDesc()
	{
		return ""; // not really a health condition, more of a mystical one
	}

	@Override
	public void unInvoke()
	{
		if(affected==null) return;
		final MOB mob=(MOB)affected;
		if(canBeUninvoked())
		{
			if(!mob.amDead())
				spreadImmunity(mob);
			mob.tell(L("The contagion fades."));
		}
		super.unInvoke();
	}

	@Override public int spreadBitmap(){return DiseaseAffect.SPREAD_PROXIMITY;}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;

		if(affected==null) return false;
		if(!(affected instanceof MOB)) return false;
		final MOB mob=(MOB)affected;
		if(mob.location().numInhabitants()==1)
			return true;
		final Vector choices=new Vector();
		for(final Enumeration<Ability> a=mob.effects();a.hasMoreElements();)
		{
			final Ability A=a.nextElement();
			if((A!=null)
			   &&(A.canBeUninvoked())
			   &&(!A.ID().equals(ID()))
			   &&(A.abstractQuality()==Ability.QUALITY_MALICIOUS)
			   &&(((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_SPELL)
				  ||((A.classificationCode()&Ability.ALL_ACODES)==Ability.ACODE_PRAYER))
			   &&(!A.isAutoInvoked()))
				choices.addElement(A);
		}
		if(choices.size()==0) return true;
		final MOB target=mob.location().fetchRandomInhabitant();
		final Ability thisOne=(Ability)choices.elementAt(CMLib.dice().roll(1,choices.size(),-1));
		if((target==null)||(thisOne==null)||(target.fetchEffect(ID())!=null))
			return true;
		if(CMLib.dice().rollPercentage()>(target.charStats().getSave(CharStats.STAT_SAVE_DISEASE)))
		{
			((Ability)this.copyOf()).invoke(target,target,true,0);
			if(target.fetchEffect(ID())!=null)
				((Ability)thisOne.copyOf()).invoke(target,target,true,0);
		}
		else
			spreadImmunity(target);
		return true;
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		final MOB target=this.getTarget(mob,commands,givenTarget);
		if(target==null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.

			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto)|CMMsg.MASK_MALICIOUS,auto?L("<T-NAME> become(s) contagious!"):L("^S<S-NAME> @x1 for a contagion to inflict <T-NAMESELF>.^?",prayWord(mob)));
			final CMMsg msg2=CMClass.getMsg(mob,target,this,CMMsg.TYP_DISEASE|CMMsg.MASK_MALICIOUS,null);
			if((mob.location().okMessage(mob,msg))&&(mob.location().okMessage(mob,msg2)))
			{
				mob.location().send(mob,msg);
				mob.location().send(mob,msg2);
				if((msg.value()<=0)&&(msg2.value()<=0))
					success=maliciousAffect(mob,target,asLevel,0,-1)!=null;
				else
					spreadImmunity(target);
			}
		}
		else
			return maliciousFizzle(mob,target,L("<S-NAME> point(s) at <T-NAMESELF> and @x1, but nothing happens.",prayWord(mob)));


		// return whether it worked
		return success;
	}
}
