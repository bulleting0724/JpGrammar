package org.jpgrammar;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

@Slf4j
public class ParticleSplit implements SplitProcessor{
    public HashMap<Integer, String> calculatePosOfParticle(String sentence) {
        HashMap<Integer, String> particlePosInSentence = new HashMap<>();
        //calculate the index of the occurrence
        for (Particles particle: Particles.values()) {
            log.info("processing : {}", particle.kana);
            int i = sentence.indexOf(particle.kana);
            if (i != -1) {
                particlePosInSentence.put(i, particle.kana);
            }
        }

        return particlePosInSentence;
    }
    @Override
    public String[] split(String sentence) {

        HashMap<Integer, String> particlePos = calculatePosOfParticle(sentence);
        log.info("particle pos: {}", particlePos);
        Object[] mapkey = particlePos.keySet().toArray();
        Arrays.sort(mapkey);

        int prevPos = 0;
        int lastPos = sentence.length();
        ArrayList<String> a = new ArrayList<>();
        for (int k=0; k < mapkey.length; k ++) {
            int currentPos = (int) mapkey[k];
            String part = sentence.substring(prevPos, currentPos);
            prevPos = currentPos;
            a.add(part);
            if (k == mapkey.length -1 && currentPos < lastPos) {
                part = sentence.substring(currentPos, lastPos);
                a.add(part);
            }

        }
        log.info(String.valueOf(a));
        return new String[0];
    }
}
