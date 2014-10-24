package com.suscipio_solutions.consecro_mud.Abilities.Diseases;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.DiseaseAffect;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;



public class Disease_Cannibalism extends Disease
{
	@Override public String ID() { return "Disease_Cannibalism"; }
	private final static String localizedName = CMLib.lang().L("Cannibalism");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Cannibalism)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	@Override public boolean putInCommandlist(){return false;}
	@Override public int difficultyLevel(){return 6;}

	@Override protected int DISEASE_TICKS(){return 999999;}
	@Override protected int DISEASE_DELAY(){return 100;}
	@Override
	protected String DISEASE_DONE()
   {
	  String desiredMeat = "";
	  if(affected instanceof MOB)
	  {
		 final MOB mob = (MOB) affected;
		 desiredMeat = mob.charStats().raceName();
	  }
	  else
	  {
		 desiredMeat = "your race's";
	  }
	  return "<S-NAME> no longer hunger for "+ desiredMeat +" meat.";
   }
	@Override
	protected String DISEASE_START()
   {
	  String desiredMeat = "";
	  if(affected instanceof MOB)
	  {
		 final MOB mob = (MOB) affected;
		 desiredMeat = mob.charStats().raceName();
	  }
	  else
	  {
		 desiredMeat = "your race's";
	  }
	  return "^G<S-NAME> hunger(s) for "+ desiredMeat +" meat.^?";
   }
	@Override protected String DISEASE_AFFECT(){return "";}
	@Override public int spreadBitmap(){return DiseaseAffect.SPREAD_CONSUMPTION;}

	@Override
	public void unInvoke()
	{
		if(affected==null)
			return;
		if(affected instanceof MOB)
		{
			final MOB mob=(MOB)affected;

			super.unInvoke();
			if(canBeUninvoked())
				mob.tell(mob,null,this,DISEASE_DONE());
		}
		else
			super.unInvoke();
	}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if((affected!=null)&&(affected instanceof MOB))
		{
		 final MOB source=msg.source();
		 if(source == null)
			return false;
			final MOB mob=(MOB)affected;
		 if(msg.targetMinor() == CMMsg.TYP_EAT)
		 {
			final Environmental food = msg.target();
			if((food!=null)
			&&(food.name().toLowerCase().indexOf(mob.charStats().raceName()) < 0))
			{
				final CMMsg newMessage=CMClass.getMsg(mob,null,this,CMMsg.MSG_OK_VISUAL,L("^S<S-NAME> attempt(s) to eat @x1, but can't stomach it....^?",food.Name()));
				if(mob.location().okMessage(mob,newMessage))
					mob.location().send(mob,newMessage);
				return false;
			}
		 }
	  }
		if((affected!=null)&&(affected instanceof MOB))
		{
			final MOB mob=(MOB)affected;
			if(msg.amITarget(mob)
			&&(msg.tool()!=null)
			&&(msg.tool().ID().equals("Spell_Hungerless")))
			{
				mob.tell(L("You don't feel any less hungry."));
				return false;
			}
		}

	  return super.okMessage(myHost,msg);
	}
}
