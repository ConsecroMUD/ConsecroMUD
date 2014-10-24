package com.suscipio_solutions.consecro_mud.Abilities.Diseases;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.DiseaseAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Weapon;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class Disease_Lepresy extends Disease
{
	@Override public String ID() { return "Disease_Lepresy"; }
	private final static String localizedName = CMLib.lang().L("Leprosy");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Leprosy)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public boolean putInCommandlist(){return false;}

	@Override protected int DISEASE_TICKS(){return 999999;}
	@Override protected int DISEASE_DELAY(){return 10;}
	protected int lastHP=Integer.MAX_VALUE;
	@Override protected String DISEASE_DONE(){return "Your leprosy is cured!";}
	@Override protected String DISEASE_START(){return "^G<S-NAME> look(s) pale!^?";}
	@Override protected String DISEASE_AFFECT(){return "";}
	@Override public int spreadBitmap(){return DiseaseAffect.SPREAD_CONSUMPTION;}
	@Override public int difficultyLevel(){return 4;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!(affected instanceof MOB))
			return super.okMessage(myHost,msg);

		final MOB mob=(MOB)affected;

		// when this spell is on a MOBs Affected list,
		// it should consistantly prevent the mob
		// from trying to do ANYTHING except sleep
		if((msg.amITarget(mob))
		&&(msg.targetMinor()==CMMsg.TYP_DAMAGE)
		&&(msg.targetMessage()!=null))
		{
			if((msg.targetMessage().indexOf("<DAMAGE>")>=0)
			||(msg.targetMessage().indexOf("<DAMAGES>")>=0))
			msg.modify(msg.source(),
						  msg.target(),
						  msg.tool(),
						  msg.sourceCode(),msg.sourceMessage(),
						  msg.targetCode(),CMLib.combat().replaceDamageTag(msg.targetMessage(),1,0,'T'),
						  msg.othersCode(),msg.othersMessage());
			else
			if((msg.tool()!=null)&&(msg.tool() instanceof Weapon))
			msg.modify(msg.source(),
						  msg.target(),
						  msg.tool(),
						  msg.sourceCode(),msg.sourceMessage(),
						  msg.targetCode(),"^e^<FIGHT^>"+((Weapon)msg.tool()).hitString(1)+"^</FIGHT^>^?",
						  msg.othersCode(),msg.othersMessage());
		}
		return super.okMessage(myHost,msg);
	}

}
