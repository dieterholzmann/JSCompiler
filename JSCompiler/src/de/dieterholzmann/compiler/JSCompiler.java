package de.dieterholzmann.compiler;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.javascript.jscomp.Compiler;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.JSSourceFile;

/**
 * 
 * @author Dieter Holzmann
 * Diese Klasse durchsucht einen im "--path" angegebenen Ordner nach JavaScript Dateien
 * und komprimiert diese mit dem "google closure compiler" zu einer Datei.
 */
public class JSCompiler {

	/**
	 * @param args
	 *            - path, out_path, r
	 */
	public static void main(String[] args) {

		for (int i = 0, l = args.length; i < l; i++) {

			if (args[i].equals("--r")) {
				recursive = true;
			}

			if (args[i].equals("--path")) {
				path = args[i + 1];
			}

			if (args[i].equals("--out_path")) {
				outPath = args[i + 1];
			}
		}
		
		if (path != null) {
			System.out.println("search in: " + path);
		} else {
			System.out.println("please select a folder");
			System.exit(1);
		}
		
		new JSCompiler();

	}

	public JSCompiler() {
		init();
	}

	private void init() {
		FileFilter filefilter = new FileFilter() {
			public boolean accept(File file) {
				// if the file extension is "js" return true, else false
				if (file.getName().endsWith(".js") || file.isDirectory()) {
					return true;
				}
				return false;
			}
		};

		compile(path, filefilter);
	}

	private void compile(String path, FileFilter filter) {
		File c = new File(path);
		File[] files = c.listFiles(filter);

		ArrayList<JSSourceFile> primaryJavascriptFiles = new ArrayList<JSSourceFile>();

		ArrayList<JSSourceFile> externalJavascriptFiles = new ArrayList<JSSourceFile>();

		for (File f : files) {
			if (f.isDirectory()) {
				if (recursive) {
					compile(f.getPath(), filter);
				}
			} else {
				String name = f.getName();

				if (name.indexOf("mini") > 0 || name.indexOf("min") > 0) {
				} else {
					primaryJavascriptFiles.add(JSSourceFile.fromFile(f));
				}
			}
		}

		Compiler compiler = new Compiler();
		CompilerOptions options = new CompilerOptions();
		compiler.compile(externalJavascriptFiles, primaryJavascriptFiles,
				options);

		for (JSError message : compiler.getWarnings()) {
			System.err.println("Warning message: " + message.toString());
		}

		for (JSError message : compiler.getErrors()) {
			System.err.println("Error message: " + message.toString());
		}

		FileWriter outputFile;
		try {
			outputFile = new FileWriter(outPath + "out.mini.js");
			outputFile.write(compiler.toSource());
			outputFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static String path = null;
	private static String outPath = "";
	private static Boolean recursive = false;
}
