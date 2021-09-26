package info.kgeorgiy.ja.istratov.bank;

public class AbstactPerson implements Person {
    private final String name, lastname, passport;
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getLastname() {
        return lastname;
    }

    @Override
    public String getPassport() {
        return passport;
    }
    protected AbstactPerson(String name, String lastname, String passport) {
        this.name = name;
        this.lastname = lastname;
        this.passport = passport;
    }
}
