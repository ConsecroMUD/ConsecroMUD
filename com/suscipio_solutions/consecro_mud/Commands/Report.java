package com.suscipio_solutions.consecro_mud.Commands;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Commands.interfaces.Command;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMProps;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMStrings;


@SuppressWarnings({"unchecked","rawtypes"})
public class Report extends Skills
{
	public Report(){}

	private final String[] access=I(new String[]{"REPORT"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		if(commands.size()<2)
		{
			final StringBuffer buf=new StringBuffer(
								L("say \"I have @x1/@x2 hit points, @x3/@x4 mana, @x5/@x6 move",
									""+mob.curState().getHitPoints(),
									""+mob.maxState().getHitPoints(),
									""+mob.curState().getMana(),
									""+mob.maxState().getMana(),
									""+mob.curState().getMovement(),
									""+mob.maxState().getMovement()));
			if((!CMSecurity.isDisabled(CMSecurity.DisFlag.EXPERIENCE))
			&&!mob.charStats().getCurrentClass().expless()
			&&!mob.charStats().getMyRace().expless()
			&&(mob.getExpNeededLevel()<Integer.MAX_VALUE))
			   buf.append(L(", and need @x1 to level",""+mob.getExpNeededLevel()));
			buf.append(".\"");
			final Command C=CMClass.getCommand("Say");
			if(C!=null) C.execute(mob,CMParms.parse(buf.toString()),metaFlags);
		}
		else
		{
			final int level=parseOutLevel(commands);
			final String s=CMParms.combine(commands,1).toUpperCase();
			final StringBuffer say=new StringBuffer("");
			if("AFFECTS".startsWith(s)||(s.equalsIgnoreCase("ALL")))
			{

				final StringBuffer aff=new StringBuffer("\n\r^!I am affected by:^? ");
				final Command C=CMClass.getCommand("Affect");
				if(C!=null) aff.append(C.executeInternal(mob,metaFlags,mob).toString());
				say.append(aff.toString());
			}
			if("STATS".startsWith(s)||(s.equalsIgnoreCase("ALL")))
			{
				final StringBuffer stats=new StringBuffer("");
				final int max=CMProps.getIntVar(CMProps.Int.BASEMAXSTAT);
				final CharStats CT=mob.charStats();
				for(final int i : CharStats.CODES.BASECODES())
					stats.append("^c" + CMStrings.capitalizeAndLower(CMStrings.limit(CharStats.CODES.NAME(i),3))+": ^w"
							+CMStrings.padRight(Integer.toString(CT.getStat(i)),2)
							+"/"+(max+CT.getStat(CharStats.CODES.toMAXBASE(i)))+", ");
				say.append("\n\r^NMy stats:^? "+stats.toString());
			}
			if(s.equalsIgnoreCase("ALL"))
			{

				final Vector V=new Vector();
				V.addElement(Integer.valueOf(Ability.ACODE_THIEF_SKILL));
				V.addElement(Integer.valueOf(Ability.ACODE_SKILL));
				V.addElement(Integer.valueOf(Ability.ACODE_COMMON_SKILL));
				V.addElement(Integer.valueOf(Ability.ACODE_SPELL));
				V.addElement(Integer.valueOf(Ability.ACODE_PRAYER));
				V.addElement(Integer.valueOf(Ability.ACODE_SUPERPOWER));
				V.addElement(Integer.valueOf(Ability.ACODE_TECH));
				V.addElement(Integer.valueOf(Ability.ACODE_CHANT));
				V.addElement(Integer.valueOf(Ability.ACODE_SONG));
				say.append("\n\r^NMy skills:^? "+getAbilities(null,mob,V,Ability.ALL_ACODES,false,level));
			}
			else
			if("SPELLS".startsWith(s))
				say.append("\n\r^NMy spells:^? "+getAbilities(null,mob,Ability.ACODE_SPELL,-1,false,level));
			else
			if("SKILLS".startsWith(s))
			{
				final Vector V=new Vector();
				V.addElement(Integer.valueOf(Ability.ACODE_THIEF_SKILL));
				V.addElement(Integer.valueOf(Ability.ACODE_SKILL));
				V.addElement(Integer.valueOf(Ability.ACODE_TECH));
				V.addElement(Integer.valueOf(Ability.ACODE_COMMON_SKILL));
				say.append("\n\r^NMy skills:^? "+getAbilities(null,mob,V,Ability.ALL_ACODES,false,level));
			}
			else
			if("PRAYERS".startsWith(s))
				say.append("\n\r^NMy prayers:^? "+getAbilities(null,mob,Ability.ACODE_PRAYER,-1,false,level));
			else
			if(("POWERS".startsWith(s))||("SUPER POWERS".startsWith(s)))
				say.append("\n\r^NMy super powers:^? "+getAbilities(null,mob,Ability.ACODE_SUPERPOWER,-1,false,level));
			else
			if("CHANTS".startsWith(s))
				say.append("\n\r^NMy chants:^? "+getAbilities(null,mob,Ability.ACODE_CHANT,-1,false,level));
			else
			if("SONGS".startsWith(s))
				say.append("\n\r^NMy songs:^? "+getAbilities(null,mob,Ability.ACODE_SONG,-1,false,level));
			else
			if("TECH SKILLS".startsWith(s))
				say.append("\n\r^NMy tech skills:^? "+getAbilities(null,mob,Ability.ACODE_TECH,-1,false,level));


			if(say.length()==0)
				mob.tell(L("'@x1' is unknown.  Try SPELLS, SKILLS, PRAYERS, CHANTS, SONGS, STATS, or ALL.",s));
			else
				CMLib.commands().postSay(mob,null,say.toString(),false,false);
		}
		return false;
	}
	@Override public double combatActionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandCombatActionCost(ID());}
	@Override public double actionsCost(final MOB mob, final List<String> cmds){return CMProps.getCommandActionCost(ID());}
	@Override public boolean canBeOrdered(){return true;}


}
