// Copyright (c) 2015 K Team. All Rights Reserved.
package org.kframework.debugger;

import org.junit.Before;
import org.junit.Test;
import org.kframework.attributes.Source;
import org.kframework.kore.K;
import org.kframework.kore.compile.KtoKORE;
import org.kframework.utils.KoreUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Random;

import static org.junit.Assert.assertEquals;


/**
 * Created by Manasvi on 6/19/15.
 * <p>
 * Test File for the Debugger Interface Implementation
 */
public class TstKDebugOnKORE_IT {


    private KoreUtils utils;
    private K parsed;
    private KDebug debuggerSession;
    private KtoKORE trans;
    private String fileName;

    @Before
    public void setup() throws URISyntaxException, IOException {

        fileName = "/convertor-tests/kore_imp.k";
        utils = new KoreUtils(fileName);
        trans = new KtoKORE();
        String pgm = "int s, n; n = 10; while(0<=n) { s = s + n; n = n + -1; }";
        parsed = utils.getParsed(pgm, Source.apply("generated by " + getClass().getSimpleName()));
        debuggerSession = new KoreKDebug(parsed, new KoreUtils(fileName).getRewriter());
    }

    @Test
    public void normalExecutionTest() throws IOException, URISyntaxException {
        int steps = getRandomSteps(1, 100);
        K debugResult = debuggerSession.step(steps).getCurrentK();
        K expectedResult = utils.stepRewrite(parsed, Optional.ofNullable(new Integer(steps)));
        assertEquals("Normal and Debug results don't match, when both allowed to run for some random steps", expectedResult, debugResult);
    }
    @Test
    public void jumpBackTest() {
        /* Going Back on Debugger */
        int forward = getRandomSteps(20, 150);
        int backward = getRandomSteps(1, forward);
        debuggerSession.step(forward);
        K debugResult = debuggerSession.backStep(backward).getCurrentK();
        K expectedResult = utils.stepRewrite(parsed, Optional.ofNullable(new Integer(forward - backward)));
        assertEquals("Normal and Debug results don't match, when normal takes 25 steps, and debugger goes back 25 steps", expectedResult, debugResult);
    }

    @Test
    public void jumpCommandTest() {

        /*  Jumping on Debugger and 50 steps for normal executor */
        int stateNum = getRandomSteps(20, 50);
        K debugResult = trans.apply(debuggerSession.jumpTo(stateNum).getCurrentK());
        K expectedResult = trans.apply(utils.stepRewrite(parsed, Optional.ofNullable(new Integer(stateNum))));
        assertEquals("Normal and Debug results don't match, when normal takes 50 steps, and debugger jumps to state 50", expectedResult, debugResult);
    }

    @Test
    public void multipleStepTest() {
        int steps1 = getRandomSteps(1, 50);
        int steps2 = getRandomSteps(1, 50);
        debuggerSession.step(steps1);
        K debugResult = debuggerSession.step(steps2).getCurrentK();
        K expectedResult = utils.stepRewrite(parsed, Optional.ofNullable(new Integer(steps1 + steps2)));
        assertEquals("Normal and Debug results don't match, when normal takes 20 steps, and debugger 10 steps in ongoing session", expectedResult, debugResult);
    }


    private int getRandomSteps(int min, int max) {
        Random rand = new Random();
        int randomNum = min + (int)(Math.random() * ((max - min) + 1));
        return randomNum;
    }
}
