package info.kgeorgiy.ja.istratov.bank;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemotePerson extends AbstactPerson {
    int port;
    public RemotePerson(String name, String surname, String passport, int port) throws RemoteException {
        super(name, surname, passport);
        this.port = port;
        UnicastRemoteObject.exportObject(this, port);
    }
}
