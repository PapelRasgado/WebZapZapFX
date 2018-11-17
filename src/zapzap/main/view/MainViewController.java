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

	// Elementos relacionados a tabela de clientes da interface gr�fica
	@FXML
	private TableView<Cliente> clienteTable;
	@FXML
	private TableColumn<Cliente, String> nameColumn;
	@FXML
	private TableColumn<Cliente, String> numberColumn;
	@FXML
	private TableColumn<Cliente, LocalDate> dateColumn;

	// Elementos relacionados a tabela de falhas da interface gr�fica
	@FXML
	private TableView<Cliente> clienteTableFail;
	@FXML
	private TableColumn<Cliente, String> nameColumnFail;
	@FXML
	private TableColumn<Cliente, String> numberColumnFail;
	@FXML
	private TableColumn<Cliente, LocalDate> dateColumnFail;

	// Componentes para inser��o de dados da interface gr�fica
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

	// Bot�es responsaveis pelas a��es de inserir clientes ou falhas
	@FXML
	private Button salvar;
	@FXML
	private Button cancelar;

	private MainApp mainApp;

	// C�pia do cliente que est� sendo editado
	private Cliente clienteEditar;

	// Variavel que define se a edi��o trata de um cliente ou uma
	// falha(true:cliente, false:falha)
	private Boolean tipoEditar;

	public MainViewController() {
	}

	/**
	 * Inicializa os componentes do MainView
	 */
	@FXML
	private void initialize() {
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		numberColumn.setCellValueFactory(cellData -> cellData.getValue().numberProperty());
		dateColumn.setCellValueFactory(cellData -> cellData.getValue().dataProperty());

		nameColumnFail.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
		numberColumnFail.setCellValueFactory(cellData -> cellData.getValue().numberProperty());
		dateColumnFail.setCellValueFactory(cellData -> cellData.getValue().dataProperty());

		// responsavel por chamar o m�todo showClienteDetail para um cliente ap�s um
		// double click
		clienteTable.setRowFactory(tv -> {
			TableRow<Cliente> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					Cliente rowData = row.getItem();
					showClienteDetails(rowData, true);
				}
			});
			return row;
		});

		// responsavel por chamar o m�todo showClienteDetail para uma falha ap�s um
		// double click
		clienteTableFail.setRowFactory(tv -> {
			TableRow<Cliente> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					Cliente rowData = row.getItem();
					showClienteDetails(rowData, false);
				}
			});
			return row;
		});
	}

	/**
	 * M�todo responsavel por setar o mainApp e transferir as listas de clientes e
	 * de falhas para a interface gr�fica
	 */
	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;

		clienteTable.setItems(mainApp.getClienteData());
		clienteTableFail.setItems(mainApp.getClienteFailData());
	}

	/**
	 * M�todo chamado ap�s double click em um cliente ou uma falha e chama o m�todo
	 * responsavel por mostrar os detalhes do item no mainApp
	 * 
	 * @param cliente Item que sofreu o double click e ser� exibido pelo m�todo
	 * @param teste   Define se o item citado anteriormente trata de um cliente ou
	 *                uma falha (true:cliente, false:falha)
	 */
	private void showClienteDetails(Cliente cliente, boolean teste) {
		mainApp.showClienteDetails(cliente, teste);
	}

	/**
	 * M�todo chamado com o click do bot�o adicionar na interface gr�fica e pode ser
	 * chamado tanto para a adi��o quanto para a edi��o de um cliente
	 */
	@FXML
	private void handleAdicionar() {
		// verifica se o nome, n�mero ou a data n�o for�o preenchidos
		if (nomeField.getText() != null && !nomeField.getText().isEmpty()) {
			if (numberField.getText() != null && !numberField.getText().isEmpty()) {
				if (datePicker.getValue() != null) {
					// Cria o cliente que ser� adicionado
					Cliente cli = new Cliente(nomeField.getText(), numberField.getText(), datePicker.getValue(),
							!messageArea.getText().isEmpty() ? messageArea.getText()
									: "*Dantas Importadora*\nFalta menos de 5 dias para o vencimento de sua fatura!");
					if (clienteEditar == null) {
						//Adiciona cliente
						cli.setUuid(UUID.randomUUID().toString());
						mainApp.getClienteData().add(cli);

					} else {
						//Editar cliente
						if (tipoEditar) {
							mainApp.getClienteData().remove(clienteEditar);
							mainApp.getClienteData().add(cli);
						} else {
							mainApp.getClienteFailData().remove(clienteEditar);
							mainApp.getClienteData().add(cli);
						}

						clienteEditar = null;
						tipoEditar = null;
					}
					atualizarDados();

				} else {
					//Alerta caso o campo data n�o foi preenchido
					Alert alert = new Alert(AlertType.WARNING);
					alert.setTitle("Data n�o informado");
					alert.setHeaderText("A data n�o foi selecionada");
					alert.setContentText("Por favor, selecione a data.");

					alert.showAndWait();
				}
			} else {
				//Alerta caso o campo n�mero n�o foi preenchido
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Numero n�o informado");
				alert.setHeaderText("O campo numero n�o foi preenchido");
				alert.setContentText("Por favor, preencha o campo numero com alguma informa��o.");

				alert.showAndWait();
			}
		} else {
			//Alerta caso o campo nome n�o foi preenchido
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Nome n�o informado");
			alert.setHeaderText("O campo nome n�o foi preenchido");
			alert.setContentText("Por favor, preencha o campo nome com alguma informa��o.");

			alert.showAndWait();
		}

	}

	/**
	 * Define o Item e o tipo do mesmo (Cliente ou falha) que ser� editado
	 * 
	 * @param cli  Item que ser� editado
	 * @param tipo Tipo do item
	 */
	public void editar(Cliente cli, boolean tipo) {
		clienteEditar = cli;
		tipoEditar = tipo;
		atualizarDados();
	}

	/**
	 * M�todo responsavel por preparar o ambiente de edi��o com o cliente desejado
	 */
	private void atualizarDados() {
		if (clienteEditar != null) {
			if (tipoEditar) {
				titulo.setText("Editar Cliente:");
			} else {
				titulo.setText("Editar Falha:");
			}
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

	/**
	 * M�todo chamado pelo click do bot�o cancelar na interface gr�fica e �
	 * responsavel por cance�ar a edi��o de um item
	 */
	@FXML
	private void handlerCancelar() {
		clienteEditar = null;
		tipoEditar = null;
		atualizarDados();
	}

}
