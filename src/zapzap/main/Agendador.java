package zapzap.main;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import zapzap.main.model.Cliente;

public class Agendador extends TimerTask {

	private MainApp mainApp;
	private WebDriver driver;
	
	public Agendador(MainApp mainApp, WebDriver driver) {
		this.mainApp = mainApp;
		this.driver = driver;
	}

	@Override
	public void run() {
		ArrayList<Cliente> clientes =  new ArrayList<Cliente>(mainApp.getClienteData());
		
		for (Cliente cliente : clientes) {
			long dias = ChronoUnit.DAYS.between(cliente.getData(), LocalDate.now());
			if (dias < 2) {
				driver.get("https://web.whatsapp.com/send?phone=+55" + cliente.getNumber());
				while (true) {
					try {
						Thread.sleep(100);
						List<WebElement> text = driver.findElements(By.className("_2S1VP"));
						text.get(0).sendKeys(cliente.getMessage());
						text.get(0).sendKeys(Keys.ENTER);
						break;
					} catch (IndexOutOfBoundsException e) {
						System.out.println("Conversa Carregando!");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				clientes.remove(cliente);
			}
		}
		
	}

}
