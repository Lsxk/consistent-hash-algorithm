import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException {

        List<Ketama.MemcachedNode> node = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            node.add(new Ketama.MemcachedNode("127.0.0." + (i + 1)));
        }

        Ketama ketama = new Ketama();
        ketama.setKetamaNodes(node);

        Map<Ketama.MemcachedNode, Integer> nodes = new HashMap<>();
        // 10w个key测试分布
        for (int i = 0; i < 100000; i++) {
            Ketama.MemcachedNode o = ketama.getKetemaNodeForKey(i + "");
            if (nodes.containsKey(o)) {
                nodes.put(o, nodes.get(o) + 1);
            } else {
                nodes.put(o, 1);
            }
        }

        nodes.forEach(
            (memcachedNode, integer) -> System.out.println(
                memcachedNode.toString() + " " + "的比率是" + ((float) integer / 100000) * 100 + "%"));

    }
}
