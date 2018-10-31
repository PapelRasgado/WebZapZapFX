package zapzap.main.view;

import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import zapzap.main.MainApp;
import zapzap.main.model.Cliente;
import javafx.scene.control.Label;

public class DetailsDialogController {

	@FXML
	private Label name;
	@FXML
	private Label number;
	@FXML
	private Label date;
	@FXML
	private Label message;

	private Stage dialogStage;
	private Cliente cliente;
	private MainApp mainApp;

	@FXML
	private void initialize() {
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
		this.dialogStage.setResizable(false);
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;

		name.setText(cliente.getName());
		number.setText(cliente.getNumber());
		date.setText(cliente.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		message.setText(cliente.getMessage());
	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	@FXML
	private void handleVoltar() {
		dialogStage.close();
	}

	@FXML
	private void handleRemover() {
		mainApp.getClienteData().remove(cliente);
		dialogStage.close();
	}
	
	@FXML
	private void handleEdit() {
		mainApp.editar(cliente);
		dialogStage.close();
	}
	
}
