package org.jpgrammar;

import lombok.extern.slf4j.Slf4j;

/**
 * @author MUJUN
 * this class contain some utilities like split a sentences into parts by
 * particles
 */
@Slf4j
public class Utils {

    public void splitWords(String jpSentence) {
        log.info("jap sentence is : {}", jpSentence);
        // split by 、
        jpSentence.split("、");
    }
}
