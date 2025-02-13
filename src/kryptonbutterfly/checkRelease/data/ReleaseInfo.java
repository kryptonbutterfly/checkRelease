package kryptonbutterfly.checkRelease.data;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.annotations.Expose;

public class ReleaseInfo
{
	public static final String				dateFormatPattern	= "yyyy-MM-dd'T'HH:mm:ss'Z'Z";
	private static final SimpleDateFormat	sdf					= new SimpleDateFormat(dateFormatPattern);
	
	@Expose
	public String html_url = null;
	
	@Expose
	public String tag_name = null;
	
	@Expose
	public String name = null;
	
	@Expose
	public boolean draft = false;
	
	@Expose
	public boolean prerelease = false;
	
	@Expose
	public Date published_at = null;
	
	@Override
	public String toString()
	{
		return """
				{
					html_url = "%s",
					tag_name = "%s",
					name = "%s",
					draft = %s,
					prerelease = %s,
					published_at = "%s"
				}
				""".formatted(
			html_url,
			tag_name,
			name,
			draft,
			prerelease,
			sdf.format(published_at));
	}
}