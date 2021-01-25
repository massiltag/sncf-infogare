package fr.pantheonsorbonne.ufr27.miage.dao;

import java.util.List;

public interface Dao<T> { 
    List<T> getAll();  
    boolean save(T t);
    void update(T t, Object[] params);
    boolean delete(T t);
}