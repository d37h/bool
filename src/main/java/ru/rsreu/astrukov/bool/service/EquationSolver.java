package ru.rsreu.astrukov.bool.service;

import org.codehaus.janino.CompileException;
import org.codehaus.janino.ExpressionEvaluator;
import org.codehaus.janino.Parser;
import org.codehaus.janino.Scanner;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class EquationSolver {


    //сюда должны подаваться ост функции складываемые по модулю 2
    public int getWeight(
            String function, List<String> variables
    ) throws CompileException, Scanner.ScanException, Parser.ParseException, InvocationTargetException {

        ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator();

        final String[] parameterNames = variables.toArray(new String[0]);
        Class[] classParameters = new Class[parameterNames.length];
        Arrays.fill(classParameters, boolean.class);

        expressionEvaluator.setParameters(parameterNames, classParameters);
        expressionEvaluator.setExpressionType(boolean.class);
        expressionEvaluator.cook(function);

        ArrayList<Integer> weightList = new ArrayList<>();

        for (int variableIndex = 0; variableIndex < variables.size(); variableIndex++) {

            int weight = 0;

            ArrayList<List<Boolean>> falsySets = new ArrayList<>(toBinarySets(variables.size(), variableIndex, false));
            ArrayList<List<Boolean>> truthySets =  new ArrayList<>(toBinarySets(variables.size(), variableIndex, true));

            for (int setIndex = 0; setIndex < falsySets.size(); setIndex++) {

                Boolean[] truthySet = new Boolean[truthySets.get(setIndex).size()];
                truthySets.get(setIndex).toArray(truthySet);

                Boolean[] falsySet = new Boolean[falsySets.get(setIndex).size()];
                falsySets.get(setIndex).toArray(falsySet);

                boolean falsyResult = (Boolean) expressionEvaluator.evaluate(truthySet);
                boolean truthyResult = (Boolean) expressionEvaluator.evaluate(truthySets.get(setIndex).toArray());

                if (falsyResult ^ truthyResult) {
                    weight++;
                }
            }

            weightList.add(weight);

        }

        int maxWeight = Collections.max(weightList);
        int variableToExcludeIndex = weightList.indexOf(maxWeight);

        System.out.println(variableToExcludeIndex);


        return variableToExcludeIndex;
    }

    private Set<List<Boolean>> toBinarySets(int binaryLength, int fixedNumIndex, boolean fixedNumValue) {

        final int binarySetsCount = Double.valueOf(Math.pow(binaryLength, 2)).intValue();
        Set<List<Boolean>> set = new HashSet<>();

        //todo: evade duplicated arrays via condition
        for (int number = 0; number < binarySetsCount; number++) {

            List<Boolean> list = new ArrayList<>(binaryLength);

            for (int i = 0; i < binaryLength; i++) {

                if (i == fixedNumIndex) {
                    list.add(fixedNumValue);
                } else {
                    list.add((1 << i & number) != 0);
                }

            }

            set.add(list);
        }

        return set;
    }

}
