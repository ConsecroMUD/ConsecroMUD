package com.suscipio_solutions.consecro_mud.Abilities.Songs;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class Skill_Stability extends BardSkill
{
	@Override public String ID() { return "Skill_Stability"; }
	private final static String localizedName = CMLib.lang().L("Stability");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){return "";}
	@Override public int abstractQuality(){ return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_ACROBATIC;}

	@Override
	public boolean okMessage(final Environmental myHost, final CMMsg msg)
	{
		if(!super.okMessage(myHost,msg))
			return false;

		if(!(affected instanceof MOB))
			return true;

		final MOB mob=(MOB)affected;
		if((msg.tool()!=null)
		&&(msg.tool() instanceof Ability)
		&&(msg.amITarget(affected))
		&&(((Ability)msg.tool()).abstractQuality()==Ability.QUALITY_MALICIOUS)
		&&(CMath.bset(((Ability)msg.tool()).flags(),Ability.FLAG_MOVING))
		&&((mob.fetchAbility(ID())==null)||proficiencyCheck(null,-40+(2*getXLEVELLevel(mob)),false)))
		{
			Room roomS=null;
			Room roomD=null;
			if((msg.target()!=null)&&(msg.target() instanceof MOB))
				roomD=((MOB)msg.target()).location();
			if((msg.source()!=null)&&(msg.source().location()!=null))
				roomS=msg.source().location();
			if((msg.target()!=null)&&(msg.target() instanceof Room))
				roomD=(Room)msg.target();

			if((roomS!=null)&&(roomD!=null)&&(roomS==roomD))
				roomD=null;

			if(roomS!=null)
				roomS.show((MOB)affected,null,msg.tool(),CMMsg.MSG_OK_VISUAL,L("<S-NAME> remain(s) stable despite the <O-NAME>."));
			if(roomD!=null)
				roomD.show((MOB)affected,null,msg.tool(),CMMsg.MSG_OK_VISUAL,L("<S-NAME> remain(s) stable despite the <O-NAME>."));
			helpProficiency((MOB)affected, 0);
			return false;
		}
		return true;
	}


}
