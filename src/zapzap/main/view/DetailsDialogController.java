package zapzap.main.view;

import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.stage.Stage;
import zapzap.main.model.Cliente;
import javafx.scene.control.Label;

/**
 * Dialog para editar detalhes de uma pessoa.
 * 
 * @author Marco Jakob
 */
public class DetailsDialogController {

    @FXML
    private Label name;
    @FXML
    private Label number;
    @FXML
    private Label date;


    private Stage dialogStage;
    private Cliente cliente;

    /**
     * Inicializa a classe controlle. Este m�todo � chamado automaticamente
     * ap�s o arquivo fxml ter sido carregado.
     */
    @FXML
    private void initialize() {
    }

    /**
     * Define o palco deste dialog.
     * 
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        this.dialogStage.setResizable(false);
    }

    /**
     * Define a pessoa a ser editada no dialog.
     * 
     * @param person
     */
    public void setCliente(Cliente cliente) {
        this.cliente = cliente;

        name.setText(cliente.getName());
        number.setText(cliente.getNumber());
        date.setText(cliente.getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    
    @FXML
    private void handleVoltar() {
        dialogStage.close();
    }
}