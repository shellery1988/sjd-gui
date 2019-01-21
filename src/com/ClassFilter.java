package com;

import java.io.File;

import javax.swing.filechooser.FileFilter;

final class ClassFilter extends FileFilter{

	@Override
	public boolean accept(File f) {
		if (f.isDirectory())
        {
            return true;
        }
		String filename = f.getName();
        String extension = f.getName().substring(filename.lastIndexOf("."));
        if (extension != null)
        {
            if (extension.equals(".class"))
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        return false;
	}

	@Override
	public String getDescription() {
		return "class类文件(*.class)";
	}

}
