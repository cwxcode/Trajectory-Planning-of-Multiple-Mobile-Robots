package tt.util.intervaltree;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

/**
 * balancing of the tree is not correct probably due to possible duplicity of the key value... so it is not able
 * to handle classic query but otherwise functions intersect() works pretty much well
 * <p/>
 * TODO fix me
 *
 * @author Alternative
 */
public class IntervalAVLTree<V> {

    public IntervalAVLNode<V> root;

    public IntervalAVLTree() {
    }

    public void insert(V value, Interval interval) {
        IntervalAVLNode<V> node = root;

        if (root == null) {
            root = new IntervalAVLNode<V>(value, interval);
            node = root;

        } else {
            while (true) {

                if (node.value.equals(value)) {
                    return;
                }

                if (node.interval.getA() > interval.getA()) {
                    if (node.left == null) {
                        node.left = new IntervalAVLNode<V>(value, interval, node, IntervalAVLNode.LEFT);
                        node = node.left;
                        break;
                    } else {
                        node = node.left;
                    }

                } else {
                    if (node.right == null) {
                        node.right = new IntervalAVLNode<V>(value, interval, node, IntervalAVLNode.RIGHT);
                        node = node.right;
                        break;
                    } else {
                        node = node.right;
                    }
                }
            }
        }
        node.refreshUpperNodes();
        balanceTree(node);
    }

    public Set<V> intersects(double point) {
        Set<V> list = new HashSet<V>();

        if (root == null) {
            return list;
        }

        Queue<IntervalAVLNode<V>> queue = new ArrayDeque<IntervalAVLNode<V>>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            IntervalAVLNode<V> node = queue.poll();
            if (node.interval.intersects(point))
                list.add(node.value);

            if (node.left != null && node.left.covered.intersects(point))
                queue.offer(node.left);

            if (node.right != null && node.right.covered.intersects(point))
                queue.offer(node.right);
        }

        return list;
    }

    public Set<V> intersects(Interval interval) {
        Set<V> list = new HashSet<V>();

        if (root == null) {
            return list;
        }

        Queue<IntervalAVLNode<V>> queue = new ArrayDeque<IntervalAVLNode<V>>();
        queue.offer(root);

        while (!queue.isEmpty()) {
            IntervalAVLNode<V> node = queue.poll();
            if (node.interval.intersects(interval))
                list.add(node.value);

            if (node.left != null && node.left.covered.intersects(interval))
                queue.offer(node.left);

            if (node.right != null && node.right.covered.intersects(interval))
                queue.offer(node.right);
        }

        return list;
    }

    private void balanceTree(IntervalAVLNode<V> node) {
        int childDirection = 0;
        int grandChildDirection = 0;

        IntervalAVLNode<V> childNode = null;
        while (true) {
            if (node.isUnbalanced() && !(childDirection == 0 || grandChildDirection == 0)) {
                if (grandChildDirection == childDirection) {
                    if (childDirection == IntervalAVLNode.LEFT) {
                        rotateR(node);
                    } else {
                        rotateL(node);
                    }
                } else {
                    if (childDirection == IntervalAVLNode.LEFT) {
                        rotateL(childNode);
                        rotateR(node);

                    } else {
                        rotateR(childNode);
                        rotateL(node);
                    }
                }
                break;
            } else {
                if (node.directionFromParent != 0) {
                    grandChildDirection = childDirection;
                    childDirection = node.directionFromParent;
                    childNode = node;
                    node = node.parent;
                } else {
                    break;
                }

            }
        }
    }

    private void rotateR(IntervalAVLNode<V> A) {
        IntervalAVLNode<V> E = A.left.right;
        IntervalAVLNode<V> B = A.left;

        B.right = A;
        if (A.directionFromParent == 0) {
            root = B;
            B.parent = null;
            B.directionFromParent = 0;

        } else {
            if (A.directionFromParent == IntervalAVLNode.LEFT) {
                A.parent.left = B;
            } else {
                A.parent.right = B;
            }
            B.parent = A.parent;
            B.directionFromParent = A.directionFromParent;
        }
        A.parent = B;
        A.directionFromParent = IntervalAVLNode.RIGHT;

        A.left = E;
        if (E != null) {
            E.parent = A;
            E.directionFromParent = IntervalAVLNode.LEFT;
            E.refreshUpperNodes();
        } else {
            A.heightL = -1;
            A.refreshUpperNodes();
        }

    }

    private void rotateL(IntervalAVLNode<V> A) {
        IntervalAVLNode<V> E = A.right.left;
        IntervalAVLNode<V> B = A.right;

        B.left = A;
        if (A.directionFromParent == 0) {
            root = B;
            B.parent = null;
            B.directionFromParent = 0;
        } else {
            if (A.directionFromParent == IntervalAVLNode.LEFT) {
                A.parent.left = B;
            } else {
                A.parent.right = B;
            }
            B.parent = A.parent;
            B.directionFromParent = A.directionFromParent;
        }

        A.parent = B;
        A.directionFromParent = IntervalAVLNode.LEFT;
        A.right = E;

        if (E != null) {
            E.parent = A;
            E.directionFromParent = IntervalAVLNode.RIGHT;
            E.refreshUpperNodes();
        } else {
            A.heightR = -1;
            A.refreshUpperNodes();
        }

    }


    public void printTree() {
        printTree(this.root, 0);
        System.out.println();
    }

    private void printTree(IntervalAVLNode<V> node, int depth) {
        if (node == null) {
            return;
        }

        for (int i = 0; i < depth; i++) {
            System.out.print(" ");
        }

        System.out.println(node);

        printTree(node.left, depth + 1);
        printTree(node.right, depth + 1);
    }
}
