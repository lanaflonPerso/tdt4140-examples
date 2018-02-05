package tdt4140.gr1800.app.ui;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import tdt4140.gr1800.app.doc.IDocumentImporter;
import tdt4140.gr1800.app.doc.IDocumentStorage;

public class FileMenuController {

	IDocumentStorage<File> documentStorage;

	public void setDocumentStorage(IDocumentStorage<File> documentStorage) {
		this.documentStorage = documentStorage;
	}
	
	@FXML
	public void handleNewAction() {
		documentStorage.newDocument();
	}

	private List<File> recentFiles = new ArrayList<File>();

	@FXML
	private Menu recentMenu;
	
	protected void updateRecentMenu(File file) {
		recentFiles.remove(file);
		recentFiles.add(0, file);
		recentMenu.getItems().clear();
		for (File recentFile : recentFiles) {
			MenuItem menuItem = new MenuItem();
			menuItem.setText(recentFile.toString());
			menuItem.setOnAction(event -> handleOpenAction(event));
			recentMenu.getItems().add(menuItem);
		}
	}
	
	private FileChooser fileChooser;

	FileChooser getFileChooser() {
		if (fileChooser == null) {
			fileChooser = new FileChooser();
		}
		return fileChooser;
	}

	@FXML
	public void handleOpenAction(ActionEvent event) {
		File selection = null;
		if (event.getSource() instanceof MenuItem) {
			File file = new File(((MenuItem) event.getSource()).getText());
			if (file.exists()) {
				selection = file;
			}
		}
		if (selection == null) {
			FileChooser fileChooser = getFileChooser();
			selection = fileChooser.showOpenDialog(null);
		}
		if (selection != null) {
			handleOpenAction(selection);
		}
	}

	void handleOpenAction(File selection) {
		try {
			documentStorage.openDocument(selection);
			updateRecentMenu(selection);
		} catch (IOException e) {
			// TODO
		}
	}
	
	@FXML
	public void handleSaveAction() {
		try {
			documentStorage.saveDocument();
		} catch (IOException e) {
			// TODO
		}
	}
	
	@FXML
	public void handleSaveAsAction() {
		FileChooser fileChooser = getFileChooser();
		File selection = fileChooser.showSaveDialog(null);
		handleSaveAsAction(selection);
	}

	void handleSaveAsAction(File selection) {
		File oldStorage = documentStorage.getDocumentLocation();
		try {
			documentStorage.setDocumentLocation(selection);
			documentStorage.saveDocument();
		} catch (IOException e) {
			// TODO
			documentStorage.setDocumentLocation(oldStorage);
		}
	}
	
	@FXML
	public void handleSaveCopyAsAction() {
		FileChooser fileChooser = getFileChooser();
		File selection = fileChooser.showSaveDialog(null);
		handleSaveCopyAsAction(selection);
	}

	void handleSaveCopyAsAction(File selection) {
		File oldStorage = documentStorage.getDocumentLocation();
		try {
			documentStorage.setDocumentLocation(selection);
			documentStorage.saveDocument();
		} catch (IOException e) {
			// TODO
		} finally {
			documentStorage.setDocumentLocation(oldStorage);
		}
	}

	@FXML
	public void handleImportAction() {
		FileChooser fileChooser = getFileChooser();
		File selection = fileChooser.showOpenDialog(null);
//		String path = selection.getPath();
//		int pos = path.lastIndexOf('.');
//		String ext = (pos > 0 ? path.substring(pos + 1) : null);
		handleImportAction(selection);
	}

	void handleImportAction(File selection) {
		for (IDocumentImporter<File> importer : documentStorage.getDocumentImporters()) {
			try {
				importer.importDocument(selection);
				break;
			} catch (Exception e) {
			}
		}
	}
}
