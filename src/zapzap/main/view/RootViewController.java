package zapzap.main.view;

import java.io.File;

import javafx.fxml.FXML;
import javafx.stage.FileChooser;
import zapzap.main.MainApp;

import org.controlsfx.dialog.Dialogs;

/**
 * O controlador para o root layout. O root layout prov� um layout b�sico
 * para a aplica��o contendo uma barra de menu e um espa�o onde outros elementos
 * JavaFX podem ser colocados.
 * 
 * @author Marco Jakob
 */
public class RootViewController {

    // Refer�ncia � aplica��o principal
    private MainApp mainApp;

    /**
     * � chamado pela aplica��o principal para referenciar a si mesma.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Cria uma agenda vazia.
     */
    @FXML
    private void handleNew() {
        mainApp.getClienteData().clear();
        mainApp.setPersonFilePath(null);
    }

    /**
     * Abre o FileChooser para permitir o usu�rio selecionar uma agenda
     * para carregar.
     */
    @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();

        // Define um filtro de extens�o
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Mostra a janela de salvar arquivo
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

        if (file != null) {
            mainApp.loadPersonDataFromFile(file);
        }
    }

    /**
     * Salva o arquivo para o arquivo de pessoa aberto atualmente. Se n�o houver
     * arquivo aberto, a janela "salvar como" � mostrada.
     */
    @FXML
    private void handleSave() {
        File personFile = mainApp.getPersonFilePath();
        if (personFile != null) {
            mainApp.savePersonDataToFile(personFile);
        } else {
            handleSaveAs();
        }
    }

    /**
     * Abre um FileChooser para permitir o usu�rio selecionar um arquivo
     * para salvar.
     */
    @FXML
    private void handleSaveAs() {
        FileChooser fileChooser = new FileChooser();

        // Define o filtro de extens�o
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);

        // Mostra a janela para salvar arquivo
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

        if (file != null) {
            // Certifica de que esta � a extens�o correta
            if (!file.getPath().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }
            mainApp.savePersonDataToFile(file);
        }
    }

    /**
     * Abre uma janela Sobre.
     */
    @FXML
    private void handleAbout() {
        Dialogs.create()
            .title("Webzapzap")
            .masthead("Sobre")
            .message("Autor: Jp Mito")
            .showInformation();
    }

    /**
     * Fecha a aplica��o.
     */
    @FXML
    private void handleExit() {
        System.exit(0);
    }
}
