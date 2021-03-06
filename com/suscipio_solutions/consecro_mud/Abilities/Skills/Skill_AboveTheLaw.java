package com.suscipio_solutions.consecro_mud.Abilities.Skills;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Areas.interfaces.Area;
import com.suscipio_solutions.consecro_mud.Behaviors.interfaces.LegalBehavior;
import com.suscipio_solutions.consecro_mud.Common.interfaces.LegalWarrant;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


public class Skill_AboveTheLaw extends StdSkill
{
	@Override public String ID() { return "Skill_AboveTheLaw"; }
	private final static String localizedName = CMLib.lang().L("Above The Law");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_BENEFICIAL_SELF;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_LEGAL;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	protected LegalBehavior B=null;
	protected Area O=null;
	protected Area A=null;

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking, tickID))
			return false;
		if(affected instanceof MOB)
		{
			final MOB mob=(MOB)affected;
			final Room room=mob.location();
			if(room!=null)
			{
				if((A==null)||(room.getArea()!=A))
				{
					if (isSavable()
					|| ((mob.getStartRoom() != null) && (room.getArea() == mob.getStartRoom().getArea())))
					{
						A=room.getArea();
						if(isSavable() || proficiencyCheck(mob,0,false))
						{
							O=CMLib.law().getLegalObject(A);
							B=CMLib.law().getLegalBehavior(room);
						}
					}
				}
			}
			if(B!=null)
			{
				final List<LegalWarrant> warrants=B.getWarrantsOf(O,mob);
				for(final LegalWarrant W : warrants)
				{
					W.setCrime("pardoned");
					W.setOffenses(0);
					if((!isSavable())&&(CMLib.dice().rollPercentage()<10))
						helpProficiency(mob, 0);
				}
			}
		}
		return true;
	}
}
