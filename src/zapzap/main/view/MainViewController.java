package zapzap.main.view;

import java.time.LocalDate;
import java.util.UUID;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import zapzap.main.MainApp;
import zapzap.main.model.Cliente;

public class MainViewController {

	@FXML
	private TableView<Cliente> clienteTable;
	@FXML
	private TableColumn<Cliente, String> nameColumn;
	@FXML
	private TableColumn<Cliente, String> numberColumn;
	@FXML
	private TableColumn<Cliente, LocalDate> dateColumn;

	@FXML
	private TableView<Cliente> clienteTableFail;
	@FXML
	private TableColumn<Cliente, String> nameColumnFail;
	@FXML
	private TableColumn<Cliente, String> numberColumnFail;
	@FXML
	private TableColumn<Cliente, LocalDate> dateColumnFail;
	
	@FXML
	private TextField nomeField;
	@FXML
	private TextField numberField;
	@FXML
	private DatePicker datePicker;
	@FXML
	private TextArea messageArea;
	@FXML
	private Label titulo;
	@FXML
	private Button salvar;
	@FXML 
	private Button cancelar;

	private MainApp mainApp;
	private Cliente clienteEditar;

	public MainViewController() {
	}

	@FXML
	private void initialize() {
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		numberColumn.setCellValueFactory(cellData -> cellData.getValue().numberProperty());
		dateColumn.setCellValueFactory(cellData -> cellData.getValue().dataProperty());
		
		nameColumnFail.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		numberColumnFail.setCellValueFactory(cellData -> cellData.getValue().numberProperty());
		dateColumnFail.setCellValueFactory(cellData -> cellData.getValue().dataProperty());

		clienteTable.setRowFactory(tv -> {
			TableRow<Cliente> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					Cliente rowData = row.getItem();
					showClienteDetails(rowData);
				}
			});
			return row;
		});
	}
	
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		
		clienteTable.setItems(mainApp.getClienteData());
		clienteTableFail.setItems(mainApp.getClienteFailData());
	}

	private void showClienteDetails(Cliente cliente) {
		mainApp.showClienteDetails(cliente);
	}

	@FXML
	private void handleAdicionar() {
		if (nomeField.getText() != null && !nomeField.getText().isEmpty()) {
			if (numberField.getText() != null && !numberField.getText().isEmpty()) {
				if (datePicker.getValue() != null) {
					Cliente cli = new Cliente(nomeField.getText(), numberField.getText(), datePicker.getValue(),!messageArea.getText().isEmpty() ? messageArea.getText():"Voce eh gado");
					if (clienteEditar == null) {
						cli.setUuid(UUID.randomUUID().toString());
						mainApp.getClienteData().add(cli);
						
					} else {
						mainApp.getClienteData().remove(clienteEditar);
						mainApp.getClienteData().add(cli);
					
						clienteEditar = null;
					}
					atualizarDados();
					
					
				} else {
					Alert alert = new Alert(AlertType.WARNING);
		            alert.setTitle("Data não informado");
		            alert.setHeaderText("A data não foi selecionada");
		            alert.setContentText("Por favor, selecione a data.");

		            alert.showAndWait();
				}
			} else {
				Alert alert = new Alert(AlertType.WARNING);
	            alert.setTitle("Numero não informado");
	            alert.setHeaderText("O campo numero não foi preenchido");
	            alert.setContentText("Por favor, preencha o campo numero com alguma informação.");

	            alert.showAndWait();
			}
		} else {
			Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Nome não informado");
            alert.setHeaderText("O campo nome não foi preenchido");
            alert.setContentText("Por favor, preencha o campo nome com alguma informação.");

            alert.showAndWait();
		}
		
	}
	
	public void editar(Cliente cli) {
		clienteEditar = cli;
		atualizarDados();
	}

	private void atualizarDados() {
		if (clienteEditar != null) {
			titulo.setText("Editar Cliente:");
			nomeField.setText(clienteEditar.getName());
			numberField.setText(clienteEditar.getNumber());
			datePicker.setValue(clienteEditar.getData());
			messageArea.setText(clienteEditar.getMessage());
			salvar.setText("Salvar...");
			cancelar.setVisible(true);
		} else {
			titulo.setText("Adicionar Cliente:");
			nomeField.setText("");
			numberField.setText("");
			messageArea.setText("");
			datePicker.setValue(null);
			salvar.setText("Adicionar...");
			cancelar.setVisible(false);
		}
	}
	
	@FXML
	private void handlerCancelar() {
		clienteEditar = null;
		atualizarDados();
	}

}
