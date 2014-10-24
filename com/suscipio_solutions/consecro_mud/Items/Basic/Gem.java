package com.suscipio_solutions.consecro_mud.Items.Basic;
import java.util.Random;

import com.suscipio_solutions.consecro_mud.Items.interfaces.RawMaterial;


public class Gem extends StdItem
{
	@Override public String ID(){	return "Gem";}
	public final static int QUARTZ 			= 0;
	public final static int AZURITE			= 1;
	public final static int BLOODSTONE		= 2;
	public final static int JADE			= 3;
	public final static int DIAMOND			= 4;
	public final static int RUBY			= 5;
	public final static int OPAL			= 6;
	public final static int TOPAZ			= 7;
	public final static int SAPPHIRE		= 8;
	public final static int ONYX	 		= 9;
	public final static int PEARL			= 10;
	public final static int EMERALD			= 11;
	public final static int AMETHYST		= 12;
	private int lastLevel=-1;

	public Gem()
	{
		super();


		final Random randomizer = new Random(System.currentTimeMillis());
		final int ringType = Math.abs(randomizer.nextInt() % 12);
		basePhyStats.setLevel(ringType);
		recoverPhyStats();
	}

	@Override
	public void recoverPhyStats()
	{
		super.recoverPhyStats();
		if(lastLevel!=phyStats().level())
		{
			lastLevel=phyStats().level();
			setItemDescription(phyStats.level());
		}
	}

	public void setItemDescription(int level)
	{
		switch(level)
		{
			case AZURITE:
				setName("a piece of azurite");
				setDisplayText("a piece of azurite lies here.");
				setDescription("A piece of blue stone.");
				baseGoldValue=20;
				material=RawMaterial.RESOURCE_GEM;
				break;
			case BLOODSTONE:
				setName("a bloodstone");
				setDisplayText("a bloodstone lies here.");
				setDescription("It dark grey stone with flecks of red.");
				baseGoldValue=100;
				material=RawMaterial.RESOURCE_GEM;
				break;
			case JADE:
				setName("a jade stone");
				setDisplayText("a jade stone lies here.");
				setDescription("A beutiful green stone.");
				baseGoldValue=200;
				material=RawMaterial.RESOURCE_JADE;
				break;
			case DIAMOND:
				setName("a diamond");
				setDisplayText("a diamond lies here.");
				setDescription("Finely cut and sparkling.");
				baseGoldValue=5000;
				material=RawMaterial.RESOURCE_DIAMOND;
				break;
			case QUARTZ:
				setName("a piece of quartz");
				setDisplayText("a piece of quartz lies here.");
				setDescription("It is a glasslike stone, gorgeous to the eye.");
				baseGoldValue=30;
				material=RawMaterial.RESOURCE_CRYSTAL;
				break;
			case RUBY:
				setName("a ruby");
				setDisplayText("a ruby lies here.");
				setDescription("A beautiful red ruby with a smooth surface.");
				baseGoldValue=5000;
				material=RawMaterial.RESOURCE_GEM;
				break;
			case OPAL:
				setName("an opal");
				setDisplayText("an opal lies here.");
				setDescription("Pale blue and lovely.");
				baseGoldValue=2000;
				material=RawMaterial.RESOURCE_GEM;
				break;
			case TOPAZ:
				setName("a piece of topaz");
				setDisplayText("a piece of topaz lies here.");
				setDescription("A yellow stone.");
				baseGoldValue=500;
				material=RawMaterial.RESOURCE_GEM;
				break;
			case SAPPHIRE:
				setName("a sapphire");
				setDisplayText("a sapphire lies here.");
				setDescription("Clear, blue, and very fancy.");
				baseGoldValue=1000;
				material=RawMaterial.RESOURCE_GEM;
				break;
			case ONYX:
				setName("an onyx stone");
				setDisplayText("a onyx stone lies here.");
				setDescription("A beautiful rich black stone.");
				baseGoldValue=100;
				material=RawMaterial.RESOURCE_GEM;
				break;
			case PEARL:
				setName("a pearl");
				setDisplayText("a pearl lies here.");
				setDescription("Perfectly round, pure and white.");
				baseGoldValue=300;
				material=RawMaterial.RESOURCE_PEARL;
				break;
			case EMERALD:
				setName("an emerald");
				setDisplayText("an emerald lies here.");
				setDescription("A beautiful clear green stone.");
				baseGoldValue=5000;
				material=RawMaterial.RESOURCE_GEM;
				break;
			default:
				setName("a hunk of metal");
				setDisplayText("a hunk of steel ring is on the ground.");
				setDescription("It is a simple steel ring.");
				material=RawMaterial.RESOURCE_STEEL;
				break;
		}
	}
}
