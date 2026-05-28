package bankcentral.view;

import bankcentral.domain.Cliente;
import bankcentral.service.ClienteServiceImpl;

import java.util.List;
import java.util.Scanner;

public class ClienteView {

    Scanner sc = new Scanner(System.in);

    private final ClienteServiceImpl clienteServiceImpl;

    public ClienteView(ClienteServiceImpl clienteServiceImpl) {
        this.clienteServiceImpl = clienteServiceImpl; // Inyeccion de dependencias
    }

    public void createCliente() {
        clienteServiceImpl.createClienteService();
    }

    public void getClienteById(int id) {
        clienteServiceImpl.getClienteById(id);
    }

    public List<Cliente> getAllClientes() {
        return clienteServiceImpl.getAllClientes();
    }

    public void updateCliente(int id) {
        clienteServiceImpl.updateClienteService(id);
    }

    public void deleteCliente(int id) {
        System.out.println("Estoy en el view de Cliente");
        clienteServiceImpl.deleteCliente(id);
    }

    public Cliente login(String usuario, String contrasena) {
        return clienteServiceImpl.login(usuario, contrasena);
    }
}
