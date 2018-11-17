package zapzap.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

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
import net.thegreshams.firebase4j.error.FirebaseException;
import net.thegreshams.firebase4j.service.Firebase;
import zapzap.main.model.Cliente;
import zapzap.main.view.DetailsDialogController;
import zapzap.main.view.MainViewController;
import zapzap.main.view.RootViewController;

public class MainApp extends Application {

	private Stage primaryStage;
	private BorderPane rootLayout;
	private MainViewController mainViewController;
	
	//Url para o local aonde o arquivo com as informações são salvos.
	private String url = System.getProperty("user.home") + "//Documents//Webzapzap";
	
	//Driver do Selenium utilizado para manipular o whatsweb
	private WebDriver driver;

	private ObservableList<Cliente> clienteData = FXCollections.observableArrayList();
	private ObservableList<Cliente> clienteFailData = FXCollections.observableArrayList();
	
	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("WebZapZap");
		this.primaryStage.setResizable(false);

		this.primaryStage.getIcons().add(new Image("file:resources/images/logo.png"));
		initRootLayout();

		showClientOverview();
	}
	
	public ObservableList<Cliente> getClienteFailData() {
		return clienteFailData;
	}

	public MainApp() {
		read();
		System.setProperty("webdriver.gecko.driver", "resources/lib/geckodriver.exe");
		driver = new FirefoxDriver();

		driver.get("https://web.whatsapp.com/");
		new Thread() {
			public void run() {
				
				try {
					Firebase database = new Firebase("https://webzapzap-ade09.firebaseio.com/");
					String id = database.get("id-qr").getRawBody();
					id = id.substring(1, id.length()-1);
					while (true) {
						List<WebElement> elems = driver.findElements(By.className(id));
						if (elems.size() == 0) {
							break;
						} else {
							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}
				} catch (FirebaseException e1) {
					e1.printStackTrace();
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}
				
			};
		}.start();
		
		
		Timer timer = new Timer();
        Agendador agendador = new Agendador(this, driver);
        Calendar data = Calendar.getInstance();
        if(data.get(Calendar.HOUR_OF_DAY) > 12) {
        	data.add(Calendar.DATE, 1);
        }
        data.set(Calendar.HOUR_OF_DAY, 12);
        data.set(Calendar.MINUTE, 00);
        data.set(Calendar.SECOND, 0);
        timer.schedule(agendador, data.getTimeInMillis()-Calendar.getInstance().getTimeInMillis(), 86400000);
		
	}


	public ObservableList<Cliente> getClienteData() {
		return clienteData;
	}

	public void initRootLayout() {
		try {
			
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootView.fxml"));
			rootLayout = (BorderPane) loader.load();

			RootViewController controller = loader.getController();
			controller.setMainApp(this);
			
			
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void showClientOverview() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/MainView.fxml"));
			AnchorPane clienteOverview = (AnchorPane) loader.load();

			rootLayout.setCenter(clienteOverview);

			mainViewController = loader.getController();
			mainViewController.setMainApp(this);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public static void main(String[] args) {
		launch(args);
	}

	public boolean showClienteDetails(Cliente cliente, boolean teste) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/DetailsDialog.fxml"));
			AnchorPane page = (AnchorPane) loader.load();

			Stage dialogStage = new Stage();
			if (teste) {
				dialogStage.setTitle("Detalhes Cliente");
			} else {
				dialogStage.setTitle("Detalhes Falha");
			}
			
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.initOwner(primaryStage);
			
			Scene scene = new Scene(page);
			dialogStage.setScene(scene);
			dialogStage.getIcons().add(new Image("file:resources/images/logo.png"));

			DetailsDialogController controller = loader.getController();
			controller.setDialogStage(dialogStage);
			controller.setCliente(cliente);
			controller.setMainApp(this);
			controller.setTipo(teste);

			dialogStage.showAndWait();

			return true;
		}catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void save() {
		try {

			FileOutputStream fout = new FileOutputStream(url + "/save.ser");

			ObjectOutputStream oos = new ObjectOutputStream(fout);

			oos.writeObject(new ArrayList<Cliente>(clienteData));

			oos.close();
			
			fout = new FileOutputStream(url + "/savefail.ser");
			
			ObjectOutputStream oosFail = new ObjectOutputStream(fout);
			
			oosFail.writeObject(new ArrayList<Cliente>(clienteFailData));
			
			oosFail.close();

		} catch(FileNotFoundException f) {
			File diretorio = new File(url);
            diretorio.mkdir();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public void read() {
		try {

			FileInputStream fin = new FileInputStream(url + "/save.ser");

			ObjectInputStream ois = new ObjectInputStream(fin);

			List<Cliente> list = (List<Cliente>) ois.readObject();
			clienteData = FXCollections.observableList(list);
			
			ois.close();
			
			fin = new FileInputStream(url + "/savefail.ser");
			
			ObjectInputStream oisFail = new ObjectInputStream(fin);

			List<Cliente> listFail = (List<Cliente>) oisFail.readObject();
			clienteFailData = FXCollections.observableList(listFail);
			
			oisFail.close();

		} catch (FileNotFoundException ex) {
			save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	
	@Override
	public void stop() throws Exception {
		save();
		System.exit(0);
		driver.close();
		super.stop();
	}

	public void editar(Cliente cliente, boolean tipo) {
		mainViewController.editar(cliente, tipo);
		
	}
	
}
