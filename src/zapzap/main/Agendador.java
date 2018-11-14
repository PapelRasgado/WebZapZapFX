package zapzap.main;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import net.thegreshams.firebase4j.error.FirebaseException;
import net.thegreshams.firebase4j.service.Firebase;
import zapzap.main.model.Cliente;
import zapzap.main.MainApp;

public class Agendador extends TimerTask {

	private MainApp mainApp;
	private WebDriver driver;
	private String idInvalido;
	private String idEdit;

	public Agendador(MainApp mainApp, WebDriver driver) {
		this.mainApp = mainApp;
		this.driver = driver;
	}

	@Override
	public void run() {
		Firebase database;
		try {
			database = new Firebase("https://testando-18461.firebaseio.com/");
			String id = database.get("id-invalido").getRawBody();
			idInvalido = id.substring(1, id.length());
			id = database.get("id-edit").getRawBody();
			idEdit = id.substring(1, id.length());
		} catch (FirebaseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Não começou");
		ArrayList<Cliente> clientes = new ArrayList<Cliente>(mainApp.getClienteData());
		for (Cliente cliente : clientes) {
			long dias = ChronoUnit.DAYS.between(LocalDate.now(), cliente.getData());
			System.out.println(cliente.getName() + " " + dias);
			if (dias <= 2) {
				driver.get("https://web.whatsapp.com/send?phone=+55" + cliente.getNumber());
				while (true) {
					try {
						Thread.sleep(1000);
						List<WebElement> text = driver.findElements(By.className(idEdit));
						try {
							WebElement aviso = driver.findElement(By.xpath(idInvalido));
							if (aviso != null) {
								if (aviso.getText().contains("url é inválido")) {
									mainApp.getClienteFailData().add(cliente);
									mainApp.getClienteData().remove(cliente);
								
									break;
								}
							}
						} catch (Exception ex) {
							System.out.println("ta carregando irmão calma ai");
						}
						if (text.size() > 0) {
							text.get(0).sendKeys(cliente.getMessage());
							text.get(0).sendKeys(Keys.ENTER);
							Thread.sleep(5000);
							mainApp.getClienteData().remove(cliente);
							break;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
