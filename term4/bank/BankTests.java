package info.kgeorgiy.ja.istratov.bank;

import org.junit.BeforeClass;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class BankTests {
    private static RemoteBank bank;
    private static final int PORT = 8888;
    private static final String HOST_NAME = "//localhost/bank";
    private static final int PERSONS_AMOUNT = 10;
    private static final String p = "p";
    private static final String a = "account";
    private static final Random random = new Random();

    @BeforeClass
    public static void beforeClass() throws Exception {
        bank = new RemoteBank(PORT);
        UnicastRemoteObject.exportObject(bank, PORT);
        Registry reg = LocateRegistry.createRegistry(PORT);
        reg.rebind(HOST_NAME, bank);
        System.out.println("Server started");
    }

    @org.junit.Test
    public void personCreateAndGetTest() throws RemoteException {
        String prefix = "personTest";
        IntStream.range(0, PERSONS_AMOUNT).forEach(id -> {
            String test = prefix + id;
            try {
                assertNull(bank.getRemotePerson(test));
                assertNull(bank.getLocalPerson(test));
                bank.createPerson(test, "ln", test);
                assertNotNull(bank.getRemotePerson(test));
                assertNotNull(bank.getLocalPerson(test));
                assertEquals(bank.getRemotePerson(test).getPassport(), bank.getLocalPerson(test).getPassport());
                assertEquals(bank.getRemotePerson(test).getName(), bank.getLocalPerson(test).getName());
                assertEquals(bank.getRemotePerson(test).getLastname(), bank.getLocalPerson(test).getLastname());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @org.junit.Test
    public void accountCreateAndGetTest() {
        String prefix = "accountTest";
        IntStream.range(0, PERSONS_AMOUNT).forEach(id -> {
            String test = prefix + id;
            try {
                bank.createPerson(test, "ln", test);
                String curAcc = "acc0";
                bank.createAccount(test, curAcc);
                LocalPerson local = (LocalPerson) bank.getLocalPerson(test); //cast??
                RemotePerson remote = (RemotePerson) bank.getRemotePerson(test);
                assertEquals(bank.getPersonAccounts(local).size(), bank.getPersonAccounts(remote).size());
                assertEquals(bank.getPersonAccounts(local), bank.getPersonAccounts(remote));
                bank.createAccount(remote.getPassport(), "acc1");
                assertEquals(bank.getPersonAccounts(remote).size(), 2);
                assertEquals(bank.getPersonAccounts(local).size(), 1);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @org.junit.Test
    public void AmountTest() throws RemoteException {
        bank.createPerson(p, "ln", p);
        assertNotNull(bank.getRemotePerson(p));
        bank.createAccount(p, a);
        LocalPerson local = (LocalPerson) bank.getLocalPerson(p);
        RemotePerson remote = (RemotePerson) bank.getRemotePerson(p);
        assertEquals(bank.getAccount(local, a).getAmount(), bank.getAccount(remote, a).getAmount());
        bank.setAmount(remote, a, 100);
        assertNotEquals(bank.getAccount(local, a).getAmount(), bank.getAccount(remote, a).getAmount());
        assertEquals(bank.getAccount(remote, a).getAmount(), 100);
        assertEquals(bank.getAccount(local, a).getAmount(), 0);
        bank.setAmount(local, a, 200);
        assertNotEquals(bank.getAccount(local, a).getAmount(), bank.getAccount(remote, a).getAmount());
        assertEquals(bank.getAccount(remote, a).getAmount(), 100);
        assertEquals(bank.getAccount(local, a).getAmount(), 200);
        LocalPerson local2 = (LocalPerson) bank.getLocalPerson(p);
        assertEquals(bank.getAccount(local2, a).getAmount(), bank.getAccount(remote, a).getAmount());
        bank.setAmount(local2, a, 500);
        assertEquals(bank.getAccount(local2, a).getAmount(), 500);
        assertEquals(bank.getAccount(local, a).getAmount(), 200);
        RemotePerson remote2 = (RemotePerson) bank.getRemotePerson(p);
        bank.setAmount(remote2, a, 600);
        assertEquals(bank.getAccount(remote, a).getAmount(), bank.getAccount(remote2, a).getAmount());
    }

    @org.junit.Test
    public void samePersonTest() throws RemoteException {
        bank.createPerson(p, p, p);
        assertFalse(bank.createPerson(p, p, p));
    }

    @org.junit.Test
    public void samePersonAccountTest() throws RemoteException {
        bank.createPerson(p, p, p);
        bank.createAccount(p, a);
        assertFalse(bank.createAccount(p, a));
    }

    private String getRandomString(int count){
        byte[] array = new byte[count];
        new Random().nextBytes(array);
        return new String(array);
    }

    private int getSize() {
        return Math.min(10 + Math.abs(random.nextInt(1000000)), 100);
    }

    @org.junit.Test
    public void encodingTest() {
        IntStream.range(0, PERSONS_AMOUNT).forEach(num -> {
            try {
            String name = getRandomString(getSize());
            String lastname = getRandomString(getSize());
            String passport = getRandomString(getSize());
            //System.out.println(name + " - " + lastname + " - " + passport);
            bank.createPerson(name, lastname, passport);
            LocalPerson local = (LocalPerson) bank.getLocalPerson(passport);
            RemotePerson remote = (RemotePerson) bank.getRemotePerson(passport);
            assertEquals(remote.getPassport(), local.getPassport());
            assertEquals(remote.getPassport(), passport);
            assertEquals(remote.getName(), local.getName());
            assertEquals(local.getName(), name);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }
/*
    @org.junit.Test
    public void multiThreads() throws RemoteException {
        int threads = 5;
        bank.createPerson(p, p, p);
        bank.createAccount(a);
        ExecutorService executors = Executors.newFixedThreadPool(threads);
        for (int i = 0; i < 5; i++) {
            executors.submit(() -> {
                try {
                    RemotePerson person = (RemotePerson) bank.getRemotePerson(p);
                    for (int j = 0; j < 10; j++) {
                        bank.setAmount(person, a, bank.getAccount(person, a).getAmount() + 1);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
        }
        assertEquals(50, bank.getAccount());
    } */
}
