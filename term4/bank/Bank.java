package info.kgeorgiy.ja.istratov.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface Bank extends Remote {
    /**
     * Creates a new account with specified identifier if it is not already exists.
     * @param id account id
     * @return created or existing account.
     */
    Account createAccount(String id) throws RemoteException;

    /**
     * Returns account by identifier.
     * @param id account id
     * @return account with specified identifier or {@code null} if such account does not exists.
     */
    Account getAccount(String id) throws RemoteException;

    Account getAccount(final Person person, final String subId) throws RemoteException;

    Person getPerson(String passport, PersonType type) throws RemoteException;

    Person getLocalPerson(String passport) throws RemoteException;

    Person getRemotePerson(String passport) throws RemoteException;

    boolean createPerson(String name, String lastName, String passportId) throws RemoteException;

    Set<String> getPersonAccounts(Person person) throws RemoteException;

    void setAmount(Person person, String subId, int amount) throws RemoteException;

    boolean createAccount(String passport, String subId) throws RemoteException;
}
