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
				if (ln.indexOf("base") > 0)
					setBaseURL(ln);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Pattern p = Pattern.compile("src=\"(.*?)\"");
		Matcher m = p.matcher(html);

		String filePath = null;
		while (m.find()) {
			filePath = m.group(1);
			if (filePath.indexOf(".js") > 0) {

				if (!filePath.startsWith(baseURL)
						&& (!filePath.startsWith("http")
								&& !filePath.startsWith("www") && !filePath
									.startsWith("//")))
					files.add(baseURL + filePath);
				else
					files.add(filePath);
			}
		}

		System.out.println("Base URL: " + baseURL);

		for (String file : files) {
			System.out.println(file);
		}

	}

	private void setBaseURL(String str) {
		Pattern p = Pattern.compile("<base href=\"(.*?)\"");
		Matcher m = p.matcher(str);

		while (m.find()) {
			if (!m.group(1).equals(""))
				baseURL = m.group(1);
		}

		if (!baseURL.equals("") && baseURL.lastIndexOf("/") <= 0)
			baseURL += "/";
	}

	private String url = "";
	private String baseURL = null;
}
