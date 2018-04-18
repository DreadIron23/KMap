package sk.uniza.fri.janmokry.karnaughmap.data.event;

import sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.Solution;

/**
 * Event signalizing that Logic Expression for certain map was edited and should be recalculated.
 *
 * Created by Janci on 18.4.2018.
 */

public class LogicExpressionEditCompletionEvent {

    public Solution editedSolution;

    public LogicExpressionEditCompletionEvent(Solution editedSolution) {
        this.editedSolution = editedSolution;
    }
}
