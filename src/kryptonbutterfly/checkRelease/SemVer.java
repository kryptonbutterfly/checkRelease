package kryptonbutterfly.checkRelease;

import static kryptonbutterfly.math.utils.range.Range.*;

import java.util.Objects;

public record SemVer(int major, int minor, int patch) implements Comparable<SemVer>
{
	/**
	 * @param tag
	 *            A git tag containing a semantic version. May start with 'v' or
	 *            'V'.
	 * @return a new SemVer
	 * @throws NumberFormatException
	 *             if major, minor or patch are not valid decimal numbers.
	 * @throws IllegalArgumentException
	 *             if the tag doesn't contain major.minor.path
	 */
	public static SemVer fromGitTag(String tag) throws NumberFormatException, IllegalArgumentException
	{
		Objects.requireNonNull(tag);
		
		tag = tag.trim().toLowerCase();
		
		if (tag.startsWith("v"))
			tag = tag.substring(1);
		
		try
		{
			final var split = tag.split("\\.");
			if (split.length < 2)
				throw new IllegalArgumentException(
					"The supplied tag '%s' is not a valid Semantic Version!".formatted(tag));
			
			final int	major	= Integer.valueOf(split[0]);
			final int	minor	= Integer.valueOf(split[1]);
			
			int end = 0;
			for (final var e : range(split[2]).element())
			{
				if (!Character.isDigit(e))
					break;
				end++;
			}
			final int patch = Integer.valueOf(split[2].substring(0, end));
			return new SemVer(major, minor, patch);
		}
		catch (NumberFormatException e)
		{
			throw e;
		}
	}
	
	public String toGitTag()
	{
		return "v%d.%d.%d".formatted(major(), minor(), patch());
	}
	
	@Override
	public String toString()
	{
		return "%s.%s.%s".formatted(major(), minor(), patch());
	}
	
	@Override
	public int compareTo(SemVer o)
	{
		int c = Integer.signum(o.major() - major());
		if (c != 0)
			return c;
		c = Integer.signum(o.minor() - minor());
		if (c != 0)
			return c;
		return Integer.signum(o.patch() - patch());
	}
}
