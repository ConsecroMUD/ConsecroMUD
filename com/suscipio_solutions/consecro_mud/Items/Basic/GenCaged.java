package com.suscipio_solutions.consecro_mud.Items.Basic;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.Common.interfaces.PhyStats;
import com.suscipio_solutions.consecro_mud.Items.interfaces.CagedAnimal;
import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;
import com.suscipio_solutions.consecro_mud.Libraries.interfaces.XMLLibrary;
import com.suscipio_solutions.consecro_mud.Locales.interfaces.Room;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMLib;
import com.suscipio_solutions.consecro_mud.core.CMSecurity;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.Log;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class GenCaged extends GenItem implements CagedAnimal
{
	@Override public String ID(){	return "GenCaged";}
	public GenCaged()
	{
		super();
		setName("a caged creature");
		basePhyStats.setWeight(150);
		setDisplayText("a caged creature sits here.");
		setDescription("");
		baseGoldValue=5;
		basePhyStats().setLevel(1);
		setMaterial(RawMaterial.RESOURCE_MEAT);
		recoverPhyStats();
	}
	protected byte[]	readableText=null;
	@Override public String readableText(){return readableText==null?"":CMLib.encoder().decompressString(readableText);}
	@Override public void setReadableText(String text){readableText=(text.trim().length()==0)?null:CMLib.encoder().compressString(text);}
	@Override
	public boolean cageMe(MOB M)
	{
		if(M==null) return false;
		if(!M.isMonster()) return false;
		name=M.Name();
		displayText=M.displayText();
		setDescription(M.description());
		basePhyStats().setLevel(M.basePhyStats().level());
		basePhyStats().setWeight(M.basePhyStats().weight());
		basePhyStats().setHeight(M.basePhyStats().height());
		final StringBuffer itemstr=new StringBuffer("");
		itemstr.append("<MOBITEM>");
		itemstr.append(CMLib.xml().convertXMLtoTag("MICLASS",CMClass.classID(M)));
		itemstr.append(CMLib.xml().convertXMLtoTag("MISTART",CMLib.map().getExtendedRoomID(M.getStartRoom())));
		itemstr.append(CMLib.xml().convertXMLtoTag("MIDATA",CMLib.coffeeMaker().getPropertiesStr(M,true)));
		itemstr.append("</MOBITEM>");
		setCageText(itemstr.toString());
		recoverPhyStats();
		return true;
	}

	@Override
	public void destroy()
	{
		if((CMSecurity.isDebugging(CMSecurity.DbgFlag.MISSINGKIDS))&&(fetchEffect("Age")!=null)&&CMath.isInteger(fetchEffect("Age").text())&&(CMath.s_int(fetchEffect("Age").text())>Short.MAX_VALUE))
			Log.debugOut("MISSKIDS",new Exception(Name()+" went missing form "+CMLib.map().getExtendedRoomID(CMLib.map().roomLocation(this))));
		super.destroy();
	}

	@Override
	public void executeMsg(final Environmental myHost, final CMMsg msg)
	{
		if((msg.amITarget(this)
			||((msg.tool()==this)&&(msg.target()==container())&&(container()!=null)))
		&&((basePhyStats().ability()&ABILITY_MOBPROGRAMMATICALLY)==0)
		&&((msg.targetMinor()==CMMsg.TYP_GET)||(msg.targetMinor()==CMMsg.TYP_DROP)))
		{
			final MOB M=unCageMe();
			if((M!=null)&&(msg.source().location()!=null))
				M.bringToLife(msg.source().location(),true);
			destroy();
			return;
		}
		super.executeMsg(myHost,msg);
	}
	@Override
	public MOB unCageMe()
	{
		MOB M=null;
		if(cageText().length()==0) return M;
		final List<XMLLibrary.XMLpiece> buf=CMLib.xml().parseAllXML(cageText());
		if(buf==null)
		{
			Log.errOut("Caged","Error parsing 'MOBITEM'.");
			return M;
		}
		final XMLLibrary.XMLpiece iblk=CMLib.xml().getPieceFromPieces(buf,"MOBITEM");
		if((iblk==null)||(iblk.contents==null))
		{
			Log.errOut("Caged","Error parsing 'MOBITEM'.");
			return M;
		}
		final String itemi=CMLib.xml().getValFromPieces(iblk.contents,"MICLASS");
		final String startr=CMLib.xml().getValFromPieces(iblk.contents,"MISTART");
		final Environmental newOne=CMClass.getMOB(itemi);
		final List<XMLLibrary.XMLpiece> idat=CMLib.xml().getContentsFromPieces(iblk.contents,"MIDATA");
		if((idat==null)||(newOne==null)||(!(newOne instanceof MOB)))
		{
			Log.errOut("Caged","Error parsing 'MOBITEM' data.");
			return M;
		}
		CMLib.coffeeMaker().setPropertiesStr(newOne,idat,true);
		M=(MOB)newOne;
		M.basePhyStats().setRejuv(PhyStats.NO_REJUV);
		M.setStartRoom(null);
		if(M.isGeneric())
			CMLib.coffeeMaker().resetGenMOB(M,M.text());
		if((startr.length()>0)&&(!startr.equalsIgnoreCase("null")))
		{
			final Room R=CMLib.map().getRoom(startr);
			if(R!=null)
				M.setStartRoom(R);
		}
		return M;
	}
	@Override public String cageText(){ return CMLib.xml().restoreAngleBrackets(readableText());}
	@Override
	public void setCageText(String text)
	{
		setReadableText(CMLib.xml().parseOutAngleBrackets(text));
		CMLib.flags().setReadable(this,false);
	}
}
