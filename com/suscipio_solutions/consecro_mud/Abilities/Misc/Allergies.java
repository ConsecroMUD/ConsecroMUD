package com.suscipio_solutions.consecro_mud.Abilities.Misc;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.StdAbility;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Abilities.interfaces.HealthCondition;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.Races.interfaces.Race;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;



@SuppressWarnings({"unchecked","rawtypes"})
public class Allergies extends StdAbility implements HealthCondition
{
	@Override public String ID() { return "Allergies"; }
	private final static String localizedName = CMLib.lang().L("Allergies");
	@Override public String name() { return localizedName; }
	@Override public String displayText(){ return "";}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	@Override protected int canTargetCode(){return 0;}
	@Override public int abstractQuality(){return Ability.QUALITY_OK_SELF;}
	@Override public int classificationCode(){return Ability.ACODE_PROPERTY;}
	@Override public boolean isAutoInvoked(){return true;}
	@Override public boolean canBeUninvoked(){return false;}
	protected Set<Integer> resourceAllergies=new HashSet<Integer>();
	protected Set<Race> raceAllergies=new HashSet<Race>();
	protected int allergicCheckDown=0;

	@Override
	public String getHealthConditionDesc()
	{
		final List<String> list=new ArrayList<String>();
		for(final Integer I : resourceAllergies)
			list.add(RawMaterial.CODES.NAME(I.intValue()).toLowerCase());
		for(final Race R : raceAllergies)
			list.add(R.name()+" dander");
		if(list.size()==0) return "";
		return "Suffers from allergies to "+CMLib.english().toEnglishStringList(list)+".";
	}


	@Override
	public void setMiscText(String newText)
	{
		super.setMiscText(newText);
		resourceAllergies.clear();
		raceAllergies.clear();
		final Vector<String> V=CMParms.parse(newText.toUpperCase().trim());
		final RawMaterial.CODES codes = RawMaterial.CODES.instance();
		for(int s=0;s<codes.total();s++)
			if(V.contains(codes.names()[s]))
				resourceAllergies.add(Integer.valueOf(codes.get(s)));
		Race R=null;
		for(final Enumeration r=CMClass.races();r.hasMoreElements();)
		{
			R=(Race)r.nextElement();
			if(V.contains(R.ID().toUpperCase()))
				raceAllergies.add(R);
		}
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if(((++allergicCheckDown)>10)
		&&(affected instanceof MOB))
		{
			allergicCheckDown=0;
			final MOB mob=(MOB)affected;
			if((CMLib.flags().aliveAwakeMobile(mob,true))&&(CMLib.flags().isInTheGame(mob,true)))
			{
				final Room R=CMLib.map().roomLocation(mob);
				if(raceAllergies.size()>0)
				{
					MOB M=null;
					for(int i=0;i<R.numInhabitants();i++)
					{
						M=R.fetchInhabitant(i);
						if((M!=null)&&(M!=mob)&&(raceAllergies.contains(M.charStats().getMyRace())))
							R.show(mob,null,this,CMMsg.TYP_NOISYMOVEMENT,L("<S-NAME> sneeze(s)! AAAAACHHHOOOO!"));
					}
				}
				else
				if(resourceAllergies.size()>0)
				{
					Item I=null;
					for(int i=0;i<R.numItems();i++)
					{
						I=R.getItem(i);
						if((I!=null)
						&&(I.container()==null)
						&&(resourceAllergies.contains(Integer.valueOf(I.material()))))
							R.show(mob,null,this,CMMsg.TYP_NOISYMOVEMENT,L("<S-NAME> sneeze(s)! AAAAACHHHOOOO!"));
					}
					if(R.numInhabitants()>0)
					{
						final MOB M=R.fetchRandomInhabitant();
						if(M!=null)
						for(int i=0;i<M.numItems();i++)
						{
							I=M.getItem(i);
							if((I!=null)
							&&(I.container()==null)
							&&(resourceAllergies.contains(Integer.valueOf(I.material()))))
								R.show(mob,null,this,CMMsg.TYP_NOISYMOVEMENT,L("<S-NAME> sneeze(s)! AAAAACHHHOOOO!"));
						}
					}
				}
			}
		}
		return true;
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((affected!=null)
		&&(affected instanceof MOB))
		{
			if(msg.source()==affected)
			{
				if((msg.targetMinor()==CMMsg.TYP_EAT)
				&&(((msg.target() instanceof Item)&&(resourceAllergies.contains(Integer.valueOf(((Item)msg.target()).material()))))
					||((msg.target() instanceof MOB)&&(raceAllergies.contains(((MOB)msg.target()).charStats().getMyRace())))))
				{
					final Ability A=CMClass.getAbility("Poison_Heartstopper");
					if(A!=null) A.invoke(msg.source(),msg.source(),true,0);
				}
				else
				if(((msg.targetMinor()==CMMsg.TYP_GET)||(msg.targetMinor()==CMMsg.TYP_PUSH)||(msg.targetMinor()==CMMsg.TYP_PULL))
				&&((msg.target() instanceof Item)&&(resourceAllergies.contains(Integer.valueOf(((Item)msg.target()).material())))))
				{
					final Ability A=CMClass.getAbility("Poison_Hives");
					if(A!=null) A.invoke(msg.source(),msg.source(),true,0);
				}
			}
			else
			if((msg.target()==affected)
			&&(raceAllergies.contains(msg.source().charStats().getMyRace()))
			&&((msg.targetMajor(CMMsg.MASK_HANDS))
			   ||(msg.targetMajor(CMMsg.MASK_MOVE)))
			&&(((MOB)affected).location()!=null)
			&&(((MOB)affected).location().isInhabitant(msg.source()))
			&&((msg.tool()==null)||((!msg.tool().ID().equals("Poison_Hives"))&&(!msg.tool().ID().equals("Poison_Heartstopper")))))
			{
				final Ability A=CMClass.getAbility("Poison_Hives");
				if(A!=null) A.invoke(msg.source(),affected,true,0);
			}
		}
		super.executeMsg(myHost,msg);
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		String choice="";
		if(givenTarget!=null)
		{
			if((commands.size()>0)&&(((String)commands.firstElement()).equals(givenTarget.name())))
				commands.removeElementAt(0);
			choice=CMParms.combine(commands,0);
			commands.clear();
		}
		else
		if(commands.size()>1)
		{
			choice=CMParms.combine(commands,1);
			while(commands.size()>1)
				commands.removeElementAt(1);
		}
		final MOB target=getTarget(mob,commands,givenTarget);

		if(target==null) return false;
		if(target.fetchEffect(ID())!=null) return false;

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;
		final boolean success=proficiencyCheck(mob,0,auto);
		if(success)
		{
			final Vector allChoices=new Vector();
			for(final int code : RawMaterial.CODES.ALL())
				if(((code&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_LIQUID)
				&&((code&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_ENERGY)
				&&((code&RawMaterial.MATERIAL_MASK)!=RawMaterial.MATERIAL_GAS)
				&&(code!=RawMaterial.RESOURCE_COTTON)
				&&(code!=RawMaterial.RESOURCE_IRON)
				&&(code!=RawMaterial.RESOURCE_WOOD))
					allChoices.addElement(RawMaterial.CODES.NAME(code));
			Race R=null;
			for(final Enumeration r=CMClass.races();r.hasMoreElements();)
			{
				R=(Race)r.nextElement();
				allChoices.addElement(R.ID().toUpperCase());
			}
			String allergies="";
			if((choice.length()>0)&&(allChoices.contains(choice.toUpperCase())))
				allergies=choice.toUpperCase();
			else
			for(int i=0;i<allChoices.size();i++)
				if((CMLib.dice().roll(1,allChoices.size(),0)==1)
				&&(!(((String)allChoices.elementAt(i)).equalsIgnoreCase(mob.charStats().getMyRace().ID().toUpperCase()))))
					allergies+=" "+(String)allChoices.elementAt(i);
			if(allergies.length()==0) return false;

			final CMMsg msg=CMClass.getMsg(mob,target,this,CMMsg.MSG_OK_VISUAL,"");
			if(target.location()!=null)
			{
				if(target.location().okMessage(target,msg))
				{
					target.location().send(target,msg);
					final Ability A=(Ability)copyOf();
					A.setMiscText(allergies.trim());
					target.addNonUninvokableEffect(A);
				}
			}
			else
			{
				final Ability A=(Ability)copyOf();
				A.setMiscText(allergies.trim());
				target.addNonUninvokableEffect(A);
			}
		}
		return success;
	}
}
