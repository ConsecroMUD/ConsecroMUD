package com.suscipio_solutions.consecro_mud.Abilities.Immortal;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.Session;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMFile;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;


@SuppressWarnings("rawtypes")
public class Immortal_Record extends Immortal_Skill
{
	boolean doneTicking=false;
	@Override public String ID() { return "Immortal_Record"; }
	private final static String localizedName = CMLib.lang().L("Record");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return CAN_MOBS;}
	@Override public int abstractQuality(){return Ability.QUALITY_MALICIOUS;}
	private static final String[] triggerStrings =I(new String[] {"RECORD"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_IMMORTAL;}
	@Override public int maxRange(){return adjustedMaxInvokerRange(1);}
	@Override public int usageType(){return USAGE_MOVEMENT;}
	Session sess=null;

	@Override
	public void unInvoke()
	{
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;

		super.unInvoke();

		if(canBeUninvoked())
		{
			if(mob.session()==null)
				mob.setSession(null);
			else
			if(sess!=null)
				mob.session().setBeingSnoopedBy(sess,false);
			sess=null;
		}
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID)) return false;
		if(sess==null) return false;
		if((affected instanceof MOB)
		&&(((MOB)affected).session()!=null)
		&&(!(((MOB)affected).session().isBeingSnoopedBy(sess))))
			((MOB)affected).session().setBeingSnoopedBy(sess, true);
		return true;
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		MOB target=CMLib.players().getLoadPlayer(CMParms.combine(commands,0));
		if(target==null) target=getTargetAnywhere(mob,commands,givenTarget,false,true,false);
		if(target==null) return false;

		final Immortal_Record A=(Immortal_Record)target.fetchEffect(ID());
		if(A!=null)
		{
			target.delEffect(A);
			if(target.playerStats()!=null) target.playerStats().setLastUpdated(0);
			mob.tell(L("@x1 will no longer be recorded.",target.Name()));
			return true;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MASK_MOVE|CMMsg.TYP_JUSTICE|(auto?CMMsg.MASK_ALWAYS:0),L("^F<S-NAME> begin(s) recording <T-NAMESELF>.^?"));
			CMLib.color().fixSourceFightColor(msg);
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				final String filename="/"+target.Name()+System.currentTimeMillis()+".log";
				final CMFile file=new CMFile(filename,null,CMFile.FLAG_LOGERRORS);
				if(!file.canWrite())
				{
					if(!CMSecurity.isASysOp(mob)||(CMSecurity.isASysOp(target)))
						Log.sysOut("Record",mob.Name()+" failed to start recording "+target.name()+".");
				}
				else
				{
					if(!CMSecurity.isASysOp(mob)||(CMSecurity.isASysOp(target)))
						Log.sysOut("Record",mob.Name()+" started recording "+target.name()+" to /"+filename+".");
					final Immortal_Record A2=(Immortal_Record)copyOf();
					final Session F=(Session)CMClass.getCommon("FakeSession");
					F.initializeSession(null,Thread.currentThread().getThreadGroup().getName(),filename);
					if(target.session()==null)
						target.setSession(F);
					A2.sess=F;
					target.addNonUninvokableEffect(A2);
					mob.tell(L("Enter RECORD @x1 again to stop recording.",target.Name()));
				}
			}
		}
		else
			return beneficialVisualFizzle(mob,target,L("<S-NAME> attempt(s) to hush <T-NAMESELF>, but fail(s)."));
		return success;
	}
}
