package me.demoniaque.htac;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class Controller {

	@FXML
	public Button selectFiles;
	@FXML
	public TextArea textArea;
	@FXML
	public TextField jsonTitle;
	@FXML
	public Text infoText;
	@FXML
	public TextField documentTitle;

	public void fileChooserAction(ActionEvent actionEvent) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("txt", "*.txt"),
				new FileChooser.ExtensionFilter("json", "*.json")
		);
		List<File> files = fileChooser.showOpenMultipleDialog(Main.primaryStage);

		if (files == null || files.isEmpty()) {
			infoText.setText("Failed. -> No files selected.");
			return;
		}

		convertFiles(files);

		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new FileTransferable(files), (clipboard, contents) -> System.out.println("Lost ownership"));

		infoText.setText("Success! Copied file to clipboard!");
	}

	public void directoryChooserAction(ActionEvent actionEvent) {
		DirectoryChooser chooser = new DirectoryChooser();
		File dir = chooser.showDialog(Main.primaryStage);

		if (dir == null) {
			infoText.setText("Failed. -> Directory is null.");
			return;
		}

		HashSet<File> files = new HashSet<>();
		try {
			Files.find(Paths.get(dir.getPath()), 999, (p, bfa) ->
					bfa.isRegularFile()
							&& (p.getFileName().toString().matches(".*\\.json")
							|| p.getFileName().toString().matches(".*\\.txt")))
					.forEach(path -> files.add(path.toFile()));
		} catch (IOException e) {
			e.printStackTrace();
			infoText.setText("Failed. -> " + e.getMessage());
			return;
		}

		convertFiles(files);

		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new FileTransferable(new ArrayList<>(files)), (clipboard, contents) -> System.out.println("Lost ownership"));

		infoText.setText("Success! Copied file to clipboard!");
	}

	public void convertToJson(ActionEvent actionEvent) {
		if (jsonTitle.getText().isEmpty()) {
			infoText.setText("No title specified.");
			return;
		}

		if (textArea.getText().isEmpty()) {
			infoText.setText("No text inputted.");
			return;
		}

		if (documentTitle.getText().isEmpty()) {
			infoText.setText("No document title specified.");
			return;
		}

		DirectoryChooser chooser = new DirectoryChooser();
		File dir = chooser.showDialog(Main.primaryStage);

		if (dir == null) {
			infoText.setText("Failed. -> Directory is null.");
			return;
		}

		File file = new File(dir, jsonTitle.getText() + ".json");
		if (file.exists()) {
			infoText.setText("Failed. -> File with the same title already exists.");
		} else {
			try {
				file.createNewFile();

				JsonObject object = new JsonObject();
				object.addProperty("title", documentTitle.getText());
				object.addProperty("type", "content");

				JsonArray array = new JsonArray();

				JsonObject object1 = new JsonObject();
				JsonArray array1 = new JsonArray();

				String[] chunks = convertString(textArea.getText()).split("\n");
				StringBuilder lastString = new StringBuilder();
				for (String chunk : chunks) {
					if (chunk.isEmpty()) {
						array1.add(lastString.toString());
						lastString = new StringBuilder();
						continue;
					}
					lastString.append(chunk);
				}
				if (lastString.length() > 0)
					array1.add(lastString.toString());

				object1.add("text", array1);
				array.add(object1);
				object.add("content", array);

				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				String json = gson.toJson(object);

				for (Charset charset : Charset.availableCharsets().values()) {
					try {
						Files.write(Paths.get(file.getPath()), Collections.singleton(json), charset);
						return;
					} catch (IOException ignored) {
					}
				}

				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new FileTransferable(file), (clipboard, contents) -> System.out.println("Lost ownership"));

				infoText.setText("Success! Copied file to clipboard!");
			} catch (IOException e) {
				e.printStackTrace();
				infoText.setText("Failed. -> " + e.getMessage());
			}
		}
	}

	public String convertString(String string) {
		string = string
				.replace("’", "'")
				.replace("–", "-")
				.replace(".", ".")
				.replace("‘", "'")
				.replace("’", "'")
				.replace("‚", ",")
				.replace("“", "\\\"")
				.replace("”", "\\\"")
				.replace("‛", "'")
				.replace("‟", "\\\"")
				.replace("〝", "\\\"")
				.replace("〞", "\\\"")
				.replace("＂", "\\\"")
				.replace("’", "'")
				.replace(",", ",")
				.replace("”", "\\\"")
				.replace("“", "\\\"")
				.replace("-", "-")
				.replace("`", "'");

		StringBuilder word = new StringBuilder();
		String newText = string;

		for (int index = word.indexOf("\\u"); index >= 0; index = word.indexOf("\\u", index + 1)) {
			String token = string.substring(index, index + 6);
			System.out.println(token);
		}

		return newText;
		//while (position != -1) {
		//	if (position != 0) {
		//		word.append(newText.substring(0, position));
		//	}
		//	String token = newText.substring(position + 2, position + 5);
		//	newText = newText.substring(position + 5);
		//	word.append((char) Integer.parseInt(token));
		//	position = newText.indexOf("\\u");
		//}
		//word.append(newText);
		//return word.toString();
	}

	public void convertFiles(Collection<File> files) {
		for (File file : files) {

			for (Charset charset : Charset.availableCharsets().values()) {
				try {
					List<String> fileContent = Files.readAllLines(Paths.get(file.getPath()), charset);

					if (fileContent.isEmpty()) return;

					for (int i = 0; i < fileContent.size(); i++) {
						fileContent.set(i, convertString(fileContent.get(i)));
					}

					Files.write(Paths.get(file.getPath()), fileContent, charset);
					return;
				} catch (IOException ignored) {
				}
			}
		}
	}
}
