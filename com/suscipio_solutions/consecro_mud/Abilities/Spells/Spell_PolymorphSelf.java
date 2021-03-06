package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Spell_PolymorphSelf extends Spell
{
	@Override public String ID() { return "Spell_PolymorphSelf"; }
	private final static String localizedName = CMLib.lang().L("Polymorph Self");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Polymorph Self)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_TRANSMUTATION;}
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_SELF;}

	Race newRace=null;

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		super.affectPhyStats(affected,affectableStats);
		if(newRace!=null)
		{
			if(affected.name().indexOf(' ')>0)
				affectableStats.setName(L("@x1 called @x2",CMLib.english().startWithAorAn(newRace.name()),affected.name()));
			else
				affectableStats.setName(L("@x1 the @x2",affected.name(),newRace.name()));
			final int oldAdd=affectableStats.weight()-affected.basePhyStats().weight();
			newRace.setHeightWeight(affectableStats,'M');
			if(oldAdd>0) affectableStats.setWeight(affectableStats.weight()+oldAdd);
		}
	}
	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		super.affectCharStats(affected,affectableStats);
		if(newRace!=null)
		{
			final int oldCat=affected.baseCharStats().ageCategory();
			affectableStats.setMyRace(newRace);
			if(affected.baseCharStats().getStat(CharStats.STAT_AGE)>0)
				affectableStats.setStat(CharStats.STAT_AGE,newRace.getAgingChart()[oldCat]);
		}
	}


	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		super.unInvoke();
		if(canBeUninvoked())
			if((mob.location()!=null)&&(!mob.amDead()))
				mob.location().show(mob,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> morph(s) back into <S-HIM-HERSELF> again."));
	}



	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if((auto||mob.isMonster())&&((commands.size()<1)||(((String)commands.firstElement()).equals(mob.name()))))
		{
			commands.clear();
			final XVector<Race> V=new XVector<Race>(CMClass.races());
			for(int v=V.size()-1;v>=0;v--)
				if(!CMath.bset(V.elementAt(v).availabilityCode(),Area.THEME_FANTASY))
					V.removeElementAt(v);
			if(V.size()>0)
				commands.addElement(V.elementAt(CMLib.dice().roll(1,V.size(),-1)).name());
		}
		if(commands.size()==0)
		{
			mob.tell(L("You need to specify what to turn yourself into!"));
			return false;
		}
		final String race=CMParms.combine(commands,0);
		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;
		final Race R=CMClass.getRace(race);
		if((R==null)||(!CMath.bset(R.availabilityCode(),Area.THEME_FANTASY)))
		{
			mob.tell(L("You can't turn yourself into @x1!",CMLib.english().startWithAorAn(race)));
			return false;
		}
		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(target,null,null,L("<S-NAME> <S-IS-ARE> already polymorphed."));
			return false;
		}

		if(target.baseCharStats().getMyRace() != target.charStats().getMyRace())
		{
			mob.tell(target,null,null,L("<S-NAME> <S-IS-ARE> already polymorphed."));
			return false;
		}

		// the invoke method for spells receives as
		// parameters the invoker, and the REMAINING
		// command line parameters, divided into words,
		// and added as String objects to a vector.
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		int mobStatTotal=0;
		for(final int s: CharStats.CODES.BASECODES())
			mobStatTotal+=mob.baseCharStats().getStat(s);

		final MOB fakeMOB=CMClass.getFactoryMOB();
		for(final int s: CharStats.CODES.BASECODES())
			fakeMOB.baseCharStats().setStat(s,mob.baseCharStats().getStat(s));
		fakeMOB.baseCharStats().setMyRace(R);
		fakeMOB.recoverCharStats();
		fakeMOB.recoverPhyStats();
		fakeMOB.recoverMaxState();
		int fakeStatTotal=0;
		for(final int s: CharStats.CODES.BASECODES())
			fakeStatTotal+=fakeMOB.charStats().getStat(s);

		fakeMOB.destroy();
		final int statDiff=mobStatTotal-fakeStatTotal;
		boolean success=proficiencyCheck(mob,(statDiff*5),auto);
		if(success)
		{
			// it worked, so build a copy of this ability,
			// and add it to the affects list of the
			// affected MOB.  Then tell everyone else
			// what happened.
			invoker=mob;
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?"":L("^S<S-NAME> whisper(s) to <T-NAMESELF> about @x1.^?",CMLib.english().makePlural(R.name())));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(msg.value()<=0)
				{
					newRace=R;
					mob.location().show(target,null,CMMsg.MSG_OK_VISUAL,L("<S-NAME> become(s) a @x1!",CMLib.english().startWithAorAn(newRace.name())));
					success=beneficialAffect(mob,target,asLevel,0)!=null;
					target.recoverCharStats();
					CMLib.utensils().confirmWearability(target);
				}
			}
		}
		else
			return beneficialWordsFizzle(mob,target,L("<S-NAME> whisper(s) to <T-NAMESELF>, but the spell fizzles."));

		// return whether it worked
		return success;
	}
}
