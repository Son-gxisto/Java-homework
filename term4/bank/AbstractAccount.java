package info.kgeorgiy.ja.istratov.bank;

import java.rmi.RemoteException;

public class AbstractAccount implements Account {
    protected final String id;
    protected int amount;

    public AbstractAccount(String id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    @Override
    public String getId() throws RemoteException {
        return id;
    }

    @Override
    public int getAmount() throws RemoteException {
        return amount;
    }

    @Override
    public void setAmount(int amount) throws RemoteException {
        this.amount = amount;
    }
}
