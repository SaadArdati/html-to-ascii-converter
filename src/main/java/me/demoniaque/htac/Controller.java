package me.demoniaque.htac;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class Controller {

	@FXML
	public Button selectFiles;

	public void fileChooserAction(ActionEvent actionEvent) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("txt", "*.txt"),
				new FileChooser.ExtensionFilter("json", "*.json")
		);
		List<File> files = fileChooser.showOpenMultipleDialog(Main.primaryStage);

		if (files == null || files.isEmpty()) return;

		convert(files);
	}

	public void directoryChooserAction(ActionEvent actionEvent) {
		DirectoryChooser chooser = new DirectoryChooser();
		File dir = chooser.showDialog(Main.primaryStage);

		if (dir == null) return;

		HashSet<File> files = new HashSet<>();
		try {
			Files.find(Paths.get(dir.getPath()), 999, (p, bfa) ->
					bfa.isRegularFile()
							&& (p.getFileName().toString().matches(".*\\.json")
							|| p.getFileName().toString().matches(".*\\.txt")))
					.forEach(path -> files.add(path.toFile()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		convert(files);
	}

	public void convert(Collection<File> files) {
		for (File file : files) {
			try {
				List<String> fileContent = new ArrayList<>(Files.readAllLines(Paths.get(file.getPath()), StandardCharsets.UTF_8));

				for (int i = 0; i < fileContent.size(); i++) {
					fileContent.set(i, fileContent.get(i).replace("’", "'").replace("–", "--"));
				}

				Files.write(Paths.get(file.getPath()), fileContent, StandardCharsets.UTF_8);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
