package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Scroll;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Skill_SongWrite extends BardSkill
{
	@Override public String ID() { return "Skill_SongWrite"; }
	private final static String localizedName = CMLib.lang().L("Song Write");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return CAN_ITEMS;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"SONGWRITE"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_CALLIGRAPHY;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(commands.size()<2)
		{
			mob.tell(L("Write which song onto what?"));
			return false;
		}
		final Environmental target=mob.location().fetchFromMOBRoomFavorsItems(mob,null,(String)commands.lastElement(),Wearable.FILTER_UNWORNONLY);
		if((target==null)||(!CMLib.flags().canBeSeenBy(target,mob)))
		{
			mob.tell(L("You don't see '@x1' here.",((String)commands.lastElement())));
			return false;
		}
		if(!(target instanceof Scroll))
		{
			mob.tell(L("You can't write music on that."));
			return false;
		}
		if((mob.curState().getMana()<mob.maxState().getMana())&&(!auto))
		{
			mob.tell(L("You need to be at full mana to cast this."));
			return false;
		}

		commands.removeElementAt(commands.size()-1);
		final Scroll scroll=(Scroll)target;

		final String spellName=CMParms.combine(commands,0).trim();
		Song scrollThis=null;
		for(int a=0;a<mob.numAbilities();a++)
		{
			final Ability A=mob.fetchAbility(a);
			if((A!=null)
			&&(A instanceof Song)
			&&(A.name().toUpperCase().startsWith(spellName.toUpperCase()))
			&&(!A.ID().equals(this.ID())))
				scrollThis=(Song)A;
		}
		if(scrollThis==null)
		{
			mob.tell(L("You don't know how to write '@x1'.",spellName));
			return false;
		}
		int numSpells=(CMLib.ableMapper().qualifyingClassLevel(mob,this)+(2*getXLEVELLevel(mob))-CMLib.ableMapper().qualifyingLevel(mob,this));
		if(numSpells<0) numSpells=0;
		if(scroll.getSpells().size()>numSpells)
		{
			mob.tell(L("You aren't powerful enough to write any more magic onto @x1.",scroll.name()));
			return false;
		}

		final List<Ability> spells=scroll.getSpells();
		for(final Ability spell: spells)
			if(spell.ID().equals(scrollThis.ID()))
			{
				mob.tell(L("That spell is already written on @x1.",scroll.name()));
				return false;
			}

		// lose all the mana!
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		if(!auto)mob.curState().setMana(0);

		int experienceToLose=20*CMLib.ableMapper().lowestQualifyingLevel(scrollThis.ID());
		experienceToLose=getXPCOSTAdjustment(mob,experienceToLose);
		CMLib.leveler().postExperience(mob,null,null,-experienceToLose,false);
		mob.tell(L("You lose @x1 experience points for the effort.",""+experienceToLose));

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			setMiscText(scrollThis.ID());
			final CMMsg msg=CMClass.getMsg(mob,target,this,(auto?CMMsg.MASK_ALWAYS:0)|CMMsg.MSG_DELICATE_SMALL_HANDS_ACT,L("^S<S-NAME> write(s) music onto <T-NAMESELF>, singing softly.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(scroll.getSpellList().length()==0)
					scroll.setSpellList(scrollThis.ID());
				else
					scroll.setSpellList(scroll.getSpellList()+";"+scrollThis.ID());
				if((scroll.usesRemaining()==Integer.MAX_VALUE)||(scroll.usesRemaining()<0))
					scroll.setUsesRemaining(0);
				scroll.setUsesRemaining(scroll.usesRemaining()+1);
			}

		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> attempt(s) to write music on <T-NAMESELF>, singing softly, and looking very frustrated."));


		// return whether it worked
		return success;
	}
}
