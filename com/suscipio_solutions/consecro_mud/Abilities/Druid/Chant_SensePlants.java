package com.suscipio_solutions.consecro_mud.Abilities.Druid;
import java.util.Vector;

import com.suscipio_solutions.consecro_mud.Abilities.interfaces.Ability;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Exits.interfaces.Exit;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.Directions;
import com.suscipio_solutions.consecro_mud.core.interfaces.Physical;
import com.suscipio_solutions.consecro_mud.core.interfaces.Tickable;




@SuppressWarnings("rawtypes")
public class Chant_SensePlants extends Chant
{
	@Override public String ID() { return "Chant_SensePlants"; }
	private final static String localizedName = CMLib.lang().L("Sense Plants");
	@Override public String name() { return localizedName; }
	private final static String localizedStaticDisplay = CMLib.lang().L("(Sensing Plants)");
	@Override public String displayText() { return localizedStaticDisplay; }
	@Override public int classificationCode(){return Ability.ACODE_CHANT|Ability.DOMAIN_PLANTCONTROL;}
	@Override public int abstractQuality(){ return Ability.QUALITY_OK_SELF;}
	@Override protected int canAffectCode(){return CAN_MOBS;}
	Room lastRoom=null;
	protected String word(){return "plants";}
	private final int[] myMats={RawMaterial.MATERIAL_VEGETATION,
						  RawMaterial.MATERIAL_WOODEN};
	protected int[] okMaterials(){	return myMats;}
	private final int[] myRscs={RawMaterial.RESOURCE_COTTON,
						  RawMaterial.RESOURCE_HEMP};
	protected int[] okResources(){	return myRscs;}

	@Override
	public void unInvoke()
	{
		if(!(affected instanceof MOB))
			return;
		final MOB mob=(MOB)affected;
		if(canBeUninvoked())
			lastRoom=null;
		super.unInvoke();
		if(canBeUninvoked())
			mob.tell(L("Your senses are no longer sensitive to @x1.",word()));
	}
	public String itsHere(MOB mob, Room R)
	{
		if(R==null) return "";
		if((okMaterials()!=null)&&(okMaterials().length>0))
			for(int m=0;m<okMaterials().length;m++)
				if((R.myResource()&RawMaterial.MATERIAL_MASK)==okMaterials()[m])
					return "You sense "+RawMaterial.CODES.NAME(R.myResource()).toLowerCase()+" here.";
		if((okResources()!=null)&&(okResources().length>0))
			for(int m=0;m<okResources().length;m++)
				if(R.myResource()==okResources()[m])
					return "You sense "+RawMaterial.CODES.NAME(R.myResource()).toLowerCase()+" here.";
		return "";
	}

	public void messageTo(MOB mob)
	{
		final String here=itsHere(mob,mob.location());
		if(here.length()>0)
			mob.tell(here);
		else
		{
			String last="";
			String dirs="";
			for(int d=Directions.NUM_DIRECTIONS()-1;d>=0;d--)
			{
				final Room R=mob.location().getRoomInDir(d);
				final Exit E=mob.location().getExitInDir(d);
				if((R!=null)&&(E!=null)&&(itsHere(mob,R).length()>0))
				{
					if(last.length()>0)
						dirs+=", "+last;
					last=Directions.getFromDirectionName(d);
				}
			}
			if((dirs.length()==0)&&(last.length()>0))
				mob.tell(L("You sense @x1 to @x2.",word(),last));
			else
			if((dirs.length()>2)&&(last.length()>0))
				mob.tell(L("You sense @x1 to @x2, and @x3.",word(),dirs.substring(2),last));
		}
	}

	@Override
	public boolean tick(Tickable ticking, int tickID)
	{
		if(!super.tick(ticking,tickID))
			return false;
		if((tickID==Tickable.TICKID_MOB)
		   &&(affected!=null)
		   &&(affected instanceof MOB)
		   &&(((MOB)affected).location()!=null)
		   &&((lastRoom==null)||(((MOB)affected).location()!=lastRoom)))
		{
			lastRoom=((MOB)affected).location();
			messageTo((MOB)affected);
		}
		return true;
	}

	@Override
	public boolean invoke(MOB mob, Vector commands, Physical givenTarget, boolean auto, int asLevel)
	{
		if(!super.invoke(mob,commands,givenTarget,auto,asLevel))
			return false;

		MOB target=mob;
		if((auto)&&(givenTarget!=null)&&(givenTarget instanceof MOB))
			target=(MOB)givenTarget;
		if(target.fetchEffect(this.ID())!=null)
		{
			mob.tell(target,null,null,L("<S-NAME> <S-IS-ARE> already sensing @x1.",word()));
			return false;
		}
		final boolean success=proficiencyCheck(mob,0,auto);

		if(success)
		{
			final CMMsg msg=CMClass.getMsg(mob,target,this,verbalCastCode(mob,target,auto),auto?L("<T-NAME> gain(s) sensitivity to @x1!",word()):L("^S<S-NAME> chant(s) and gain(s) sensitivity to @x1!^?",word()));
			if(mob.location().okMessage(mob,msg))
			{
				mob.location().send(mob,msg);
				beneficialAffect(mob,target,asLevel,0);
			}
		}
		else
			beneficialVisualFizzle(mob,null,L("<S-NAME> chant(s), but nothing happens."));

		return success;
	}
}
