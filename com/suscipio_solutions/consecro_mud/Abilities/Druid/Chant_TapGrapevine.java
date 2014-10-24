package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Items.interfaces.Item;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.collections.XVector;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;



@SuppressWarnings({"unchecked","rawtypes"})
public class Chant_TapGrapevine extends Chant
{
	@Override public String ID() { return "Chant_TapGrapevine"; }
	private final static String localizedName = CMLib.lang().L("Tap Grapevine");
	@Override public String name() { return localizedName; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PLANTCONTROL;}
	@Override public int abstractQuality(){return Ability.QUALITY_INDIFFERENT;}
	@Override protected int canAffectCode(){return Ability.CAN_MOBS|Ability.CAN_ITEMS;}
	@Override protected int canTargetCode(){return 0;}
	protected List<Ability> myChants=new Vector();

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		super.executeMsg(myHost,msg);
		if((affected instanceof Item)
		&&(((Item)affected).owner() instanceof Room)
		&&(((Room)((Item)affected).owner()).isContent((Item)affected))
		&&(msg.sourceMinor()==CMMsg.TYP_SPEAK)
		&&(invoker!=null)
		&&(invoker.location()!=((Room)((Item)affected).owner()))
		&&(msg.othersMessage()!=null))
			invoker.executeMsg(invoker,msg);
	}

	@Override
	public CMObject copyOf()
	{
		final Chant_TapGrapevine obj=(Chant_TapGrapevine)super.copyOf();
		obj.myChants=new Vector<Ability>();
		obj.myChants.addAll(myChants);
		return obj;
	}

	@Override
	public void unInvoke()
	{
		if((affected instanceof MOB)&&(myChants!=null))
		{
			final List<Ability> V=myChants;
			myChants=null;
			for(int i=0;i<V.size();i++)
			{
				final Ability A=V.get(i);
				if((A.affecting()!=null)
				   &&(A.ID().equals(ID()))
				   &&(A.affecting() instanceof Item))
				{
					final Item I=(Item)A.affecting();
					I.delEffect(A);
				}
			}
		}
		super.unInvoke();
	}

	public static Ability isPlant(Item I)
	{
		if((I!=null)&&(I.rawSecretIdentity().length()>0))
		{
			for(final Enumeration<Ability> a=I.effects();a.hasMoreElements();)
			{
				final Ability A=a.nextElement();
				if((A!=null)
				&&(A.invoker()!=null)
				&&(A instanceof Chant_SummonPlants))
					return A;
			}
		}
		return null;
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if((mob.fetchEffect(ID())!=null)||(mob.fetchEffect("Chant_Grapevine")!=null))
		{
			mob.tell(L("You are already listening through a grapevine."));
			return false;
		}
		MOB tapped=null;
		for(int i=0;i<mob.location().numItems();i++)
		{
			final Item I=mob.location().getItem(i);
			if((I!=null)&&(isPlant(I)!=null))
			{
				final Ability A=isPlant(I);
				if((A!=null)&&(A.invoker()!=mob))
					tapped=A.invoker();
			}
		}

		final Vector myRooms=(tapped==null)?null:Druid_MyPlants.myPlantRooms(tapped);
		if((myRooms==null)||(myRooms.size()==0))
		{
			mob.tell(L("There doesn't appear to be any plants around here to listen through."));
			return false;
		}
		Item myPlant=Druid_MyPlants.myPlant(mob.location(),tapped,0);
		if((!auto)&&(myPlant==null))
		{
			mob.tell(L("You must be in the same room as someone elses plants to initiate this chant."));
			return false;
		}

		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,myPlant,this,verbalCastCode(mob,myPlant,auto),auto?"":L("^S<S-NAME> chant(s) to <T-NAMESELF> and listen(s) carefully to <T-HIM-HER>!^?"));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				myChants=new Vector();
				beneficialAffect(mob,mob,asLevel,0);
				final Chant_TapGrapevine C=(Chant_TapGrapevine)mob.fetchEffect(ID());
				if(C==null) return false;
				for(int i=0;i<myRooms.size();i++)
				{
					final Room R=(Room)myRooms.elementAt(i);
					int ii=0;
					myPlant=Druid_MyPlants.myPlant(R,tapped,ii);
					while(myPlant!=null)
					{
						Ability A=myPlant.fetchEffect(ID());
						if(A!=null) myPlant.delEffect(A);
						myPlant.addNonUninvokableEffect((Ability)C.copyOf());
						A=myPlant.fetchEffect(ID());
						if(A!=null) myChants.add(A);
						ii++;
						myPlant=Druid_MyPlants.myPlant(R,tapped,ii);
					}
				}
				C.myChants=new XVector<Ability>(myChants);
				myChants=new Vector();
			}

		}
		else
			beneficialVisualFizzle(mob,myPlant,L("<S-NAME> chant(s) to <T-NAMESELF>, but nothing happens."));


		// return whether it worked
		return success;
	}
}
