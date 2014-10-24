package com.suscipio_solutions.consecro_mud.Abilities.Properties;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Climate;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;


public class Prop_Weather extends Property
{
	@Override public String ID() { return "Prop_Weather"; }
	@Override public String name(){ return "Weather Setter";}
	@Override protected int canAffectCode(){return Ability.CAN_AREAS;}

	int code=-1;

	@Override
	public void affectPhyStats(Physical host, PhyStats stats)
	{
		super.affectPhyStats(host,stats);
		if((code<0)&&(text().length()>0))
		{
			for(int i=0;i<Climate.WEATHER_DESCS.length;i++)
				if(Climate.WEATHER_DESCS[i].equalsIgnoreCase(text()))
					code=i;
		}
		if(code>=0)
		{
			if(affected instanceof Room)
			{
				((Room)affected).getArea().getClimateObj().setCurrentWeatherType(code);
				((Room)affected).getArea().getClimateObj().setNextWeatherType(code);
			}
			else
			if(affected instanceof Area)
			{
				((Area)affected).getClimateObj().setCurrentWeatherType(code);
				((Area)affected).getClimateObj().setNextWeatherType(code);
			}
		}
	}

}
