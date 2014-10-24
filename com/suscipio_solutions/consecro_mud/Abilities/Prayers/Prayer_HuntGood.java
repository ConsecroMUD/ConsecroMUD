package com.suscipio_solutions.consecro_mud.Abilities.Prayers;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;




public class Prayer_HuntGood extends Prayer_HuntEvil
{
	@Override public String ID() { return "Prayer_HuntGood"; }
	private final static String localizedName = CMLib.lang().L("Hunt Good");
	@Override public String name() { return localizedName; }
	@Override public long flags(){return Ability.FLAG_UNHOLY|Ability.FLAG_TRACKING;}
	@Override public int classificationCode(){return Ability.ACODE_PRAYER|Ability.DOMAIN_NEUTRALIZATION;}
	private final static String localizedStaticDisplay = CMLib.lang().L("(Hunting Good)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected String word(){return "good";}
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_SELF;}

	@Override
	protected MOB gameHere(Room room)
	{
		if(room==null) return null;
		for(int i=0;i<room.numInhabitants();i++)
		{
			final MOB mob=room.fetchInhabitant(i);
			if(CMLib.flags().isGood(mob))
				return mob;
		}
		return null;
	}

}
