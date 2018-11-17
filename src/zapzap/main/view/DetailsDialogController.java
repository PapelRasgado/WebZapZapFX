package zapzap.main.view;

import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import zapzap.main.MainApp;
import zapzap.main.model.Cliente;
import javafx.scene.control.Label;

public class DetailsDialogController {

	// Componentes da interface gráfica para exibição dos dados do cliente
	@FXML
	private Label name;
	@FXML
	private Label number;
	@FXML
	private Label date;
	@FXML
	private Label message;

	private Stage dialogStage;

	// Item a ser exibido
	private Cliente cliente;

	private MainApp mainApp;

	// Determina o tipo do item exibido
	private boolean tipo;

	@FXML
	private void initialize() {
	}

	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
		this.dialogStage.setResizable(false);
	}

	/**
	 * Define o tipo do item
	 * 
	 * @param tipo Indica se o item é um cliente ou uma falha
	 */
	public void setTipo(boolean tipo) {
		this.tipo = tipo;
	}

	/**
	 * Define o objeto
	 * 
	 * @param cliente Objeto a ser exibido
	 */
	public void setCliente(Cliente cliente) {
		this.cliente = cliente;

		name.setText(cliente.getName());
		number.setText(cliente.getNumber());
		date.setText(cliente.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		message.setText(cliente.getMessage());
	}

	/**
	 * Define mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
	}

	/**
	 * Finaliza o dialogo
	 */
	@FXML
	private void handleVoltar() {
		dialogStage.close();
	}

	/**
	 * Remove o item da lista presente em mainApp e finaliza o dialogo
	 */
	@FXML
	private void handleRemover() {
		if (tipo) {
			mainApp.getClienteData().remove(cliente);
		} else {
			mainApp.getClienteFailData().remove(cliente);
		}

		dialogStage.close();
	}

	/**
	 * Abre o ambiente de edição do cliente
	 */
	@FXML
	private void handleEdit() {
		mainApp.editar(cliente, tipo);
		dialogStage.close();
	}

}
