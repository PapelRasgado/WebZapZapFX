package zapzap.main;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.NoSuchElementException;
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

	// Controlador principal, utilizado aqui para obter os dados.
	private WebDriver driver;

	// S�rie de ids utilizados para localizar os objetos com o selenium, todos eles
	// sao obtidos usando o firebase
	private String idInvalido;
	private String idEdit;
	private String idConexao;

	private String emails;

	public Agendador(MainApp mainApp, WebDriver driver) {
		this.mainApp = mainApp;
		this.driver = driver;
	}

	@Override
	public void run() {
		Firebase database;
		try {
			// Tenta criar conex�o com o firebase
			database = new Firebase("https://webzapzap-ade09.firebaseio.com/");

			// Identificador do aviso de que o numero � invalido
			String id = database.get("id-invalido").getRawBody();
			idInvalido = id.substring(1, id.length() - 1);

			// Identificador de edit utilizado para inserir o texto
			id = database.get("id-edit").getRawBody();
			idEdit = id.substring(1, id.length() - 1);

			// Identificador do aviso de que a conex�o com o celular foi perdida
			id = database.get("id-conexao").getRawBody();
			idConexao = id.substring(1, id.length() - 1);

			// Pega do banco os emails dos desenvolvedores para alertalos caso ocorra algum
			// erro que necessite manuten��o manual
			String emails = database.get("emails").getRawBody();
			this.emails = emails.substring(1, emails.length() - 1);
			
		} catch (FirebaseException e1) {
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// Puxa a lista de dados do mainApp
		ArrayList<Cliente> clientes = new ArrayList<Cliente>(mainApp.getClienteData());

		loop: for (Cliente cliente : clientes) {
			// Teste verificando a hora, se j� passou das 18h ele para a execu��o
			if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) >= 18) {
				break;
			}

			// Contador que representa o numero de testes em localizar o edit que a mensagem
			// vai ser inserida
			int contador = 0;

			// Calcula a quantidade de dias que falta para a conta do cliente vencer, se for
			// menos que 5 dias ele envia a mensagem
			long dias = ChronoUnit.DAYS.between(LocalDate.now(), cliente.getData());
			if (dias <= 5) {

				// Aqui ele abre a converso do usuario no driver selenium
				driver.get("https://web.whatsapp.com/send?phone=+55" + cliente.getNumber());

				// Loop infinito proposital, que s� � quebrado quando a mensagem � enviada, ou
				// quando algum erro � detectado
				while (true) {
					try {
						// Tempo de espera para que a execu��o n�o fique muito pesada
						Thread.sleep(1000);

						// Porcura o aviso de conversa inexistente, ou seja, o n�mero fornecido �
						// invalido ou n�o possui whatsapp
						WebElement aviso = driver.findElement(By.xpath(idInvalido));
						// Caso n�o encontre ele retorna null
						if (aviso != null) {
							if (aviso.getText().contains("url � inv�lido")) {
								// Nesse ponto ele retira o cliente da lista de agendamento e adiciona na de
								// falhas
								mainApp.getClienteFailData().add(cliente);
								mainApp.getClienteData().remove(cliente);
								break;
							}
						}

						// Caso n�o encontre o aviso de falha, ele procura o edit para inserir a
						// mensagem
						WebElement text = driver.findElement(By.className(idEdit));
						// Caso n�o encontre ele retorna null
						if (text != null) {
							// Espera 3 segundos para garantir que a tela de carregamento j� passou
							Thread.sleep(3000);
							text.sendKeys(cliente.getMessage());
							text.sendKeys(Keys.ENTER);
							mainApp.getClienteData().remove(cliente);
							break;
						}
					} catch (NoSuchElementException e) {
						// Em caso de n�o achar nenhum dos dois elemento ele contabiliza uma falha, ao
						// acumular 200 um alerta � emitido
						contador++;
						if (contador == 200) {
							contador = 0;
							// Quando ele verifica as falhas e reseta elas, procura saber o motivo, se
							// encontrar o aviso de conex�o perdida exibe um alerta para o usuario tentar
							// reestabelecer conex�o entre o celular e o webwhats
							WebElement aviso = driver.findElement(By.className(idConexao));
							if (aviso != null) {
								Alert alert = new Alert(AlertType.WARNING);
								alert.setTitle("Erro ao enviar mensagem");
								alert.setHeaderText("Verifique se o celular esta conectado");

								// Ao confirmar o aviso a execu��o volta ao normal
								alert.showAndWait();
							} else {
								// Caso n�o encontre o aviso, o possivel erro est� nos identificadores, que
								// possivelmente foram trocas pela equipe do whatsaap, quando isso acontece um
								// email � enviado para os desenvolvedores, para que eles atualizem no firebase
								sendMail();

								Alert alert = new Alert(AlertType.WARNING);
								alert.setTitle(
										"Os identificadores de elemento do WhatsApp/n possivelmente foram atualizas");
								alert.setHeaderText(
										"Os desenvolvedores j� foram avisados e est�o/n tentando corrigir o problema!");
								// Ao confirmar esse aviso a execu��o dos envios � parada, j� que sem os novos
								// identificares � impossivel enviar, logo, para n�o gerar mais erros o loop �
								// quebrado
								alert.showAndWait();
								break loop;
							}

						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * M�todo copiado do link
	 * https://www.devmedia.com.br/enviando-email-com-javamail-utilizando-gmail/18034
	 * com algumas adapta��es
	 */
	private void sendMail() {
		Properties props = new Properties();

		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				// Email e senha do email criado para utilizar firebase e enviar os emails
				return new PasswordAuthentication("webzapdantas@gmail.com", "d4n7a52018");
			}
		});

		try {

			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("webzapdantas@gmail.com")); // Remetente

			Address[] toUser = InternetAddress // Destinat�rio(s)
					.parse(emails);

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
