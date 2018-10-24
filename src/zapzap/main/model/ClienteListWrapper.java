package zapzap.main.model;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "clientes")
public class ClienteListWrapper {

    private List<Cliente> clientes;

    @XmlElement(name = "person")
    public List<Cliente> getPersons() {
        return clientes;
    }

    public void setPersons(List<Cliente> clientes) {
        this.clientes = clientes;
    }
}