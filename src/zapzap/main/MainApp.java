package zapzap.main;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.prefs.Preferences;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.controlsfx.dialog.Dialogs;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import zapzap.main.model.Cliente;
import zapzap.main.model.ClienteListWrapper;
import zapzap.main.view.DetailsDialogController;
import zapzap.main.view.MainViewController;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;

	private ObservableList<Cliente> clienteData = FXCollections.observableArrayList();

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("WebZapZap");
		this.primaryStage.setResizable(false);
		
		this.primaryStage.getIcons().add(new Image("file:resources/images/logo.png"));
		
		initRootLayout();

		showPersonOverview();
	}

	public MainApp() {
		// Add some sample data
		clienteData.add(new Cliente("Hans", "83996283690", LocalDate.now()));
		clienteData.add(new Cliente("Hans2", "83954354354", LocalDate.now()));
		clienteData.add(new Cliente("Hans3", "83992342690", LocalDate.now()));
		clienteData.add(new Cliente("Hans4", "83996223690", LocalDate.now()));
	}

	public ObservableList<Cliente> getClienteData() {
		return clienteData;
	}

	/**
	 * Inicializa o root layout (layout base).
	 */
	public void initRootLayout() {
		try {
			// Carrega o root layout do arquivo fxml.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootView.fxml"));
			rootLayout = (BorderPane) loader.load();

			// Mostra a scene (cena) contendo o root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Mostra o person overview dentro do root layout.
	 */
	public void showPersonOverview() {
		try {
			// Carrega o person overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/MainView.fxml"));
			AnchorPane personOverview = (AnchorPane) loader.load();

			// Define o person overview dentro do root layout.
			rootLayout.setCenter(personOverview);

			// Dá ao controlador acesso à the main app.
			MainViewController controller = loader.getController();
			controller.setMainApp(this);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retorna o palco principal.
	 * 
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}

	public boolean showClienteDetails(Cliente cliente) {
		try {
			// Carrega o arquivo fxml e cria um novo stage para a janela popup.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/DetailsDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			// Cria o palco dialogStage.
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Detalhes Cliente");
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);

			// Define a pessoa no controller.
			DetailsDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setCliente(cliente);

			// Mostra a janela e espera até o usuário fechar.
			dialogStage.showAndWait();

			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public void loadPersonDataFromFile(File file) {
	    try {
	        JAXBContext context = JAXBContext
	                .newInstance(ClienteListWrapper.class);
	        Unmarshaller um = context.createUnmarshaller();

	        // Reading XML from the file and unmarshalling.
	        ClienteListWrapper wrapper = (ClienteListWrapper) um.unmarshal(file);

	        clienteData.clear();
	        clienteData.addAll(wrapper.getPersons());

	        // Save the file path to the registry.
	        setPersonFilePath(file);

	    } catch (Exception e) { // catches ANY exception
	        Dialogs.create()
	                .title("Erro")
	                .masthead("Não foi possível carregar dados do arquivo:\n" 
	                          + file.getPath()).showException(e);
	    }
	}

	/**
	 * Salva os dados da pessoa atual no arquivo especificado.
	 * 
	 * @param file
	 */
	public void savePersonDataToFile(File file) {
	    try {
	        JAXBContext context = JAXBContext
	                .newInstance(ClienteListWrapper.class);
	        Marshaller m = context.createMarshaller();
	        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

	        // Envolvendo nossos dados da pessoa.
	        ClienteListWrapper wrapper = new ClienteListWrapper();
	        wrapper.setPersons(clienteData);

	        // Enpacotando e salvando XML  no arquivo.
	        m.marshal(wrapper, file);

	        // Saalva o caminho do arquivo no registro.
	        setPersonFilePath(file);
	    } catch (Exception e) { // catches ANY exception
	        Dialogs.create().title("Erro")
	                .masthead("Não foi possível salvar os dados do arquivo:\n" 
	                          + file.getPath()).showException(e);
	    }
	}
	
	public File getPersonFilePath() {
	    Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    String filePath = prefs.get("filePath", null);
	    if (filePath != null) {
	        return new File(filePath);
	    } else {
	        return null;
	    }
	}

	public void setPersonFilePath(File file) {
	    Preferences prefs = Preferences.userNodeForPackage(MainApp.class);
	    if (file != null) {
	        prefs.put("filePath", file.getPath());

	        // Update the stage title.
	        primaryStage.setTitle("AddressApp - " + file.getName());
	    } else {
	        prefs.remove("filePath");

	        // Update the stage title.
	        primaryStage.setTitle("AddressApp");
	    }
	}
}
