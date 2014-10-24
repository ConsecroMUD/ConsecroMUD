package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Prop_EnlargeRoom extends Property
{
	@Override public String ID() { return "Prop_EnlargeRoom"; }
	@Override public String name(){ return "Change a rooms movement requirements";}
	@Override protected int canAffectCode(){return Ability.CAN_ROOMS|Ability.CAN_AREAS;}

	@Override
	public String accountForYourself()
	{ return "Enlarged";	}

	@Override public long flags(){return Ability.FLAG_ADJUSTER;}

	protected double dval(String s)
	{
		if(s.indexOf('.')>=0)
			return CMath.s_double(s);
		return CMath.s_int(s);
	}

	protected int ival(String s)
	{
		return (int)Math.round(dval(s));
	}

	@Override
	public void affectPhyStats(Physical affected, PhyStats affectableStats)
	{
		if(text().length()>0)
		{
			final int weight=affectableStats.weight();
			switch(text().charAt(0))
			{
			case '+':
				affectableStats.setWeight(weight+ival(text().substring(1).trim()));
				break;
			case '-':
				affectableStats.setWeight(weight-ival(text().substring(1).trim()));
				break;
			case '*':
				affectableStats.setWeight((int)Math.round(CMath.mul(weight,dval(text().substring(1).trim()))));
				break;
			case '/':
				affectableStats.setWeight((int)Math.round(CMath.div(weight,dval(text().substring(1).trim()))));
				break;
			default:
				affectableStats.setWeight(ival(text()));
				break;
			}
		}
	}
}
