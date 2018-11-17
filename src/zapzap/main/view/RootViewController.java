package zapzap.main.view;

import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import zapzap.main.MainApp;

public class RootViewController {

	private MainApp mainApp;

	/**
	 * Define mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	/**
	 * Salva a lista de cliente
	 */
	@FXML
	private void handleSave() {
		mainApp.save();
	}

	/**
	 * Limpa a lista de clientes
	 */
	@FXML
	private void handleDelete() {
		//Exibe um aviso para confirma��o da a��o
		ButtonType yes = new ButtonType("SIM", ButtonData.YES);
		ButtonType no = new ButtonType("N�o", ButtonData.NO);
		Alert alerta = new Alert(AlertType.WARNING, null,yes, no);
		alerta.setHeaderText("Voc� realmente deseja apagar todos os dados?");
		alerta.setTitle("Apagar dados");
		Optional<ButtonType> result = alerta.showAndWait();
		
		if(result.get() == yes){
			mainApp.getClienteData().clear();
			mainApp.save();
		}
	}

	/**
	 * Exibe uma tela com as informa��es sobre o programa
	 */
	@FXML
	private void handleAbolt() {
		Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Sobre");
        alert.setHeaderText("Esse programa foi desenvolvido pelos alunos do IFPB Jo�o\nPaulo Dantas e Eniedson J�nior para a empresa Dantas distribuidora.");
        alert.showAndWait();
	}
	
	/**
	 * Limpa a lista de falhas
	 */
	@FXML
	private void handleReset() {
		mainApp.getClienteFailData().clear();
	}
}
