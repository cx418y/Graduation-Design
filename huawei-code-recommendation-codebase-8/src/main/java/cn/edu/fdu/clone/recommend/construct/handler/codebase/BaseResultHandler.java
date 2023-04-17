package cn.edu.fdu.clone.recommend.construct.handler.codebase;

public abstract class BaseResultHandler {

    protected String sagaFolderPath;

    public BaseResultHandler(String sagaFolderPath) {
        this.sagaFolderPath = sagaFolderPath;
    }

    public abstract void handle();

}
