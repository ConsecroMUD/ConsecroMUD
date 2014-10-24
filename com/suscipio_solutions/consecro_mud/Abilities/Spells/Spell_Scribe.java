package com.suscipio_solutions.consecro_mud.Abilities.Spells;
import java.util.Enumeration;
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
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings("rawtypes")
public class Spell_Scribe extends Spell
{
	@Override public String ID() { return "Spell_Scribe"; }
	private final static String localizedName = CMLib.lang().L("Scribe");
	@Override public String name() { return localizedName; }
	@Override protected int canTargetCode(){return CAN_ITEMS;}
	@Override public int classificationCode(){return Ability.ACODE_SPELL|Ability.DOMAIN_EVOCATION;}
	@Override protected int overrideMana(){return Ability.COST_ALL;}
	@Override public long flags(){return Ability.FLAG_NOORDERING;}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(commands.size()<2)
		{
			mob.tell(L("Scribe which spell onto what?"));
			return false;
		}
		final Physical target=mob.location().fetchFromMOBRoomFavorsItems(mob,null,(String)commands.lastElement(),Wearable.FILTER_UNWORNONLY);
		if((target==null)||(!CMLib.flags().canBeSeenBy(target,mob)))
		{
			mob.tell(L("You don't see '@x1' here.",((String)commands.lastElement())));
			return false;
		}
		if(!(target instanceof Scroll))
		{
			mob.tell(L("You can't scribe onto that."));
			return false;
		}

		commands.removeElementAt(commands.size()-1);
		final Scroll scroll=(Scroll)target;

		final String spellName=CMParms.combine(commands,0).trim();
		Spell scrollThis=null;
		for(final Enumeration<Ability> a=mob.allAbilities();a.hasMoreElements();)
		{
			final Ability A=a.nextElement();
			if((A!=null)
			&&(A instanceof Spell)
			&&(A.name().equalsIgnoreCase(spellName))
			&&(!A.ID().equals(this.ID())))
				scrollThis=(Spell)A;
		}
		if(scrollThis==null)
			for(final Enumeration<Ability> a=mob.allAbilities();a.hasMoreElements();)
			{
				final Ability A=a.nextElement();
				if((A!=null)
				&&(A instanceof Spell)
				&&(CMLib.english().containsString(A.name(),spellName))
				&&(!A.ID().equals(this.ID())))
					scrollThis=(Spell)A;
			}
		if(scrollThis==null)
		{
			mob.tell(L("You don't know how to scribe '@x1'.",spellName));
			return false;
		}
		if(CMLib.ableMapper().lowestQualifyingLevel(scrollThis.ID())>24)
		{
			mob.tell(L("That spell is too powerful to scribe."));
			return false;
		}

		int numSpells=(CMLib.ableMapper().qualifyingClassLevel(mob,this)-CMLib.ableMapper().qualifyingLevel(mob,this));
		if(numSpells<0) numSpells=1;
		if(scroll.getSpells().size()>numSpells)
		{
			mob.tell(L("You aren't powerful enough to scribe any more spells onto @x1.",scroll.name()));
			return false;
		}

		final List<Ability> spells=scroll.getSpells();
		for(final Ability spell: spells)
			if(spell.ID().equals(scrollThis.ID()))
			{
				mob.tell(L("That spell is already scribed onto @x1.",scroll.name()));
				return false;
			}

		int experienceToLose=10*CMLib.ableMapper().lowestQualifyingLevel(scrollThis.ID());
		if((mob.getExperience()-experienceToLose)<0)
		{
			mob.tell(L("You don't have enough experience to cast this spell."));
			return false;
		}
		// lose all the mana!
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		experienceToLose=getXPCOSTAdjustment(mob,experienceToLose);
		CMLib.leveler().postExperience(mob,null,null,-experienceToLose,false);
		mob.tell(L("You lose @x1 experience points for the effort.",""+experienceToLose));

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			setMiscText(scrollThis.ID());
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),L("^S<S-NAME> move(s) <S-HIS-HER> fingers around <T-NAMESELF>, incanting softly.^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				if(scroll.getSpellList().trim().length()==0)
					scroll.setSpellList(scrollThis.ID());
				else
					scroll.setSpellList(scroll.getSpellList()+";"+scrollThis.ID());
				if((scroll.usesRemaining()==Integer.MAX_VALUE)||(scroll.usesRemaining()<0))
					scroll.setUsesRemaining(0);
				scroll.setUsesRemaining(scroll.usesRemaining()+1);
			}

		}
		else
			beneficialWordsFizzle(mob,target,L("<S-NAME> move(s) <S-HIS-HER> fingers around <T-NAMESELF>, incanting softly, and looking very frustrated."));


		// return whether it worked
		return success;
	}
}
