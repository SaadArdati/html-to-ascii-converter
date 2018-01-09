package me.demoniaque.htac;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileTransferable implements Transferable {

	private List<File> listOfFiles;

	public FileTransferable(List<File> listOfFiles) {
		this.listOfFiles = listOfFiles;
	}

	public FileTransferable(File file) {
		listOfFiles = new ArrayList<>();
		listOfFiles.add(file);
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[]{DataFlavor.javaFileListFlavor};
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return DataFlavor.javaFileListFlavor.equals(flavor);
	}

	@Override
	public Object getTransferData(DataFlavor flavor) {
		return listOfFiles;
	}
}
