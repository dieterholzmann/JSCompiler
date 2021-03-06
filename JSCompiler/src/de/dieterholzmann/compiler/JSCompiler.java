package de.dieterholzmann.compiler;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.JSSourceFile;

/**
 * 
 * @author Dieter Holzmann Diese Klasse durchsucht einen im "--path" angegebenen
 *         Ordner nach JavaScript Dateien und komprimiert diese mit dem
 *         "google closure compiler" zu einer Datei.
 */
public class JSCompiler {

	public JSCompiler(HashMap<String, String> argsList) {
		init(argsList);
	}

	private void init(HashMap<String, String> argsList) {
		FileFilter filefilter = new FileFilter() {
			public boolean accept(File file) {
				// if the file extension is "js" return true, else false
				if (file.getName().endsWith(".js") || file.isDirectory()) {
					return true;
				}
				return false;
			}
		};

		findJSFiles(argsList.get("path"), filefilter);
	}

	private void findJSFiles(String path, FileFilter filter) {
		System.out.println();
		File c = new File(path);
		File[] files = c.listFiles(filter);

		ArrayList<JSSourceFile> primaryJavascriptFiles = new ArrayList<JSSourceFile>();
		ArrayList<JSSourceFile> externalJavascriptFiles = new ArrayList<JSSourceFile>();

		for (File f : files) {
			if (f.isDirectory()) {
				// if (recursive) {
				findJSFiles(f.getPath(), filter);
				// }
			} else {
				String name = f.getName();

				if (name.indexOf("mini") > 0 || name.indexOf("min") > 0) {
				} else {
					primaryJavascriptFiles.add(JSSourceFile.fromFile(f));
				}
			}
		}
		compileFiles(externalJavascriptFiles, primaryJavascriptFiles);
	}

	private void compileFiles(ArrayList<JSSourceFile> externalJavascriptFiles,
			ArrayList<JSSourceFile> primaryJavascriptFiles) {

		if (externalJavascriptFiles.size() > 0
				|| primaryJavascriptFiles.size() > 0) {

			// compile the js files
			new MyCompiler(externalJavascriptFiles, primaryJavascriptFiles,
					argsList);
		}
	}

	// private static Boolean recursive = false;
	private static HashMap<String, String> argsList = new HashMap<String, String>();
}
