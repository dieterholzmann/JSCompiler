package de.dieterholzmann.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HTTPJSCompiler {
	public static void main(String[] args) {
		new HTTPJSCompiler();
	}

	public HTTPJSCompiler() {
		init();
	}

	private void init() {
		// String externalJavascriptResources[] = {
		// "http://dothot.de/script.js"
		// };
		ArrayList<String> files = new ArrayList<String>();
		URL url = null;
		try {
			url = new URL(this.url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		BufferedReader in = null;
		String ln;
		String html = "";
		
		try {
			in = new BufferedReader(new InputStreamReader(url.openStream()));

			while ((ln = in.readLine()) != null) {
				html += ln;
				if(ln.indexOf("base") > 0)
					setBaseURL(ln);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Pattern p = Pattern.compile("src=\"(.*?)\"");
		Matcher m = p.matcher(html);
		
		

		while (m.find()) {
			if (m.group(1).indexOf(".js") > 0) {
				files.add(m.group(1));
			}
		}
	}
	
	private void setBaseURL(String str) {
		Pattern p = Pattern.compile("<base href=\"(.*?)\"");
		Matcher m = p.matcher(str);
		
		while (m.find()) {
			if(!m.group(1).equals(""))
				baseURL = m.group(1);
		}
	}

	 private String url = "";
	 private String baseURL = null;
}
