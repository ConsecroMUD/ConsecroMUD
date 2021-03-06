package com.suscipio_solutions.consecro_mud.Items.MiscMagic;
import com.suscipio_solutions.consecro_mud.CharClasses.interfaces.CharClass;
import com.suscipio_solutions.consecro_mud.Common.interfaces.AccountStats.PrideStat;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CharStats;
import com.suscipio_solutions.consecro_mud.Common.interfaces.CMTableRow;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.ImmortalOnly;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMParms;
import com.suscipio_solutions.consecro_mud.core.CMStrings;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class GenSuperPill extends GenPill implements ImmortalOnly
{
	@Override public String ID(){	return "GenSuperPill";}
	public GenSuperPill()
	{
		super();

		setName("a pill");
		basePhyStats.setWeight(1);
		setDisplayText("A strange pill lies here.");
		setDescription("Large and round, with strange markings.");
		secretIdentity="";
		baseGoldValue=200;
		recoverPhyStats();
		material=RawMaterial.RESOURCE_CORN;
	}


	@Override public boolean isGeneric(){return true;}

	@Override
	public String secretIdentity()
	{
		final String tx=StdScroll.makeSecretIdentity("super pill",super.secretIdentity(),"",getSpells());
		String id=readableText;
		int x=id.toUpperCase().indexOf("ARM");
		for(final StringBuffer ID=new StringBuffer(id);((x>0)&&(x<id.length()));x++)
			if(id.charAt(x)=='-')
			{
				ID.setCharAt(x,'+');
				id=ID.toString();
				break;
			}
			else
			if(id.charAt(x)=='+')
			{
				ID.setCharAt(x,'-');
				id=ID.toString();
				break;
			}
			else
			if(Character.isDigit(id.charAt(x)))
				break;
		x=id.toUpperCase().indexOf("DIS");
		if(x>=0)
		{
			final long val=CMParms.getParmPlus(id,"dis");
			final int y=id.indexOf(""+val,x);
			if((val!=0)&&(y>x))
			{
				final StringBuffer middle=new StringBuffer("");
				for(int num=0;num<PhyStats.IS_VERBS.length;num++)
					if(CMath.bset(val,CMath.pow(2,num)))
						middle.append(PhyStats.IS_VERBS[num]+" ");
				id=id.substring(0,x)+middle.toString().trim()+id.substring(y+((""+val).length()));
			}
		}
		x=id.toUpperCase().indexOf("SEN");
		if(x>=0)
		{
			final long val=CMParms.getParmPlus(id,"sen");
			final int y=id.indexOf(""+val,x);
			if((val!=0)&&(y>x))
			{
				final StringBuffer middle=new StringBuffer("");
				for(int num=0;num<PhyStats.CAN_SEE_VERBS.length;num++)
					if(CMath.bset(val,CMath.pow(2,num)))
						middle.append(PhyStats.CAN_SEE_VERBS[num]+" ");
				id=id.substring(0,x)+middle.toString().trim()+id.substring(y+((""+val).length()));
			}
		}
		return tx+"\n("+id+")\n";
	}

	public void EATME(MOB mob)
	{
		boolean redress=false;
		if(getSpells().size()>0)
			eatIfAble(mob);
		if((CMParms.getParmPlus(readableText,"beacon")>0)
		&&(mob.location()!=null))
			mob.setStartRoom(mob.location());
		mob.basePhyStats().setAbility(mob.basePhyStats().ability()+CMParms.getParmPlus(readableText,"abi"));
		mob.basePhyStats().setArmor(mob.basePhyStats().armor()+CMParms.getParmPlus(readableText,"arm"));
		mob.basePhyStats().setAttackAdjustment(mob.basePhyStats().attackAdjustment()+CMParms.getParmPlus(readableText,"att"));
		mob.basePhyStats().setDamage(mob.basePhyStats().damage()+CMParms.getParmPlus(readableText,"dam"));
		mob.basePhyStats().setDisposition(mob.basePhyStats().disposition()|CMParms.getParmPlus(readableText,"dis"));
		final int levels=CMParms.getParmPlus(readableText,"lev");
		for(int i=0;i<levels;i++)
			CMLib.leveler().level(mob);
		mob.basePhyStats().setLevel(mob.basePhyStats().level());
		mob.basePhyStats().setRejuv(mob.basePhyStats().rejuv()+CMParms.getParmPlus(readableText,"rej"));
		mob.basePhyStats().setSensesMask(mob.basePhyStats().sensesMask()|CMParms.getParmPlus(readableText,"sen"));
		mob.basePhyStats().setSpeed(mob.basePhyStats().speed()+CMParms.getParmPlus(readableText,"spe"));
		mob.basePhyStats().setWeight(mob.basePhyStats().weight()+CMParms.getParmPlus(readableText,"wei"));
		if(CMParms.getParmPlus(readableText,"wei")!=0) redress=true;
		mob.basePhyStats().setHeight(mob.basePhyStats().height()+CMParms.getParmPlus(readableText,"hei"));
		if(CMParms.getParmPlus(readableText,"hei")!=0) redress=true;

		String val=CMParms.getParmStr(readableText,"gen","").toUpperCase();
		if((val.length()>0)&&((val.charAt(0)=='M')||(val.charAt(0)=='F')||(val.charAt(0)=='N')))
			mob.baseCharStats().setStat(CharStats.STAT_GENDER,val.charAt(0));
		val=CMParms.getParmStr(readableText,"cla","").toUpperCase();
		if(val.length()>0)
		{
			final CharClass C=CMClass.findCharClass(val);
			if((C!=null)&&(C.availabilityCode()!=0))
			{
				mob.baseCharStats().setCurrentClass(C);
				if((!mob.isMonster())&&(mob.soulMate()==null))
					CMLib.coffeeTables().bump(mob,CMTableRow.STAT_CLASSCHANGE);
			}
		}
		if(CMParms.getParmPlus(readableText,"lev")!=0)
			mob.baseCharStats().setClassLevel(mob.baseCharStats().getCurrentClass(),mob.baseCharStats().getClassLevel(mob.baseCharStats().getCurrentClass())+CMParms.getParmPlus(readableText,"lev"));
		val=CMParms.getParmStr(readableText,"rac","").toUpperCase();
		if((val.length()>0)&&(CMClass.getRace(val)!=null))
		{
			redress=true;
			mob.baseCharStats().setMyRace(CMClass.getRace(val));
			mob.baseCharStats().getMyRace().startRacing(mob,false);
		}
		for(final int i : CharStats.CODES.BASECODES())
		{
			mob.baseCharStats().setStat(i,mob.baseCharStats().getStat(i)+CMParms.getParmPlus(readableText,CMStrings.limit(CharStats.CODES.NAME(i).toLowerCase(),3)));
			final int max = CharStats.CODES.toMAXBASE(i);
			mob.baseCharStats().setStat(max,mob.baseCharStats().getStat(max)+CMParms.getParmPlus(readableText,"max"+CMStrings.limit(CharStats.CODES.NAME(i).toLowerCase(),3)));
		}

		mob.baseState().setHitPoints(mob.baseState().getHitPoints()+CMParms.getParmPlus(readableText,"hit"));
		mob.curState().setHunger(mob.curState().getHunger()+CMParms.getParmPlus(readableText,"hun"));
		mob.baseState().setMana(mob.baseState().getMana()+CMParms.getParmPlus(readableText,"man"));
		mob.baseState().setMovement(mob.baseState().getMovement()+CMParms.getParmPlus(readableText,"mov"));
		mob.curState().setThirst(mob.curState().getThirst()+CMParms.getParmPlus(readableText,"thi"));

		mob.setPractices(mob.getPractices()+CMParms.getParmPlus(readableText,"prac"));
		mob.setTrains(mob.getTrains()+CMParms.getParmPlus(readableText,"trai"));
		final int qp=CMParms.getParmPlus(readableText,"ques");
		if(qp!=0)
			mob.setQuestPoint(mob.getQuestPoint()+qp);
		if(qp>0)
			CMLib.players().bumpPrideStat(mob,PrideStat.QUESTPOINTS_EARNED, qp);
		final int newMoney=CMParms.getParmPlus(readableText,"coin");
		if(newMoney!=0) CMLib.beanCounter().setMoney(mob,CMLib.beanCounter().getMoney(mob)+newMoney);
		final int exp=CMParms.getParmPlus(readableText,"expe");
		if(exp!=0) CMLib.leveler().postExperience(mob,null,null,exp,false);
		mob.recoverCharStats();
		mob.recoverPhyStats();
		mob.recoverMaxState();
		if(redress)	CMLib.utensils().confirmWearability(mob);
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if(msg.amITarget(this))
		{
			final MOB mob=msg.source();
			switch(msg.targetMinor())
			{
			case CMMsg.TYP_EAT:
				if((msg.sourceMessage()==null)&&(msg.othersMessage()==null))
				{
					EATME(mob);
					super.executeMsg(myHost,msg);
				}
				else
					msg.addTrailerMsg(CMClass.getMsg(msg.source(),msg.target(),msg.tool(),CMMsg.NO_EFFECT,null,msg.targetCode(),msg.targetMessage(),CMMsg.NO_EFFECT,null));
				break;
			default:
				super.executeMsg(myHost,msg);
				break;
			}
		}
		else
			super.executeMsg(myHost,msg);
	}
}
