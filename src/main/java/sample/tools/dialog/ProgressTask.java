package sample.tools.dialog;

import javafx.concurrent.Task;

public abstract class ProgressTask extends Task<Void> {

    @Override
    protected Void call() {
        execute();
        return null;
    }

    @Override
    public void updateMessage(String message) {
        super.updateMessage(message);
    }

    protected abstract void execute();
}
