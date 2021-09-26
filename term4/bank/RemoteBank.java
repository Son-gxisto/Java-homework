package info.kgeorgiy.ja.istratov.bank;

import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RemoteBank implements Bank {
    private final ConcurrentMap<String, Person> persons = new ConcurrentHashMap<>();
    private final int port;
    private final ConcurrentMap<String, Account> accounts = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Set<String>> accountsFromPassport = new ConcurrentHashMap<>();

    public RemoteBank(final int port) {
        this.port = port;
    }

    @Override
    public Account createAccount(final String id) throws RemoteException {
        System.out.println("Creating account " + id);
        final Account account = new RemoteAccount(id, port);
        if (accounts.putIfAbsent(id, account) == null) {
            return account;
        } else {
            return getAccount(id);
        }
    }

    @Override
    public Account getAccount(final String id) throws RemoteException {
        System.out.println("Retrieving account " + id);
        return accounts.get(id);
    }

    @Override
    public Account getAccount(final Person person, final String subId) throws RemoteException {
        if (person instanceof LocalPerson) {
            return ((LocalPerson) person).getAccount(person.getPassport() + ":" + subId);
        }
        return accounts.get(person.getPassport() + ":" + subId);
    }

    @Override
    public Person getPerson(String passport, PersonType type) throws RemoteException {
        if (passport != null) {
            switch (type) {
                case REMOTE -> {
                    return persons.get(passport);
                }
                case LOCAL -> {
                    Person person = persons.get(passport);
                    if (person != null) {
                        Map<String, Account> accounts = new ConcurrentHashMap<>();
                        accountsFromPassport.get(passport).forEach((id) -> {
                            try {
                                Account account = getAccount(id);
                                accounts.put(id, new LocalAccount(account.getId(), account.getAmount()));
                            } catch (RemoteException e) {
                                System.err.println("Error while creating Local Account:" + e.getMessage());
                            }
                        });
                        return new LocalPerson(person.getName(), person.getLastname(), person.getPassport(), accounts);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Person getLocalPerson(String passport) throws RemoteException {
        return getPerson(passport, PersonType.LOCAL);
    }

    @Override
    public Person getRemotePerson(String passport) throws RemoteException {
        return getPerson(passport, PersonType.REMOTE);
    }

    @Override
    public boolean createPerson(String name, String lastName, String passportId) throws RemoteException {
        if (name != null && lastName != null && persons.get(passportId) == null) {
            persons.put(passportId, new RemotePerson(name, lastName, passportId, port));
            accountsFromPassport.put(passportId, ConcurrentHashMap.newKeySet());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Set<String> getPersonAccounts(Person person) throws RemoteException {
        if (person != null) {
            if (person instanceof LocalPerson) {
                return ((LocalPerson) person).getAccounts();
            } else {
                return accountsFromPassport.get(person.getPassport());
            }
        }
        return null;
    }

    @Override
    public void setAmount(Person person, String subId, int amount) throws RemoteException {
        if (person != null && subId != null) {
            getAccount(person, subId).setAmount(amount);
        }
    }

    @Override
    public boolean createAccount(String passport, String subId) throws RemoteException {
        String id = passport + ":" + subId;
        if (passport == null || subId == null ||
                !accountsFromPassport.containsKey(passport) ||
                accountsFromPassport.get(passport).contains(id)) {
            return false;
        }
        createAccount(id);
        accountsFromPassport.get(passport).add(id);
        return true;
    }
}
