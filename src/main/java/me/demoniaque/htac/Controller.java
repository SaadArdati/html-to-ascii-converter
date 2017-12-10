package me.demoniaque.htac;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class Controller {

	@FXML
	public Button selectFiles;

	public void run(ActionEvent actionEvent) {
		FileChooser fileChooser = new FileChooser();
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("text or json", "*.txt", "*.json");
		fileChooser.getExtensionFilters().add(extFilter);
		List<File> files = fileChooser.showOpenMultipleDialog(Main.primaryStage);

		if (files.isEmpty()) return;

		for (File file : files) {
			if (file == null) continue;

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
