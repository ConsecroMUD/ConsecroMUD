package com.suscipio_solutions.consecro_mud.Abilities.Thief;
import java.util.Enumeration;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Trap;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.ListingLibrary;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


@SuppressWarnings({"unchecked","rawtypes"})
public class Thief_MakeBomb extends ThiefSkill
{
	@Override public String ID() { return "Thief_MakeBomb"; }
	private final static String localizedName = CMLib.lang().L("Make Bombs");
	@Override public String name() { return localizedName; }
	@Override protected int canAffectCode(){return 0;}
	@Override protected int canTargetCode(){return Ability.CAN_ITEMS;}
	@Override public int classificationCode(){return Ability.ACODE_THIEF_SKILL|Ability.DOMAIN_TRAPPING;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	private static final String[] triggerStrings =I(new String[] {"BOMB"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int usageType(){return USAGE_MOVEMENT|USAGE_MANA;}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		Trap theTrap=null;
		final Vector traps=new Vector();
		final int qualifyingClassLevel=CMLib.ableMapper().qualifyingClassLevel(mob,this)+(getXLEVELLevel(mob));
		for(final Enumeration<Ability> a=CMClass.abilities();a.hasMoreElements();)
		{
			final Ability A=a.nextElement();
			if((A instanceof Trap)
			   &&(((Trap)A).isABomb())
			   &&(((Trap)A).maySetTrap(mob,qualifyingClassLevel)))
				traps.addElement(A);
		}
		final int colWidth=ListingLibrary.ColFixer.fixColWidth(15,mob.session());
		Physical trapThis=givenTarget;
		if(trapThis!=null)
			theTrap=(Trap)traps.elementAt(CMLib.dice().roll(1,traps.size(),-1));
		else
		if(CMParms.combine(commands,0).equalsIgnoreCase("list"))
		{
			final StringBuffer buf=new StringBuffer(L("@x1 Requires\n\r",CMStrings.padRight(L("Bomb Name"),colWidth)));
			for(int r=0;r<traps.size();r++)
			{
				final Trap T=(Trap)traps.elementAt(r);
				buf.append(CMStrings.padRight(T.name(),colWidth)+" ");
				buf.append(T.requiresToSet()+"\n\r");
			}
			if(mob.session()!=null) mob.session().rawPrintln(buf.toString());
			return true;
		}
		else
		{
			if(commands.size()<2)
			{
				mob.tell(L("Make a bomb from what, with what kind of bomb? Use bomb list for a list."));
				return false;
			}
			final String name=(String)commands.lastElement();
			commands.removeElementAt(commands.size()-1);
			for(int r=0;r<traps.size();r++)
			{
				final Trap T=(Trap)traps.elementAt(r);
				if(CMLib.english().containsString(T.name(),name))
					theTrap=T;
			}
			if(theTrap==null)
			{
				mob.tell(L("'@x1' is not a valid bomb name.  Try BOMB LIST.",name));
				return false;
			}

			trapThis=this.getAnyTarget(mob,commands,givenTarget,Wearable.FILTER_UNWORNONLY);
			if(trapThis==null) return false;
			if((!auto)&&(!theTrap.canSetTrapOn(mob,trapThis)))
				return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		boolean success=proficiencyCheck(mob,+((mob.phyStats().level()+(getXLEVELLevel(mob)*3)
											 -trapThis.phyStats().level())*3),auto);
		final Trap theOldTrap=CMLib.utensils().fetchMyTrap(trapThis);
		if(theOldTrap!=null)
		{
			if(theOldTrap.disabled())
				success=false;
			else
			{
				theOldTrap.spring(mob);
				return false;
			}
		}

		final CMMsg msg=CMClass.getMsg(mob,trapThis,this,auto?CMMsg.MSG_OK_ACTION:CMMsg.MSG_THIEF_ACT,CMMsg.MASK_ALWAYS|CMMsg.MSG_THIEF_ACT,CMMsg.MSG_OK_ACTION,(auto?L("@x1 begins to glow!",trapThis.name()):L("<S-NAME> attempt(s) to make a bomb out of <T-NAMESELF>.")));
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			if(success)
			{
				mob.tell(L("You have completed your task."));
				theTrap.setTrap(mob,trapThis,getXLEVELLevel(mob),adjustedLevel(mob,asLevel),false);
			}
			else
			{
				if(CMLib.dice().rollPercentage()>50)
				{
					final Trap T=theTrap.setTrap(mob,trapThis,getXLEVELLevel(mob),adjustedLevel(mob,asLevel),false);
					mob.location().show(mob,null,CMMsg.MSG_OK_ACTION,L("<S-NAME> set(s) the bomb off on accident!"));
					T.spring(mob);
				}
				else
				{
					mob.tell(L("You fail in your attempt."));
				}
			}
		}
		return success;
	}
}
