package com.suscipio_solutions.consecro_mud.Abilities.Skills;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.TimeManager;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.Deity;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.collections.PairVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Skill_Convert extends StdSkill
{
	@Override public String ID() { return "Skill_Convert"; }
	private final static String localizedName = CMLib.lang().L("Convert");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"CONVERT"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_EVANGELISM;}
	protected static PairVector<MOB,Long> convertStack=new PairVector<MOB,Long>();
	@Override public int overrideMana(){return 50;}
	@Override public String displayText(){return "";}
	protected String priorFaith="";

	@Override
	public void unInvoke()
	{
		// undo the affects of this spell
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();

		if(canBeUninvoked())
		{
			if(text().length()>0)
				mob.tell(L("You start to have doubts about @x1.",text()));
			mob.setWorshipCharID(priorFaith);
		}
	}
	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((text().length()>0)&&(affected instanceof MOB)&&(!text().equals(((MOB)affected).getWorshipCharID())))
			((MOB)affected).setWorshipCharID(text());
		return super.tick(ticking,tickID);
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(commands.size()==0)
		{
			mob.tell(L("You must specify either a deity to convert yourself to, or a player to convert to your religion."));
			if(mob.isMonster())
				CMLib.commands().postSay(mob,null,L("I am unable to convert."),false,false);
			return false;
		}

		MOB target=mob;
		Deity D=CMLib.map().getDeity(CMParms.combine(commands,0));
		if(D==null)
		{
			D=mob.getMyDeity();
			target=getTarget(mob,commands,givenTarget,false,true);
			if(target==null)
			{
				mob.tell(L("You've also never heard of a deity called '@x1'.",CMParms.combine(commands,0)));
				if(mob.isMonster())
					CMLib.commands().postSay(mob,target,L("I've never heard of '@x1'.",CMParms.combine(commands,0)),false,false);
				return false;
			}
			if(D==null)
			{
				mob.tell(L("A faithless one cannot convert @x1.",target.name(mob)));
				if(mob.isMonster())
					CMLib.commands().postSay(mob,target,L("I am faithless, and can not convert you."),false,false);
				return false;
			}
		}
		if((CMLib.flags().isAnimalIntelligence(target))
		||((target.isMonster())&&(target.phyStats().level()>mob.phyStats().level())))
		{
			mob.tell(L("You can't convert @x1.",target.name(mob)));
			if(mob.isMonster())
				CMLib.commands().postSay(mob,target,L("I can not convert you."),false,false);
			return false;
		}
		if(target.getMyDeity()==D)
		{
			mob.tell(L("@x1 already worships @x2.",target.name(mob),D.name()));
			if(mob.isMonster())
				CMLib.commands().postSay(mob,target,L("You already worship @x1.",D.Name()),false,false);
			return false;
		}
		if(!auto)
		{
			if(convertStack.containsFirst(target))
			{
				final Long L=convertStack.getSecond(convertStack.indexOfFirst(target));
				if((System.currentTimeMillis()-L.longValue())>CMProps.getMillisPerMudHour()*5)
					convertStack.removeElementFirst(target);
			}
			if(convertStack.containsFirst(target))
			{
				mob.tell(L("@x1 must wait to be converted again.",target.name(mob)));
				if(mob.isMonster())
					CMLib.commands().postSay(mob,target,L("You must wait to be converted again."),false,false);
				return false;
			}
		}

		final boolean success=proficiencyCheck(mob,0,auto);
		boolean targetMadeSave=CMLib.dice().roll(1,100,0)>(target.charStats().getSave(CharStats.STAT_FAITH));
		if(CMSecurity.isASysOp(mob)) targetMadeSave=false;
		if((!target.isMonster())&&(success)&&(targetMadeSave)&&(target.getMyDeity()!=null))
		{
			mob.tell(L("@x1 is worshipping @x2.  @x3 must REBUKE @x4 first.",target.name(mob),target.getMyDeity().name(),target.charStats().HeShe(),target.getMyDeity().charStats().himher()));
			if(mob.isMonster())
				CMLib.commands().postSay(mob,target,L("You already worship @x1.",target.getMyDeity().Name()),false,false);
			return false;
		}
		if((success)&&(targetMadeSave)&&(!target.isMonster())&&(target!=mob))
		{
			try
			{
				if(!target.session().confirm(L("\n\r@x1 is trying to convert you to the worship of @x2.  Is this what you want (N/y)?",mob.name(target),D.name()),L("N")))
				{
					mob.location().show(mob,target,CMMsg.MSG_SPEAK,L("<S-YOUPOSS> attempt to convert <T-NAME> to the worship of @x1 is rejected.",D.name()));
					return false;
				}
				targetMadeSave=!success;
			}
			catch(final Exception e)
			{
				return false;
			}
		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		if((success)&&((!targetMadeSave)||(target==mob)))
		{
			Room dRoom=D.location();
			if(dRoom==mob.location()) dRoom=null;
			if(target.getMyDeity()!=null)
			{
				final Ability A=target.fetchEffect(ID());
				if(A!=null){ A.unInvoke(); target.delEffect(A);}
				final CMMsg msg2=CMClass.getMsg(target,D,this,CMMsg.MSG_REBUKE,null);
				if((mob.location().okMessage(mob,msg2))&&((dRoom==null)||(dRoom.okMessage(mob,msg2))))
				{
					mob.location().send(target,msg2);
					if(dRoom!=null) dRoom.send(target,msg2);
				}
			}
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_SPEAK,auto?L("<T-NAME> <T-IS-ARE> converted!"):L("<S-NAME> convert(s) <T-NAMESELF> to the worship of @x1.",D.name()));
			final CMMsg msg2=CMClass.getMsg(target,D,this,CMMsg.MSG_SERVE,null);
			if((mob.location().okMessage(mob,msg))
			   &&(mob.location().okMessage(mob,msg2))
			   &&((dRoom==null)||(dRoom.okMessage(mob,msg2))))
			{
				mob.location().send(mob,msg);
				mob.location().send(target,msg2);
				if(dRoom!=null)
					dRoom.send(target,msg2);
				convertStack.addElement(target,Long.valueOf(System.currentTimeMillis()));
				if(mob!=target)
					if(target.isMonster())
						CMLib.leveler().postExperience(mob,null,null,1,false);
					else
						CMLib.leveler().postExperience(mob,null,null,200,false);
				if(target.isMonster())
				{
					beneficialAffect(mob,target,asLevel,(int)(TimeManager.MILI_HOUR/CMProps.getTickMillis()));
					final Skill_Convert A=(Skill_Convert)target.fetchEffect(ID());
					if(A!=null) A.priorFaith=target.getWorshipCharID();
				}

			}
		}
		else
		{
			if((target.isMonster())&&(target.fetchEffect("Prayer_ReligiousDoubt")==null))
			{
				final Ability A=CMClass.getAbility("Prayer_ReligiousDoubt");
				if(A!=null) A.invoke(mob,target,true,asLevel);
			}
			else
				beneficialWordsFizzle(mob,target,L("<S-NAME> attempt(s) to convert <T-NAMESELF>, but <S-IS-ARE> unconvincing."));
		}

		// return whether it worked
		return success;
	}

	@Override
	public void makeLongLasting()
	{
		tickDown=(int)(CMProps.getTicksPerMinute()*60*24*7);
	}
}
