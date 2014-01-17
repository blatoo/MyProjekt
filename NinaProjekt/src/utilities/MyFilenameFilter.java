package utilities;

import java.io.File;
import java.io.FilenameFilter;

public class MyFilenameFilter implements FilenameFilter{
	String ending = "";
	String starting = "";
	
	public MyFilenameFilter(String _ending){
		ending = _ending;
	}
	
	public MyFilenameFilter(String _starting, String _ending){
		starting = _starting;
		ending = _ending;
	}
	
	
	public MyFilenameFilter() {
	
	}


	public boolean accept( File f, String s )
	{
		return s.toLowerCase().startsWith(starting) && s.toLowerCase().endsWith(ending);
	}
}
