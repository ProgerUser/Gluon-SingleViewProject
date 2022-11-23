package app;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class AppC {

	private String Mess = "";

	@FXML
	void Go(ActionEvent event) {
		try {

			if (killProcess()) {

			}

			String workingDirectory;
			String OS = (System.getProperty("os.name")).toUpperCase();

			if (OS.contains("WIN")) {
				Mess += "\r\n-----------------------\r\nOS Windows\r\n";
				workingDirectory = System.getenv("AppData");

				// System.out.println(workingDirectory + "\\Microsoft\\UProof\\");

				File cistdic = new File(workingDirectory + "\\Microsoft\\UProof\\CUSTOM.DIC");
				if (cistdic.exists()) {
					Mess += "\r\n-----------------------\r\nCUSTOM.DIC существует\r\n";
					if (cistdic.delete()) {
						Mess += "\r\n-----------------------\r\nCUSTOM.DIC удален\r\n";

						Mess += "\r\n-----------------------\r\n user.dir удален" + System.getProperty("user.dir")
								+ "\\CUSTOM.DIC" + "\r\n";
						File source = new File(System.getProperty("user.dir"));
						File dest = new File(workingDirectory + "\\Microsoft\\UProof");

						// System.out.println(System.getProperty("user.dir") + "\\CUSTOM.DIC");
						Mess += "\r\n-----------------------\r\nCUSTOM.DIC Сопирование\r\n";
						try {
							FileUtils.copyDirectory(source, dest);
							Mess += "\r\n-----------------------\r\nCUSTOM.DIC Успешно\r\n";
							Message(Mess);
						} catch (Exception e) {
							Message(ExceptionUtils.getStackTrace(e));
						}
					}
				}
			} else {
				workingDirectory = System.getProperty("user.home");
				workingDirectory += "/Library/Application Support";
			}

		} catch (Exception e) {
			Message(ExceptionUtils.getStackTrace(e));
		}
	}

	/**
	 * Убить процесс word и удалить файлы
	 * 
	 * @throws Exception
	 */
	public boolean killProcess() throws Exception {

		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c",
				"wmic Path win32_process Where \"CommandLine Like '%WORD.EXE%'\" Call Terminate");
		builder.redirectErrorStream(true);
		Process p = builder.start();

		boolean exitStatus = p.waitFor(5, TimeUnit.SECONDS);

		BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream(), "866"));

		Mess += r.lines().collect(Collectors.joining("\r\n")) + "\r\n<" + "-----------------------"
				+ new Boolean(exitStatus).toString().toUpperCase() + ">";

		return true;
	}

	/**
	 * Вывод сообщения
	 * 
	 * @param mess
	 */
	public static void Message(String mess) {
		if (mess != null && !mess.equals("")) {

			AlertType AlertTp = null;
			String error = null;
			if (mess.length() >= 200) {
				AlertTp = AlertType.ERROR;
				error = mess.substring(0, 150);
			} else {
				AlertTp = AlertType.INFORMATION;
				error = mess;
			}
			Alert alert = new Alert(AlertTp);
			// Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
			// stage.getIcons().add(new Image("/icon.png"));
			alert.setTitle("Внимание");
			alert.setHeaderText(error);
			// alert.setContentText(mess.substring(0, mess.indexOf("\r\n")));
			Label label = new Label("Трассировка стека исключения:");

			TextArea textArea = new TextArea(mess);
			textArea.setEditable(false);
			textArea.setWrapText(true);

			textArea.setMaxWidth(Double.MAX_VALUE);
			textArea.setMaxHeight(Double.MAX_VALUE);

			GridPane.setVgrow(textArea, Priority.ALWAYS);
			GridPane.setHgrow(textArea, Priority.ALWAYS);

			GridPane expContent = new GridPane();
			expContent.setMaxWidth(Double.MAX_VALUE);
			expContent.add(label, 0, 0);
			expContent.add(textArea, 0, 1);

			// Set expandable Exception into the dialog pane.
			alert.getDialogPane().setExpandableContent(expContent);

			alert.showAndWait();
		}
	}
}
