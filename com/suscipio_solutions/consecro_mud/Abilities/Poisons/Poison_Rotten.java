package com.suscipio_solutions.consecro_mud.Abilities.Poisons;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Food;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Drink;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class Poison_Rotten extends Poison
{
	@Override public String ID() { return "Poison_Rotten"; }
	private final static String localizedName = CMLib.lang().L("Rotten");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"POISONROTT"});
	@Override public String[] triggerStrings(){return triggerStrings;}

	@Override protected int POISON_TICKS(){return 50;} // 0 means no adjustment!
	@Override protected int POISON_DELAY(){return 5;}
	@Override
	protected boolean POISON_AFFECTTARGET()
	{
		if(((affected instanceof Food)&&((((Food)affected).material()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_FLESH))
		||((affected instanceof Drink)
			&&(affected instanceof Item)
			&&(((Item)affected).material()!=RawMaterial.RESOURCE_MILK)
			&&(((Item)affected).material()!=RawMaterial.RESOURCE_BLOOD)))
				return false;
		return true;
	}
	@Override
	protected String POISON_START_TARGETONLY()
	{
		if(((affected instanceof Food)&&((((Food)affected).material()&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_FLESH))
		||((affected instanceof Drink)
			&&(affected instanceof Item)
			&&(((Item)affected).material()!=RawMaterial.RESOURCE_MILK)
			&&(((Item)affected).material()!=RawMaterial.RESOURCE_BLOOD)))
				return "^G"+affected.name()+" was rotten! Blech!^?";
		return "";
	}
	@Override
	protected String POISON_START()
	{
		if((affected instanceof Food)||(affected instanceof Drink))
			return "^G"+affected.name()+" was rotten! <S-NAME> bend(s) over with horrid stomach pains!^?";
		return "^G<S-NAME> bend(s) over with horrid stomach pains!^?";
	}
	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((msg.amITarget(affected))
		&&(msg.targetMinor()==CMMsg.TYP_SNIFF)
		&&(affected instanceof Item)
		&&(CMLib.flags().canSmell(msg.source())))
			msg.source().tell(msg.source(),affected,null,L("<T-NAME> smell(s) rotten!"));
	}
	@Override protected String POISON_AFFECT(){return "^G<S-NAME> moan(s) and clutch(es) <S-HIS-HER> stomach.";}
	@Override protected String POISON_CAST(){return "^F^<FIGHT^><S-NAME> poison(s) <T-NAMESELF>!^</FIGHT^>^?";}
	@Override protected String POISON_FAIL(){return "<S-NAME> attempt(s) to poison <T-NAMESELF>, but fail(s).";}
	@Override protected int POISON_DAMAGE(){return CMLib.dice().roll(1,3,1);}

	@Override
	public void affectCharStats(MOB affected, CharStats affectableStats)
	{
		affectableStats.setStat(CharStats.STAT_CONSTITUTION,affectableStats.getStat(CharStats.STAT_CONSTITUTION)-10);
		if(affectableStats.getStat(CharStats.STAT_CONSTITUTION)<=0)
			affectableStats.setStat(CharStats.STAT_CONSTITUTION,1);
		affectableStats.setStat(CharStats.STAT_STRENGTH,affectableStats.getStat(CharStats.STAT_STRENGTH)-8);
		if(affectableStats.getStat(CharStats.STAT_STRENGTH)<=0)
			affectableStats.setStat(CharStats.STAT_STRENGTH,1);
	}
}

