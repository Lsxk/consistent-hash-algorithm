import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.TreeMap;

/**
 * 功能描述：
 *
 * @version 1.0.0
 * @since 2020-05-25
 */
public class Main {

    private TreeMap<Long, MemcachedNode> ketamaNodes;

    protected void setKetamaNodes(List<MemcachedNode> nodes) throws NoSuchAlgorithmException {
        TreeMap<Long, MemcachedNode> newNodeMap = new TreeMap<>();
        int numReps = 20;

        for (MemcachedNode node : nodes) {
            for (int i = 0; i < numReps / 4; i++) {
                MessageDigest md5 = MessageDigest.getInstance("MD5");

                md5.update(getKeyForNode(node, i));
                byte[] digest = md5.digest();

                // 一个md5，可产生4个唯一key
                for (int j = 0; j < 4; j++) {
                    Long k = ((long) (digest[3 + j * 4] & 0xFF) << 24) | ((long) (digest[2 + j * 4] & 0xFF) << 16) | (
                        (long) (digest[1 + j * 4] & 0xFF) << 8) | (long) (digest[j * 4] & 0xFF);
                    newNodeMap.put(k, node);
                    System.out.println(String.format("Adding node %s in position %d", node, k));
                }

            }
        }

        assert newNodeMap.size() == numReps * nodes.size();
        ketamaNodes = newNodeMap;

    }

    class MemcachedNode {
        private String ip;

        public String getIp() {
            return ip;
        }
    }

    private byte[] getKeyForNode(MemcachedNode node, int virtualNodeGroupNum) {
        return (node.getIp() + "#" + virtualNodeGroupNum).getBytes();
    }
}
