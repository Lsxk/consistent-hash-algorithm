import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 功能描述：
 *
 * @version 1.0.0
 * @since 2020-05-25
 */
public class Ketama {

    private TreeMap<Long, MemcachedNode> ketamaNodes;

    protected void setKetamaNodes(List<MemcachedNode> nodes) throws NoSuchAlgorithmException {
        TreeMap<Long, MemcachedNode> newNodeMap = new TreeMap<>();
        int numReps = 160;

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

    public TreeMap<Long, MemcachedNode> getKetamaNodes() {
        return ketamaNodes;
    }

    private MemcachedNode getNodeForHash(long hash) {
        if (!ketamaNodes.containsKey(hash)) {
            SortedMap<Long, MemcachedNode> tailMap = ketamaNodes.tailMap(hash);
            if (tailMap.isEmpty()) {
                hash = ketamaNodes.firstKey();
            } else {
                hash = tailMap.firstKey();
            }
        }
        return ketamaNodes.get(hash);
    }

    public MemcachedNode getKetemaNodeForKey(String key) throws NoSuchAlgorithmException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(key.getBytes());
        byte[] digest = md5.digest();

        return getNodeForHash(
            ((long) (digest[3] & 0xff) << 24) | ((long) (digest[2] & 0xff) << 16) | ((long) (digest[2] & 0xff) << 8)
                | ((long) (digest[0] & 0xff)));
    }

    public static class MemcachedNode {
        private String ip;

        public MemcachedNode(String ip) {
            this.ip = ip;
        }

        public String getIp() {
            return ip;
        }

        @java.lang.Override
        public java.lang.String toString() {
            return "MemcachedNode{" +
                "ip='" + ip + '\'' +
                '}';
        }
    }

    private byte[] getKeyForNode(MemcachedNode node, int virtualNodeGroupNum) {
        return (node.getIp() + "#" + virtualNodeGroupNum).getBytes();
    }
}
