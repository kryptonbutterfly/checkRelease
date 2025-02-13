package kryptonbutterfly.checkRelease;

import java.util.Calendar;
import java.util.GregorianCalendar;

public enum Cadence implements ICadence
{
	Daily(Calendar.DATE, 1),
	Weekly(Calendar.DATE, 7),
	Monthly(Calendar.MONTH, 1),
	Never(-1, -1);
	
	private final int	field;
	private final int	amount;
	
	private Cadence(int field, int amount)
	{
		this.field	= field;
		this.amount	= amount;
	}
	
	@Override
	public Long nextSchedule(ICheckReleaseState settings)
	{
		if (field == -1)
			return null;
		
		final var calendar = new GregorianCalendar();
		calendar.setTimeInMillis(settings.lastQuery());
		calendar.add(field, amount);
		return calendar.getTimeInMillis();
	}
}