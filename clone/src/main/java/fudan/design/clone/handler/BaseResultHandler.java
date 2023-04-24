package fudan.design.clone.handler;

public abstract class BaseResultHandler {

    protected String sagaFolderPath;

    public BaseResultHandler(String sagaFolderPath) {
        this.sagaFolderPath = sagaFolderPath;
    }

    public abstract void handle();

}
