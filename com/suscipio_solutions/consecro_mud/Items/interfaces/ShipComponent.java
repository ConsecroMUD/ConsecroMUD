package com.suscipio_solutions.consecro_mud.Items.interfaces;

public interface ShipComponent extends Electronics
{
	public float getInstalledFactor();
	public void setInstalledFactor(float pct);

	public interface ShipEngine extends ShipComponent, Electronics.FuelConsumer
	{
		public enum ThrustPort { AFT, PORT, VENTRAL, DORSEL, STARBOARD, FORWARD }

		public int getMaxThrust();
		public void setMaxThrust(int max);
		public int getThrust();
		public void setThrust(int max);
		public long getSpecificImpulse();
		public void setSpecificImpulse(long amt);
		public double getFuelEfficiency();
		public void setFuelEfficiency(double amt);
	}
}
