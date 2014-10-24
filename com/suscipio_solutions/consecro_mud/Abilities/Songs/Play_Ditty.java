package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



public class Play_Ditty extends Play
{
	@Override public String ID() { return "Play_Ditty"; }
	private final static String localizedName = CMLib.lang().L("Ditty");
	@Override public String name() { return localizedName; }
	@Override public int abstractQuality(){ return Ability.QUALITY_BENEFICIAL_OTHERS;}
	@Override protected String songOf(){return CMLib.english().startWithAorAn(name());}
	@Override public long flags(){return Ability.FLAG_HEALINGMAGIC;}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if((affected!=null)&&(affected instanceof MOB)&&(invoker()!=null))
		{
			final MOB mob=(MOB)affected;
			final int healing=4+(int)Math.round(CMath.mul(adjustedLevel(invoker(),0),0.25));
			CMLib.combat().postHealing(invoker(),mob,this,CMMsg.MASK_ALWAYS|CMMsg.TYP_CAST_SPELL,healing,null);
		}
		return true;
	}
}
