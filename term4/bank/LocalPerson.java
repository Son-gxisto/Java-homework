package info.kgeorgiy.ja.istratov.bank;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class LocalPerson extends AbstactPerson implements Serializable  {
    private final Map<String, Account> accounts;
    public LocalPerson(String name, String surname, String passport, Map<String,Account> accounts) {
        super(name, surname, passport);
        this.accounts = accounts;
    }
    public Set<String> getAccounts() {
        return accounts.keySet();
    }
    public Account getAccount(String id) {
        return accounts.get(id);
    }
}
