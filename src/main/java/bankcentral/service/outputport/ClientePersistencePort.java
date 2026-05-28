package bankcentral.service.outputport;

import bankcentral.domain.Cliente;

import java.util.List;
import java.util.Optional;

public interface ClientePersistencePort {

    Cliente        saveCliente(Cliente cliente);
    List<Cliente>  findAllClientes();
    Optional<Cliente> findClienteById(int id);
    Optional<Cliente> findClienteByUsuario(String usuario);
    Cliente        updateCliente(Cliente cliente);
    void           deleteCliente(int id);
}
