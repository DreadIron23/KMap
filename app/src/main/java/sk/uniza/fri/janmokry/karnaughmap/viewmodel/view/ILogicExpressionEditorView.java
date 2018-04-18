package sk.uniza.fri.janmokry.karnaughmap.viewmodel.view;

import sk.uniza.fri.janmokry.karnaughmap.algorithm.quinemccluskey.Solution;

public interface ILogicExpressionEditorView extends IBaseProjectView {

    void init(Solution solution, int numberOfVariables);
    void finishView();
}
