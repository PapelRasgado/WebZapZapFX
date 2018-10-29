package zapzap.main;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TimerTask;

import org.openqa.selenium.By;
import org.openqa.selenium.By.ByLinkText;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import zapzap.main.model.Cliente;
import zapzap.main.MainApp;

public class Agendador extends TimerTask {

	private MainApp mainApp;
	private WebDriver driver;

	public Agendador(MainApp mainApp, WebDriver driver) {
		this.mainApp = mainApp;
		this.driver = driver;
	}

	@Override
	public void run() {
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
						List<WebElement> text = driver.findElements(By.className("_2S1VP"));
						try {
							WebElement aviso = driver.findElement(By.xpath("html/body/div/div/span[3]/div/span/div/div/div/div/div/div"));
							if (aviso != null) {
								if (aviso.getText().contains("url é inválido")) {
									System.out.println(driver
											.findElement(
													By.xpath("html/body/div/div/span[3]/div/span/div/div/div/div/div/div"))
											.getText());
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
							// mainApp.getClienteData().remove(cliente);
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
