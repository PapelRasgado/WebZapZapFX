package zapzap.main.model;

import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Cliente {

    private final StringProperty name;
    private final StringProperty number;
    private final ObjectProperty<LocalDate> data;

    /**
     *  Construtor padrão.
     */
    public Cliente() {
        this(null, null, null);
    }
    
    /**
     * Construtor com alguns dados iniciais.
     * 
     * @param firstName Primeiro nome da Pessoa.
     * @param lastName Sobrenome da Pessoa.
     */
    public Cliente(String name, String number, LocalDate data) {
        this.name = new SimpleStringProperty(name);
        this.number = new SimpleStringProperty(number);
        
        this.data = new SimpleObjectProperty<LocalDate>(data);
    }
    
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }
    
    public StringProperty nameProperty() {
        return name;
    }

    public String getNumber() {
        return number.get();
    }

    public void setNumber(String number) {
        this.number.set(number);
    }
    
    public StringProperty numberProperty() {
        return number;
    }

    public LocalDate getData() {
        return data.get();
    }

    public void setData(LocalDate data) {
        this.data.set(data);
    }
    
    public ObjectProperty<LocalDate> dataProperty() {
        return data;
    }
}
