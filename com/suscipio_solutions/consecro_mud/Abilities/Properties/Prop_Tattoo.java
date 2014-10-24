package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMath;


public class Prop_Tattoo extends Property
{
	@Override public String ID() { return "Prop_Tattoo"; }
	@Override public String name(){ return "A Tattoo";}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS;}

	public static List<String> getTattoos(MOB mob)
	{
		List<String> tattos=new Vector<String>();
		Ability A=mob.fetchAbility("Prop_Tattoo");
		if(A!=null)
			tattos=CMParms.parseSemicolons(A.text().toUpperCase(),true);
		else
		{
			A=mob.fetchEffect("Prop_Tattoo");
			if(A!=null)
				tattos=CMParms.parseSemicolons(A.text().toUpperCase(),true);
		}
		return tattos;
	}

	@Override
	public void setMiscText(String text)
	{
		if(affected instanceof MOB)
		{
			final MOB M=(MOB)affected;
			final List<String> V=CMParms.parseSemicolons(text,true);
			for(int v=0;v<V.size();v++)
			{
				final String s=V.get(v);
				final int x=s.indexOf(' ');
				if((x>0)&&(CMath.isNumber(s.substring(0,x))))
					M.addTattoo(new MOB.Tattoo(s.substring(x+1).trim(),CMath.s_int(s.substring(0,x))));
				else
					M.addTattoo(new MOB.Tattoo(s));
			}
		}
		savable=false;
	}
}
