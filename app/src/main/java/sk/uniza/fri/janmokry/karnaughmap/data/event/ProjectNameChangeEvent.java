package sk.uniza.fri.janmokry.karnaughmap.data.event;

/**
 * Event signalizing that List Of Projects content has changed.
 */
public class ProjectNameChangeEvent {

    public final String newProjectName;

    public ProjectNameChangeEvent(String newProjectName) {
        this.newProjectName = newProjectName;
    }
}
