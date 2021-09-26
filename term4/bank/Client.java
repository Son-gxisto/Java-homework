package info.kgeorgiy.ja.istratov.bank;

import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public final class Client {
    private Client() {}

    //possible args
    //cli lname pas1 ac1 1
    public static void main(final String... args) throws RemoteException {
        final Bank bank;
        try {
            Registry registry = LocateRegistry.getRegistry(8080);
            bank = (Bank) registry.lookup("//localhost/bank");
        } catch (final NotBoundException e) {
            System.err.println("Bank is not bound");
            return;
        } catch (final ConnectException e) {
            e.printStackTrace();
            return;
        }
        if (args.length != 5) {
            System.err.println("Wrong number of arguments, usage: name, lastname, passportId, accountId, count");
            return;
        }
        try {
            final String name = args[0],
                    lastname = args[1],
                    passportId = args[2],
                    accountId = args[3];
            final int amount = Integer.parseInt(args[4]);
            Person person;
            if ((person = bank.getRemotePerson(passportId)) == null) {
                System.out.println("Creating person");
                bank.createPerson(name, lastname, passportId);
                person = bank.getRemotePerson(passportId);
            } else {
                if (!person.getName().equals(name) || !person.getLastname().equals(lastname)) {
                    System.out.println("name or lastname doesn't match");
                    return;
                }
                System.out.println("Person found");
            }
            Account account;
            if ((account = bank.getAccount(person, accountId)) == null) {
                System.out.println("Creating account");
                bank.createAccount(passportId, accountId);
                account = bank.getAccount(person, accountId);
            } else {
                System.out.println("Account found");
            }
            System.out.println("Account id: " + account.getId());
            System.out.println("Money: " + account.getAmount());
            System.out.println("Adding money");
            account.setAmount(account.getAmount() + amount);
            System.out.println("Money: " + account.getAmount());
        } catch (RemoteException e) {
            System.err.println("Bank error:" + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.err.println("Last argument should be Integer");
        }
    }
}
