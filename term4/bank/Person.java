package info.kgeorgiy.ja.istratov.bank;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Person extends Remote {
    String getName() throws RemoteException;
    String getLastname() throws RemoteException;
    String getPassport() throws RemoteException;
}
