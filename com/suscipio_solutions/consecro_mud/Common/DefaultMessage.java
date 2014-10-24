package com.suscipio_solutions.consecro_mud.Common;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Common.interfaces.CMMsg;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;
import com.suscipio_solutions.consecro_mud.core.CMClass;
import com.suscipio_solutions.consecro_mud.core.CMath;
import com.suscipio_solutions.consecro_mud.core.collections.SLinkedList;
import com.suscipio_solutions.consecro_mud.core.interfaces.CMObject;
import com.suscipio_solutions.consecro_mud.core.interfaces.Environmental;


public class DefaultMessage implements CMMsg
{
	@Override public String ID(){return "DefaultMessage";}
	@Override public String name() { return ID();}
	@Override public CMObject newInstance(){try{return getClass().newInstance();}catch(final Exception e){return new DefaultMessage();}}
	@Override public void initializeClass(){}
	@Override public int compareTo(CMObject o){ return CMClass.classID(this).compareToIgnoreCase(CMClass.classID(o));}

	protected int   		targetMajorMask=0;
	protected int   		sourceMajorMask=0;
	protected int   		othersMajorMask=0;
	protected int   		targetMinorType=0;
	protected int   		sourceMinorType=0;
	protected int   		othersMinorType=0;
	protected String		targetMsg=null;
	protected String		othersMsg=null;
	protected String		sourceMsg=null;
	protected MOB   		myAgent=null;
	protected Environmental myTarget=null;
	protected Environmental myTool=null;
	protected int   		value=0;
	protected List<CMMsg>	trailMsgs=null;
	protected List<Runnable>trailRunnables=null;

	@Override
	public CMObject copyOf()
	{
		try
		{
			return (DefaultMessage)this.clone();
		}
		catch(final CloneNotSupportedException e)
		{
			return newInstance();
		}
	}

	@Override
	protected void finalize() throws Throwable
	{
		targetMajorMask=0;
		sourceMajorMask=0;
		othersMajorMask=0;
		targetMinorType=0;
		sourceMinorType=0;
		othersMinorType=0;
		targetMsg=null;
		othersMsg=null;
		sourceMsg=null;
		myAgent=null;
		myTarget=null;
		myTool=null;
		trailMsgs=null;
		trailRunnables=null;
		value=0;
		if(!CMClass.returnMsg(this))
			super.finalize();
	}

	@Override
	public CMMsg modify(final MOB source, final Environmental target, final int newAllCode, final String allMessage)
	{
		myAgent=source;
		myTarget=target;
		myTool=null;
		sourceMsg=allMessage;
		targetMsg=allMessage;
		targetMajorMask=newAllCode&CMMsg.MAJOR_MASK;
		sourceMajorMask=targetMajorMask;
		othersMajorMask=targetMajorMask;
		targetMinorType=newAllCode&CMMsg.MINOR_MASK;
		sourceMinorType=targetMinorType;
		othersMinorType=targetMinorType;
		othersMsg=allMessage;
		return this;
	}

	@Override
	public CMMsg modify(final MOB source, final int newAllCode, final String allMessage)
	{
		myAgent=source;
		myTarget=null;
		myTool=null;
		sourceMsg=allMessage;
		targetMsg=allMessage;
		targetMajorMask=newAllCode&CMMsg.MAJOR_MASK;
		sourceMajorMask=targetMajorMask;
		othersMajorMask=targetMajorMask;
		targetMinorType=newAllCode&CMMsg.MINOR_MASK;
		sourceMinorType=targetMinorType;
		othersMinorType=targetMinorType;
		othersMsg=allMessage;
		return this;
	}

	@Override
	public CMMsg modify(final MOB source, final int newAllCode, final String allMessage, final int newValue)
	{
		 myAgent=source;
		 myTarget=null;
		 myTool=null;
		 sourceMsg=allMessage;
		 targetMsg=allMessage;
		 targetMajorMask=newAllCode&CMMsg.MAJOR_MASK;
		 sourceMajorMask=targetMajorMask;
		 othersMajorMask=targetMajorMask;
		 targetMinorType=newAllCode&CMMsg.MINOR_MASK;
		 sourceMinorType=targetMinorType;
		 othersMinorType=targetMinorType;
		 othersMsg=allMessage;
		 value=newValue;
			return this;
	}

	@Override
	public CMMsg modify(final MOB source, final Environmental target, final Environmental tool,
						final int newAllCode, final String allMessage)
	{
		myAgent=source;
		myTarget=target;
		myTool=tool;
		sourceMsg=allMessage;
		targetMsg=allMessage;
		targetMajorMask=newAllCode&CMMsg.MAJOR_MASK;
		sourceMajorMask=targetMajorMask;
		othersMajorMask=targetMajorMask;
		targetMinorType=newAllCode&CMMsg.MINOR_MASK;
		sourceMinorType=targetMinorType;
		othersMinorType=targetMinorType;
		othersMsg=allMessage;
		return this;
	}

	@Override
	public CMMsg modify(final MOB source,
						final Environmental target,
						final Environmental tool,
						final int newAllCode,
						final String sourceMessage,
						final String targetMessage,
						final String othersMessage)
	{
		myAgent=source;
		myTarget=target;
		myTool=tool;
		sourceMsg=sourceMessage;
		targetMsg=targetMessage;
		targetMajorMask=newAllCode&CMMsg.MAJOR_MASK;
		sourceMajorMask=targetMajorMask;
		othersMajorMask=targetMajorMask;
		targetMinorType=newAllCode&CMMsg.MINOR_MASK;
		sourceMinorType=targetMinorType;
		othersMinorType=targetMinorType;
		othersMsg=othersMessage;
		return this;
	}

	@Override
	public CMMsg setSourceCode(final int code)
	{
		sourceMajorMask=code&CMMsg.MAJOR_MASK;
		sourceMinorType=code&CMMsg.MINOR_MASK;
		return this;
	}
	
	@Override
	public CMMsg setTargetCode(final int code)
	{
		targetMajorMask=code&CMMsg.MAJOR_MASK;
		targetMinorType=code&CMMsg.MINOR_MASK;
		return this;
	}
	
	@Override
	public CMMsg setOthersCode(final int code)
	{
		othersMajorMask=code&CMMsg.MAJOR_MASK;
		othersMinorType=code&CMMsg.MINOR_MASK;
		return this;
	}
	
	@Override 
	public CMMsg setSourceMessage(final String str)
	{
		sourceMsg=str;
		return this;
	}
	
	@Override 
	public CMMsg setTargetMessage(final String str)
	{
		targetMsg=str;
		return this;
	}
	
	@Override 
	public CMMsg setOthersMessage(final String str)
	{
		othersMsg=str;
		return this;
	}

	@Override 
	public int value()
	{
		return value;
	}
	
	@Override
	public CMMsg setValue(final int amount)
	{
		value=amount;
		return this;
	}

	@Override
	public List<CMMsg> trailerMsgs()
	{
		return trailMsgs;
	}

	@Override
	public List<Runnable> trailerRunnables()
	{
		return trailRunnables;
	}

	@Override
	public CMMsg addTrailerMsg(final CMMsg msg)
	{
		if(trailMsgs==null) 
			trailMsgs=new SLinkedList<CMMsg>();
		trailMsgs.add(msg);
		return this;
	}

	@Override
	public CMMsg addTrailerRunnable(final Runnable r)
	{
		if(trailRunnables==null) 
			trailRunnables=new SLinkedList<Runnable>();
		trailRunnables.add(r);
		return this;
	}
	
	@Override
	public CMMsg modify(final MOB source,
						final Environmental target,
						final Environmental tool,
						final int newSourceCode,
						final String sourceMessage,
						final int newTargetCode,
						final String targetMessage,
						final int newOthersCode,
						final String othersMessage)
	{
		myAgent=source;
		myTarget=target;
		myTool=tool;
		sourceMsg=sourceMessage;
		targetMsg=targetMessage;
		targetMajorMask=newTargetCode&CMMsg.MAJOR_MASK;
		sourceMajorMask=newSourceCode&CMMsg.MAJOR_MASK;
		othersMajorMask=newOthersCode&CMMsg.MAJOR_MASK;
		targetMinorType=newTargetCode&CMMsg.MINOR_MASK;
		sourceMinorType=newSourceCode&CMMsg.MINOR_MASK;
		othersMinorType=newOthersCode&CMMsg.MINOR_MASK;
		othersMsg=othersMessage;
		return this;
	}
	
	@Override
	public CMMsg modify(final MOB source,
						final Environmental target,
						final Environmental tool,
						final int newSourceCode,
						final int newTargetCode,
						final int newOthersCode,
						final String allMessage)
	{
		myAgent=source;
		myTarget=target;
		myTool=tool;
		targetMsg=allMessage;
		sourceMsg=allMessage;
		targetMajorMask=newTargetCode&CMMsg.MAJOR_MASK;
		sourceMajorMask=newSourceCode&CMMsg.MAJOR_MASK;
		othersMajorMask=newOthersCode&CMMsg.MAJOR_MASK;
		targetMinorType=newTargetCode&CMMsg.MINOR_MASK;
		sourceMinorType=newSourceCode&CMMsg.MINOR_MASK;
		othersMinorType=newOthersCode&CMMsg.MINOR_MASK;
		othersMsg=allMessage;
		return this;
	}
	
	@Override 
	public final MOB source()
	{ 
		return myAgent; 
	}
	
	@Override 
	public final CMMsg setSource(final MOB mob)
	{
		myAgent=mob;
		return this;
	}
	
	@Override 
	public final Environmental target() 
	{ 
		return myTarget; 
	}
	
	@Override 
	public final CMMsg setTarget(final Environmental E)
	{
		myTarget=E;
		return this;
	}
	
	@Override 
	public final Environmental tool() 
	{ 
		return myTool; 
	}
	
	@Override 
	public final CMMsg setTool(final Environmental E)
	{
		myTool=E;
		return this;
	}
	
	@Override 
	public final int targetMajor() 
	{ 
		return targetMajorMask; 
	}
	
	@Override 
	public final int sourceMajor() 
	{ 
		return sourceMajorMask;
	}
	
	@Override 
	public final int othersMajor() 
	{ 
		return othersMajorMask; 
	}
	
	@Override 
	public final boolean targetMajor(final int bitMask) 
	{ 
		return (targetMajorMask&bitMask)==bitMask; 
	}
	
	@Override 
	public final int targetMinor() 
	{ 
		return targetMinorType; 
	}
	
	@Override 
	public final int targetCode() 
	{ 
		return targetMajorMask | targetMinorType; 
	}
	
	@Override 
	public final String targetMessage() 
	{ 
		return targetMsg;
	}
	
	@Override 
	public final int sourceCode() 
	{ 
		return sourceMajorMask | sourceMinorType; 
	}
	
	@Override 
	public final boolean sourceMajor(final int bitMask) 
	{ 
		return (sourceMajorMask&bitMask)==bitMask; 
	}
	
	@Override 
	public final int sourceMinor() 
	{ 
		return sourceMinorType;
	}
	
	@Override 
	public final String sourceMessage() 
	{ 
		return sourceMsg;
	}
	
	@Override 
	public final boolean othersMajor(final int bitMask) 
	{ 
		return (othersMajorMask&bitMask)==bitMask; 
	}
	
	@Override 
	public final int othersMinor() 
	{ 
		return othersMinorType; 
	}
	
	@Override 
	public final int othersCode() 
	{  
		return othersMajorMask | othersMinorType; 
	}
	
	@Override 
	public final String othersMessage() 
	{ 
		return othersMsg; 
	}
	
	@Override  
	public final boolean amITarget(final Environmental thisOne)
	{ 
		return ((thisOne!=null)&&(thisOne==target()));
	}

	@Override  
	public final boolean amISource(final MOB thisOne)
	{
		return ((thisOne!=null)&&(thisOne==source()));
	}

	@Override  
	public final boolean isTarget(final Environmental E)
	{
		return amITarget(E);
	}

	@Override  
	public final boolean isTarget(final int codeOrMask)
	{
		return matches(targetMajorMask, targetMinorType,codeOrMask);
	}

	@Override  
	public final boolean isTarget(final String codeOrMaskDesc)
	{
		return matches(targetMajorMask, targetMinorType,codeOrMaskDesc);
	}

	@Override  
	public final boolean isSource(final Environmental E)
	{
		return (E instanceof MOB)?amISource((MOB)E):false;
	}

	@Override  
	public final boolean isSource(final int codeOrMask)
	{
		return matches(sourceMajorMask, sourceMinorType, codeOrMask);
	}

	@Override  
	public final boolean isSource(final String codeOrMaskDesc)
	{
		return matches(sourceMajorMask, sourceMinorType,codeOrMaskDesc);
	}

	@Override  
	public final boolean isOthers(final Environmental E)
	{
		return (!isTarget(E))&&(!isSource(E));
	}

	@Override  
	public final boolean isOthers(final int codeOrMask)
	{
		return matches(othersMajorMask, othersMinorType, codeOrMask);
	}

	@Override  
	public final boolean isOthers(final String codeOrMaskDesc)
	{
		return matches(othersMajorMask, othersMinorType, codeOrMaskDesc);
	}

	protected static final boolean matches(final int major, final int minor, final int code)
	{
		return (major == code) || (minor == code);
	}
	
	protected static final boolean matches(final int major, final int minor, String code2)
	{
		Integer I=Desc.getMSGTYPE_DESCS().get(code2.toUpperCase());
		if(I==null)
		{
			code2=code2.toUpperCase();
			for(int i=0;i<TYPE_DESCS.length;i++)
				if(code2.startsWith(TYPE_DESCS[i]))
				{ I=Integer.valueOf(i); break;}
			if(I==null)
			for(int i=0;i<TYPE_DESCS.length;i++)
				if(TYPE_DESCS[i].startsWith(code2))
				{ I=Integer.valueOf(i); break;}
			if(I==null)
			for(int i=0;i<MASK_DESCS.length;i++)
				if(code2.startsWith(MASK_DESCS[i]))
				{ I=Integer.valueOf((int)CMath.pow(2,11+i)); break;}
			if(I==null)
			for(int i=0;i<MASK_DESCS.length;i++)
				if(MASK_DESCS[i].startsWith(code2))
				{ I=Integer.valueOf((int)CMath.pow(2,11+i)); break;}
			if(I==null)
				for (final Object[] element : MISC_DESCS)
					if(code2.startsWith((String)element[0]))
					{ I=(Integer)element[1]; break;}
			if(I==null)
				for (final Object[] element : MISC_DESCS)
					if(((String)element[0]).startsWith(code2))
					{ I=(Integer)element[1]; break;}
			if(I==null) return false;
		}
		return matches(major, minor, I.intValue());
	}

	@Override
	public boolean equals(Object o)
	{
		if(o instanceof CMMsg)
		{
			final CMMsg m=(CMMsg)o;
			return (m.sourceCode()==sourceCode())
					&&(m.targetCode()==targetCode())
					&&(m.othersCode()==othersCode())
					&&(m.source()==source())
					&&(m.target()==target())
					&&(m.tool()==tool())
					&&((m.sourceMessage()==sourceMessage())||((sourceMessage()!=null)&&(sourceMessage().equals(m.sourceMessage()))))
					&&((m.targetMessage()==targetMessage())||((targetMessage()!=null)&&(targetMessage().equals(m.targetMessage()))))
					&&((m.othersMessage()==othersMessage())||((othersMessage()!=null)&&(othersMessage().equals(m.othersMessage()))));
		}
		else
			return super.equals(o);
	}

	@Override
	public int hashCode()
	{
		return super.hashCode();
	}

}
