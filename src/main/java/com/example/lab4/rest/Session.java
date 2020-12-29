package com.example.lab4.rest;

import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class Session {
    private final Set<String> sessionKeys = new HashSet<String>();
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();


    public Boolean isValidUser(String secKey) {
        readWriteLock.readLock().lock();
        Boolean b =  sessionKeys.contains(secKey);
        readWriteLock.readLock().unlock();
        return b;
    }

    public String getNewKey() {
        String secKey = new BigInteger(130, new SecureRandom()).toString(32);
        readWriteLock.writeLock().lock();
        sessionKeys.add(secKey);
        readWriteLock.writeLock().unlock();

        return secKey;
    }

    public void deleteKey(String key) {
        readWriteLock.writeLock().lock();
        sessionKeys.remove(key);
        readWriteLock.writeLock().unlock();
    }

}
