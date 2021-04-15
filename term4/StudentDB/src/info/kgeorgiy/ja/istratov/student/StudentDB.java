package info.kgeorgiy.ja.istratov.student;

import info.kgeorgiy.java.advanced.student.GroupName;
import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentQuery;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StudentDB implements StudentQuery {
/*
    public static void main(String[] args) {
        Student a = new Student(1, "Артем", "Шашуловский", GroupName.M3234);
        Student b = new Student(5, "Анастасия", "Тушканова", GroupName.M3234);
        Student c = new Student(3, "Аня", "Тушканова", GroupName.M3234);
        Student d = new Student(4, "Анастасия", "Тушканова", GroupName.M3234);
        List<Student> list = new ArrayList<>();
        list.add(a); list.add(b); list.add(c); list.add(d);
        StudentDB db = new StudentDB();
        System.out.println(db.getFirstNames(list));
        System.out.println(db.getFullNames(list));
        System.out.println(db.getGroups(list)));
        System.out.println(db.getMaxStudentFirstName(list));
        System.out.println(db.sortStudentsByName(list));
    }
 */

    private List<String> getStudentsInfo(List<Student> students, Function<Student, String> function) {
        return (students.stream().map(function)).collect(Collectors.toList());
    }

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return getStudentsInfo(students, Student::getFirstName);
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return getStudentsInfo(students, Student::getLastName);
    }

    @Override
    public List<GroupName> getGroups(List<Student> students) {
        return (students.stream().map(Student::getGroup)).collect(Collectors.toList());
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return getStudentsInfo(students, student -> student.getFirstName() + " " + student.getLastName());
    }

    // :NOTE: redundant sort or missing information
    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return students.stream().map(Student::getFirstName).collect(Collectors.toSet());
    }

    @Override
    public String getMaxStudentFirstName(List<Student> students) {
        return students.stream().max(Student::compareTo).map(Student::getFirstName).orElse("");
    }

    // :NOTE: copypaste
    private final Comparator<Student> nameComparator = Comparator.comparing(Student::getLastName).reversed()
            .thenComparing(Comparator.comparing(Student::getFirstName).reversed())
            .thenComparing(Student::getId);

    private List<Student> sortStudents(Collection<Student> students, Comparator<Student> comparator) {
        return students.stream().sorted(comparator).collect(Collectors.toList());
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return sortStudents(students, Comparator.comparing(Student::getId));
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return sortStudents(students, nameComparator);
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return filterByPredicate(students, student -> student.getFirstName().equals(name));
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return filterByPredicate(students, student -> student.getLastName().equals(name));
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, GroupName group) {
        return filterByPredicate(students, student -> student.getGroup().equals(group));
    }

    private List<Student> filterByPredicate(Collection<Student> students, Predicate<Student> predicate) {
        return students.stream().filter(predicate).sorted(nameComparator).collect(Collectors.toList());
    }

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, GroupName group) {
        return findStudentsByGroup(students, group).stream()
                .collect(Collectors.toMap(
                        Student::getLastName, Student::getFirstName, BinaryOperator.minBy(String::compareTo)
                ));
    }
}