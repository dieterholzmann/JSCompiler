package de.dieterholzmann.compiler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

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
		URL url = null;
		try {
			url = new URL("http://dothot.de/");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		BufferedReader in = null;
		String ln;
		String html = "";
		try {
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			
			while ((ln = in.readLine()) != null) {
				html += ln;
			}
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(html);
		
		String pattern = "(?i)(<script.*?>)";
		System.out.println(html.matches(".*(<script.*?>)"));
	}

//	private static URL url;
}
