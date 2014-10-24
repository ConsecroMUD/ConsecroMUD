package com.suscipio_solutions.consecro_mud.Abilities.Common;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.DeadBody;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Wearable;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.interfaces.ItemPossessor;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class Scalp extends CommonSkill
{
	@Override public String ID() { return "Scalp"; }
	private final static String localizedName = CMLib.lang().L("Scalping");
	@Override public String name() { return localizedName; }
	private static final String[] triggerStrings =I(new String[] {"SCALP","SCALPING"});
	@Override public String[] triggerStrings(){return triggerStrings;}
	public static Vector lastSoManyScalps=new Vector();
	@Override public int classificationCode(){return Ability.ACODE_SKILL|Ability.DOMAIN_ANATOMY;}

	private DeadBody body=null;
	protected boolean failed=false;
	public Scalp()
	{
		super();
		displayText=L("You are scalping something...");
		verb=L("scalping");
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if((body!=null)
		&&(affected instanceof MOB)
		&&(((MOB)affected).location()!=null)
		&&((!((MOB)affected).location().isContent(body)))
		&&((!((MOB)affected).isMine(body))))
			unInvoke();
		return super.tick(ticking,tickID);
	}
	@Override
	public void unInvoke()
	{
		if(canBeUninvoked())
		{
			if((affected!=null)&&(affected instanceof MOB))
			{
				final MOB mob=(MOB)affected;
				if((body!=null)&&(!aborted))
				{
					if((failed)||(!mob.location().isContent(body)))
						commonTell(mob,L("You messed up your scalping completely."));
					else
					{
						mob.location().show(mob,null,body,getActivityMessageType(),L("<S-NAME> manage(s) to scalp <O-NAME>."));
						lastSoManyScalps.addElement(body);
						if(lastSoManyScalps.size()>100)
							lastSoManyScalps.removeElementAt(0);
						final Item scalp=CMClass.getItem("GenItem");
						String race="";
						if((body.charStats()!=null)&&(body.charStats().getMyRace()!=null))
							race=" "+body.charStats().getMyRace().name();
						if(body.name().startsWith("the body"))
							scalp.setName(L("the@x1 scalp@x2",race,body.name().substring(8)));
						else
							scalp.setName(L("a@x1 scalp",race));
						if(body.displayText().startsWith("the body"))
							scalp.setDisplayText(L("the@x1 scalp@x2",race,body.displayText().substring(8)));
						else
							scalp.setDisplayText(L("a@x1 scalp sits here",race));
						scalp.setBaseValue(1);
						scalp.setDescription(L("This is the bloody top of that poor creatures head."));
						scalp.setMaterial(RawMaterial.RESOURCE_MEAT);
						scalp.setSecretIdentity("This scalp was cut by "+mob.name()+".");
						mob.location().addItem(scalp,ItemPossessor.Expire.Monster_EQ);
					}
				}
			}
		}
		super.unInvoke();
	}


	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(super.checkStop(mob, commands))
			return true;
		body=null;
		Item I=null;
		if((mob.isMonster()
		&&(!CMLib.flags().isAnimalIntelligence(mob)))
		&&(commands.size()==0))
		{
			for(int i=0;i<mob.location().numItems();i++)
			{
				final Item I2=mob.location().getItem(i);
				if((I2 instanceof DeadBody)
				&&(CMLib.flags().canBeSeenBy(I2,mob))
				&&(I2.container()==null))
				{
					I=I2;
					break;
				}
			}
		}
		else
			I=getTarget(mob,mob.location(),givenTarget,commands,Wearable.FILTER_UNWORNONLY);

		if(I==null) return false;
		if((!(I instanceof DeadBody))
		   ||(((DeadBody)I).charStats()==null)
		   ||(((DeadBody)I).charStats().getMyRace()==null)
		   ||(((DeadBody)I).charStats().getMyRace().bodyMask()[Race.BODY_HEAD]==0))
		{
			commonTell(mob,L("You can't scalp @x1.",I.name(mob)));
			return false;
		}
		if(lastSoManyScalps.contains(I))
		{
			commonTell(mob,L("@x1 has already been scalped.",I.name(mob)));
			return false;

		}
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		failed=!proficiencyCheck(mob,0,auto);
		final CMMsg msg=CMClass.getMsg(mob,I,this,getActivityMessageType(),getActivityMessageType(),getActivityMessageType(),L("<S-NAME> start(s) scalping <T-NAME>."));
		if(mob.location().okMessage(mob,msg))
		{
			mob.location().send(mob,msg);
			I=(Item)msg.target();
			body=(DeadBody)I;
			verb=L("scalping @x1",I.name());
			playSound="ripping.wav";
			int duration=(I.phyStats().weight()/(10+getXLEVELLevel(mob)));
			if(duration<3) duration=3;
			if(duration>40) duration=40;
			beneficialAffect(mob,mob,asLevel,duration);
		}
		return true;
	}
}
