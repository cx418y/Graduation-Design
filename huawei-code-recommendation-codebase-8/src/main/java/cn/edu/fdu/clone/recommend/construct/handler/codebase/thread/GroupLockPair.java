package cn.edu.fdu.clone.recommend.construct.handler.codebase.thread;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class GroupLockPair {

    protected int gatherId;
    protected ReentrantReadWriteLock lock;

    public GroupLockPair(int gatherId){
        this.gatherId = gatherId;
        this.lock = new ReentrantReadWriteLock();
    }
}
