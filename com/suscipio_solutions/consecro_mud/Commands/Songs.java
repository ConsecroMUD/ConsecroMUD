package com.suscipio_solutions.consecro_mud.Commands;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;


@SuppressWarnings({"unchecked","rawtypes"})
public class Songs  extends Skills
{
	public Songs(){}

	private final String[] access=I(new String[]{"SONGS"});
	@Override public String[] getAccessWords(){return access;}
	@Override
	public boolean execute(MOB mob, Vector commands, int metaFlags)
		throws java.io.IOException
	{
		final StringBuffer msg=new StringBuffer("");
		final String qual=CMParms.combine(commands,1).toUpperCase();
		if(parsedOutIndividualSkill(mob,qual,Ability.ACODE_SONG))
			return true;
		final int[] level=new int[1];
		final int[] domain=new int[1];
		final String[] domainName=new String[1];
		domainName[0]="";
		level[0]=-1;
		parseDomainInfo(mob,commands,new XVector(Integer.valueOf(Ability.ACODE_SONG)),level,domain,domainName);
		msg.append(L("\n\r^HYour @x1songs:^? @x2",domainName[0].replace('_',' '),getAbilities(mob,mob,Ability.ACODE_SONG,domain[0],true,level[0]).toString()));
		if(!mob.isMonster())
			mob.session().wraplessPrintln(msg.toString());
		return false;
	}

	@Override public boolean canBeOrdered(){return true;}


}
