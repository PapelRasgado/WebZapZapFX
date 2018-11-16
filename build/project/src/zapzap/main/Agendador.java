package zapzap.main;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.TimerTask;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.config.PropertyPrinter;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
			database = new Firebase("https://webzapzap-ade09.firebaseio.com/");
			String id = database.get("id-invalido").getRawBody();
			idInvalido = id.substring(1, id.length() - 1);
			id = database.get("id-edit").getRawBody();
			idEdit = id.substring(1, id.length() - 1);
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
			int contador = 0;
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
							if (contador == 500) {
								Alert alert = new Alert(AlertType.WARNING);
								alert.setTitle("Erro ao enviar mensagem");
								alert.setHeaderText("Verifique se o celular esta conectado");

								alert.showAndWait();
								sendMail();
							}
							contador++;
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

	private void sendMail() {
		Properties props = new Properties();

		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("webzapdantas@gmail.com", "d4n7a52018");
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("webzapdantas@gmail.com")); // Remetente

			Address[] toUser = InternetAddress // Destinatário(s)
					.parse("eniedson.silva@gmail.com, joaodantas31.jpd@gmail.com");

			message.setRecipients(Message.RecipientType.TO, toUser);
			SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			message.setSubject("WebZapZap - " + s.format(Calendar.getInstance().getTime()));// Assunto
			message.setText("Come on brow, what are you doing?? webzapzap need of one manutezation");
			Transport.send(message);


		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}
}
