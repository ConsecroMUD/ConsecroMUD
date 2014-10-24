package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Dance_Jingledress extends Dance
{
	@Override public String ID() { return "Dance_Jingledress"; }
	private final static String localizedName = CMLib.lang().L("Jingledress");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override protected String danceOf(){return name()+" Dance";}
	@Override public long flags(){return Ability.FLAG_HEALINGMAGIC;}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;

		final MOB mob=(MOB)affected;
		if(mob==null)
			return false;

		if(invoker()!=null)
		{
			final int healing=CMLib.dice().roll(2,adjustedLevel(invoker(),0),4);
			CMLib.combat().postHealing(invoker(),mob,this,CMMsg.MASK_ALWAYS|CMMsg.TYP_CAST_SPELL,healing,null);
		}
		return true;
	}


}
