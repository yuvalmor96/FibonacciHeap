
/**
 * FibonacciHeap
 *
 * An implementation of fibonacci heap over integers.
 */
public class FibonacciHeap
{
	protected static final double GOLDEN_RATIO = (1 + Math.sqrt(5))/2;
	protected static int totalLinks = 0;
	protected static int totalCuts = 0;
	protected HeapNode min;
	protected HeapNode first;
	protected int size;
	public int treeNum;
	public int markedNum;
	
	/**
	 * public FibonacciHeap()
	 *
	 * precondition: none
	 * 
	 * The method creates a new heap with default values..
	 *   
	 */
	public FibonacciHeap() {
		this.min = null;
		this.first = null;
		this.size = 0;
		this.treeNum = 0;
		this.markedNum = 0;
	}
	
	
   /**
    * public boolean isEmpty()
    *
    * precondition: none
    * 
    * The method returns true if and only if the heap
    * is empty.
    *   
    */
    public boolean isEmpty()
    {
    	return this.first == null;
    }
		
   /**
    * public HeapNode insert(int key)
    *
    * Creates a node (of type HeapNode) which contains the given key, and inserts it into the heap.
    * 
    * Returns the new node created. 
    */
    public HeapNode insert(int key)
    {    
    	HeapNode newFirst = new HeapNode(key);
    	
    	if (this.isEmpty())
    		this.min = newFirst;
    	else {
    		this.first.prevBro.setNextBro(newFirst); // next of last
    		newFirst.setPrevBro(this.first.prevBro); // prev of new first 
    		this.first.prevBro = newFirst; // prev of first
    		newFirst.setNextBro(this.first); // next of new first
    		if (key < this.min.getKey())
        		this.min = newFirst;
    	}
    	
    	this.first = newFirst;
    	this.size++;
    	this.treeNum++;
    
    	return newFirst;
    }

    
    /**
     * public HeapNode insertLMin(heapNode n)
     *
     * Creates a node (of type HeapNode) which contains the given node as a kMinNode parameter, and inserts it into the heap.
     * 
     * Returns the new node created. 
     */
     private HeapNode insertKMin(HeapNode n)
     {    
     	HeapNode newFirst = new HeapNode(n);
     	
     	if (this.isEmpty())
     		this.min = newFirst;
     	else {
     		this.first.prevBro.setNextBro(newFirst); // next of last
     		newFirst.setPrevBro(this.first.prevBro); // prev of new first 
     		this.first.prevBro = newFirst; // prev of first
     		newFirst.setNextBro(this.first); // next of new first
     		if (n.getKey() < this.min.getKey())
         		this.min = newFirst;
     	}
     	
     	this.first = newFirst;
     	this.size++;
     	this.treeNum++;
     
     	return newFirst;
     }

    
    /**
     * private HeapNode insertTree(int key)
     *
     * Inserts the given node (or tree) into the heap, as last tree.
     *  
     * Returns the given HeapNode;
     */
     private HeapNode insertTree(HeapNode x)
     {    
     	if (this.isEmpty()) {
     		this.first = x;
     		this.min = x;
     	}
     	else {
     		x.setPrevBro(this.first.getPrevBro()); // next of last
     		this.first.getPrevBro().setNextBro(x); // next of last
     		x.setNextBro(this.first); // prev of first
     		this.first.setPrevBro(x); // prev of first
     		if (x.getKey() < this.min.getKey())
         		this.min = x;
     		this.first = x;
     	}
     	this.treeNum++;
     	return x;
     }
    
    /**
     * private HeapNode link(HeapNode x, HeapNode y)()
     *
     * The method creates a simple link between 
     * two trees of the same order, by comparing their keys.
     */    
    protected HeapNode link(HeapNode x, HeapNode y) {
    	HeapNode temp = null;
    	if (x.getKey() > y.getKey()) { // in order to make y the bigger-key-node
    		temp = y;
    		y = x;
    		x = temp;
    	}
    	if (x.getFirstChild() == null) { // if x doesn't have children
    		y.setNextBro(y);
    		y.setPrevBro(y);
    	}
    	else {
    		y.setPrevBro(x.getFirstChild().getPrevBro());//old last child to y
    		x.getFirstChild().getPrevBro().setNextBro(y);// old last child to y
    		y.setNextBro(x.getFirstChild()); // old first child to y
    		x.getFirstChild().setPrevBro(y); // old first child to y
    	}
    	x.setFirstChild(y);
    	y.setParent(x);
    	
    	this.treeNum--;
    	totalLinks++;
    	return x;
    }

    /**
     * private void clearHeap()
     *
     * The method clears the attributes of the heap. 
     */
    private void clearHeap() {
    	this.first = null;
    	this.min = null;
    	//this.size = 0;
    	this.treeNum = 0;
    }
    
    /**
     * private HeapNode[] toBuckets()
     *
     * The method organises the trees of the heap in buckets 
     * and creates the links if necessary.
     * After running this method, the heap will have at most
     * one tree from any order.
     * The method returns an array of the trees, ordered by order.
     */
    private HeapNode[] toBuckets() {
    	if (this.isEmpty())
    		return new HeapNode[0];
    	HeapNode x = this.first;
    	int arraySize = Math.max(1, (int) Math.ceil(Math.log(this.size())/ Math.log(GOLDEN_RATIO)));
    	HeapNode[] B = new HeapNode[arraySize];
    	x.getPrevBro().setNextBro(null); // disconnecting x from the brothers cyclic list
    	x.setPrevBro(null);
    	while (x != null) {
    		HeapNode y = x; // variable for the node in buckets
    		x = x.getNextBro();
    		y.setPrevBro(y);
    		y.setNextBro(y);
    		while (B[y.getRank()] != null) {
    			y = link(y, B[y.getRank()]); // linking y with tree in bucket
    			y.setRank(y.getRank()+1); // incrementing rank
    			B[y.getRank()-1] = null;
    		}
    		B[y.getRank()] = y;
    	}
    	return B; // buckets list
    }
    
    /**
     * private void fromBuckets()
     *
     * The method gets an array of tree and inserts them to the heap
     * left to right. 
     */
    private void fromBuckets(HeapNode[] B) {
    	clearHeap();
       	for (int i=B.length-1; i>=0; i--) { // scanning the buckets array
    		if (B[i] != null)
    			this.insertTree(B[i]);
    		}
    }
    
    /**
     * private void consolidate()
     *
     * The method changes the heap to fulfil the rule
     * of at most one tree from any order.
     */
    private void consolidate() {
    	HeapNode[] B = toBuckets();
    	fromBuckets(B);
    }
    
    /**
     * private FibonacciHeap removeMin()
     *
     * The method removes the min node and returns a heap 
     * that contains the subtrees of the min node.
     *
     */
    private void removeMin() {
    	HeapNode minNode = this.min;
    	if (minNode.getMark() == 1)
    		this.markedNum--;
    	
    	HeapNode firstChild = minNode.getFirstChild();
    	
    	if (firstChild != null) { // if min has children
    		if (minNode.getNextBro() == minNode) { // min is only child
    			this.first = firstChild;
    		}
    		else {
    			minNode.getNextBro().setPrevBro(firstChild.getPrevBro()); // min's children before its next bro
	    		firstChild.getPrevBro().setNextBro(minNode.getNextBro()); // min's children before its next bro
	    		minNode.getPrevBro().setNextBro(firstChild); // min's children after its prev bro
	    		firstChild.setPrevBro(minNode.getPrevBro()); // min's children after its prev bro
	    
	    		if (minNode == this.first) 
	        		this.first = firstChild;
    		}
    	}
    	else { // if min doesn't have children
    		if (minNode == this.first) {
    			if (minNode.getNextBro() == minNode) // min is only child
    				this.first = null;
    			else  // min doesn't have children and has brothers
    				this.first = minNode.getNextBro();
    		}
    		minNode.getPrevBro().setNextBro(minNode.getNextBro()); // skipping min in brothers list
			minNode.getNextBro().setPrevBro(minNode.getPrevBro()); // skipping min in brothers list
    	}
    	
    	this.min = this.first; // dummy, will be updated in updateMin
    	minNode = null;
    	this.size--;
    }
    
    /**
     * public void meld (FibonacciHeap heap2)
     *
     * Meld the heap with heap2
     *
     */
     public void meld (FibonacciHeap heap2)
     {
    	 if (this.isEmpty()) {
    		this.first = heap2.first;
    		this.min = heap2.findMin();
    	 }
    	 else if (!heap2.isEmpty()){
	     	  if (heap2.findMin().getKey() < this.findMin().getKey()) // updating min if necessary
	     		  this.min = heap2.findMin();
	     	  
	     	  HeapNode heap2Last = heap2.first.prevBro;
	     	  HeapNode thisLast = this.first.prevBro;
	     	  thisLast.setNextBro(heap2.first); // this last and heap2 first
	     	  heap2.first.setPrevBro(thisLast); // this last and heap2 first
	     	  heap2Last.setNextBro(this.first); // heap2 last and this first
	     	  this.first.setPrevBro(heap2Last); // heap2 last and this first
	     	  
    	  }
    	 
    	  this.size += heap2.size(); // updating size
    	  this.markedNum += heap2.markedNum;
    	  this.treeNum += heap2.treeNum;
     }
    
    /**
     * private void updateMin()
     *
     * The method iterates over the heap's roots 
     * and updates the min node of the heap.
     *
     */
    private void updateMin() {
    	if (!this.isEmpty()) {
	    	int min = this.first.getKey();
	    	HeapNode cur = this.first.getNextBro();
	    	while (cur != this.first) {
	    		if (cur.getKey() < min) {
	    			this.min = cur;
	    			min = cur.getKey();
	    		}
	    		cur = cur.getNextBro();
	    	}
	    }
    }
    
   /**
    * public void deleteMin()
    *
    * Delete the node containing the minimum key.
    *
    */
    public void deleteMin()
    {
    	removeMin();
    	consolidate();
    	updateMin();
    }

   /**
    * public HeapNode findMin()
    *
    * Return the node of the heap whose key is minimal. 
    *
    */
    public HeapNode findMin()
    {
    	return this.min;
    } 
   

   /**
    * public int size()
    *
    * Return the number of elements in the heap
    *   
    */
    public int size()
    {
    	return this.size; 
    }
    	
    /**
    * public int[] countersRep()
    *
    * Return a counters array, where the value of the i-th entry is the number of trees of order i in the heap. 
    * 
    */
    public int[] countersRep()
    {
    	int arraySize = Math.max(1, (int) Math.ceil(Math.log(this.size())/ Math.log(GOLDEN_RATIO)));
    	int[] arr = new int[arraySize];
    	arr[this.first.getRank()]++;
    	HeapNode cur = this.first.getNextBro();
    	while (cur != this.first) { // iterating over roots and counting by their ranks
    		arr[cur.getRank()]++;
    		cur = cur.getNextBro();
    	}
        return arr; 
    }
	
   /**
    * public void delete(HeapNode x)
    *
    * Deletes the node x from the heap. 
    *
    */
    public void delete(HeapNode x) 
    {    
    	this.decreaseKey(x, Integer.MAX_VALUE);
    	this.deleteMin();
    }
    
    /**
     * private void cut(HeapNode x, HeapNode y)
     *
     * Cuts x from its parent, y.
     *
     */
    private void cut(HeapNode x, HeapNode y) {
    	x.setParent(null);
    	if (x.getMark() == 1) {
    		x.setMark(0);
    		this.markedNum--;
    	}
    	y.setRank(y.getRank()-1); // y has one less child
    	if (x.getNextBro().equals(x)) // if x was an only child
    		y.setFirstChild(null);
    	else {
    		y.setFirstChild(x.getNextBro()); // second child is first child
    		x.getPrevBro().setNextBro(x.getNextBro()); // deleting x from y's children list
    		x.getNextBro().setPrevBro(x.getPrevBro()); // deleting x from y's children list
    	}
    	this.insertTree(x);
    	totalCuts++;    	
    }
    
    /**
     * private void cascadingCut(HeapNode x, HeapNode y)
     *
     * Cuts x from its parent, y. If needed, keeps cutting until gets to an unmarked parent.
     *
     */
    private void cascadingCut(HeapNode x, HeapNode y) {
	    cut(x,y);
	    if (y.getParent() != null) { // y is not the root
	    	if (y.getMark() == 0) {// if y wasn't marked
	    		y.setMark(1);
	    		this.markedNum++;
	    	}
	    	else
	    		cascadingCut(y, y.getParent());
    	}
    }

   /**
    * public void decreaseKey(HeapNode x, int delta)
    *
    * The function decreases the key of the node x by delta. The structure of the heap should be updated
    * to reflect this change (for example, the cascading cuts procedure should be applied if needed).
    */
    public void decreaseKey(HeapNode x, int delta)
    {    
    	x.setKey(x.getKey()-delta); // updating x's key
    	if (x != this.min) {
	    	if (x.getKey() < this.min.getKey())
	    		this.min = x;
	    	if (x.getParent() != null && x.getKey() < x.getParent().getKey()) // if x isn't the root of the tree and heap rule is broken
	    		cascadingCut(x, x.getParent());
    	}
    	
    }

   /**
    * public int potential() 
    *
    * This function returns the current potential of the heap, which is:
    * Potential = #trees + 2*#marked
    * The potential equals to the number of trees in the heap plus twice the number of marked nodes in the heap. 
    */
    public int potential() 
    {    
    	return this.treeNum + 2*this.markedNum;
    }

   /**
    * public static int totalLinks() 
    *
    * This static function returns the total number of link operations made during the run-time of the program.
    * A link operation is the operation which gets as input two trees of the same rank, and generates a tree of 
    * rank bigger by one, by hanging the tree which has larger value in its root on the tree which has smaller value 
    * in its root.
    */
    public static int totalLinks()
    {    
    	return totalLinks;
    }

   /**
    * public static int totalCuts() 
    *
    * This static function returns the total number of cut operations made during the run-time of the program.
    * A cut operation is the operation which diconnects a subtree from its parent (during decreaseKey/delete methods). 
    */
    public static int totalCuts()
    {    
    	return totalCuts;
    }

     /**
    * public static int[] kMin(FibonacciHeap H, int k) 
    *
    * This static function returns the k minimal elements in a binomial tree H.
    * The function should run in O(k*deg(H)). 
    * You are not allowed to change H.
    */
    public static int[] kMin(FibonacciHeap H, int k)
    {   
    	int index = 1;
    	HeapNode curParent = H.min;
    	int[] arr = new int[k];
    	arr[0] = H.min.getKey();
        FibonacciHeap tempH = new FibonacciHeap();
        HeapNode curMin = curParent.getFirstChild();

        while (index<k) { // O(k)
        	if (curParent.getFirstChild() != null) { 
		        tempH.insertKMin(curParent.getFirstChild());
		        HeapNode cur = curParent.getFirstChild().getNextBro();
		    	while (cur != curParent.getFirstChild()) { // O(deg(H)) 
		    		tempH.insertKMin(cur); 
		    		cur = cur.getNextBro();
		    	}
        	}
	    	
        	curMin = tempH.findMin().kminNode; // O(1)
	    	arr[index] = curMin.getKey();
	    	curParent = curMin; 
	    	tempH.deleteMin(); // O(deg(H))	
	    	index++;
        }
        
        return arr;
    }
    
   /**
    * public class HeapNode
    * 
    * If you wish to implement classes other than FibonacciHeap
    * (for example HeapNode), do it in this file, not in 
    * another file 
    *  
    */
    public class HeapNode{

	public int key; // the key of the node
	int rank; // the rank (number of children) of the node
	int mark; // the mark of the node
	HeapNode firstChild; // the left-most child of the node
	HeapNode prevBro; // the previous (to the left or cyclic) brother of the node
	HeapNode nextBro; // the next (to the right) brother of the node
	HeapNode parent; // the parent of the node
	HeapNode kminNode; // an equivalent node for kmin function

  	public HeapNode(int key) {
	    this.key = key;
	    this.prevBro = this;
	    this.nextBro = this;
      }
  	
  	public HeapNode(HeapNode n) {
	    this.key = n.getKey();
	    this.prevBro = this;
	    this.nextBro = this;
	    this.kminNode = n;
      }

  	public int getKey() {
	    return this.key;
      }
  	
  	void setKey(int key) {
  		this.key = key;
  	  }
  	
  	int getRank() {
	    return this.rank;
      }
  	
  	void setRank(int rank) {
  		this.rank = rank;
  	  }
  	
  	int getMark() {
	    return this.mark;
      }
  	
  	void setMark(int mark) {
  		this.mark = mark;
  	  }
  	
  	HeapNode getFirstChild() {
	    return this.firstChild;
      }
  	
  	void setFirstChild(HeapNode child) {
  		this.firstChild = child;
  	  }
  	
  	HeapNode getPrevBro() {
	    return this.prevBro;
      }
  	
  	void setPrevBro(HeapNode prevBro) {
  		this.prevBro = prevBro;
  	  }
  	
  	HeapNode getNextBro() {
	    return this.nextBro;
      }
  	
  	void setNextBro(HeapNode nextBro) {
  		this.nextBro = nextBro;
  	  }
  	
  	HeapNode getParent() {
	    return this.parent;
      }
  	
  	void setParent(HeapNode parent) {
  		this.parent = parent;
  	  }

    }
}
