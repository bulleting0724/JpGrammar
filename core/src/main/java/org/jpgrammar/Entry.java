package org.jpgrammar;


import lombok.Data;

import java.util.List;

@Data
public class Entry {
    String entrySeq;
    List<R_Ele> r_eleList;
    List<Sense> senseList;
    List<K_Ele> k_eleList;

}
