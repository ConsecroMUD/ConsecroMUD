package com.suscipio_solutions.consecro_mud.Items.interfaces;
import java.util.List;

import com.suscipio_solutions.consecro_mud.Common.interfaces.Manufacturer;
import com.suscipio_solutions.consecro_mud.MOBS.interfaces.MOB;


public interface Electronics extends Item, Technical
{
	public long powerCapacity();
	public void setPowerCapacity(long capacity);

	public long powerRemaining();
	public void setPowerRemaining(long remaining);

	public int powerNeeds();

	public boolean activated();
	public void activate(boolean truefalse);

	public String getManufacturerName();
	public void setManufacturerName(String name);
	public Manufacturer getFinalManufacturer();

	public interface PowerSource extends Electronics
	{
	}

	public interface FuelConsumer extends Electronics
	{
		public int[] getConsumedFuelTypes();
		public void setConsumedFuelType(int[] resources);
		public int getTicksPerFuelConsume();
		public void getTicksPerFuelConsume(int tick);
		public int getFuelRemaining();
		public boolean consumeFuel(int amount);
		public int getTotalFuelCapacity();
	}

	public interface PowerGenerator extends PowerSource, FuelConsumer
	{
		public int getGeneratedAmountPerTick();
		public void setGenerationAmountPerTick(int amt);
	}

	public interface ElecPanel extends Electronics, Container
	{
		public static final TechType[] PANELTYPES={
			TechType.ANY,TechType.SHIP_WEAPON,TechType.SHIP_SHIELD,TechType.SHIP_ENGINE,TechType.SHIP_SENSOR,
			TechType.SHIP_POWER,TechType.SHIP_COMPUTER,TechType.SHIP_SOFTWARE,
			TechType.SHIP_ENVIRO_CONTROL,TechType.SHIP_GENERATOR,TechType.SHIP_DAMPENER,
			TechType.SHIP_TRACTOR
		};
		public TechType panelType();
		public void setPanelType(TechType type);
	}

	public interface Computer extends Electronics.ElecPanel
	{
		public List<Software> getSoftware();
		public List<MOB> getCurrentReaders();
		public void forceReadersMenu();
		public void forceReadersSeeNew();
		public void setActiveMenu(String internalName);
		public String getActiveMenu();
	}

}
