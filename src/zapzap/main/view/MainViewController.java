package zapzap.main.view;

import java.time.LocalDate;
import java.util.UUID;

import com.sun.javafx.geom.AreaOp.EOWindOp;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
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
	private TextField nomeField;
	@FXML
	private TextField numberField;
	@FXML
	private DatePicker datePicker;

	// Reference to the main application.
	private MainApp mainApp;

	/**
	 * O construtor. O construtor � chamado antes do m�todo inicialize().
	 */
	public MainViewController() {
	}

	/**
	 * Inicializa a classe controller. Este m�todo � chamado automaticamente ap�s o
	 * arquivo fxml ter sido carregado.
	 */
	@FXML
	private void initialize() {
		// Inicializa a tablea de pessoa com duas colunas.
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		numberColumn.setCellValueFactory(cellData -> cellData.getValue().numberProperty());
		dateColumn.setCellValueFactory(cellData -> cellData.getValue().dataProperty());

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

	/**
	 * � chamado pela aplica��o principal para dar uma refer�ncia de volta a si
	 * mesmo.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		
		// Adiciona os dados da observable list na tabela
		clienteTable.setItems(mainApp.getClienteData());
	}

	private void showClienteDetails(Cliente cliente) {
		mainApp.showClienteDetails(cliente);
	}

	@FXML
	private void handleAdicionar() {
		if (nomeField.getText() != null && !nomeField.getText().isEmpty()) {
			if (numberField.getText() != null && !numberField.getText().isEmpty()) {
				if (datePicker.getValue() != null) {
					Cliente cli = new Cliente(nomeField.getText(), numberField.getText(), datePicker.getValue());
					cli.setUuid(UUID.randomUUID().toString());
					mainApp.getClienteData().add(cli);
					
					nomeField.setText("");
					numberField.setText("");
					datePicker.setValue(null);
				} else {
					Alert alert = new Alert(AlertType.WARNING);
		            alert.setTitle("Data n�o informado");
		            alert.setHeaderText("A data n�o foi selecionada");
		            alert.setContentText("Por favor, selecione a data.");

		            alert.showAndWait();
				}
			} else {
				Alert alert = new Alert(AlertType.WARNING);
	            alert.setTitle("Numero n�o informado");
	            alert.setHeaderText("O campo numero n�o foi preenchido");
	            alert.setContentText("Por favor, preencha o campo numero com alguma informa��o.");

	            alert.showAndWait();
			}
		} else {
			Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Nome n�o informado");
            alert.setHeaderText("O campo nome n�o foi preenchido");
            alert.setContentText("Por favor, preencha o campo nome com alguma informa��o.");

            alert.showAndWait();
		}
	}

}
