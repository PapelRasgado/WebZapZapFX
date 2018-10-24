package zapzap.main.view;

import java.time.LocalDate;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import zapzap.main.MainApp;
import zapzap.main.model.Cliente;

public class MainViewController {

	@FXML
	private TableView<Cliente> personTable;
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
	@FXML
	private Button salvar;
	

	// Reference to the main application.
	private MainApp mainApp;

	/**
	 * O construtor. O construtor é chamado antes do método inicialize().
	 */
	public MainViewController() {
	}

	/**
	 * Inicializa a classe controller. Este método é chamado automaticamente após o
	 * arquivo fxml ter sido carregado.
	 */
	@FXML
	private void initialize() {
		// Inicializa a tablea de pessoa com duas colunas.
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		numberColumn.setCellValueFactory(cellData -> cellData.getValue().numberProperty());
		dateColumn.setCellValueFactory(cellData -> cellData.getValue().dataProperty());
	}

	/**
	 * É chamado pela aplicação principal para dar uma referência de volta a si
	 * mesmo.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

		// Adiciona os dados da observable list na tabela
		personTable.setItems(mainApp.getClienteData());
	}

}
