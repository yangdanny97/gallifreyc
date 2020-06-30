package gallifreyc.types;

import java.util.*;

class NameGenerator_c implements NameGenerator {
    private ArrayList<Integer> previous_string = new ArrayList<>();
    private final char[] character_map = { 'a', 'b', 'c', 'd', 'e', 'j', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
            'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

    public NameGenerator_c() {
        previous_string.add(0);
    }

    private String list_to_string(List<Integer> al) {
        StringBuilder sb = new StringBuilder();
        for (Integer i : al) {
            sb.append(character_map[i]);
        }
        return sb.toString();
    }

    private void increment_string(ArrayList<Integer> string, int index) {
        if (string.get(index) + 1 == character_map.length) {
            if (index == 0) {
                string.add(0, 0);
            } else {
                increment_string(string, index - 1);
            }
        } else {
            string.set(index, string.get(index) + 1);
        }
    }

    public String generate() {
        ArrayList<Integer> new_string = (ArrayList<Integer>) previous_string.clone();
        increment_string(new_string, new_string.size() - 1);
        return list_to_string(new_string);
    }

}
