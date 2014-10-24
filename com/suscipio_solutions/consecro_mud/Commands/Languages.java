package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMParms;


@SuppressWarnings("rawtypes")
public class Languages extends Skills
{
	public Languages(){}

	private final String[] access=I(new String[]{"LANGUAGES","LANGS"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final StringBuffer msg=new StringBuffer("");
		if(parsedOutIndividualSkill(mob,CMParms.combine(commands,1),Ability.ACODE_SPELL))
			return true;
		msg.append(L("\n\r^HLanguages known:^? @x1\n\r",getAbilities(mob,mob,Ability.ACODE_LANGUAGE,-1,true,parseOutLevel(commands)).toString()));
		if(!mob.isMonster())
			mob.session().wraplessPrintln(msg.toString());
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
