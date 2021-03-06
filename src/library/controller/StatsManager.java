package library.controller;

import library.comparators.*;
import library.entities.*;
import library.interfaces.LibraryListeners;
import library.interfaces.Notifiable;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Gerenciador de estatísticas
 * @author Carlos Bravo - cehaga@dcc.ufrj.br
 */
class StatsManager implements LibraryListeners {
    private static StatsManager statsManager;

    private final TreeSet<Author> mostPopularAuthor;
    private final TreeSet<Type> mostPopularType;
    private final TreeSet<Student> mostBorrowerStudent;
    private final TreeSet<Book> mostPopularBook;
    private final TreeSet<Borrow> lastNBorrows;
    private final TreeSet<Borrow> borrowsNDays;

    private StatsManager(){
        mostPopularAuthor = new TreeSet<>(new MostPopularAuthorsComparator());
        mostPopularType = new TreeSet<>(new MostPopularTypesComparator());
        mostPopularBook = new TreeSet<>(new MostBorrowedBooksComparator());
        mostBorrowerStudent = new TreeSet<>(new MostBorrowerStudentsComparator());
        lastNBorrows = new TreeSet<>(new MostRecentBorrowComparator());
        borrowsNDays = new TreeSet<>(new MostDaysBorrowComparator());
    }

    /**
     * Cria caso não exista e retorna a instância de StatsManager
     * @return Instância ativa de StatsManager
     */
    public static StatsManager getStatsManager() {
        if(statsManager == null) statsManager = new StatsManager();
        return statsManager;
    }

    /**
     * Retorna a lista de autores mais populares. Caso não exista, faz uma requisição a DataManager
     * @return Lista de autores mais populares
     */
    public List<Author> getMostPopularAuthor() {
        if(!mostPopularAuthor.isEmpty()){
            List<Author> mostPopularAuthorList = new ArrayList<>(mostPopularAuthor);
            mostPopularAuthorList.sort(mostPopularAuthor.comparator());
            return mostPopularAuthorList;
        }
        DataManager dm = DataManager.getDataManager();
        mostPopularAuthor.addAll(dm.getAuthors().values());
        List<Author> mostPopularAuthorList = new ArrayList<>(mostPopularAuthor);
        mostPopularAuthorList.sort(mostPopularAuthor.comparator());
        return mostPopularAuthorList;
    }

    /**
     * Retorna a lista de estilos mais populares. Caso não exista, faz uma requisição a DataManager
     * @return Lista de estilos mais populares
     */
    public List<Type> getMostPopularType() {
        if(!mostPopularType.isEmpty()){
            List<Type> mostPopularTypeList = new ArrayList<>(mostPopularType);
            mostPopularTypeList.sort(mostPopularType.comparator());
            return mostPopularTypeList;
        }
        DataManager dm = DataManager.getDataManager();
        mostPopularType.addAll(dm.getTypes().values());
        List<Type> mostPopularTypeList = new ArrayList<>(mostPopularType);
        mostPopularTypeList.sort(mostPopularType.comparator());
        return mostPopularTypeList;
    }

    /**
     * Retorna a lista de estudante que mais pegaram livros. Caso não exista, faz uma requisição a DataManager
     * @return Lista de estudante que mais pegaram livros
     */
    public List<Student> getMostBorrowerStudent() {
        if(!mostBorrowerStudent.isEmpty()){
            List<Student> mostBorrowerStudentList = new ArrayList<>(mostBorrowerStudent);
            mostBorrowerStudentList.sort(mostBorrowerStudent.comparator());
            return mostBorrowerStudentList;
        }
        DataManager dm = DataManager.getDataManager();
        mostBorrowerStudent.addAll(dm.getStudents().values());
        List<Student> mostBorrowerStudentList = new ArrayList<>(mostBorrowerStudent);
        mostBorrowerStudentList.sort(mostBorrowerStudent.comparator());
        return mostBorrowerStudentList;
    }

    public List<Book> getMostPopularBook() {
        if(!mostPopularBook.isEmpty()){
            List<Book> mostPopularBookList = new ArrayList<>(mostPopularBook);
            mostPopularBookList.sort(mostPopularBook.comparator());
            return mostPopularBookList;
        }
        DataManager dm = DataManager.getDataManager();
        mostPopularBook.addAll(dm.getBooks().values());
        List<Book> mostPopularBookList = new ArrayList<>(mostPopularBook);
        mostPopularBookList.sort(mostPopularBook.comparator());
        return mostPopularBookList;
    }

    /**
     * Retorna a lista dos últimos empréstimos. Caso não exista, faz uma requisição a DataManager
     * @return Lista dos últimos empréstimos
     */
    public List<Borrow> getLastNBorrows() {
        if(!lastNBorrows.isEmpty()){
            List<Borrow> lastNBorrowsList = new ArrayList<>(lastNBorrows);
            lastNBorrowsList.sort(lastNBorrows.comparator());
            return lastNBorrowsList;
        }
        DataManager dm = DataManager.getDataManager();
        lastNBorrows.addAll(dm.getBorrows().values());
        List<Borrow> lastNBorrowsList = new ArrayList<>(lastNBorrows);
        lastNBorrowsList.sort(lastNBorrows.comparator());
        return lastNBorrowsList;
    }

    /**
     * Retorna a lista de empréstimos com mais tempo. Caso não exista, faz uma requisição a DataManager
     * @return Lista de empréstimos com mais tempo
     */
    public List<Borrow> getBorrowsNDays() {
        if(!borrowsNDays.isEmpty()){
            List<Borrow> borrowsNDaysList = new ArrayList<>(borrowsNDays);
            borrowsNDaysList.sort(borrowsNDays.comparator());
            return borrowsNDaysList;
        }
        DataManager dm = DataManager.getDataManager();
        borrowsNDays.addAll(dm.getBorrows().values());
        List<Borrow> borrowsNDaysList = new ArrayList<>(borrowsNDays);
        borrowsNDaysList.sort(borrowsNDays.comparator());
        return borrowsNDaysList;
    }

    /**
     * Calcula o ranking dos últimos N empréstimos
     * @param n Número de registros
     * @return Lista dos N empréstimos ordenados
     */
    public List<Borrow> lastNBorrowsRank(int n){
        List<Borrow> borrows = getLastNBorrows();
        int min = Math.min(n, borrows.size());
        return new ArrayList<>(borrows.subList(0,min));
    }

    /**
     * Calcula o ranking dos empréstimo que duraram pelo menos N dias
     * @param n Número de dias
     * @return Lista dos empréstimos ordenados
     */
    public List<Borrow> borrowsNDaysRank(int n){
        List<Borrow> borrows = getBorrowsNDays();
        List<Borrow> rank = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for(Borrow borrow : borrows){
            LocalDateTime last = borrow.getBroughtDate();
            if(last == null) last = now;
            if(Duration.between(borrow.getTakenDate(),last).toDays() < n) break;
            rank.add(borrow);
        }
        return rank;
    }

    /**
     * Calcula o ranking dos N estudantes que mais pegaram livros
     * @param n Número de registros
     * @return Lista dos N estudantes ordenados
     */
    public List<Student> mostNBorrowerStudentRank(int n){
        List<Student> students = getMostBorrowerStudent();
        int min = Math.min(n, students.size());
        return new ArrayList<>(students.subList(0, min));
    }

    /**
     * Calcula o ranking dos N livros mais populares
     * @param n Número de registros
     * @return Lista dos N livros ordenados
     */
    public List<Book> mostNPopularBooksRank(int n){
        List<Book> books = getMostPopularBook();
        int min = Math.min(n, books.size());
        return new ArrayList<>(books.subList(0, min));
    }

    /**
     * Calcula o ranking dos N autores mais populares
     * @param n Número de registros
     * @return Lista dos N autores ordenados
     */
    public List<Author> mostNPopularAuthorsRank(int n){
        List<Author> authors = getMostPopularAuthor();
        int min = Math.min(n, authors.size());
        return new ArrayList<>(authors.subList(0, min));
    }

    /**
     * Calcula o ranking dos N estilos mais populares
     * @param n Número de registros
     * @return Lista dos N estilos ordenados
     */
    public List<Type> mostNPopularTypes(int n){
        List<Type> types = getMostPopularType();
        int min = Math.min(n, types.size());
        return new ArrayList<>(types.subList(0, min));
    }

    /**
     * Atualiza os registros em caso de novos dados
     * @param data Dado modificado
     */
    @Override
    public void update(Notifiable data) {
        if(data instanceof Borrow){
            getLastNBorrows();
            getBorrowsNDays();
            borrowsNDays.add((Borrow)data);
            lastNBorrows.add((Borrow)data);
        }
        if(data instanceof Book){
            getMostPopularBook();
            mostPopularBook.add((Book)data);
        }
        if(data instanceof Author){
            getMostPopularAuthor();
            mostPopularAuthor.add((Author)data);
        }
        if(data instanceof Type){
            getMostPopularType();
            mostPopularType.add((Type)data);
        }
        if(data instanceof Student){
            getMostBorrowerStudent();
            mostBorrowerStudent.add((Student)data);
        }
    }
}
