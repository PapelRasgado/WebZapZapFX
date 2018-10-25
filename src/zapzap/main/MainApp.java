package zapzap.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

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
import zapzap.main.view.DetailsDialogController;
import zapzap.main.view.MainViewController;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;
	private MainViewController mainViewController;
	private String url = System.getProperty("user.home") + "//Documents//Webzapzap//save.ser"; 

	private ObservableList<Cliente> clienteData = FXCollections.observableArrayList();

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("WebZapZap");
		this.primaryStage.setResizable(false);

		this.primaryStage.getIcons().add(new Image("file:resources/images/logo.png"));

		initRootLayout();

		showClientOverview();
	}

	public MainApp() {
		read();
		
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
	 * Mostra o cliente overview dentro do root layout.
	 */
	public void showClientOverview() {
		try {
			// Carrega o cliente overview.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/MainView.fxml"));
			AnchorPane clienteOverview = (AnchorPane) loader.load();

			// Define o cliente overview dentro do root layout.
			rootLayout.setCenter(clienteOverview);

			// Dá ao controlador acesso à the main app.
			mainViewController = loader.getController();
			mainViewController.setMainApp(this);

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
			controller.setMainApp(this);

			// Mostra a janela e espera até o usuário fechar.
			dialogStage.showAndWait();

			return true;
		}catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void save() {
		try {

			FileOutputStream fout = new FileOutputStream(url);

			ObjectOutputStream oos = new ObjectOutputStream(fout);

			oos.writeObject(new ArrayList<Cliente>(clienteData));

			oos.close();

		} catch(FileNotFoundException f) {
			File diretorio = new File(System.getProperty("user.home") + "//Documents//Webzapzap");
            diretorio.mkdir();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void read() {
		try {

			FileInputStream fin = new FileInputStream(url);

			ObjectInputStream ois = new ObjectInputStream(fin);

			List<Cliente> list = (List<Cliente>) ois.readObject();
			clienteData = FXCollections.observableList(list);
			
			ois.close();

		} catch (FileNotFoundException ex) {
			save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void stop() throws Exception {
		save();
		super.stop();
	}

	public void editar(Cliente cliente) {
		mainViewController.editar(cliente);
		
	}
	
}
